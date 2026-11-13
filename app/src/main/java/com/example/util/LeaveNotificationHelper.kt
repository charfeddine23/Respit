package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import java.util.concurrent.TimeUnit

object LeaveNotificationHelper {
    const val CHANNEL_ID = "channel_leave_notifications_v1"

    fun createNotificationChannel(context: Context, langCode: String = "ar") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val (channelName, channelDesc) = when (langCode) {
                "fr" -> "Notifications de Congés" to "Rappels des départs en congé et reprises de service"
                "en" -> "Leave Notifications" to "Reminders for leave approvals, departures, and duty returns"
                else -> "إشعارات الإجازات والعطل" to "تذكيرات باقتراب مواعيد الإجازات والموافقة عليها وتواريخ الدخول"
            }
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDesc
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Send notification when an employee's requested leave has been approved or granted.
     */
    fun notifyLeaveApproved(context: Context, personnel: Personnel, lang: String = "ar") {
        if (!hasNotificationPermission(context)) return
        createNotificationChannel(context, lang)
        val appLang = AppLanguage.fromCode(lang)
        val rankStr = formatRank(personnel.rank, appLang)
        val leaveTypeStr = formatLeaveType(personnel.leaveType, appLang)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (appLang) {
            AppLanguage.FRENCH -> "Demande de congé approuvée"
            AppLanguage.ENGLISH -> "Leave Request Approved"
            AppLanguage.ARABIC -> "تمت الموافقة على الإجازة بنجاح"
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr ${personnel.fullName} : $leaveTypeStr (${personnel.totalLeaveDays} j) - Départ le ${personnel.formattedStartDate}"
            AppLanguage.ENGLISH -> "$rankStr ${personnel.fullName}: $leaveTypeStr (${personnel.totalLeaveDays} d) - Starting ${personnel.formattedStartDate}"
            AppLanguage.ARABIC -> "$rankStr / ${personnel.fullName}: الاستفادة من $leaveTypeStr (${personnel.totalLeaveDays} يوم) ابتداءً من ${personnel.formattedStartDate}"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 5000, notification)
        } catch (_: SecurityException) {}
    }

    /**
     * Check personnel list for approaching leaves (starts within 48h) or approaching return dates (today or tomorrow).
     */
    fun checkApproachingLeaves(context: Context, list: List<Personnel>, lang: String = "ar"): Int {
        if (!hasNotificationPermission(context)) return 0
        createNotificationChannel(context, lang)

        val today = Personnel.toStartOfDay(System.currentTimeMillis())
        var notificationsSent = 0

        list.forEach { p ->
            val startDay = Personnel.toStartOfDay(p.startDateMillis)
            val returnDay = Personnel.toStartOfDay(p.returnDateMillis)

            val daysUntilStart = TimeUnit.MILLISECONDS.toDays(startDay - today)
            val daysUntilReturn = TimeUnit.MILLISECONDS.toDays(returnDay - today)

            if (!p.isReturnedToDuty && daysUntilStart in 0..2 && today <= startDay) {
                sendApproachingStartNotification(context, p, daysUntilStart, lang)
                notificationsSent++
            } else if (!p.isReturnedToDuty && p.status == LeaveStatus.RETURNING_TODAY) {
                sendReturningTodayNotification(context, p, lang)
                notificationsSent++
            } else if (!p.isReturnedToDuty && daysUntilReturn in 1..2) {
                sendApproachingReturnNotification(context, p, daysUntilReturn, lang)
                notificationsSent++
            }
        }
        return notificationsSent
    }

    private fun sendApproachingStartNotification(
        context: Context,
        personnel: Personnel,
        daysUntilStart: Long,
        lang: String
    ) {
        val appLang = AppLanguage.fromCode(lang)
        val rankStr = formatRank(personnel.rank, appLang)
        val leaveTypeStr = formatLeaveType(personnel.leaveType, appLang)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 2000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (appLang) {
            AppLanguage.FRENCH -> "Congé approchant"
            AppLanguage.ENGLISH -> "Upcoming Leave Reminder"
            AppLanguage.ARABIC -> "تنبيه: اقتراب موعد إجازة"
        }

        val timeText = when (daysUntilStart) {
            0L -> when (appLang) {
                AppLanguage.FRENCH -> "Aujourd'hui"
                AppLanguage.ENGLISH -> "Today"
                AppLanguage.ARABIC -> "اليوم"
            }
            1L -> when (appLang) {
                AppLanguage.FRENCH -> "Demain"
                AppLanguage.ENGLISH -> "Tomorrow"
                AppLanguage.ARABIC -> "غداً"
            }
            else -> when (appLang) {
                AppLanguage.FRENCH -> "Dans $daysUntilStart jours"
                AppLanguage.ENGLISH -> "In $daysUntilStart days"
                AppLanguage.ARABIC -> if (daysUntilStart == 2L) "خلال يومين" else "خلال $daysUntilStart أيام"
            }
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr ${personnel.fullName} part en $leaveTypeStr ($timeText - ${personnel.formattedStartDate})"
            AppLanguage.ENGLISH -> "$rankStr ${personnel.fullName} starts $leaveTypeStr ($timeText - ${personnel.formattedStartDate})"
            AppLanguage.ARABIC -> "يبدأ $rankStr / ${personnel.fullName} إجازته - $leaveTypeStr ($timeText - ${personnel.formattedStartDate})"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 10000, notification)
        } catch (_: SecurityException) {}
    }

    private fun sendReturningTodayNotification(context: Context, personnel: Personnel, lang: String) {
        val appLang = AppLanguage.fromCode(lang)
        val rankStr = formatRank(personnel.rank, appLang)
        val wilayaStr = formatWilaya(personnel.wilaya, appLang, includeCode = false)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 3000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (appLang) {
            AppLanguage.FRENCH -> "Date de reprise aujourd'hui"
            AppLanguage.ENGLISH -> "Duty Return Today"
            AppLanguage.ARABIC -> "تنبيه: تاريخ الدخول اليوم!"
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "Reprise prévue aujourd'hui pour $rankStr ${personnel.fullName} ($wilayaStr)"
            AppLanguage.ENGLISH -> "Expected return to duty today for $rankStr ${personnel.fullName} ($wilayaStr)"
            AppLanguage.ARABIC -> "موعد الدخول واستئناف العمل اليوم لـ $rankStr / ${personnel.fullName} ($wilayaStr)"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 20000, notification)
        } catch (_: SecurityException) {}
    }

    private fun sendApproachingReturnNotification(
        context: Context,
        personnel: Personnel,
        daysUntilReturn: Long,
        lang: String
    ) {
        val appLang = AppLanguage.fromCode(lang)
        val rankStr = formatRank(personnel.rank, appLang)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 4000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (appLang) {
            AppLanguage.FRENCH -> "Rappel : Fin de congé imminente"
            AppLanguage.ENGLISH -> "Reminder: Approaching Return Date"
            AppLanguage.ARABIC -> "تذكير: اقتراب موعد استئناف العمل"
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr ${personnel.fullName} reprend dans $daysUntilReturn jour(s) (${personnel.formattedReturnDate})"
            AppLanguage.ENGLISH -> "$rankStr ${personnel.fullName} returns in $daysUntilReturn day(s) (${personnel.formattedReturnDate})"
            AppLanguage.ARABIC -> "يتبقى $daysUntilReturn يوم على دخول $rankStr / ${personnel.fullName} (${personnel.formattedReturnDate})"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 30000, notification)
        } catch (_: SecurityException) {}
    }
}
