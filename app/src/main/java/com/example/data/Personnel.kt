package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    ON_DUTY("حاضر بالخدمة 🎖️", 0xFF059669, 0xFFD1FAE5)
}

@Entity(tableName = "personnel")
data class Personnel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val rank: String,
    val registrationNumber: String,
    val startDateMillis: Long,     // تاريخ الإستفادة (بداية الإجازة)
    val returnDateMillis: Long,    // تاريخ الدخول (تاريخ العودة للعمل)
    val isReturnedToDuty: Boolean = false, // هل التحق بالخدمة ومستأنف للعمل
    val leaveType: String = "عطلة سنوية",
    val wilaya: String = "16 - الجزائر",
    val notes: String = ""
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
    }

    // إجمالي مدة الإجازة بالأيام
    val totalLeaveDays: Long
        get() {
            val start = toStartOfDay(startDateMillis)
            val end = toStartOfDay(returnDateMillis)
            val days = TimeUnit.MILLISECONDS.toDays(end - start)
            return if (days <= 0) 1 else days
        }

    // الأيام المتبقية على الدخول (شحال بقالو فدار ويدخل)
    val daysRemainingToReturn: Long
        get() {
            val today = toStartOfDay(System.currentTimeMillis())
            val returnDay = toStartOfDay(returnDateMillis)
            return TimeUnit.MILLISECONDS.toDays(returnDay - today)
        }

    // الأيام المنقضية من الإجازة
    val daysElapsedInLeave: Long
        get() {
            val today = toStartOfDay(System.currentTimeMillis())
            val start = toStartOfDay(startDateMillis)
            val diff = today - start
            return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
        }

    // عدد أيام التأخر عن الدخول (إن وجد)
    val daysOverdue: Long
        get() {
            val today = toStartOfDay(System.currentTimeMillis())
            val returnDay = toStartOfDay(returnDateMillis)
            val diff = today - returnDay
            return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
        }

    // الأيام المنقضية في الخدمة منذ آخر عودة
    val daysSinceDutyReturn: Long
        get() {
            val today = toStartOfDay(System.currentTimeMillis())
            val returnDay = toStartOfDay(returnDateMillis)
            val diff = today - returnDay
            return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
        }

    // هل الفرد مستحق للإجازة (تجاوز عتبة الخدمة المحددة)
    fun isEligibleForLeave(thresholdDays: Int): Boolean {
        return isReturnedToDuty && daysSinceDutyReturn >= thresholdDays
    }

    // هل الفرد حالياً في إجازة بالبيت
    val isCurrentlyAtHome: Boolean
        get() {
            if (isReturnedToDuty) return false
            val today = toStartOfDay(System.currentTimeMillis())
            val start = toStartOfDay(startDateMillis)
            val returnDay = toStartOfDay(returnDateMillis)
            return today in start until returnDay
        }

    // الوضعية الحالية
    val status: LeaveStatus
        get() {
            if (isReturnedToDuty) return LeaveStatus.ON_DUTY

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

    val formattedStartDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(startDateMillis))}\u200E"
        }

    val formattedReturnDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(returnDateMillis))}\u200E"
        }

    // نسبة تقدم الإجازة من 0.0 إلى 1.0
    val leaveProgressFraction: Float
        get() {
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
