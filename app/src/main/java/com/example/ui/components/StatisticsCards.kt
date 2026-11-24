package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardStats
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlin.math.roundToInt

@Composable
fun StatisticsSection(
    stats: DashboardStats,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    selectedStatus: String = "ALL",
    onSelectStatus: (String) -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    isDarkMode: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

    val total = if (stats.total > 0) stats.total.toFloat() else 1f
    val onDutyFraction = (stats.onDuty / total).coerceIn(0f, 1f)
    val atHomeFraction = (stats.atHome / total).coerceIn(0f, 1f)
    val overdueFraction = ((stats.overdue + stats.returningToday) / total).coerceIn(0f, 1f)

    val animatedOnDuty = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = onDutyFraction,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            label = "onDutyAnim"
        ).value
    } else onDutyFraction

    val animatedAtHome = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = atHomeFraction,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            label = "atHomeAnim"
        ).value
    } else atHomeFraction

    val animatedOverdue = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = overdueFraction,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            label = "overdueAnim"
        ).value
    } else overdueFraction

    val cardSurfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF151F31) else Color(0xFFFFFFFF)
    val cardBorderColor = preset?.borderAccent?.copy(alpha = 0.28f)
        ?: if (effectiveDark) Color(0xFF283548) else Color(0xFFE2E8F0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("statistics_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = cardSurfaceColor,
            shadowElevation = if (effectiveDark) 2.dp else 3.dp,
            border = BorderStroke(1.dp, cardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                val isWide = maxWidth >= 500.dp
                val isCompact = maxWidth < 340.dp
                val ringSize = if (isWide) 120.dp else if (isCompact) 90.dp else 102.dp
                val canvasSize = if (isWide) 110.dp else if (isCompact) 82.dp else 94.dp
                val strokeWidthDp = if (isWide) 10.dp else if (isCompact) 7.5.dp else 8.5.dp
                val centerTotalSp = if (isWide) 22.sp else if (isCompact) 18.sp else 20.sp
                val centerLabelSp = if (isWide) 10.sp else if (isCompact) 8.sp else 8.5.sp

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Clean Header Row + Analytics Trigger Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(primaryAccent.copy(alpha = if (effectiveDark) 0.16f else 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = primaryAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = AppStrings.get("dashboard_title", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            onClick = onOpenAnalytics,
                            shape = CircleShape,
                            color = primaryAccent.copy(alpha = if (effectiveDark) 0.14f else 0.08f),
                            border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.28f)),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("dashboard_analytics_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = AppStrings.get("dashboard_analytics_label", currentLang),
                                    tint = primaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = AppStrings.get("dashboard_analytics_label", currentLang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = primaryAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    // Clean Ring Chart + Calm Interactive Status Rows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(ringSize)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(canvasSize)) {
                                if (size.minDimension <= 0f) return@Canvas
                                val strokeWidth = strokeWidthDp.toPx()
                                val trackColor = if (effectiveDark) Color(0xFF233044) else Color(0xFFE2E8F0)

                                drawCircle(
                                    color = trackColor,
                                    style = Stroke(width = strokeWidth)
                                )

                                val dutySweep = animatedOnDuty * 360f
                                val homeSweep = animatedAtHome * 360f
                                val overdueSweep = animatedOverdue * 360f

                                var startAngle = -90f

                                if (dutySweep > 0f) {
                                    drawArc(
                                        color = EmeraldSuccess,
                                        startAngle = startAngle,
                                        sweepAngle = dutySweep.coerceAtLeast(4f),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    startAngle += dutySweep
                                }

                                if (homeSweep > 0f) {
                                    drawArc(
                                        color = primaryAccent,
                                        startAngle = startAngle,
                                        sweepAngle = homeSweep.coerceAtLeast(4f),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    startAngle += homeSweep
                                }

                                if (overdueSweep > 0f) {
                                    drawArc(
                                        color = if (stats.overdue > 0) RedDanger else AmberWarning,
                                        startAngle = startAngle,
                                        sweepAngle = overdueSweep.coerceAtLeast(4f),
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stats.total.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = textPrimary,
                                    fontSize = centerTotalSp
                                )
                                Text(
                                    text = AppStrings.get("total_personnel", currentLang),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = centerLabelSp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(if (isWide) 18.dp else 12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(if (isWide) 8.dp else 7.dp)
                        ) {
                            DashboardMetricRow(
                                icon = Icons.Default.Shield,
                                label = AppStrings.get("on_duty_present", currentLang),
                                count = stats.onDuty.toString(),
                                percent = (onDutyFraction * 100f).roundToInt(),
                                accentColor = EmeraldSuccess,
                                isSelected = selectedStatus == "ON_DUTY",
                                isDarkMode = effectiveDark,
                                isWide = isWide,
                                onClick = { onSelectStatus(if (selectedStatus == "ON_DUTY") "ALL" else "ON_DUTY") }
                            )

                            DashboardMetricRow(
                                icon = Icons.Default.Home,
                                label = AppStrings.get("on_leave_home", currentLang),
                                count = stats.atHome.toString(),
                                percent = (atHomeFraction * 100f).roundToInt(),
                                accentColor = primaryAccent,
                                isSelected = selectedStatus == "AT_HOME",
                                isDarkMode = effectiveDark,
                                isWide = isWide,
                                onClick = { onSelectStatus(if (selectedStatus == "AT_HOME") "ALL" else "AT_HOME") }
                            )

                            val urgentCount = stats.returningToday + stats.overdue
                            DashboardMetricRow(
                                icon = if (stats.overdue > 0) Icons.Default.Warning else Icons.Default.NotificationsActive,
                                label = AppStrings.get("delayed_overdue", currentLang),
                                count = urgentCount.toString(),
                                percent = (overdueFraction * 100f).roundToInt(),
                                accentColor = if (stats.overdue > 0) RedDanger else AmberWarning,
                                isSelected = selectedStatus == "OVERDUE" || selectedStatus == "RETURNING_TODAY",
                                isDarkMode = effectiveDark,
                                isWide = isWide,
                                onClick = {
                                    onSelectStatus(
                                        if (selectedStatus == "OVERDUE") "ALL"
                                        else if (stats.overdue > 0) "OVERDUE"
                                        else "RETURNING_TODAY"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricRow(
    icon: ImageVector,
    label: String,
    count: String,
    percent: Int,
    accentColor: Color,
    isSelected: Boolean,
    isDarkMode: Boolean,
    isWide: Boolean = false,
    onClick: () -> Unit
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color(0xFFE2E8F0) else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val rowShape = RoundedCornerShape(13.dp)
    val rowSurface = if (isSelected) {
        accentColor.copy(alpha = if (effectiveDark) 0.16f else 0.08f)
    } else if (effectiveDark) {
        preset?.surfaceSecondary ?: Color(0xFF1A2538)
    } else {
        preset?.surfaceSecondary ?: Color(0xFFF8FAFC)
    }
    val rowBorder = if (isSelected) {
        accentColor.copy(alpha = 0.55f)
    } else if (effectiveDark) {
        Color(0xFF283548)
    } else {
        Color(0xFFE2E8F0)
    }

    Surface(
        onClick = onClick,
        shape = rowShape,
        color = rowSurface,
        border = BorderStroke(1.dp, rowBorder),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isWide) 12.dp else 10.dp, vertical = if (isWide) 8.dp else 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (isWide) 28.dp else 26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = if (effectiveDark) 0.18f else 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(if (isWide) 16.dp else 14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = if (isWide) 12.sp else 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$percent%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary
                )
                Text(
                    text = count,
                    fontSize = if (isWide) 14.sp else 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }
        }
    }
}
