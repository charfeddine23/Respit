package com.example.data

import com.example.util.AppLanguage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class MonthlyLeaveStat(
    val monthIndex: Int, // 0..11
    val leaveCount: Int,
    val totalDays: Long,
    val topRankRaw: String? = null
)

data class RankLeaveStat(
    val rankRaw: String,
    val leaveCount: Int,
    val totalDays: Long,
    val percentage: Float,
    val uniquePersonnelCount: Int
)

data class LeaveTypeStat(
    val leaveTypeRaw: String,
    val leaveCount: Int,
    val totalDays: Long,
    val percentage: Float
)

data class PersonnelAnnualLeaveStat(
    val personnelId: Long,
    val fullName: String,
    val fullNameLatin: String = "",
    val rankRaw: String,
    val registrationNumber: String,
    val wilayaRaw: String,
    val isActiveInPersonnel: Boolean,
    val isReturnedToDuty: Boolean,
    val leavesCountInYear: Int,
    val totalDaysInYear: Long,
    val leavesByType: Map<String, Int>,
    val referenceDateMillis: Long,
    val referenceSource: ReferenceDateSource,
    val daysSinceReference: Long,
    val isEligible: Boolean
) {
    fun getLocalizedName(lang: AppLanguage): String =
        Personnel.resolveLocalizedName(fullName, fullNameLatin, lang)

    val formattedReferenceDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(referenceDateMillis))}\u200E"
        }
}

data class LeaveAnalyticsReport(
    val selectedYear: Int,
    val availableYears: List<Int>,
    val totalLeavesInYear: Int,
    val totalDaysInYear: Long,
    val averageDurationDays: Float,
    val activeLeavesCount: Int,
    val eligiblePersonnelCount: Int,
    val monthlyStats: List<MonthlyLeaveStat>,
    val rankStats: List<RankLeaveStat>,
    val leaveTypeStats: List<LeaveTypeStat>,
    val personnelAnnualStats: List<PersonnelAnnualLeaveStat>,
    val peakMonthIndex: Int?,
    val peakMonthCount: Int,
    val topRankRaw: String?,
    val topLeaveTypeRaw: String?
)

object LeaveAnalyticsCalculator {

    fun extractYear(timeMillis: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMillis
        return cal.get(Calendar.YEAR)
    }

    fun extractMonthIndex(timeMillis: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMillis
        return cal.get(Calendar.MONTH)
    }

    fun computeReport(
        personnelList: List<Personnel>,
        allLeaves: List<LeaveRecord>,
        selectedYear: Int,
        dutyThresholdDays: Int,
        todayMillis: Long = System.currentTimeMillis()
    ): LeaveAnalyticsReport {
        val currentYear = extractYear(todayMillis)
        val yearsSet = sortedSetOf<Int>(compareByDescending { it })
        yearsSet.add(currentYear)
        yearsSet.add(selectedYear)
        allLeaves.forEach { leave ->
            yearsSet.add(extractYear(leave.startDateMillis))
            yearsSet.add(extractYear(leave.returnDateMillis))
        }
        val availableYears = yearsSet.toList()

        // Filter leaves that belong to selectedYear (attributed by startDateMillis year, or returnDateMillis if started before)
        val yearLeaves = allLeaves.filter { leave ->
            extractYear(leave.startDateMillis) == selectedYear
        }

        val totalLeavesInYear = yearLeaves.size
        val totalDaysInYear = yearLeaves.sumOf { it.durationDays.coerceAtLeast(1L) }
        val avgDuration = if (totalLeavesInYear > 0) totalDaysInYear.toFloat() / totalLeavesInYear else 0f

        // Monthly aggregation (0..11)
        val leavesByMonth = yearLeaves.groupBy { extractMonthIndex(it.startDateMillis) }
        val monthlyStats = (0..11).map { month ->
            val mLeaves = leavesByMonth[month].orEmpty()
            val topRank = mLeaves
                .groupBy { normalizeRank(it.rank) }
                .maxByOrNull { it.value.size }
                ?.key
            MonthlyLeaveStat(
                monthIndex = month,
                leaveCount = mLeaves.size,
                totalDays = mLeaves.sumOf { it.durationDays.coerceAtLeast(1L) },
                topRankRaw = topRank
            )
        }

        val peakMonth = monthlyStats.filter { it.leaveCount > 0 }.maxByOrNull { it.leaveCount }

        // Rank distribution aggregation (ordered by count desc, then military hierarchy)
        val leavesByRank = yearLeaves.groupBy { normalizeRank(it.rank) }
        val rankStats = leavesByRank.entries
            .map { (rank, list) ->
                val count = list.size
                val days = list.sumOf { it.durationDays.coerceAtLeast(1L) }
                val uniqueKeys = list.map {
                    it.registrationNumber.trim().lowercase(Locale.ROOT).ifBlank { "id_${it.personnelId}" }
                }.toSet()
                val pct = if (totalLeavesInYear > 0) (count.toFloat() / totalLeavesInYear.toFloat()) * 100f else 0f
                RankLeaveStat(
                    rankRaw = rank,
                    leaveCount = count,
                    totalDays = days,
                    percentage = pct,
                    uniquePersonnelCount = uniqueKeys.size
                )
            }
            .sortedWith(
                compareByDescending<RankLeaveStat> { it.leaveCount }
                    .thenByDescending { it.totalDays }
                    .thenBy { val idx = algerianRanks.indexOf(it.rankRaw); if (idx >= 0) idx else Int.MAX_VALUE }
            )

        // Leave type breakdown
        val leaveTypeStats = yearLeaves
            .groupBy { it.leaveType.ifBlank { "إجازة" } }
            .entries
            .map { (type, list) ->
                val count = list.size
                val days = list.sumOf { it.durationDays.coerceAtLeast(1L) }
                val pct = if (totalLeavesInYear > 0) (count.toFloat() / totalLeavesInYear.toFloat()) * 100f else 0f
                LeaveTypeStat(
                    leaveTypeRaw = type,
                    leaveCount = count,
                    totalDays = days,
                    percentage = pct
                )
            }
            .sortedByDescending { it.leaveCount }

        // Per-Personnel Annual Statistics + Reference Date (last leave returnDateMillis or addedAtMillis)
        val todayStart = Personnel.toStartOfDay(todayMillis)
        val matchedKeys = mutableSetOf<String>()
        val personnelStatsList = mutableListOf<PersonnelAnnualLeaveStat>()

        personnelList.forEach { person ->
            val regKey = person.registrationNumber.trim().lowercase(Locale.ROOT)
            if (regKey.isNotBlank()) matchedKeys.add(regKey)

            val personAllLeaves = Personnel.findLeavesForPersonnel(person, allLeaves)
            val personYearLeaves = personAllLeaves.filter { extractYear(it.startDateMillis) == selectedYear }
            val dutyInfo = person.computeDutyReferenceInfo(allLeaves, todayMillis)
            val typeCounts = personYearLeaves.groupingBy { it.leaveType.ifBlank { "إجازة" } }.eachCount()

            personnelStatsList.add(
                PersonnelAnnualLeaveStat(
                    personnelId = person.id,
                    fullName = person.fullName,
                    fullNameLatin = person.fullNameLatin,
                    rankRaw = normalizeRank(person.rank),
                    registrationNumber = person.registrationNumber,
                    wilayaRaw = person.wilaya,
                    isActiveInPersonnel = true,
                    isReturnedToDuty = person.isReturnedToDuty,
                    leavesCountInYear = personYearLeaves.size,
                    totalDaysInYear = personYearLeaves.sumOf { it.durationDays.coerceAtLeast(1L) },
                    leavesByType = typeCounts,
                    referenceDateMillis = dutyInfo.referenceDateMillis,
                    referenceSource = dutyInfo.source,
                    daysSinceReference = dutyInfo.daysSinceReference,
                    isEligible = person.isReturnedToDuty && dutyInfo.daysSinceReference >= dutyThresholdDays
                )
            )
        }

        // Also include historical personnel present only in leave_records (if personnel table was cleared)
        val orphanLeaveGroups = allLeaves.filter { leave ->
            val regKey = leave.registrationNumber.trim().lowercase(Locale.ROOT)
            regKey.isNotBlank() && regKey !in matchedKeys
        }.groupBy { it.registrationNumber.trim().lowercase(Locale.ROOT) }

        orphanLeaveGroups.forEach { (_, personAllLeaves) ->
            val latest = personAllLeaves.maxWithOrNull(
                compareBy<LeaveRecord> { it.returnDateMillis }.thenBy { it.createdAtMillis }
            ) ?: return@forEach
            val personYearLeaves = personAllLeaves.filter { extractYear(it.startDateMillis) == selectedYear }
            val refStart = Personnel.toStartOfDay(latest.returnDateMillis)
            val daysSince = Personnel.signedDaysBetween(refStart, todayStart).coerceAtLeast(0L)
            val typeCounts = personYearLeaves.groupingBy { it.leaveType.ifBlank { "إجازة" } }.eachCount()

            personnelStatsList.add(
                PersonnelAnnualLeaveStat(
                    personnelId = latest.personnelId,
                    fullName = latest.personnelName,
                    rankRaw = normalizeRank(latest.rank),
                    registrationNumber = latest.registrationNumber,
                    wilayaRaw = latest.destinationWilaya,
                    isActiveInPersonnel = false,
                    isReturnedToDuty = true,
                    leavesCountInYear = personYearLeaves.size,
                    totalDaysInYear = personYearLeaves.sumOf { it.durationDays.coerceAtLeast(1L) },
                    leavesByType = typeCounts,
                    referenceDateMillis = refStart,
                    referenceSource = ReferenceDateSource.LAST_LEAVE_RETURN,
                    daysSinceReference = daysSince,
                    isEligible = daysSince >= dutyThresholdDays
                )
            )
        }

        val sortedPersonnelStats = personnelStatsList.sortedWith(
            compareByDescending<PersonnelAnnualLeaveStat> { it.leavesCountInYear }
                .thenByDescending { it.totalDaysInYear }
                .thenByDescending { it.daysSinceReference }
                .thenBy { it.fullName }
        )

        val activeLeavesCount = personnelList.count { !it.isReturnedToDuty }
        val eligiblePersonnelCount = personnelStatsList.count { it.isActiveInPersonnel && it.isEligible }

        return LeaveAnalyticsReport(
            selectedYear = selectedYear,
            availableYears = availableYears,
            totalLeavesInYear = totalLeavesInYear,
            totalDaysInYear = totalDaysInYear,
            averageDurationDays = avgDuration,
            activeLeavesCount = activeLeavesCount,
            eligiblePersonnelCount = eligiblePersonnelCount,
            monthlyStats = monthlyStats,
            rankStats = rankStats,
            leaveTypeStats = leaveTypeStats,
            personnelAnnualStats = sortedPersonnelStats,
            peakMonthIndex = peakMonth?.monthIndex,
            peakMonthCount = peakMonth?.leaveCount ?: 0,
            topRankRaw = rankStats.firstOrNull()?.rankRaw,
            topLeaveTypeRaw = leaveTypeStats.firstOrNull()?.leaveTypeRaw
        )
    }
}
