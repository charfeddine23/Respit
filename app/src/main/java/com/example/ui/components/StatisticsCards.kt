package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardStats
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun StatisticsSection(
    stats: DashboardStats,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    selectedStatus: String = "ALL",
    onSelectStatus: (String) -> Unit = {},
    isDarkMode: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val total = if (stats.total > 0) stats.total.toFloat() else 1f
    val onDutyFraction = (stats.onDuty / total).coerceIn(0f, 1f)
    val atHomeFraction = (stats.atHome / total).coerceIn(0f, 1f)
    val overdueFraction = ((stats.overdue + stats.returningToday) / total).coerceIn(0f, 1f)

    val animatedOnDuty = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = onDutyFraction,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "onDutyAnim"
        ).value
    } else onDutyFraction

    val animatedAtHome = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = atHomeFraction,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "atHomeAnim"
        ).value
    } else atHomeFraction

    val animatedOverdue = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = overdueFraction,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "overdueAnim"
        ).value
    } else overdueFraction

    val pulseAlpha = if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulseInfinite")
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        ).value
    } else 1f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("statistics_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Main Unified Glassmorphic / Neumorphic Dashboard Card
        androidx.compose.foundation.layout.BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(26.dp),
                    elevation = 6.dp,
                    borderAlpha = 0.9f,
                    isDark = isDarkMode,
                    isNeumorphic = isNeumorphic,
                    cornerRadius = 26.dp
                )
                .padding(16.dp)
        ) {
            val isWide = maxWidth >= 500.dp
            val isCompact = maxWidth < 340.dp
            val ringSize = if (isWide) 140.dp else if (isCompact) 110.dp else 126.dp
            val canvasSize = if (isWide) 128.dp else if (isCompact) 100.dp else 116.dp
            val strokeWidthDp = if (isWide) 12.dp else if (isCompact) 9.dp else 11.dp
            val centerTotalSp = if (isWide) 28.sp else if (isCompact) 22.sp else 26.sp
            val centerLabelSp = if (isWide) 12.sp else if (isCompact) 10.sp else 11.sp

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Top Header Row: Dashboard Title & Pulse Dot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(
                                    (if (isDarkMode) CyanGlow else VibrantCyan).copy(alpha = pulseAlpha)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("dashboard_title", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color.White else SlateTextPrimary
                        )
                    }

                    // Total Count Pill Badge
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                        border = BorderStroke(1.dp, Color(0xFF00BCD4).copy(alpha = 0.6f)),
                        modifier = Modifier.clickable { onSelectStatus("ALL") }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${stats.total} ",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color(0xFF00838F)
                            )
                            Text(
                                text = AppStrings.get("registered_system", currentLang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF006064)
                            )
                        }
                    }
                }

                // Central Visualization Row: Ring Chart + Summary Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Modern Multi-Segment Radial Ring Chart
                    Box(
                        modifier = Modifier
                            .size(ringSize)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(canvasSize)) {
                            val strokeWidth = strokeWidthDp.toPx()
                            val trackColor = if (isDarkMode) Color(0x26334155) else Color(0x33CBD5E1)

                            // Background Track
                            drawCircle(
                                color = trackColor,
                                style = Stroke(width = strokeWidth)
                            )

                            // Calculate Sweep Angles
                            val dutySweep = animatedOnDuty * 360f
                            val homeSweep = animatedAtHome * 360f
                            val overdueSweep = animatedOverdue * 360f

                            var startAngle = -90f

                            // Segment 1: On Duty (Emerald Green)
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

                            // Segment 2: On Leave (Vibrant Cyan)
                            if (homeSweep > 0f) {
                                drawArc(
                                    color = if (isDarkMode) CyanGlow else VibrantCyan,
                                    startAngle = startAngle,
                                    sweepAngle = homeSweep.coerceAtLeast(4f),
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                startAngle += homeSweep
                            }

                            // Segment 3: Overdue / Returning (Amber/Red)
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

                        // Central Metric Inset
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stats.total.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = if (isDarkMode) Color.White else SlateTextPrimary,
                                fontSize = centerTotalSp
                            )
                            Text(
                                text = AppStrings.get("total_personnel", currentLang),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = centerLabelSp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(if (isWide) 18.dp else 14.dp))

                    // Real-Time Metrics Column (Interactive rows)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(if (isWide) 10.dp else 8.dp)
                    ) {
                        // Metric 1: On Duty
                        DashboardMetricRow(
                            icon = Icons.Default.Shield,
                            label = AppStrings.get("on_duty_present", currentLang),
                            count = stats.onDuty.toString(),
                            accentColor = EmeraldSuccess,
                            isSelected = selectedStatus == "ON_DUTY",
                            isDarkMode = isDarkMode,
                            isWide = isWide,
                            isNeumorphic = isNeumorphic,
                            onClick = { onSelectStatus(if (selectedStatus == "ON_DUTY") "ALL" else "ON_DUTY") }
                        )

                        // Metric 2: On Leave (Home)
                        DashboardMetricRow(
                            icon = Icons.Default.Home,
                            label = AppStrings.get("on_leave_home", currentLang),
                            count = stats.atHome.toString(),
                            accentColor = if (isDarkMode) CyanGlow else VibrantCyan,
                            isSelected = selectedStatus == "AT_HOME",
                            isDarkMode = isDarkMode,
                            isWide = isWide,
                            isNeumorphic = isNeumorphic,
                            onClick = { onSelectStatus(if (selectedStatus == "AT_HOME") "ALL" else "AT_HOME") }
                        )

                        // Metric 3: Returning Today / Overdue
                        val urgentCount = stats.returningToday + stats.overdue
                        DashboardMetricRow(
                            icon = if (stats.overdue > 0) Icons.Default.Warning else Icons.Default.NotificationsActive,
                            label = AppStrings.get("delayed_overdue", currentLang),
                            count = urgentCount.toString(),
                            accentColor = if (stats.overdue > 0) RedDanger else AmberWarning,
                            isSelected = selectedStatus == "OVERDUE" || selectedStatus == "RETURNING_TODAY",
                            isDarkMode = isDarkMode,
                            isWide = isWide,
                            isNeumorphic = isNeumorphic,
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

@Composable
private fun DashboardMetricRow(
    icon: ImageVector,
    label: String,
    count: String,
    accentColor: Color,
    isSelected: Boolean,
    isDarkMode: Boolean,
    isWide: Boolean = false,
    isNeumorphic: Boolean = false,
    onClick: () -> Unit
) {
    val rowShape = RoundedCornerShape(16.dp)
    val iconShape = if (isNeumorphic) RoundedCornerShape(12.dp) else CircleShape
    val iconBg = if (isNeumorphic) {
        when (accentColor) {
            EmeraldSuccess -> if (isDarkMode) Color(0x3310B981) else Color(0xFFDCFCE7)
            RedDanger -> if (isDarkMode) Color(0x33EF4444) else Color(0xFFFFE4E6)
            AmberWarning -> if (isDarkMode) Color(0x33F59E0B) else Color(0xFFFEF3C7)
            else -> if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F2FE)
        }
    } else {
        accentColor.copy(alpha = 0.18f)
    }

    Surface(
        onClick = onClick,
        shape = rowShape,
        color = if (isSelected) {
            if (isDarkMode) accentColor.copy(alpha = 0.25f) else accentColor.copy(alpha = 0.15f)
        } else {
            if (isNeumorphic) {
                if (isDarkMode) Color(0xFF161F2E) else Color(0xFFEDF2F8)
            } else {
                if (isDarkMode) Color(0x221E293B) else Color.White.copy(alpha = 0.7f)
            }
        },
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) accentColor else (if (isDarkMode) Color(0x26475569) else Color(0x33CBD5E1))
        ),
        modifier = Modifier.fillMaxWidth()
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
                        .size(if (isWide) 36.dp else 32.dp)
                        .clip(iconShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(if (isWide) 20.dp else 18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(9.dp))
                Text(
                    text = label,
                    fontSize = if (isWide) 13.5.sp else 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isDarkMode) Color(0xFFE2E8F0) else SlateTextPrimary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = count,
                fontSize = if (isWide) 17.5.sp else 16.5.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
        }
    }
}
