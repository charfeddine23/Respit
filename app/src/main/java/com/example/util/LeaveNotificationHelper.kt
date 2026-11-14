package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
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
    const val CHANNEL_URGENT_ID = "channel_leave_urgent_v2"
    private const val GROUP_KEY_LEAVES = "com.example.RESPITE_PULSE_LEAVES"
    private const val NOTIFY_ID_SUMMARY = 90001

    private const val COLOR_CRIMSON = 0xFFEF4444.toInt()
    private const val COLOR_CRIMSON_DARK = 0xFFDC2626.toInt()
    private const val COLOR_CYAN = 0xFF06B6D4.toInt()
    private const val COLOR_BLUE = 0xFF2563EB.toInt()
    private const val COLOR_AMBER = 0xFFF59E0B.toInt()
    private const val COLOR_ORANGE = 0xFFEA580C.toInt()
    private const val COLOR_EMERALD = 0xFF10B981.toInt()
    private const val COLOR_TEAL = 0xFF059669.toInt()

    fun createNotificationChannel(context: Context, langCode: String = "ar") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val (urgentName, urgentDesc) = when (langCode) {
                "fr" -> "Alertes Urgentes (Reprises & Retards)" to "Notifications prioritaires pour les retards et reprises du jour"
                "en" -> "Urgent Alerts (Returns & Overdue)" to "High-priority alerts for overdue returns and due today"
                else -> "التنبيهات العاجلة (الدخول والتأخر)" to "تنبيهات عالية الأولوية لحالات التأخر ومواعيد الدخول اليوم"
            }
            val urgentChannel = NotificationChannel(
                CHANNEL_URGENT_ID,
                urgentName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = urgentDesc
                enableVibration(true)
                vibrationPattern = longArrayOf(0L, 200L, 120L, 220L)
                enableLights(true)
                lightColor = COLOR_CYAN
            }

            val (channelName, channelDesc) = when (langCode) {
                "fr" -> "Suivi des Congés & Approbations" to "Rappels des départs en congé et reprises de service"
                "en" -> "Leave Reminders & Approvals" to "Reminders for leave approvals, departures, and upcoming returns"
                else -> "متابعة الإجازات والموافقات" to "تذكيرات بمواعيد الإجازات القادمة والموافقات المسجلة"
            }
            val standardChannel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDesc
                enableVibration(true)
                enableLights(true)
                lightColor = COLOR_EMERALD
            }

            notificationManager.createNotificationChannel(urgentChannel)
            notificationManager.createNotificationChannel(standardChannel)
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

    private fun buildContentPendingIntent(
        context: Context,
        requestCode: Int,
        personnelId: Long
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_notifications", true)
            putExtra("personnel_id", personnelId)
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Generates a high-resolution rounded gradient status badge bitmap for the system notification shade.
     */
    private fun createStatusBadgeBitmap(
        startColor: Int,
        endColor: Int,
        glyphText: String
    ): Bitmap {
        val sizePx = 128
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, sizePx.toFloat(), sizePx.toFloat(),
                startColor, endColor, Shader.TileMode.CLAMP
            )
        }
        val rect = RectF(4f, 4f, sizePx - 4f, sizePx - 4f)
        canvas.drawRoundRect(rect, 34f, 34f, bgPaint)

        // Subtle inner highlight border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = 0x55FFFFFF
        }
        canvas.drawRoundRect(RectF(6f, 6f, sizePx - 6f, sizePx - 6f), 32f, 32f, borderPaint)

        // Centered bold badge glyph or short counter
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = if (glyphText.length <= 2) 48f else 36f
        }
        val fontMetrics = textPaint.fontMetrics
        val baselineY = (sizePx / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f)
        canvas.drawText(glyphText, sizePx / 2f, baselineY, textPaint)

        return bitmap
    }

    private fun extractInitials(fullName: String): String {
        val parts = fullName.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (parts.isEmpty()) return "RP"
        val first = parts.first().firstOrNull()?.toString() ?: ""
        return first.ifBlank { "RP" }
    }

    /**
     * Send notification when an employee's leave has been approved or updated.
     */
    fun notifyLeaveApproved(context: Context, personnel: Personnel, lang: String = "ar") {
        if (!hasNotificationPermission(context)) return
        createNotificationChannel(context, lang)
        val appLang = AppLanguage.fromCode(lang)
        val rankStr = formatRank(personnel.rank, appLang)
        val leaveTypeStr = formatLeaveType(personnel.leaveType, appLang)
        val wilayaStr = formatWilaya(personnel.wilaya, appLang, includeCode = false)

        val pendingIntent = buildContentPendingIntent(context, personnel.id.toInt() + 1000, personnel.id)
        val openActionLabel = AppStrings.get("notif_action_open", appLang)

        val subText = when (appLang) {
            AppLanguage.FRENCH -> "Congé validé • ${personnel.totalLeaveDays} j"
            AppLanguage.ENGLISH -> "Leave Approved • ${personnel.totalLeaveDays} d"
            AppLanguage.ARABIC -> "موافقة إجازة • ${personnel.totalLeaveDays} يوم"
        }

        val title = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ENGLISH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ARABIC -> "$rankStr / ${personnel.fullName}"
        }

        val summaryLine = when (appLang) {
            AppLanguage.FRENCH -> "$leaveTypeStr (${personnel.totalLeaveDays} j) — Départ le ${personnel.formattedStartDate}"
            AppLanguage.ENGLISH -> "$leaveTypeStr (${personnel.totalLeaveDays} d) — Starts ${personnel.formattedStartDate}"
            AppLanguage.ARABIC -> "اعتماد $leaveTypeStr (${personnel.totalLeaveDays} يوم) ابتداءً من ${personnel.formattedStartDate}"
        }

        val expandedBody = when (appLang) {
            AppLanguage.FRENCH -> "$leaveTypeStr • Durée : ${personnel.totalLeaveDays} jours\nDu ${personnel.formattedStartDate} au ${personnel.formattedReturnDate}\nDestination : $wilayaStr"
            AppLanguage.ENGLISH -> "$leaveTypeStr • Duration: ${personnel.totalLeaveDays} days\nFrom ${personnel.formattedStartDate} to ${personnel.formattedReturnDate}\nDestination: $wilayaStr"
            AppLanguage.ARABIC -> "• النوع: $leaveTypeStr (${personnel.totalLeaveDays} يوم)\n• الفترة: ${personnel.formattedStartDate} ← ${personnel.formattedReturnDate}\n• الولاية: $wilayaStr"
        }

        val badgeBitmap = createStatusBadgeBitmap(COLOR_EMERALD, COLOR_TEAL, "✓")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif_pulse)
            .setLargeIcon(badgeBitmap)
            .setColor(COLOR_EMERALD)
            .setSubText(subText)
            .setContentTitle(title)
            .setContentText(summaryLine)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(expandedBody)
                    .setSummaryText(AppStrings.get("app_title", appLang))
            )
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(GROUP_KEY_LEAVES)
            .setContentIntent(pendingIntent)
            .addAction(0, openActionLabel, pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 5000, notification)
        } catch (_: SecurityException) {}
    }

    /**
     * Check personnel list for overdue, returning today, approaching returns, and upcoming leaves.
     * Dispatches rich visual status-bar notifications + bundled InboxStyle summary when 2+ alerts fire.
     */
    fun checkApproachingLeaves(context: Context, list: List<Personnel>, lang: String = "ar"): Int {
        if (!hasNotificationPermission(context)) return 0
        createNotificationChannel(context, lang)

        val appLang = AppLanguage.fromCode(lang)
        val today = Personnel.toStartOfDay(System.currentTimeMillis())
        val summaryLines = mutableListOf<String>()

        list.forEach { p ->
            val startDay = Personnel.toStartOfDay(p.startDateMillis)
            val returnDay = Personnel.toStartOfDay(p.returnDateMillis)
            val daysUntilStart = TimeUnit.MILLISECONDS.toDays(startDay - today)
            val daysUntilReturn = TimeUnit.MILLISECONDS.toDays(returnDay - today)

            when {
                !p.isReturnedToDuty && p.status == LeaveStatus.OVERDUE -> {
                    sendOverdueNotification(context, p, appLang)
                    summaryLines.add("${formatRank(p.rank, appLang)} ${p.fullName} — +${p.daysOverdue}d")
                }
                !p.isReturnedToDuty && p.status == LeaveStatus.RETURNING_TODAY -> {
                    sendReturningTodayNotification(context, p, appLang)
                    summaryLines.add("${formatRank(p.rank, appLang)} ${p.fullName} — ${p.formattedReturnDate}")
                }
                !p.isReturnedToDuty && daysUntilStart in 0..2 && today <= startDay -> {
                    sendApproachingStartNotification(context, p, daysUntilStart, appLang)
                    summaryLines.add("${formatRank(p.rank, appLang)} ${p.fullName} — ${p.formattedStartDate}")
                }
                !p.isReturnedToDuty && daysUntilReturn in 1..2 -> {
                    sendApproachingReturnNotification(context, p, daysUntilReturn, appLang)
                    summaryLines.add("${formatRank(p.rank, appLang)} ${p.fullName} — ${p.formattedReturnDate}")
                }
            }
        }

        if (summaryLines.size >= 2) {
            postSummaryNotification(context, summaryLines, appLang)
        }

        return summaryLines.size
    }

    private fun postSummaryNotification(
        context: Context,
        lines: List<String>,
        appLang: AppLanguage
    ) {
        val pendingIntent = buildContentPendingIntent(context, NOTIFY_ID_SUMMARY, -1L)
        val inboxStyle = NotificationCompat.InboxStyle()
        lines.take(5).forEach { inboxStyle.addLine(it) }

        val title = when (appLang) {
            AppLanguage.FRENCH -> "${lines.size} alertes de congés actives"
            AppLanguage.ENGLISH -> "${lines.size} active leave alerts"
            AppLanguage.ARABIC -> "${lines.size} تنبيهات إجازات نشطة"
        }
        inboxStyle.setBigContentTitle(title)
        inboxStyle.setSummaryText(AppStrings.get("app_title", appLang))

        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_URGENT_ID)
            .setSmallIcon(R.drawable.ic_notif_pulse)
            .setColor(COLOR_CYAN)
            .setContentTitle(title)
            .setContentText(lines.firstOrNull().orEmpty())
            .setStyle(inboxStyle)
            .setGroup(GROUP_KEY_LEAVES)
            .setGroupSummary(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFY_ID_SUMMARY, summaryNotification)
        } catch (_: SecurityException) {}
    }

    private fun sendOverdueNotification(
        context: Context,
        personnel: Personnel,
        appLang: AppLanguage
    ) {
        val rankStr = formatRank(personnel.rank, appLang)
        val wilayaStr = formatWilaya(personnel.wilaya, appLang, includeCode = false)
        val pendingIntent = buildContentPendingIntent(context, personnel.id.toInt() + 6000, personnel.id)
        val openActionLabel = AppStrings.get("notif_action_open", appLang)

        val subText = when (appLang) {
            AppLanguage.FRENCH -> "Retard critique • +${personnel.daysOverdue}j"
            AppLanguage.ENGLISH -> "Overdue Return • +${personnel.daysOverdue}d"
            AppLanguage.ARABIC -> "تأخر عن الدخول • +${personnel.daysOverdue} يوم"
        }

        val title = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ENGLISH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ARABIC -> "$rankStr / ${personnel.fullName}"
        }

        val summary = when (appLang) {
            AppLanguage.FRENCH -> "Retard de ${personnel.daysOverdue} jour(s) • Reprise prévue : ${personnel.formattedReturnDate}"
            AppLanguage.ENGLISH -> "Overdue by ${personnel.daysOverdue} day(s) • Scheduled: ${personnel.formattedReturnDate}"
            AppLanguage.ARABIC -> "متأخر بـ ${personnel.daysOverdue} يوم عن موعد الدخول المقرر (${personnel.formattedReturnDate})"
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "Retard de reprise : +${personnel.daysOverdue} jour(s)\nDate prévue : ${personnel.formattedReturnDate}\nWilaya : $wilayaStr"
            AppLanguage.ENGLISH -> "Overdue by +${personnel.daysOverdue} day(s)\nScheduled return: ${personnel.formattedReturnDate}\nProvince: $wilayaStr"
            AppLanguage.ARABIC -> "• الوضعية: متأخر عن استئناف العمل (+${personnel.daysOverdue} يوم)\n• الموعد المقرر: ${personnel.formattedReturnDate}\n• الولاية: $wilayaStr"
        }

        val badgeBitmap = createStatusBadgeBitmap(
            COLOR_CRIMSON,
            COLOR_CRIMSON_DARK,
            "!${personnel.daysOverdue}"
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_URGENT_ID)
            .setSmallIcon(R.drawable.ic_notif_pulse)
            .setLargeIcon(badgeBitmap)
            .setColor(COLOR_CRIMSON)
            .setSubText(subText)
            .setContentTitle(title)
            .setContentText(summary)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body)
                    .setSummaryText(AppStrings.get("app_title", appLang))
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(GROUP_KEY_LEAVES)
            .setContentIntent(pendingIntent)
            .addAction(0, openActionLabel, pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 40000, notification)
        } catch (_: SecurityException) {}
    }

    private fun sendApproachingStartNotification(
        context: Context,
        personnel: Personnel,
        daysUntilStart: Long,
        appLang: AppLanguage
    ) {
        val rankStr = formatRank(personnel.rank, appLang)
        val leaveTypeStr = formatLeaveType(personnel.leaveType, appLang)
        val wilayaStr = formatWilaya(personnel.wilaya, appLang, includeCode = false)
        val pendingIntent = buildContentPendingIntent(context, personnel.id.toInt() + 2000, personnel.id)
        val openActionLabel = AppStrings.get("notif_action_open", appLang)

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

        val subText = when (appLang) {
            AppLanguage.FRENCH -> "Départ • $timeText"
            AppLanguage.ENGLISH -> "Departure • $timeText"
            AppLanguage.ARABIC -> "انطلاق إجازة • $timeText"
        }

        val title = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ENGLISH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ARABIC -> "$rankStr / ${personnel.fullName}"
        }

        val summary = when (appLang) {
            AppLanguage.FRENCH -> "Départ en $leaveTypeStr ($timeText - ${personnel.formattedStartDate})"
            AppLanguage.ENGLISH -> "Starts $leaveTypeStr ($timeText - ${personnel.formattedStartDate})"
            AppLanguage.ARABIC -> "يبدأ $leaveTypeStr ($timeText - ${personnel.formattedStartDate})"
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "Type : $leaveTypeStr ($timeText)\nDu ${personnel.formattedStartDate} au ${personnel.formattedReturnDate}\nWilaya : $wilayaStr"
            AppLanguage.ENGLISH -> "Type: $leaveTypeStr ($timeText)\nFrom ${personnel.formattedStartDate} to ${personnel.formattedReturnDate}\nProvince: $wilayaStr"
            AppLanguage.ARABIC -> "• النوع: $leaveTypeStr ($timeText)\n• الفترة: ${personnel.formattedStartDate} ← ${personnel.formattedReturnDate}\n• الولاية: $wilayaStr"
        }

        val badgeBitmap = createStatusBadgeBitmap(COLOR_EMERALD, COLOR_CYAN, extractInitials(personnel.fullName))

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif_pulse)
            .setLargeIcon(badgeBitmap)
            .setColor(COLOR_EMERALD)
            .setSubText(subText)
            .setContentTitle(title)
            .setContentText(summary)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body)
                    .setSummaryText(AppStrings.get("app_title", appLang))
            )
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(GROUP_KEY_LEAVES)
            .setContentIntent(pendingIntent)
            .addAction(0, openActionLabel, pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 10000, notification)
        } catch (_: SecurityException) {}
    }

    private fun sendReturningTodayNotification(
        context: Context,
        personnel: Personnel,
        appLang: AppLanguage
    ) {
        val rankStr = formatRank(personnel.rank, appLang)
        val wilayaStr = formatWilaya(personnel.wilaya, appLang, includeCode = false)
        val pendingIntent = buildContentPendingIntent(context, personnel.id.toInt() + 3000, personnel.id)
        val openActionLabel = AppStrings.get("notif_action_open", appLang)

        val subText = when (appLang) {
            AppLanguage.FRENCH -> "Reprise aujourd'hui"
            AppLanguage.ENGLISH -> "Return Today"
            AppLanguage.ARABIC -> "الدخول اليوم"
        }

        val title = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ENGLISH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ARABIC -> "$rankStr / ${personnel.fullName}"
        }

        val summary = when (appLang) {
            AppLanguage.FRENCH -> "Reprise du service prévue aujourd'hui ($wilayaStr)"
            AppLanguage.ENGLISH -> "Scheduled to resume duty today ($wilayaStr)"
            AppLanguage.ARABIC -> "موعد الدخول واستئناف العمل اليوم ($wilayaStr)"
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "Reprise prévue aujourd'hui (${personnel.formattedReturnDate})\nDurée effectuée : ${personnel.totalLeaveDays} j\nWilaya : $wilayaStr"
            AppLanguage.ENGLISH -> "Scheduled return today (${personnel.formattedReturnDate})\nLeave duration: ${personnel.totalLeaveDays} d\nProvince: $wilayaStr"
            AppLanguage.ARABIC -> "• التنبيه: موعد الدخول واستئناف العمل اليوم (${personnel.formattedReturnDate})\n• مدة الإجازة: ${personnel.totalLeaveDays} يوم\n• الولاية: $wilayaStr"
        }

        val badgeBitmap = createStatusBadgeBitmap(COLOR_CYAN, COLOR_BLUE, "0")

        val notification = NotificationCompat.Builder(context, CHANNEL_URGENT_ID)
            .setSmallIcon(R.drawable.ic_notif_pulse)
            .setLargeIcon(badgeBitmap)
            .setColor(COLOR_CYAN)
            .setSubText(subText)
            .setContentTitle(title)
            .setContentText(summary)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body)
                    .setSummaryText(AppStrings.get("app_title", appLang))
            )
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(GROUP_KEY_LEAVES)
            .setContentIntent(pendingIntent)
            .addAction(0, openActionLabel, pendingIntent)
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
        appLang: AppLanguage
    ) {
        val rankStr = formatRank(personnel.rank, appLang)
        val wilayaStr = formatWilaya(personnel.wilaya, appLang, includeCode = false)
        val pendingIntent = buildContentPendingIntent(context, personnel.id.toInt() + 4000, personnel.id)
        val openActionLabel = AppStrings.get("notif_action_open", appLang)

        val subText = when (appLang) {
            AppLanguage.FRENCH -> "Reprise dans ${daysUntilReturn}j"
            AppLanguage.ENGLISH -> "Returns in ${daysUntilReturn}d"
            AppLanguage.ARABIC -> if (daysUntilReturn == 1L) "الدخول غداً" else "الدخول خلال يومين"
        }

        val title = when (appLang) {
            AppLanguage.FRENCH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ENGLISH -> "$rankStr • ${personnel.fullName}"
            AppLanguage.ARABIC -> "$rankStr / ${personnel.fullName}"
        }

        val summary = when (appLang) {
            AppLanguage.FRENCH -> "Reprise dans $daysUntilReturn jour(s) • ${personnel.formattedReturnDate}"
            AppLanguage.ENGLISH -> "Returns in $daysUntilReturn day(s) • ${personnel.formattedReturnDate}"
            AppLanguage.ARABIC -> "يتبقى $daysUntilReturn يوم على استئناف العمل (${personnel.formattedReturnDate})"
        }

        val body = when (appLang) {
            AppLanguage.FRENCH -> "Fin de congé imminente : dans $daysUntilReturn jour(s)\nDate de reprise : ${personnel.formattedReturnDate}\nWilaya : $wilayaStr"
            AppLanguage.ENGLISH -> "Return approaching: in $daysUntilReturn day(s)\nReturn date: ${personnel.formattedReturnDate}\nProvince: $wilayaStr"
            AppLanguage.ARABIC -> "• المتبقي: $daysUntilReturn يوم على موعد الدخول\n• تاريخ الدخول: ${personnel.formattedReturnDate}\n• الولاية: $wilayaStr"
        }

        val badgeBitmap = createStatusBadgeBitmap(COLOR_AMBER, COLOR_ORANGE, "${daysUntilReturn}d")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif_pulse)
            .setLargeIcon(badgeBitmap)
            .setColor(COLOR_AMBER)
            .setSubText(subText)
            .setContentTitle(title)
            .setContentText(summary)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body)
                    .setSummaryText(AppStrings.get("app_title", appLang))
            )
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(GROUP_KEY_LEAVES)
            .setContentIntent(pendingIntent)
            .addAction(0, openActionLabel, pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(personnel.id.toInt() + 30000, notification)
        } catch (_: SecurityException) {}
    }
}
