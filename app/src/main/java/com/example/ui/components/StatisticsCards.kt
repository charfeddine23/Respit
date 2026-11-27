package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardStats
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SkyLeaveBlue
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
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val leaveAccent = if (effectiveDark) Color(0xFF38BDF8) else SkyLeaveBlue
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

    val total = if (stats.total > 0) stats.total.toFloat() else 1f
    val onDutyPct = ((stats.onDuty / total).coerceIn(0f, 1f) * 100f).roundToInt()
    val atHomePct = ((stats.atHome / total).coerceIn(0f, 1f) * 100f).roundToInt()
    val urgentCount = stats.returningToday + stats.overdue

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                isDark = effectiveDark,
                cornerRadius = 24.dp
            )
            .testTag("statistics_section")
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val isWide = maxWidth >= 500.dp

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header Row: [Grid Icon + Overview] .... [Analytics > pill]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(primaryAccent.copy(alpha = if (effectiveDark) 0.18f else 0.11f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.GridView,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = AppStrings.get("overview_title", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Accessible 48dp touch target around crisp frosted Analytics pill
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onOpenAnalytics)
                            .testTag("dashboard_analytics_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = primaryAccent.copy(alpha = if (effectiveDark) 0.18f else 0.11f),
                            border = BorderStroke(1.dp, primaryAccent.copy(alpha = if (effectiveDark) 0.30f else 0.18f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = AppStrings.get("dashboard_analytics_label", currentLang),
                                    tint = primaryAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = AppStrings.get("dashboard_analytics_label", currentLang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = primaryAccent,
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
                }

                // Body: Left Tall Total Personnel Glass Card + Right 3 Stacked Metric Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val totalTileBg = if (effectiveDark) {
                        Color(0xB8182734)
                    } else {
                        Color(0xCCF3F8F8)
                    }
                    val totalTileBorder by animateColorAsState(
                        targetValue = when {
                            selectedStatus == "ALL" -> primaryAccent.copy(alpha = if (effectiveDark) 0.45f else 0.25f)
                            effectiveDark -> Color(0xFF253746)
                            else -> Color(0xFFE1ECEB)
                        },
                        animationSpec = tween(durationMillis = if (animationsEnabled) 220 else 0),
                        label = "totalTileBorder"
                    )

                    Surface(
                        onClick = { onSelectStatus("ALL") },
                        shape = RoundedCornerShape(18.dp),
                        color = totalTileBg,
                        border = BorderStroke(1.dp, totalTileBorder),
                        modifier = Modifier
                            .weight(if (isWide) 0.34f else 0.37f)
                            .defaultMinSize(minHeight = 148.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stats.total.toString(),
                                fontSize = if (isWide) 36.sp else 33.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryAccent,
                                lineHeight = 38.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            Text(
                                text = AppStrings.get("total_personnel", currentLang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = textSecondary,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(if (isWide) 0.66f else 0.63f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardMetricRow(
                            icon = Icons.Default.Shield,
                            label = AppStrings.get("on_duty_present", currentLang),
                            trailingValue = "${stats.onDuty} · $onDutyPct%",
                            accentColor = EmeraldSuccess,
                            isSelected = selectedStatus == "ON_DUTY",
                            isDarkMode = effectiveDark,
                            isWide = isWide,
                            animationsEnabled = animationsEnabled,
                            onClick = { onSelectStatus(if (selectedStatus == "ON_DUTY") "ALL" else "ON_DUTY") }
                        )

                        DashboardMetricRow(
                            icon = Icons.Default.Home,
                            label = AppStrings.get("on_leave_short", currentLang),
                            trailingValue = "${stats.atHome} · $atHomePct%",
                            accentColor = leaveAccent,
                            isSelected = selectedStatus == "AT_HOME",
                            isDarkMode = effectiveDark,
                            isWide = isWide,
                            animationsEnabled = animationsEnabled,
                            onClick = { onSelectStatus(if (selectedStatus == "AT_HOME") "ALL" else "AT_HOME") }
                        )

                        DashboardMetricRow(
                            icon = if (stats.overdue > 0) Icons.Default.Warning else Icons.Default.Notifications,
                            label = AppStrings.get("delayed_overdue", currentLang),
                            trailingValue = urgentCount.toString(),
                            accentColor = if (stats.overdue > 0) RedDanger else AmberWarning,
                            isSelected = selectedStatus == "OVERDUE" || selectedStatus == "RETURNING_TODAY",
                            isDarkMode = effectiveDark,
                            isWide = isWide,
                            animationsEnabled = animationsEnabled,
                            onClick = {
                                onSelectStatus(
                                    if (selectedStatus == "OVERDUE" || selectedStatus == "RETURNING_TODAY") "ALL"
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
    trailingValue: String,
    accentColor: Color,
    isSelected: Boolean,
    isDarkMode: Boolean,
    isWide: Boolean = false,
    animationsEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val animatedAccent by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(durationMillis = if (animationsEnabled) 200 else 0),
        label = "metricRowAccent"
    )
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color(0xFFE2E8F0) else SlateTextPrimary

    val rowShape = RoundedCornerShape(15.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .liquidGlassPanel(
                shape = rowShape,
                cornerRadius = 15.dp,
                isDark = effectiveDark,
                accent = animatedAccent,
                isSelected = isSelected
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isWide) 12.dp else 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = if (effectiveDark) 0.20f else 0.13f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = if (isWide) 12.5.sp else 11.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = trailingValue,
                fontSize = if (isWide) 13.5.sp else 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1
            )
        }
    }
}
