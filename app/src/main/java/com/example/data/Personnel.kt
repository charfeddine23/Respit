package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class LeaveStatus(
    val labelAr: String,
    val hexColor: Long,
    val bgHexColor: Long
) {
    AT_HOME("في إجازة بالبيت 🏠", 0xFF0284C7, 0xFFE0F2FE),
    RETURNING_TODAY("الدخول اليوم 🔔", 0xFFD97706, 0xFFFEF3C7),
    OVERDUE("متأخر عن الدخول ⚠️", 0xFFDC2626, 0xFFFEE2E2),
    UPCOMING("إجازة مبرمجة ⏳", 0xFF7C3AED, 0xFFEDE9FE),
    ON_DUTY("حاضر بالخدمة 🎖️", 0xFF059669, 0xFFD1FAE5);

    fun getLabel(lang: AppLanguage): String = when (this) {
        AT_HOME -> AppStrings.get("status_at_home", lang)
        RETURNING_TODAY -> AppStrings.get("status_returning_today", lang)
        OVERDUE -> AppStrings.get("status_overdue", lang)
        UPCOMING -> AppStrings.get("status_upcoming", lang)
        ON_DUTY -> AppStrings.get("status_on_duty", lang)
    }

    fun getCompactBadgeLabel(lang: AppLanguage): String = when (this) {
        AT_HOME -> AppStrings.get("badge_at_home", lang)
        RETURNING_TODAY -> AppStrings.get("badge_returning_today", lang)
        OVERDUE -> AppStrings.get("badge_overdue", lang)
        UPCOMING -> AppStrings.get("badge_upcoming", lang)
        ON_DUTY -> AppStrings.get("badge_on_duty", lang)
    }
}

enum class ReferenceDateSource {
    LAST_LEAVE_RETURN,
    PERSONNEL_ADDED_DATE
}

data class DutyReferenceInfo(
    val referenceDateMillis: Long,
    val source: ReferenceDateSource,
    val daysSinceReference: Long,
    val lastLeaveStartMillis: Long? = null,
    val lastLeaveReturnMillis: Long? = null
)

data class ShortLeaveCounterInfo(
    val record: LeaveRecord,
    val daysCounter: Long
) {
    val isCurrentlyOnShortLeave: Boolean
        get() = daysCounter < 0L

    val isReturningToday: Boolean
        get() = daysCounter == 0L

    fun getCounterStatusText(lang: AppLanguage): String = when {
        daysCounter < 0L -> {
            val rem = -daysCounter
            when (lang) {
                AppLanguage.ARABIC -> when {
                    rem == 1L -> "باقي يوم واحد على الدخول"
                    rem == 2L -> "باقي يومان على الدخول"
                    rem in 3L..10L -> "باقي $rem أيام على الدخول"
                    else -> "باقي $rem يوماً على الدخول"
                }
                AppLanguage.FRENCH -> if (rem == 1L) "1 jour restant" else "$rem jours restants"
                AppLanguage.TAMAZIGHT -> "$rem n wussan i d-yeqqimen"
                else -> if (rem == 1L) "1 day remaining" else "$rem days remaining"
            }
        }
        daysCounter == 0L -> when (lang) {
            AppLanguage.ARABIC -> "الدخول اليوم"
            AppLanguage.FRENCH -> "Reprise aujourd'hui"
            AppLanguage.TAMAZIGHT -> "Tuɣalin ass-a"
            else -> "Return today"
        }
        else -> when (lang) {
            AppLanguage.ARABIC -> when {
                daysCounter == 1L -> "منقضي يوم واحد منذ العودة"
                daysCounter == 2L -> "منقضي يومان منذ العودة"
                daysCounter in 3L..10L -> "منقضي $daysCounter أيام منذ العودة"
                else -> "منقضي $daysCounter يوماً منذ العودة"
            }
            AppLanguage.FRENCH -> "$daysCounter j écoulé(s) depuis la reprise"
            AppLanguage.TAMAZIGHT -> "Zrin $daysCounter n wussan seg tuɣalin"
            else -> if (daysCounter == 1L) "1 day since return" else "$daysCounter days since return"
        }
    }
}

@Entity(tableName = "personnel")
data class Personnel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val fullNameLatin: String = "",
    val rank: String,
    val registrationNumber: String,
    val startDateMillis: Long,     // تاريخ الإستفادة (بداية الإجازة)
    val returnDateMillis: Long,    // تاريخ الدخول (تاريخ العودة للعمل)
    val isReturnedToDuty: Boolean = false, // هل التحق بالخدمة ومستأنف للعمل
    val leaveType: String = "عطلة سنوية",
    val wilaya: String = "16 - الجزائر - Alger",
    val notes: String = "",
    val addedAtMillis: Long = System.currentTimeMillis(), // تاريخ إضافة الفرد في قاعدة البيانات
    val isPinned: Boolean = false,
    val pinnedAtMillis: Long = 0L,
    val isDeleted: Boolean = false,
    val deletedAtMillis: Long = 0L
) {
    companion object {
        fun toStartOfDay(timeMillis: Long): Long {
            val cal = Calendar.getInstance().apply {
                this.timeInMillis = timeMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

        fun signedDaysBetween(fromMillis: Long, toMillis: Long): Long {
            val fromStart = toStartOfDay(fromMillis)
            val toStart = toStartOfDay(toMillis)
            return kotlin.math.round((toStart - fromStart).toDouble() / 86_400_000.0).toLong()
        }

        fun inclusiveLeaveDays(startMillis: Long, endMillis: Long): Long {
            val start = toStartOfDay(startMillis)
            val end = toStartOfDay(endMillis)
            val span = signedDaysBetween(start, end).coerceAtLeast(0L)
            return span + 1L
        }

        fun findLeavesForPersonnel(person: Personnel, allLeaves: List<LeaveRecord>): List<LeaveRecord> {
            val regNormalized = person.registrationNumber.trim()
            return allLeaves.filter { leave ->
                (person.id > 0L && leave.personnelId == person.id) ||
                    (regNormalized.isNotBlank() && leave.registrationNumber.trim().equals(regNormalized, ignoreCase = true))
            }
        }

        fun resolveLocalizedName(arabicName: String, latinName: String, lang: AppLanguage): String {
            val ar = arabicName.trim()
            val latin = latinName.trim()
            return when (lang) {
                AppLanguage.ARABIC -> ar.ifBlank { latin }
                AppLanguage.TAMAZIGHT, AppLanguage.FRENCH, AppLanguage.ENGLISH -> latin.ifBlank { ar }
            }
        }
    }

    fun getLocalizedName(lang: AppLanguage): String =
        resolveLocalizedName(fullName, fullNameLatin, lang)

    /**
     * يحسب تاريخ بداية احتساب أيام الخدمة والاستحقاق:
     * - أثناء الإجازة/العطلة (قبل استئناف العمل): يحسب العداد بالسالب (-5، -4، ... حتى 0 يوم الدخول) ثم يكمل تصاعدياً (1، 2...).
     * - عند التواجد بالخدمة: يعتمد على تاريخ الدخول (العودة) من آخر عطلة مسجلة في قاعدة البيانات،
     *   وإذا لم توجد أي عطلة سابقة فيبدأ الحساب من تاريخ إضافة الفرد.
     */
    fun computeDutyReferenceInfo(
        allLeaves: List<LeaveRecord>,
        todayMillis: Long = System.currentTimeMillis()
    ): DutyReferenceInfo {
        val personLeaves = findLeavesForPersonnel(this, allLeaves)
        val nonShortLeaves = personLeaves.filterNot { isShortLeaveType(it.leaveType) }
        val lastLeave = nonShortLeaves.maxWithOrNull(
            compareBy<LeaveRecord> { it.returnDateMillis }.thenBy { it.createdAtMillis }
        )
        val todayStart = toStartOfDay(todayMillis)

        if (!isReturnedToDuty) {
            val activeReturnStart = toStartOfDay(returnDateMillis)
            val signedDays = signedDaysBetween(activeReturnStart, todayStart)
            return DutyReferenceInfo(
                referenceDateMillis = activeReturnStart,
                source = ReferenceDateSource.LAST_LEAVE_RETURN,
                daysSinceReference = signedDays,
                lastLeaveStartMillis = toStartOfDay(startDateMillis),
                lastLeaveReturnMillis = activeReturnStart
            )
        }

        return if (lastLeave != null) {
            val refStart = toStartOfDay(lastLeave.returnDateMillis)
            val days = signedDaysBetween(refStart, todayStart).coerceAtLeast(0L)
            DutyReferenceInfo(
                referenceDateMillis = refStart,
                source = ReferenceDateSource.LAST_LEAVE_RETURN,
                daysSinceReference = days,
                lastLeaveStartMillis = toStartOfDay(lastLeave.startDateMillis),
                lastLeaveReturnMillis = refStart
            )
        } else if (returnDateMillis > 0L && !isShortLeaveType(leaveType)) {
            val refStart = toStartOfDay(returnDateMillis)
            val days = signedDaysBetween(refStart, todayStart).coerceAtLeast(0L)
            DutyReferenceInfo(
                referenceDateMillis = refStart,
                source = ReferenceDateSource.LAST_LEAVE_RETURN,
                daysSinceReference = days,
                lastLeaveStartMillis = if (startDateMillis > 0L) toStartOfDay(startDateMillis) else null,
                lastLeaveReturnMillis = refStart
            )
        } else {
            val fallbackAdded = if (addedAtMillis > 0L) addedAtMillis else todayMillis
            val refStart = toStartOfDay(fallbackAdded)
            val days = signedDaysBetween(refStart, todayStart).coerceAtLeast(0L)
            DutyReferenceInfo(
                referenceDateMillis = refStart,
                source = ReferenceDateSource.PERSONNEL_ADDED_DATE,
                daysSinceReference = days,
                lastLeaveStartMillis = null,
                lastLeaveReturnMillis = null
            )
        }
    }

    fun isEligibleForLeave(
        thresholdDays: Int,
        allLeaves: List<LeaveRecord>,
        todayMillis: Long = System.currentTimeMillis()
    ): Boolean {
        if (!isReturnedToDuty) return false
        return computeDutyReferenceInfo(allLeaves, todayMillis).daysSinceReference >= thresholdDays
    }

    fun computeLastShortLeaveInfo(
        allLeaves: List<LeaveRecord>,
        todayMillis: Long = System.currentTimeMillis()
    ): ShortLeaveCounterInfo? {
        val lastShort = findLeavesForPersonnel(this, allLeaves)
            .filter { isShortLeaveType(it.leaveType) }
            .maxWithOrNull(
                compareBy<LeaveRecord> { it.returnDateMillis }
                    .thenBy { it.startDateMillis }
                    .thenBy { it.createdAtMillis }
            ) ?: return null

        val todayStart = toStartOfDay(todayMillis)
        val returnStart = toStartOfDay(lastShort.returnDateMillis)
        return ShortLeaveCounterInfo(
            record = lastShort,
            daysCounter = signedDaysBetween(returnStart, todayStart)
        )
    }

    // إجمالي مدة الإجازة بالأيام (عدّ شامل لتاريخ البداية وتاريخ النهاية)
    val totalLeaveDays: Long
        get() = if (startDateMillis <= 0L || returnDateMillis <= 0L) 0L
        else inclusiveLeaveDays(startDateMillis, returnDateMillis)

    // الأيام المتبقية على الدخول (شحال بقالو فدار ويدخل)
    val daysRemainingToReturn: Long
        get() {
            if (returnDateMillis <= 0L) return 0L
            val today = toStartOfDay(System.currentTimeMillis())
            val returnDay = toStartOfDay(returnDateMillis)
            return signedDaysBetween(today, returnDay)
        }

    // الأيام المنقضية من الإجازة
    val daysElapsedInLeave: Long
        get() {
            if (startDateMillis <= 0L) return 0L
            val today = toStartOfDay(System.currentTimeMillis())
            val start = toStartOfDay(startDateMillis)
            return signedDaysBetween(start, today).coerceAtLeast(0)
        }

    // عدد أيام التأخر عن الدخول (إن وجد)
    val daysOverdue: Long
        get() {
            if (returnDateMillis <= 0L) return 0L
            val today = toStartOfDay(System.currentTimeMillis())
            val returnDay = toStartOfDay(returnDateMillis)
            return signedDaysBetween(returnDay, today).coerceAtLeast(0)
        }

    // عداد الأيام بالنسبة لتاريخ الدخول: سالب أثناء الإجازة (-5..-1) ثم 0 يوم الدخول ثم تصاعدي (1، 2..)
    val daysSinceDutyReturn: Long
        get() {
            val today = toStartOfDay(System.currentTimeMillis())
            val refMillis = if (returnDateMillis > 0L) returnDateMillis
            else if (addedAtMillis > 0L) addedAtMillis else System.currentTimeMillis()
            val returnDay = toStartOfDay(refMillis)
            val rawDays = signedDaysBetween(returnDay, today)
            return if (isReturnedToDuty) rawDays.coerceAtLeast(0L) else rawDays
        }

    // هل الفرد مستحق للإجازة (تجاوز عتبة الخدمة المحددة)
    fun isEligibleForLeave(thresholdDays: Int): Boolean {
        return isReturnedToDuty && daysSinceDutyReturn >= thresholdDays
    }

    // هل الفرد حالياً في إجازة بالبيت
    val isCurrentlyAtHome: Boolean
        get() {
            if (isReturnedToDuty || startDateMillis <= 0L || returnDateMillis <= 0L) return false
            val today = toStartOfDay(System.currentTimeMillis())
            val start = toStartOfDay(startDateMillis)
            val returnDay = toStartOfDay(returnDateMillis)
            return today in start until returnDay
        }

    // الوضعية الحالية
    val status: LeaveStatus
        get() {
            if (isReturnedToDuty || startDateMillis <= 0L || returnDateMillis <= 0L) return LeaveStatus.ON_DUTY

            val today = toStartOfDay(System.currentTimeMillis())
            val start = toStartOfDay(startDateMillis)
            val returnDay = toStartOfDay(returnDateMillis)

            return when {
                today < start -> LeaveStatus.UPCOMING
                today in start until returnDay -> LeaveStatus.AT_HOME
                today == returnDay -> LeaveStatus.RETURNING_TODAY
                else -> LeaveStatus.OVERDUE
            }
        }

    val isOverdue: Boolean
        get() = status == LeaveStatus.OVERDUE

    // نص العداد التوضيحي باللغة العربية
    val countdownTextAr: String
        get() {
            return when (status) {
                LeaveStatus.AT_HOME -> {
                    val rem = daysRemainingToReturn
                    when {
                        rem <= 0 -> "الدخول اليوم"
                        rem == 1L -> "باقي يوم واحد على الدخول"
                        rem == 2L -> "باقي يومان على الدخول"
                        rem in 3L..10L -> "باقي $rem أيام على الدخول"
                        else -> "باقي $rem يوماً على الدخول"
                    }
                }
                LeaveStatus.RETURNING_TODAY -> "تاريخ الدخول هو اليوم!"
                LeaveStatus.OVERDUE -> {
                    val ov = daysOverdue
                    when {
                        ov <= 0 -> "تجاوز موعد الدخول"
                        ov == 1L -> "متأخر بيوم واحد عن الدخول!"
                        ov == 2L -> "متأخر بيومين عن الدخول!"
                        ov in 3L..10L -> "متأخر بـ $ov أيام عن الدخول!"
                        else -> "متأخر بـ $ov يوماً عن الدخول!"
                    }
                }
                LeaveStatus.UPCOMING -> {
                    val today = toStartOfDay(System.currentTimeMillis())
                    val start = toStartOfDay(startDateMillis)
                    val daysUntil = TimeUnit.MILLISECONDS.toDays(start - today)
                    "تبدأ الإجازة بعد $daysUntil يوم"
                }
                LeaveStatus.ON_DUTY -> "حاضر بالخدمة (منقضي $daysSinceDutyReturn يوم)"
            }
        }

    fun getCountdownText(lang: AppLanguage): String {
        if (lang == AppLanguage.ARABIC) return countdownTextAr
        return when (status) {
            LeaveStatus.AT_HOME -> {
                val rem = daysRemainingToReturn
                when {
                    rem <= 0 -> when (lang) {
                        AppLanguage.TAMAZIGHT -> "Tuɣalin ass-a"
                        AppLanguage.FRENCH -> "Reprise aujourd'hui"
                        else -> "Return today"
                    }
                    rem == 1L -> when (lang) {
                        AppLanguage.TAMAZIGHT -> "1 wass i d-yeqqimen uqbel tuɣalin"
                        AppLanguage.FRENCH -> "1 jour restant avant reprise"
                        else -> "1 day remaining until return"
                    }
                    else -> when (lang) {
                        AppLanguage.TAMAZIGHT -> "$rem n wussan i d-yeqqimen uqbel tuɣalin"
                        AppLanguage.FRENCH -> "$rem jours restants avant reprise"
                        else -> "$rem days remaining until return"
                    }
                }
            }
            LeaveStatus.RETURNING_TODAY -> when (lang) {
                AppLanguage.TAMAZIGHT -> "Ass n tuɣalin ɣer umahil d ass-a!"
                AppLanguage.FRENCH -> "La date de reprise est aujourd'hui !"
                else -> "Return to duty is today!"
            }
            LeaveStatus.OVERDUE -> {
                val ov = daysOverdue
                when {
                    ov <= 0 -> when (lang) {
                        AppLanguage.TAMAZIGHT -> "Yezri wakud n tuɣalin"
                        AppLanguage.FRENCH -> "Délai de reprise dépassé"
                        else -> "Return deadline passed"
                    }
                    ov == 1L -> when (lang) {
                        AppLanguage.TAMAZIGHT -> "Yeɛeṭṭel s 1 wass!"
                        AppLanguage.FRENCH -> "En retard de 1 jour !"
                        else -> "Overdue by 1 day!"
                    }
                    else -> when (lang) {
                        AppLanguage.TAMAZIGHT -> "Yeɛeṭṭel s $ov n wussan!"
                        AppLanguage.FRENCH -> "En retard de $ov jours !"
                        else -> "Overdue by $ov days!"
                    }
                }
            }
            LeaveStatus.UPCOMING -> {
                val today = toStartOfDay(System.currentTimeMillis())
                val start = toStartOfDay(startDateMillis)
                val daysUntil = TimeUnit.MILLISECONDS.toDays(start - today)
                when (lang) {
                    AppLanguage.TAMAZIGHT -> "Tazwara n yimuras deg $daysUntil n wussan"
                    AppLanguage.FRENCH -> "Début du congé dans $daysUntil jour(s)"
                    else -> "Leave starts in $daysUntil day(s)"
                }
            }
            LeaveStatus.ON_DUTY -> when (lang) {
                AppLanguage.TAMAZIGHT -> "Yella deg umahil ($daysSinceDutyReturn n wussan)"
                AppLanguage.FRENCH -> "Présent au service ($daysSinceDutyReturn j écoulés)"
                else -> "On active duty ($daysSinceDutyReturn days elapsed)"
            }
        }
    }

    val formattedStartDate: String
        get() {
            if (startDateMillis <= 0L) return "—"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(startDateMillis))}\u200E"
        }

    val formattedReturnDate: String
        get() {
            if (returnDateMillis <= 0L) return "—"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(returnDateMillis))}\u200E"
        }

    // نسبة تقدم الإجازة من 0.0 إلى 1.0
    val leaveProgressFraction: Float
        get() {
            if (startDateMillis <= 0L || returnDateMillis <= 0L) return 0f
            val today = toStartOfDay(System.currentTimeMillis())
            val start = toStartOfDay(startDateMillis)
            val end = toStartOfDay(returnDateMillis)
            if (today <= start) return 0f
            if (today >= end) return 1f
            val total = (end - start).toFloat()
            if (total <= 0f) return 1f
            val elapsed = (today - start).toFloat()
            return (elapsed / total).coerceIn(0f, 1f)
        }
}
