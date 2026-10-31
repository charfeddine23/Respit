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
import java.util.Calendar
import java.util.concurrent.TimeUnit

object LeaveNotificationHelper {
    const val CHANNEL_ID = "channel_leave_notifications_v1"
    private const val CHANNEL_NAME = "إشعارات الإجازات والعطل (Leave Notifications)"
    private const val CHANNEL_DESC = "تذكيرات باقتراب مواعد الإجازات والموافقة عليها وتواريخ الدخول"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
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
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 1000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (lang) {
            "fr" -> "Demande de congé approuvée"
            "en" -> "Leave Request Approved"
            else -> "تمت الموافقة على الإجازة بنجاح"
        }

        val body = when (lang) {
            "fr" -> "${personnel.rank} ${personnel.fullName} : ${personnel.leaveType} (${personnel.totalLeaveDays} j) - Départ le ${personnel.formattedStartDate}"
            "en" -> "${personnel.rank} ${personnel.fullName}: ${personnel.leaveType} (${personnel.totalLeaveDays} d) - Starting ${personnel.formattedStartDate}"
            else -> "${personnel.rank} / ${personnel.fullName}: الاستفادة من ${personnel.leaveType} (${personnel.totalLeaveDays} يوم) ابتداءً من ${personnel.formattedStartDate}"
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
        createNotificationChannel(context)

        val today = Personnel.toStartOfDay(System.currentTimeMillis())
        var notificationsSent = 0

        list.forEach { p ->
            val startDay = Personnel.toStartOfDay(p.startDateMillis)
            val returnDay = Personnel.toStartOfDay(p.returnDateMillis)

            // Leave starting soon (0, 1, or 2 days from today)
            val daysUntilStart = TimeUnit.MILLISECONDS.toDays(startDay - today)
            // Leave return date approaching (0 or 1 days from today)
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
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 2000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (lang) {
            "fr" -> "Congé approchant"
            "en" -> "Upcoming Leave Reminder"
            else -> "تنبيه: اقتراب موعد إجازة"
        }

        val timeText = when (daysUntilStart) {
            0L -> if (lang == "fr") "Aujourd'hui" else if (lang == "en") "Today" else "اليوم"
            1L -> if (lang == "fr") "Demain" else if (lang == "en") "Tomorrow" else "غداً"
            else -> if (lang == "fr") "Dans $daysUntilStart jours" else if (lang == "en") "In $daysUntilStart days" else "خلال $daysUntilStart يومين"
        }

        val body = when (lang) {
            "fr" -> "${personnel.rank} ${personnel.fullName} part en ${personnel.leaveType} ($timeText - ${personnel.formattedStartDate})"
            "en" -> "${personnel.rank} ${personnel.fullName} starts ${personnel.leaveType} ($timeText - ${personnel.formattedStartDate})"
            else -> "يبدأ ${personnel.rank} / ${personnel.fullName} إجازته ($timeText - ${personnel.formattedStartDate})"
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
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 3000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (lang) {
            "fr" -> "Date de reprise aujourd'hui"
            "en" -> "Duty Return Today"
            else -> "تنبيه: تاريخ الدخول اليوم!"
        }

        val body = when (lang) {
            "fr" -> "Reprise prévue aujourd'hui pour ${personnel.rank} ${personnel.fullName} (${personnel.wilaya})"
            "en" -> "Expected return to duty today for ${personnel.rank} ${personnel.fullName} (${personnel.wilaya})"
            else -> "موعد الدخول واستئناف العمل اليوم لـ ${personnel.rank} / ${personnel.fullName} (${personnel.wilaya})"
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
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            personnel.id.toInt() + 4000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (lang) {
            "fr" -> "Rappel: Fin de congé imminente"
            "en" -> "Reminder: Approaching Return Date"
            else -> "تذكير: اقتراب موعد استئناف العمل"
        }

        val body = when (lang) {
            "fr" -> "${personnel.rank} ${personnel.fullName} reprend dans $daysUntilReturn jour(s) (${personnel.formattedReturnDate})"
            "en" -> "${personnel.rank} ${personnel.fullName} returns in $daysUntilReturn day(s) (${personnel.formattedReturnDate})"
            else -> "يتبقى $daysUntilReturn يوم على دخول ${personnel.rank} / ${personnel.fullName} (${personnel.formattedReturnDate})"
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
