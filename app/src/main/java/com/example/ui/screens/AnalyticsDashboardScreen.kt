package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppThemeStyle
import com.example.data.LeaveAnalyticsReport
import com.example.data.MonthlyLeaveStat
import com.example.data.PersonnelAnnualLeaveStat
import com.example.data.RankLeaveStat
import com.example.data.ReferenceDateSource
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.ui.PersonnelViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

private enum class MonthlyChartMetric { COUNT, DAYS }
private enum class PersonnelStatsSort { LEAVES_DESC, DAYS_DESC, DUTY_DESC }

private val analyticsPalette = listOf(
    Color(0xFF00BCD4),
    Color(0xFF3B82F6),
    Color(0xFF10B981),
    Color(0xFFF59E0B),
    Color(0xFF8B5CF6),
    Color(0xFFEC4899),
    Color(0xFFEF4444),
    Color(0xFF14B8A6),
    Color(0xFF6366F1),
    Color(0xFFF97316)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyticsDashboardScreen(
    viewModel: PersonnelViewModel,
    onBack: () -> Unit,
    onSelectPersonnel: ((Long) -> Unit)? = null
) {
    BackHandler { onBack() }

    val haptic = LocalHapticFeedback.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val themeStyle by viewModel.currentThemeStyle.collectAsState()
    val isNeumorphic = themeStyle == AppThemeStyle.NEUMORPHISM
    val animationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()
    val dutyThreshold by viewModel.dutyThresholdDays.collectAsState()
    val report by viewModel.analyticsReport.collectAsState()

    var chartMetric by rememberSaveable { mutableStateOf(MonthlyChartMetric.COUNT) }
    var selectedMonthIndex by rememberSaveable(report.selectedYear) {
        mutableIntStateOf(report.peakMonthIndex ?: -1)
    }
    var selectedRankIndex by rememberSaveable(report.selectedYear) {
        mutableIntStateOf(-1)
    }
    var personnelSearchQuery by rememberSaveable { mutableStateOf("") }
    var personnelSort by rememberSaveable { mutableStateOf(PersonnelStatsSort.LEAVES_DESC) }

    LaunchedEffect(report.selectedYear, report.peakMonthIndex) {
        if (selectedMonthIndex !in 0..11 || report.monthlyStats.getOrNull(selectedMonthIndex)?.leaveCount == 0) {
            selectedMonthIndex = report.peakMonthIndex ?: -1
        }
    }

    val filteredPersonnelStats = remember(
        report.personnelAnnualStats,
        personnelSearchQuery,
        personnelSort,
        currentLang
    ) {
        val q = personnelSearchQuery.trim().lowercase(Locale.getDefault())
        val filtered = if (q.isEmpty()) {
            report.personnelAnnualStats
        } else {
            report.personnelAnnualStats.filter { stat ->
                stat.fullName.lowercase(Locale.getDefault()).contains(q) ||
                    stat.fullNameLatin.lowercase(Locale.getDefault()).contains(q) ||
                    stat.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                    formatRank(stat.rankRaw, currentLang).lowercase(Locale.getDefault()).contains(q)
            }
        }
        when (personnelSort) {
            PersonnelStatsSort.LEAVES_DESC -> filtered.sortedWith(
                compareByDescending<PersonnelAnnualLeaveStat> { it.leavesCountInYear }
                    .thenByDescending { it.totalDaysInYear }
                    .thenByDescending { it.daysSinceReference }
            )
            PersonnelStatsSort.DAYS_DESC -> filtered.sortedWith(
                compareByDescending<PersonnelAnnualLeaveStat> { it.totalDaysInYear }
                    .thenByDescending { it.leavesCountInYear }
                    .thenByDescending { it.daysSinceReference }
            )
            PersonnelStatsSort.DUTY_DESC -> filtered.sortedWith(
                compareByDescending<PersonnelAnnualLeaveStat> { it.daysSinceReference }
                    .thenByDescending { it.leavesCountInYear }
            )
        }
    }

    val textPrimary = if (isDarkMode) Color.White else SlateTextPrimary
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .testTag("analytics_dashboard_screen"),
            contentAlignment = Alignment.TopCenter
        ) {
            val isTablet = maxWidth >= 600.dp
            val contentMaxWidth = if (isTablet) 980.dp else 680.dp
            val horizontalPadding = if (isTablet) 24.dp else 16.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = contentMaxWidth)
                    .testTag("analytics_lazy_column"),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Header Banner + Year Selector
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(
                                shape = LiquidGlassDefaults.CardShape,
                                elevation = 5.dp,
                                borderAlpha = 0.9f,
                                isDark = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                cornerRadius = 24.dp
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF00BCD4), Color(0xFF2563EB))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Insights,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = AppStrings.get("analytics_title", currentLang),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = AppStrings.get("analytics_subtitle", currentLang),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            // Year Selector Chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = AppStrings.get("analytics_year_label", currentLang),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary
                                )
                                report.availableYears.forEach { year ->
                                    val selected = year == report.selectedYear
                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.setAnalyticsYear(year)
                                        },
                                        shape = RoundedCornerShape(50.dp),
                                        color = if (selected) {
                                            if (isDarkMode) Color(0xFF00BCD4) else RoyalBlueAccent
                                        } else {
                                            if (isDarkMode) Color(0x331E293B) else Color(0x99FFFFFF)
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (selected) Color.Transparent
                                            else if (isDarkMode) Color(0x33475569) else Color(0x40CBD5E1)
                                        ),
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 40.dp)
                                            .testTag("analytics_year_chip_$year")
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "$year",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 13.sp,
                                                color = if (selected) Color.White else textPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Executive KPI Summary Grid
                item {
                    val avgFormatted = remember(report.averageDurationDays) {
                        String.format(Locale.US, "%.1f", report.averageDurationDays)
                    }
                    if (isTablet) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AnalyticsKpiCard(
                                title = AppStrings.get("analytics_total_leaves", currentLang),
                                value = "${report.totalLeavesInYear}",
                                subtitle = "${report.selectedYear}",
                                icon = Icons.Default.BarChart,
                                accent = VibrantCyan,
                                isDarkMode = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                modifier = Modifier.weight(1f)
                            )
                            AnalyticsKpiCard(
                                title = AppStrings.get("analytics_total_days", currentLang),
                                value = "${report.totalDaysInYear}",
                                subtitle = AppStrings.get("days", currentLang),
                                icon = Icons.Default.CalendarMonth,
                                accent = RoyalBlueAccent,
                                isDarkMode = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                modifier = Modifier.weight(1f)
                            )
                            AnalyticsKpiCard(
                                title = AppStrings.get("analytics_avg_duration", currentLang),
                                value = avgFormatted,
                                subtitle = AppStrings.get("days", currentLang),
                                icon = Icons.Default.Schedule,
                                accent = Color(0xFF8B5CF6),
                                isDarkMode = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                modifier = Modifier.weight(1f)
                            )
                            AnalyticsKpiCard(
                                title = AppStrings.get("analytics_eligible_now", currentLang),
                                value = "${report.eligiblePersonnelCount}",
                                subtitle = "$dutyThreshold+ ${AppStrings.get("days", currentLang)}",
                                icon = Icons.Default.Star,
                                accent = AmberWarning,
                                isDarkMode = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnalyticsKpiCard(
                                    title = AppStrings.get("analytics_total_leaves", currentLang),
                                    value = "${report.totalLeavesInYear}",
                                    subtitle = "${report.selectedYear}",
                                    icon = Icons.Default.BarChart,
                                    accent = VibrantCyan,
                                    isDarkMode = isDarkMode,
                                    isNeumorphic = isNeumorphic,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsKpiCard(
                                    title = AppStrings.get("analytics_total_days", currentLang),
                                    value = "${report.totalDaysInYear}",
                                    subtitle = AppStrings.get("days", currentLang),
                                    icon = Icons.Default.CalendarMonth,
                                    accent = RoyalBlueAccent,
                                    isDarkMode = isDarkMode,
                                    isNeumorphic = isNeumorphic,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AnalyticsKpiCard(
                                    title = AppStrings.get("analytics_avg_duration", currentLang),
                                    value = avgFormatted,
                                    subtitle = AppStrings.get("days", currentLang),
                                    icon = Icons.Default.Schedule,
                                    accent = Color(0xFF8B5CF6),
                                    isDarkMode = isDarkMode,
                                    isNeumorphic = isNeumorphic,
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsKpiCard(
                                    title = AppStrings.get("analytics_eligible_now", currentLang),
                                    value = "${report.eligiblePersonnelCount}",
                                    subtitle = "$dutyThreshold+ ${AppStrings.get("days", currentLang)}",
                                    icon = Icons.Default.Star,
                                    accent = AmberWarning,
                                    isDarkMode = isDarkMode,
                                    isNeumorphic = isNeumorphic,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // 3. Monthly Leaves Interactive Chart Card
                item {
                    MonthlyLeavesChartCard(
                        monthlyStats = report.monthlyStats,
                        totalLeavesInYear = report.totalLeavesInYear,
                        totalDaysInYear = report.totalDaysInYear,
                        metric = chartMetric,
                        onSelectMetric = { chartMetric = it },
                        selectedMonthIndex = selectedMonthIndex,
                        onSelectMonth = { idx ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedMonthIndex = if (selectedMonthIndex == idx) -1 else idx
                        },
                        currentLang = currentLang,
                        isDarkMode = isDarkMode,
                        isNeumorphic = isNeumorphic,
                        animationsEnabled = animationsEnabled
                    )
                }

                // 4. Rank Distribution Interactive Donut Chart + Ranked Bars
                item {
                    RankDistributionChartCard(
                        rankStats = report.rankStats,
                        totalLeavesInYear = report.totalLeavesInYear,
                        selectedRankIndex = selectedRankIndex,
                        onSelectRank = { idx ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedRankIndex = if (selectedRankIndex == idx) -1 else idx
                        },
                        currentLang = currentLang,
                        isDarkMode = isDarkMode,
                        isNeumorphic = isNeumorphic,
                        animationsEnabled = animationsEnabled,
                        isTablet = isTablet
                    )
                }

                // 5. Decision Support Insights & Leave Types
                item {
                    DecisionInsightsCard(
                        report = report,
                        currentLang = currentLang,
                        isDarkMode = isDarkMode,
                        isNeumorphic = isNeumorphic
                    )
                }

                // 6. Per-Personnel Annual Statistics Header + Search & Sort
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(
                                shape = LiquidGlassDefaults.CardShape,
                                elevation = 4.dp,
                                borderAlpha = 0.85f,
                                isDark = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                cornerRadius = 24.dp
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = AppStrings.get("analytics_personnel_annual_title", currentLang),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Black,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = AppStrings.get("analytics_personnel_annual_sub", currentLang),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkMode) 0.2f else 0.12f)
                                ) {
                                    Text(
                                        text = "${filteredPersonnelStats.size}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = personnelSearchQuery,
                                onValueChange = { personnelSearchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("analytics_search_field"),
                                placeholder = {
                                    Text(
                                        text = AppStrings.get("analytics_search_personnel", currentLang),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textSecondary
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = AppStrings.get("search_label", currentLang),
                                        tint = VibrantCyan
                                    )
                                },
                                trailingIcon = {
                                    if (personnelSearchQuery.isNotEmpty()) {
                                        IconButton(onClick = { personnelSearchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = AppStrings.get("clear_search", currentLang),
                                                tint = textSecondary
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = textPrimary,
                                    unfocusedTextColor = textPrimary,
                                    focusedContainerColor = if (isDarkMode) Color(0x331E293B) else Color(0xF5FFFFFF),
                                    unfocusedContainerColor = if (isDarkMode) Color(0x221E293B) else Color(0xD9FFFFFF),
                                    unfocusedBorderColor = if (isDarkMode) Color(0x33475569) else Color(0x40CBD5E1),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    PersonnelStatsSort.LEAVES_DESC to "analytics_sort_leaves",
                                    PersonnelStatsSort.DAYS_DESC to "analytics_sort_days",
                                    PersonnelStatsSort.DUTY_DESC to "analytics_sort_duty"
                                ).forEach { (sortKey, labelKey) ->
                                    val isSelected = personnelSort == sortKey
                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            personnelSort = sortKey
                                        },
                                        shape = RoundedCornerShape(50.dp),
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkMode) 0.28f else 0.15f)
                                        } else {
                                            if (isDarkMode) Color(0x281E293B) else Color(0x99FFFFFF)
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else if (isDarkMode) Color(0x33475569) else Color(0x40CBD5E1)
                                        ),
                                        modifier = Modifier.defaultMinSize(minHeight = 40.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = AppStrings.get(labelKey, currentLang),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 7. Individual Personnel Annual Cards
                if (filteredPersonnelStats.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .liquidGlass(
                                    shape = LiquidGlassDefaults.CardShape,
                                    elevation = 3.dp,
                                    isDark = isDarkMode,
                                    isNeumorphic = isNeumorphic
                                )
                                .padding(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = AppStrings.get("no_results", currentLang),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary
                            )
                        }
                    }
                } else {
                    items(
                        items = filteredPersonnelStats,
                        key = { "${it.personnelId}_${it.registrationNumber}" }
                    ) { stat ->
                        PersonnelAnnualStatCard(
                            stat = stat,
                            selectedYear = report.selectedYear,
                            dutyThreshold = dutyThreshold,
                            currentLang = currentLang,
                            isDarkMode = isDarkMode,
                            isNeumorphic = isNeumorphic,
                            onClick = if (stat.isActiveInPersonnel && stat.personnelId > 0L && onSelectPersonnel != null) {
                                { onSelectPersonnel(stat.personnelId) }
                            } else null
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }
    }
}

@Composable
private fun AnalyticsKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .liquidGlass(
                shape = RoundedCornerShape(20.dp),
                elevation = 4.dp,
                borderAlpha = 0.85f,
                isDark = isDarkMode,
                isNeumorphic = isNeumorphic,
                cornerRadius = 20.dp
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = if (isDarkMode) 0.22f else 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = accent,
                maxLines = 1
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else SlateTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MonthlyLeavesChartCard(
    monthlyStats: List<MonthlyLeaveStat>,
    totalLeavesInYear: Int,
    totalDaysInYear: Long,
    metric: MonthlyChartMetric,
    onSelectMetric: (MonthlyChartMetric) -> Unit,
    selectedMonthIndex: Int,
    onSelectMonth: (Int) -> Unit,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean
) {
    val textPrimary = if (isDarkMode) Color.White else SlateTextPrimary
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary

    val values = remember(monthlyStats, metric) {
        monthlyStats.map { stat ->
            if (metric == MonthlyChartMetric.COUNT) stat.leaveCount.toFloat() else stat.totalDays.toFloat()
        }
    }
    val maxVal = remember(values) { (values.maxOrNull() ?: 0f).coerceAtLeast(1f) }
    val growthProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = if (animationsEnabled) tween(durationMillis = 650, easing = FastOutSlowInEasing) else tween(0),
        label = "monthlyChartGrowth"
    )

    val monthShortLabels = remember(currentLang) { getLocalizedMonthShortNames(currentLang) }
    val monthFullLabels = remember(currentLang) { getLocalizedMonthFullNames(currentLang) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = LiquidGlassDefaults.CardShape,
                elevation = 5.dp,
                borderAlpha = 0.85f,
                isDark = isDarkMode,
                isNeumorphic = isNeumorphic,
                cornerRadius = 24.dp
            )
            .padding(16.dp)
            .testTag("analytics_monthly_chart_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = VibrantCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = AppStrings.get("analytics_monthly_chart_title", currentLang),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = textPrimary
                        )
                        Text(
                            text = AppStrings.get("analytics_monthly_chart_sub", currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                }

                // Count vs Days toggle
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (isDarkMode) Color(0x331E293B) else Color(0xFFE2E8F0)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        listOf(
                            MonthlyChartMetric.COUNT to "analytics_metric_count",
                            MonthlyChartMetric.DAYS to "analytics_metric_days"
                        ).forEach { (m, labelKey) ->
                            val active = metric == m
                            Surface(
                                onClick = { onSelectMetric(m) },
                                shape = RoundedCornerShape(50.dp),
                                color = if (active) VibrantCyan else Color.Transparent,
                                modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = AppStrings.get(labelKey, currentLang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.White else textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (totalLeavesInYear == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AppStrings.get("analytics_empty_charts", currentLang),
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                }
            } else {
                // Force LTR inside chart canvas so Jan -> Dec aligns pixel-for-pixel with bars and spline nodes
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val gridColor = if (isDarkMode) Color(0x2894A3B8) else Color(0x2864748B)
                        val barPrimary = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0284C7)
                        val barSecondary = if (isDarkMode) Color(0xFF3B82F6) else Color(0xFF2563EB)
                        val lineAccent = AmberWarning

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(185.dp)
                                .pointerInput(values) {
                                    detectTapGestures { offset ->
                                        val count = 12
                                        val colW = size.width.toFloat() / count
                                        val idx = (offset.x / colW).toInt().coerceIn(0, 11)
                                        onSelectMonth(idx)
                                    }
                                }
                                .testTag("monthly_chart_canvas")
                        ) {
                            val topPad = 18.dp.toPx()
                            val bottomPad = 8.dp.toPx()
                            val chartH = (size.height - topPad - bottomPad).coerceAtLeast(10f)
                            val colW = size.width / 12f
                            val barW = (colW * 0.54f).coerceIn(8.dp.toPx(), 26.dp.toPx())

                            // Horizontal dashed reference gridlines
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            for (step in 0..3) {
                                val y = topPad + chartH * (step / 3f)
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = if (step < 3) dashEffect else null
                                )
                            }

                            val nodePoints = ArrayList<Offset>(12)
                            values.forEachIndexed { idx, rawVal ->
                                val ratio = (rawVal / maxVal).coerceIn(0f, 1f) * growthProgress
                                val barH = (chartH * ratio).coerceAtLeast(if (rawVal > 0f) 4.dp.toPx() else 0f)
                                val cx = colW * idx + colW / 2f
                                val xLeft = cx - barW / 2f
                                val yTop = topPad + chartH - barH
                                val isSelected = idx == selectedMonthIndex

                                // Track background slot
                                drawRoundRect(
                                    color = gridColor.copy(alpha = 0.25f),
                                    topLeft = Offset(xLeft, topPad),
                                    size = Size(barW, chartH),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )

                                if (barH > 0f) {
                                    val barBrush = Brush.verticalGradient(
                                        colors = if (isSelected) {
                                            listOf(AmberWarning, Color(0xFFEA580C))
                                        } else {
                                            listOf(barPrimary, barSecondary)
                                        },
                                        startY = yTop,
                                        endY = topPad + chartH
                                    )
                                    drawRoundRect(
                                        brush = barBrush,
                                        topLeft = Offset(xLeft, yTop),
                                        size = Size(barW, barH),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )
                                }
                                nodePoints.add(Offset(cx, yTop))
                            }

                            // Smooth D3-style Catmull-Rom / cubic Bezier trend curve
                            if (nodePoints.size >= 2) {
                                val curvePath = Path().apply {
                                    moveTo(nodePoints[0].x, nodePoints[0].y)
                                    for (i in 0 until nodePoints.size - 1) {
                                        val p0 = nodePoints[i]
                                        val p1 = nodePoints[i + 1]
                                        val midX = (p0.x + p1.x) / 2f
                                        cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                                    }
                                }
                                drawPath(
                                    path = curvePath,
                                    color = lineAccent.copy(alpha = 0.85f),
                                    style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            // Nodes on top
                            nodePoints.forEachIndexed { idx, pt ->
                                val isSel = idx == selectedMonthIndex
                                if (values[idx] > 0f || isSel) {
                                    drawCircle(
                                        color = if (isDarkMode) Color(0xFF0F172A) else Color.White,
                                        radius = if (isSel) 6.dp.toPx() else 4.dp.toPx(),
                                        center = pt
                                    )
                                    drawCircle(
                                        color = if (isSel) AmberWarning else barPrimary,
                                        radius = if (isSel) 4.dp.toPx() else 2.6.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }
                        }

                        // Month Labels Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            monthShortLabels.forEachIndexed { idx, label ->
                                val isSelected = idx == selectedMonthIndex
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onSelectMonth(idx) }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 9.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                        color = if (isSelected) AmberWarning else textSecondary,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected Month Detail Callout
                val selectedStat = monthlyStats.getOrNull(selectedMonthIndex)
                AnimatedVisibility(
                    visible = selectedStat != null,
                    enter = fadeIn(tween(200)) + expandVertically(),
                    exit = fadeOut(tween(150)) + shrinkVertically()
                ) {
                    if (selectedStat != null) {
                        val sharePct = if (metric == MonthlyChartMetric.COUNT) {
                            if (totalLeavesInYear > 0) (selectedStat.leaveCount * 100f / totalLeavesInYear).roundToInt() else 0
                        } else {
                            if (totalDaysInYear > 0L) (selectedStat.totalDays * 100f / totalDaysInYear).roundToInt() else 0
                        }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDarkMode) Color(0x28F59E0B) else Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${AppStrings.get("analytics_selected_month_info", currentLang)}: ${monthFullLabels[selectedStat.monthIndex]}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = textPrimary
                                    )
                                    if (!selectedStat.topRankRaw.isNullOrBlank()) {
                                        Text(
                                            text = "${AppStrings.get("analytics_top_rank", currentLang)}: ${formatRank(selectedStat.topRankRaw, currentLang)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textSecondary
                                        )
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isDarkMode) Color(0x330F172A) else Color.White
                                    ) {
                                        Text(
                                            text = "${selectedStat.leaveCount} ${AppStrings.get("analytics_leaves_unit", currentLang)} • ${selectedStat.totalDays} ${AppStrings.get("days", currentLang)} ($sharePct%)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankDistributionChartCard(
    rankStats: List<RankLeaveStat>,
    totalLeavesInYear: Int,
    selectedRankIndex: Int,
    onSelectRank: (Int) -> Unit,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean,
    isTablet: Boolean
) {
    val textPrimary = if (isDarkMode) Color.White else SlateTextPrimary
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary

    val sweepFraction by animateFloatAsState(
        targetValue = 1f,
        animationSpec = if (animationsEnabled) tween(durationMillis = 700, easing = FastOutSlowInEasing) else tween(0),
        label = "donutSweep"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = LiquidGlassDefaults.CardShape,
                elevation = 5.dp,
                borderAlpha = 0.85f,
                isDark = isDarkMode,
                isNeumorphic = isNeumorphic,
                cornerRadius = 24.dp
            )
            .padding(16.dp)
            .testTag("analytics_rank_chart_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PieChart,
                    contentDescription = null,
                    tint = RoyalBlueAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = AppStrings.get("analytics_rank_chart_title", currentLang),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                    Text(
                        text = AppStrings.get("analytics_rank_chart_sub", currentLang),
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondary
                    )
                }
            }

            if (rankStats.isEmpty() || totalLeavesInYear == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AppStrings.get("analytics_empty_charts", currentLang),
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                }
            } else {
                val activeSlice = rankStats.getOrNull(selectedRankIndex)
                val donutContent: @Composable () -> Unit = {
                    Box(
                        modifier = Modifier
                            .size(188.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(rankStats) {
                                    detectTapGestures { tapOffset ->
                                        val cx = size.width / 2f
                                        val cy = size.height / 2f
                                        val dx = tapOffset.x - cx
                                        val dy = tapOffset.y - cy
                                        val dist = hypot(dx, dy)
                                        val outerR = minOf(size.width, size.height).toFloat() / 2f
                                        val innerR = outerR * 0.45f
                                        if (dist in innerR..outerR) {
                                            var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                            deg = (deg + 90f + 360f) % 360f
                                            var acc = 0f
                                            for (i in rankStats.indices) {
                                                val sliceAngle = (rankStats[i].leaveCount.toFloat() / totalLeavesInYear.toFloat()) * 360f
                                                if (deg in acc..(acc + sliceAngle)) {
                                                    onSelectRank(i)
                                                    break
                                                }
                                                acc += sliceAngle
                                            }
                                        }
                                    }
                                }
                                .testTag("rank_donut_canvas")
                        ) {
                            val strokeW = 26.dp.toPx()
                            val selectedExtra = 5.dp.toPx()
                            val gapDeg = if (rankStats.size > 1) 2.5f else 0f
                            var startAngle = -90f

                            rankStats.forEachIndexed { idx, stat ->
                                val rawSweep = (stat.leaveCount.toFloat() / totalLeavesInYear.toFloat()) * 360f * sweepFraction
                                val drawSweep = (rawSweep - gapDeg).coerceAtLeast(2f)
                                val isSel = idx == selectedRankIndex
                                val color = analyticsPalette[idx % analyticsPalette.size]
                                val inset = if (isSel) strokeW / 2f - 2.dp.toPx() else strokeW / 2f + 2.dp.toPx()

                                drawArc(
                                    color = if (selectedRankIndex >= 0 && !isSel) color.copy(alpha = 0.45f) else color,
                                    startAngle = startAngle,
                                    sweepAngle = drawSweep,
                                    useCenter = false,
                                    topLeft = Offset(inset, inset),
                                    size = Size(size.width - inset * 2f, size.height - inset * 2f),
                                    style = Stroke(
                                        width = if (isSel) strokeW + selectedExtra else strokeW,
                                        cap = StrokeCap.Butt
                                    )
                                )
                                startAngle += rawSweep
                            }
                        }

                        // Donut Center Metric
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (activeSlice != null) {
                                val activeColor = analyticsPalette[selectedRankIndex % analyticsPalette.size]
                                Text(
                                    text = "${activeSlice.percentage.roundToInt()}%",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = activeColor
                                )
                                Text(
                                    text = formatRank(activeSlice.rankRaw, currentLang),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.widthIn(max = 96.dp)
                                )
                                Text(
                                    text = "${activeSlice.leaveCount} • ${activeSlice.totalDays} ${AppStrings.get("days", currentLang)}",
                                    fontSize = 10.sp,
                                    color = textSecondary
                                )
                            } else {
                                Text(
                                    text = "$totalLeavesInYear",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = textPrimary
                                )
                                Text(
                                    text = AppStrings.get("analytics_all_ranks_total", currentLang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textSecondary
                                )
                            }
                        }
                    }
                }

                val legendContent: @Composable (Modifier) -> Unit = { mod ->
                    Column(
                        modifier = mod,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rankStats.forEachIndexed { idx, stat ->
                            val color = analyticsPalette[idx % analyticsPalette.size]
                            val isSel = idx == selectedRankIndex
                            Surface(
                                onClick = { onSelectRank(idx) },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSel) {
                                    color.copy(alpha = if (isDarkMode) 0.22f else 0.12f)
                                } else {
                                    if (isDarkMode) Color(0x1E1E293B) else Color(0x88F8FAFC)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (isSel) color else Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = formatRank(stat.rankRaw, currentLang),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
                                                color = textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${stat.leaveCount} (${stat.percentage.roundToInt()}%) • ${stat.totalDays}${AppStrings.get("days", currentLang).firstOrNull() ?: ""}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = color
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { (stat.percentage / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(50.dp)),
                                        color = color,
                                        trackColor = if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
                                    )
                                }
                            }
                        }
                    }
                }

                if (isTablet) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        donutContent()
                        legendContent(Modifier.weight(1f))
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        donutContent()
                        legendContent(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DecisionInsightsCard(
    report: LeaveAnalyticsReport,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    isNeumorphic: Boolean
) {
    val textPrimary = if (isDarkMode) Color.White else SlateTextPrimary
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
    val monthNames = remember(currentLang) { getLocalizedMonthFullNames(currentLang) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = LiquidGlassDefaults.CardShape,
                elevation = 4.dp,
                borderAlpha = 0.85f,
                isDark = isDarkMode,
                isNeumorphic = isNeumorphic,
                cornerRadius = 24.dp
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = null,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("analytics_decision_insights", currentLang),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = textPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InsightMiniTile(
                    label = AppStrings.get("analytics_peak_month", currentLang),
                    value = report.peakMonthIndex?.let { "${monthNames[it]} (${report.peakMonthCount})" } ?: "—",
                    accent = VibrantCyan,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f)
                )
                InsightMiniTile(
                    label = AppStrings.get("analytics_top_rank", currentLang),
                    value = report.topRankRaw?.let { formatRank(it, currentLang) } ?: "—",
                    accent = RoyalBlueAccent,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f)
                )
                InsightMiniTile(
                    label = AppStrings.get("analytics_top_leave_type", currentLang),
                    value = report.topLeaveTypeRaw?.let { formatLeaveType(it, currentLang) } ?: "—",
                    accent = EmeraldSuccess,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f)
                )
            }

            if (report.leaveTypeStats.isNotEmpty()) {
                Text(
                    text = AppStrings.get("analytics_leave_types_title", currentLang),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    report.leaveTypeStats.forEachIndexed { idx, lt ->
                        val color = analyticsPalette[(idx + 2) % analyticsPalette.size]
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = color.copy(alpha = if (isDarkMode) 0.18f else 0.12f),
                            border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "${formatLeaveType(lt.leaveTypeRaw, currentLang)}: ${lt.leaveCount} (${lt.totalDays} ${AppStrings.get("days", currentLang)})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else color,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightMiniTile(
    label: String,
    value: String,
    accent: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkMode) Color(0x281E293B) else Color(0xCCF8FAFC),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonnelAnnualStatCard(
    stat: PersonnelAnnualLeaveStat,
    selectedYear: Int,
    dutyThreshold: Int,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    onClick: (() -> Unit)?
) {
    val textPrimary = if (isDarkMode) Color.White else SlateTextPrimary
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
    val refColor = if (stat.referenceSource == ReferenceDateSource.LAST_LEAVE_RETURN) EmeraldSuccess else AmberWarning
    val refSourceLabel = AppStrings.get(
        if (stat.referenceSource == ReferenceDateSource.LAST_LEAVE_RETURN) {
            "analytics_ref_last_leave"
        } else {
            "analytics_ref_added_date"
        },
        currentLang
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(22.dp),
                elevation = 4.dp,
                borderAlpha = 0.85f,
                isDark = isDarkMode,
                isNeumorphic = isNeumorphic,
                cornerRadius = 22.dp
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(14.dp)
            .testTag("personnel_annual_stat_${stat.registrationNumber}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header row: Name + Rank + Annual Leave Count Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stat.getLocalizedName(currentLang),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (!stat.isActiveInPersonnel) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x3364748B)
                            ) {
                                Text(
                                    text = AppStrings.get("analytics_archived_tag", currentLang),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA)
                        ) {
                            Text(
                                text = formatRank(stat.rankRaw, currentLang),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDarkMode) Color(0x221E293B) else Color(0x1A64748B)
                        ) {
                            Text(
                                text = stat.registrationNumber,
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = textSecondary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Annual Utilization Summary Badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (stat.leavesCountInYear > 0) {
                        RoyalBlueAccent.copy(alpha = if (isDarkMode) 0.24f else 0.12f)
                    } else {
                        if (isDarkMode) Color(0x28334155) else Color(0xFFF1F5F9)
                    },
                    border = BorderStroke(
                        1.dp,
                        if (stat.leavesCountInYear > 0) RoyalBlueAccent.copy(alpha = 0.45f)
                        else Color.Transparent
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${stat.leavesCountInYear} ${AppStrings.get("analytics_leaves_unit", currentLang)}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (stat.leavesCountInYear > 0) {
                                if (isDarkMode) Color(0xFF60A5FA) else RoyalBlueAccent
                            } else textSecondary
                        )
                        Text(
                            text = "${stat.totalDaysInYear} ${AppStrings.get("days", currentLang)} ($selectedYear)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary
                        )
                    }
                }
            }

            // Duty Reference Date Strip (Last Leave Return Date vs Personnel Added Date)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isDarkMode) Color(0x261E293B) else Color(0xB8F8FAFC),
                border = BorderStroke(1.dp, refColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (stat.referenceSource == ReferenceDateSource.LAST_LEAVE_RETURN) {
                                Icons.Default.EventAvailable
                            } else {
                                Icons.Default.DateRange
                            },
                            contentDescription = null,
                            tint = refColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "${AppStrings.get("analytics_ref_date_label", currentLang)}: ${stat.formattedReferenceDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary
                            )
                            Text(
                                text = refSourceLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = refColor
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = (if (stat.isEligible) AmberWarning else refColor).copy(alpha = if (isDarkMode) 0.2f else 0.12f)
                    ) {
                        val formattedRefDays = if (stat.daysSinceReference < 0L) {
                            "\u200E${stat.daysSinceReference}\u200E"
                        } else {
                            "${stat.daysSinceReference}"
                        }
                        Text(
                            text = "${AppStrings.get("analytics_elapsed_since_ref", currentLang)} $formattedRefDays ${AppStrings.get("days", currentLang)}" +
                                if (stat.isEligible) " • ${AppStrings.get("badge_eligible", currentLang)}" else "",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (stat.isEligible) AmberWarning else refColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Leave Types Breakdown Chips for the year
            if (stat.leavesByType.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stat.leavesByType.forEach { (typeRaw, count) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDarkMode) Color(0x337C3AED) else Color(0xFFEDE9FE)
                        ) {
                            Text(
                                text = "${formatLeaveType(typeRaw, currentLang)}: $count",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFFC4B5FD) else Color(0xFF6D28D9),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = AppStrings.get("analytics_no_leaves_year", currentLang),
                    fontSize = 10.5.sp,
                    color = textSecondary
                )
            }
        }
    }
}

private fun getLocalizedMonthShortNames(lang: AppLanguage): List<String> = when (lang) {
    AppLanguage.ARABIC -> listOf("جان", "فيف", "مار", "أفر", "ماي", "جوا", "جوي", "أوت", "سبت", "أكت", "نوف", "ديس")
    AppLanguage.FRENCH -> listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc")
    AppLanguage.ENGLISH -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
}

private fun getLocalizedMonthFullNames(lang: AppLanguage): List<String> = when (lang) {
    AppLanguage.ARABIC -> listOf(
        "جانفي", "فيفري", "مارس", "أفريل", "ماي", "جوان",
        "جويلية", "أوت", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )
    AppLanguage.FRENCH -> listOf(
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    )
    AppLanguage.ENGLISH -> listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
}
