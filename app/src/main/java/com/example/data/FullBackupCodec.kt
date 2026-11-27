package com.example.data

import com.example.BuildConfig
import com.example.ui.DashboardStats
import com.example.ui.theme.ThemeEngine
import com.example.util.AppLanguage
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RestorableSettings(
    val dutyThresholdDays: Int?,
    val themeMode: String?,
    val themeEngineId: String?,
    val language: String?,
    val isDynamicAnimationsEnabled: Boolean?,
    val isLivingLuxuryMode: Boolean? = null,
    val isBiometricEnabled: Boolean?,
    val isNoScreenshotEnabled: Boolean?,
    val isDriveSyncEnabled: Boolean?,
    val lastDriveBackupTime: Long?,
    val selectedAnalyticsYear: Int?,
    val pinnedNotificationIds: Set<String>?,
    val dismissedNotificationIds: Set<String>?,
    val recentSearches: List<String>?
)

data class ParsedFullBackup(
    val personnelList: List<Personnel>,
    val leaveRecords: List<LeaveRecord>,
    val settings: RestorableSettings?
)

object FullBackupCodec {

    private const val BACKUP_FORMAT_VERSION = 2
    private const val DB_SCHEMA_VERSION = 9

    fun exportFullBackupJson(
        personnelList: List<Personnel>,
        leaveRecords: List<LeaveRecord>,
        stats: DashboardStats,
        dutyThresholdDays: Int,
        themeMode: AppThemeMode,
        themeEngineId: String = ThemeEngine.DEFAULT_ID,
        language: AppLanguage,
        isDynamicAnimationsEnabled: Boolean,
        isLivingLuxuryMode: Boolean = false,
        isBiometricEnabled: Boolean,
        isNoScreenshotEnabled: Boolean,
        isDriveSyncEnabled: Boolean,
        lastDriveBackupTime: Long,
        selectedAnalyticsYear: Int,
        pinnedNotificationIds: Set<String>,
        dismissedNotificationIds: Set<String>,
        recentSearches: List<String>,
        nowMillis: Long = System.currentTimeMillis()
    ): String {
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateTimeFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        val root = JSONObject()

        // 1. Metadata & Backup Info
        root.put(
            "backupInfo",
            JSONObject().apply {
                put("backupFormatVersion", BACKUP_FORMAT_VERSION)
                put("appName", "Respite Pulse")
                put("appVersion", BuildConfig.VERSION_NAME)
                put("databaseVersion", DB_SCHEMA_VERSION)
                put("exportedAtMillis", nowMillis)
                put("exportedAtFormatted", dateTimeFmt.format(Date(nowMillis)))
                put("language", language.code)
            }
        )

        // 2. Comprehensive Summary & Totals
        val totalLeaveDaysRecorded = leaveRecords.sumOf { it.durationDays }
        root.put(
            "summary",
            JSONObject().apply {
                put("totalPersonnel", stats.total)
                put("atHome", stats.atHome)
                put("returningToday", stats.returningToday)
                put("overdue", stats.overdue)
                put("onDuty", stats.onDuty)
                put("upcoming", stats.upcoming)
                put("eligible", stats.eligible)
                put("pinnedPersonnelCount", personnelList.count { it.isPinned })
                put("totalLeaveRecords", leaveRecords.size)
                put("totalLeaveDaysRecorded", totalLeaveDaysRecorded)
                put("dutyThresholdDays", dutyThresholdDays)
            }
        )

        // 3. Full App Settings & Preferences
        root.put(
            "settings",
            JSONObject().apply {
                put("dutyThresholdDays", dutyThresholdDays)
                put("themeMode", themeMode.code)
                put("themeEngineId", themeEngineId)
                put("language", language.code)
                put("isDynamicAnimationsEnabled", isDynamicAnimationsEnabled)
                put("isLivingLuxuryMode", isLivingLuxuryMode)
                put("isBiometricEnabled", isBiometricEnabled)
                put("isNoScreenshotEnabled", isNoScreenshotEnabled)
                put("isDriveSyncEnabled", isDriveSyncEnabled)
                put("lastDriveBackupTime", lastDriveBackupTime)
                put("selectedAnalyticsYear", selectedAnalyticsYear)
                put("pinnedNotificationIds", JSONArray(pinnedNotificationIds.toList()))
                put("dismissedNotificationIds", JSONArray(dismissedNotificationIds.toList()))
                put("recentSearches", JSONArray(recentSearches))
            }
        )

        // 4. Personnel Table (Full database fields + every computed day & status value)
        val personnelArray = JSONArray()
        personnelList.forEach { p ->
            val dutyRef = p.computeDutyReferenceInfo(leaveRecords, nowMillis)
            val personLeaves = Personnel.findLeavesForPersonnel(p, leaveRecords)
            val personTotalLeavesDays = personLeaves.sumOf { it.durationDays }
            val isEligible = p.isEligibleForLeave(dutyThresholdDays, leaveRecords, nowMillis)

            personnelArray.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("fullName", p.fullName)
                    put("fullNameLatin", p.fullNameLatin)
                    put("localizedName", p.getLocalizedName(language))
                    put("rank", p.rank)
                    put("rankLocalized", formatRank(p.rank, language))
                    put("wilaya", p.wilaya)
                    put("wilayaLocalized", formatWilaya(p.wilaya, language, includeCode = true))
                    put("registrationNumber", p.registrationNumber)
                    put("leaveType", p.leaveType)
                    put("leaveTypeLocalized", formatLeaveType(p.leaveType, language))
                    put("startDateMillis", p.startDateMillis)
                    put("returnDateMillis", p.returnDateMillis)
                    put("startDate", if (p.startDateMillis > 0L) dateFmt.format(Date(p.startDateMillis)) else "-")
                    put("returnDate", if (p.returnDateMillis > 0L) dateFmt.format(Date(p.returnDateMillis)) else "-")
                    put("addedAtMillis", p.addedAtMillis)
                    put("addedAtDate", dateTimeFmt.format(Date(p.addedAtMillis)))
                    put("isReturnedToDuty", p.isReturnedToDuty)
                    put("isPinned", p.isPinned)
                    put("pinnedAtMillis", p.pinnedAtMillis)
                    if (p.pinnedAtMillis > 0L) {
                        put("pinnedAtDate", dateTimeFmt.format(Date(p.pinnedAtMillis)))
                    }
                    put("notes", p.notes)

                    // Day counts & leave eligibility metrics
                    put("totalDays", p.totalLeaveDays)
                    put("daysRemainingToReturn", p.daysRemainingToReturn)
                    put("daysElapsedInLeave", p.daysElapsedInLeave)
                    put("daysOverdue", p.daysOverdue)
                    put("daysSinceDutyReturn", p.daysSinceDutyReturn)
                    put("effectiveDaysSinceDuty", dutyRef.daysSinceReference)
                    put("dutyReferenceSource", dutyRef.source.name)
                    put("dutyReferenceDateMillis", dutyRef.referenceDateMillis)
                    put("dutyReferenceDate", dateFmt.format(Date(dutyRef.referenceDateMillis)))
                    put("dutyThresholdDaysUsed", dutyThresholdDays)
                    put("isEligibleForLeave", isEligible)
                    put("leavesCountAllTime", personLeaves.size)
                    put("totalLeaveDaysAllTime", personTotalLeavesDays)

                    // Current status & localized descriptions
                    put("status", p.status.name)
                    put("statusLabel", p.status.getCompactBadgeLabel(language))
                    put("countdownText", p.getCountdownText(language))
                }
            )
        }
        root.put("personnel", personnelArray)

        // 5. Complete Leave Records Archive Table
        val leavesArray = JSONArray()
        leaveRecords.forEach { leave ->
            leavesArray.put(
                JSONObject().apply {
                    put("id", leave.id)
                    put("personnelId", leave.personnelId)
                    put("personnelName", leave.personnelName)
                    put("personnelNameLatin", leave.personnelNameLatin)
                    put("rank", leave.rank)
                    put("rankLocalized", formatRank(leave.rank, language))
                    put("registrationNumber", leave.registrationNumber)
                    put("leaveType", leave.leaveType)
                    put("leaveTypeLocalized", formatLeaveType(leave.leaveType, language))
                    put("startDateMillis", leave.startDateMillis)
                    put("returnDateMillis", leave.returnDateMillis)
                    put("startDate", if (leave.startDateMillis > 0L) dateFmt.format(Date(leave.startDateMillis)) else "-")
                    put("returnDate", if (leave.returnDateMillis > 0L) dateFmt.format(Date(leave.returnDateMillis)) else "-")
                    put("durationDays", leave.durationDays)
                    put("statusText", leave.statusText)
                    put("destinationWilaya", leave.destinationWilaya)
                    put("notes", leave.notes)
                    put("createdAtMillis", leave.createdAtMillis)
                    put("createdAtDate", dateTimeFmt.format(Date(leave.createdAtMillis)))
                }
            )
        }
        root.put("leaveRecords", leavesArray)

        return root.toString(2)
    }

    fun parseFullBackupJson(
        jsonString: String,
        replaceAll: Boolean,
        fallbackNowMillis: Long = System.currentTimeMillis()
    ): ParsedFullBackup {
        val trimmed = jsonString.trim()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        var personnelArray = JSONArray()
        var leavesArray = JSONArray()
        var settings: RestorableSettings? = null

        if (trimmed.startsWith("{")) {
            val rootObj = JSONObject(trimmed)
            personnelArray = rootObj.optJSONArray("personnel")
                ?: rootObj.optJSONArray("records")
                ?: rootObj.optJSONArray("data")
                ?: JSONArray()
            leavesArray = rootObj.optJSONArray("leaveRecords")
                ?: rootObj.optJSONArray("leave_records")
                ?: rootObj.optJSONArray("leaves")
                ?: JSONArray()
            rootObj.optJSONObject("settings")?.let { sObj ->
                settings = RestorableSettings(
                    dutyThresholdDays = if (sObj.has("dutyThresholdDays")) sObj.optInt("dutyThresholdDays") else null,
                    themeMode = if (sObj.has("themeMode")) sObj.optString("themeMode") else null,
                    themeEngineId = if (sObj.has("themeEngineId")) sObj.optString("themeEngineId") else null,
                    language = if (sObj.has("language")) sObj.optString("language") else null,
                    isDynamicAnimationsEnabled = if (sObj.has("isDynamicAnimationsEnabled")) sObj.optBoolean("isDynamicAnimationsEnabled") else null,
                    isLivingLuxuryMode = if (sObj.has("isLivingLuxuryMode")) sObj.optBoolean("isLivingLuxuryMode") else null,
                    isBiometricEnabled = if (sObj.has("isBiometricEnabled")) sObj.optBoolean("isBiometricEnabled") else null,
                    isNoScreenshotEnabled = if (sObj.has("isNoScreenshotEnabled")) sObj.optBoolean("isNoScreenshotEnabled") else null,
                    isDriveSyncEnabled = if (sObj.has("isDriveSyncEnabled")) sObj.optBoolean("isDriveSyncEnabled") else null,
                    lastDriveBackupTime = if (sObj.has("lastDriveBackupTime")) sObj.optLong("lastDriveBackupTime") else null,
                    selectedAnalyticsYear = if (sObj.has("selectedAnalyticsYear")) sObj.optInt("selectedAnalyticsYear") else null,
                    pinnedNotificationIds = sObj.optJSONArray("pinnedNotificationIds")?.let { arr ->
                        (0 until arr.length()).mapNotNull { idx -> arr.optString(idx).takeIf { it.isNotBlank() } }.toSet()
                    },
                    dismissedNotificationIds = sObj.optJSONArray("dismissedNotificationIds")?.let { arr ->
                        (0 until arr.length()).mapNotNull { idx -> arr.optString(idx).takeIf { it.isNotBlank() } }.toSet()
                    },
                    recentSearches = sObj.optJSONArray("recentSearches")?.let { arr ->
                        (0 until arr.length()).mapNotNull { idx -> arr.optString(idx).takeIf { it.isNotBlank() } }
                    }
                )
            }
        } else {
            personnelArray = JSONArray(trimmed)
        }

        val importedPersonnel = mutableListOf<Personnel>()
        for (i in 0 until personnelArray.length()) {
            val obj = personnelArray.getJSONObject(i)
            val fullNameLatin = obj.optString("fullNameLatin", "").ifBlank {
                obj.optString("full_name_latin", "")
            }.trim()
            val rawFullName = obj.optString("fullName", "").ifBlank {
                obj.optString("name", "")
            }.trim()
            val fullName = if (rawFullName.isBlank() && fullNameLatin.isBlank()) "غير محدد" else rawFullName
            val rank = normalizeRank(obj.optString("rank", "جندي - Djoundi / Soldat"))
            val regNumber = obj.optString("registrationNumber", "").ifBlank {
                obj.optString("regNumber", "DZ-000000")
            }
            val wilaya = normalizeWilaya(obj.optString("wilaya", "16 - الجزائر - Alger"))
            val leaveType = obj.optString("leaveType", "عطلة سنوية")
            val notes = obj.optString("notes", "")
            val isReturnedToDuty = obj.optBoolean("isReturnedToDuty", false)

            var startMillis = obj.optLong("startDateMillis", 0L)
            if (startMillis == 0L) {
                val dateStr = obj.optString("startDate", "").replace("\u200E", "").replace("\u200F", "").trim()
                val parsedStart = if (dateStr.isNotBlank() && dateStr != "-") {
                    runCatching { sdf.parse(dateStr)?.time }.getOrNull() ?: 0L
                } else 0L
                startMillis = when {
                    parsedStart > 0L -> parsedStart
                    !isReturnedToDuty -> fallbackNowMillis
                    else -> 0L
                }
            }

            var returnMillis = obj.optLong("returnDateMillis", 0L)
            if (returnMillis == 0L) {
                val dateStr = obj.optString("returnDate", "").replace("\u200E", "").replace("\u200F", "").trim()
                val parsedReturn = if (dateStr.isNotBlank() && dateStr != "-") {
                    runCatching { sdf.parse(dateStr)?.time }.getOrNull() ?: 0L
                } else 0L
                returnMillis = when {
                    parsedReturn > 0L -> parsedReturn
                    startMillis > 0L -> startMillis + 9 * 86_400_000L
                    else -> 0L
                }
            }

            val id = if (replaceAll) obj.optLong("id", 0L) else 0L
            val addedAt = obj.optLong("addedAtMillis", 0L).let { if (it > 0L) it else fallbackNowMillis }
            val isPinned = obj.optBoolean("isPinned", false)
            val pinnedAt = obj.optLong("pinnedAtMillis", 0L)

            importedPersonnel.add(
                Personnel(
                    id = id,
                    fullName = fullName,
                    fullNameLatin = fullNameLatin,
                    rank = rank,
                    registrationNumber = regNumber,
                    startDateMillis = startMillis,
                    returnDateMillis = returnMillis,
                    isReturnedToDuty = isReturnedToDuty,
                    leaveType = leaveType,
                    wilaya = wilaya,
                    notes = notes,
                    addedAtMillis = addedAt,
                    isPinned = isPinned,
                    pinnedAtMillis = if (isPinned && pinnedAt <= 0L) fallbackNowMillis else pinnedAt
                )
            )
        }

        val importedLeaves = mutableListOf<LeaveRecord>()
        for (i in 0 until leavesArray.length()) {
            val obj = leavesArray.getJSONObject(i)
            val personnelName = obj.optString("personnelName", "").ifBlank {
                obj.optString("fullName", "غير محدد")
            }
            val personnelNameLatin = obj.optString("personnelNameLatin", "").ifBlank {
                obj.optString("fullNameLatin", "")
            }.trim()
            val rank = normalizeRank(obj.optString("rank", "جندي - Djoundi / Soldat"))
            val regNumber = obj.optString("registrationNumber", "DZ-000000")
            val leaveType = obj.optString("leaveType", "عطلة سنوية")
            val startMillis = obj.optLong("startDateMillis", fallbackNowMillis)
            val returnMillis = obj.optLong("returnDateMillis", startMillis + 86400000L)
            val durationDays = obj.optLong("durationDays", 1L).coerceAtLeast(1L)
            val statusText = obj.optString("statusText", "جارية")
            val destinationWilaya = normalizeWilaya(obj.optString("destinationWilaya", "16 - الجزائر - Alger"))
            val notes = obj.optString("notes", "")
            val createdAt = obj.optLong("createdAtMillis", fallbackNowMillis).let { if (it > 0L) it else fallbackNowMillis }
            val pId = obj.optLong("personnelId", 0L)

            importedLeaves.add(
                LeaveRecord(
                    id = 0L,
                    personnelId = pId,
                    personnelName = personnelName,
                    personnelNameLatin = personnelNameLatin,
                    rank = rank,
                    registrationNumber = regNumber,
                    leaveType = leaveType,
                    startDateMillis = startMillis,
                    returnDateMillis = returnMillis,
                    durationDays = durationDays,
                    statusText = statusText,
                    destinationWilaya = destinationWilaya,
                    notes = notes,
                    createdAtMillis = createdAt
                )
            )
        }

        return ParsedFullBackup(
            personnelList = importedPersonnel,
            leaveRecords = importedLeaves,
            settings = settings
        )
    }
}
