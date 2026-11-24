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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassPanel
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
    val secondaryAccent = preset?.secondary ?: RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

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

    val isLivingLuxury = LocalLivingLuxuryMode.current
    val cardSurfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF151E30) else Color(0xFFFEFFFF)
    val cardBorderColor = preset?.borderHighlight?.copy(alpha = 0.5f)
        ?: if (effectiveDark) Color(0x2E38BDF8) else Color(0xFFE2E8F0)

    val pulseAlpha: Float
    val livingBreathScale: Float
    if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulseInfinite")
        pulseAlpha = infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = if (isLivingLuxury) 950 else 1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        ).value
        livingBreathScale = if (isLivingLuxury) {
            infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.032f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "livingRingBreath"
            ).value
        } else 1f
    } else {
        pulseAlpha = 1f
        livingBreathScale = 1f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("statistics_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = cardSurfaceColor,
            shadowElevation = if (effectiveDark) 4.dp else 6.dp,
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
                val ringSize = if (isWide) 124.dp else if (isCompact) 92.dp else 104.dp
                val canvasSize = if (isWide) 114.dp else if (isCompact) 84.dp else 96.dp
                val strokeWidthDp = if (isWide) 10.5.dp else if (isCompact) 8.dp else 9.dp
                val centerTotalSp = if (isWide) 23.sp else if (isCompact) 18.sp else 21.sp
                val centerLabelSp = if (isWide) 10.sp else if (isCompact) 8.sp else 8.5.sp

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Top Header Row: Pulsing Dashboard Indicator & Analytics Entry Pill
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
                                .size(30.dp)
                                .liquidGlassPanel(
                                    shape = RoundedCornerShape(10.dp),
                                    cornerRadius = 10.dp,
                                    isDark = effectiveDark,
                                    accent = primaryAccent,
                                    secondaryAccent = secondaryAccent,
                                    isSelected = true,
                                    softTintWhenSelected = true
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(primaryAccent.copy(alpha = pulseAlpha))
                            )
                        }
                        Text(
                            text = AppStrings.get("dashboard_title", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Interactive Liquid Glass Analytics Entry Pill
                    Surface(
                        onClick = onOpenAnalytics,
                        shape = RoundedCornerShape(50.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .liquidGlassPanel(
                                shape = RoundedCornerShape(50.dp),
                                cornerRadius = 25.dp,
                                isDark = effectiveDark,
                                accent = primaryAccent,
                                secondaryAccent = secondaryAccent,
                                isSelected = true,
                                softTintWhenSelected = true
                            )
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
                                fontWeight = FontWeight.ExtraBold,
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

                // Central Visualization Row: Ring Chart + Interactive Liquid Glass Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(ringSize)
                            .graphicsLayer {
                                scaleX = livingBreathScale
                                scaleY = livingBreathScale
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(canvasSize)) {
                            if (size.minDimension <= 0f) return@Canvas
                            val strokeWidth = strokeWidthDp.toPx()
                            val trackColor = if (effectiveDark) Color(0x2E334155) else Color(0x36CBD5E1)

                            if (isLivingLuxury) {
                                val haloRadius = (size.minDimension * 0.62f).coerceAtLeast(1f)
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(
                                            primaryAccent.copy(alpha = 0.22f * pulseAlpha),
                                            EmeraldSuccess.copy(alpha = 0.12f * pulseAlpha),
                                            Color.Transparent
                                        ),
                                        radius = haloRadius
                                    ),
                                    radius = haloRadius
                                )
                            }

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
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(if (isWide) 18.dp else 12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(if (isWide) 9.dp else 7.dp)
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
    val rowShape = RoundedCornerShape(14.dp)
    val rowSurface = if (isSelected) {
        accentColor.copy(alpha = if (effectiveDark) 0.16f else 0.08f)
    } else if (effectiveDark) {
        preset?.surfaceSecondary ?: Color(0xFF1E293B)
    } else {
        preset?.surfaceSecondary ?: Color(0xFFF8FAFC)
    }
    val rowBorder = if (isSelected) {
        accentColor.copy(alpha = 0.55f)
    } else if (effectiveDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color(0xFFE2E8F0)
    }

    Surface(
        onClick = onClick,
        shape = rowShape,
        color = rowSurface,
        border = BorderStroke(if (isSelected) 1.2.dp else 0.8.dp, rowBorder),
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
                        .size(if (isWide) 32.dp else 28.dp)
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(10.dp),
                            cornerRadius = 10.dp,
                            isDark = effectiveDark,
                            accent = accentColor,
                            secondaryAccent = accentColor,
                            isSelected = true,
                            softTintWhenSelected = true
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(if (isWide) 17.dp else 15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = label,
                        fontSize = if (isWide) 12.sp else 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "$percent%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor.copy(alpha = 0.85f)
                )
                Box(
                    modifier = Modifier
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(50.dp),
                            cornerRadius = 50.dp,
                            isDark = effectiveDark,
                            accent = accentColor,
                            secondaryAccent = accentColor,
                            isSelected = true,
                            softTintWhenSelected = true
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = count,
                        fontSize = if (isWide) 14.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor
                    )
                }
            }
        }
    }
}
