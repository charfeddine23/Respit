package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.formatRank
import com.example.ui.DashboardStats
import com.example.ui.SortOption
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow

private data class StatusFilterItem(
    val code: String,
    val label: String,
    val count: Int?,
    val icon: ImageVector,
    val accent: Color
)

private fun cleanFilterLabel(raw: String): String {
    return raw.replace(Regex("[\\p{So}\\p{Cn}]+"), "").trim()
}

@Composable
fun FilterSortDialog(
    selectedSort: SortOption,
    onSelectSort: (SortOption) -> Unit,
    selectedStatus: String,
    onSelectStatus: (String) -> Unit,
    selectedRank: String,
    onSelectRank: (String) -> Unit,
    uniqueRanks: List<String>,
    stats: DashboardStats,
    onResetFilters: () -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val effectiveNeumorphic = isNeumorphic && (preset?.isLiquidGlass != true)
    val haptic = LocalHapticFeedback.current

    val primaryColor = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryColor = preset?.secondary ?: if (effectiveDark) Color(0xFF8B5CF6) else RoyalBlueAccent

    val dialogBg = when {
        preset != null -> preset.surfacePrimary.copy(alpha = if (preset.isLiquidGlass) 0.94f else 0.98f)
        effectiveNeumorphic && effectiveDark -> Color(0xFF161F30)
        effectiveNeumorphic -> Color(0xFFE6EEF8)
        effectiveDark -> Color(0xF50F172A)
        else -> Color(0xFAFFFFFF)
    }
    val sectionBg = when {
        preset != null -> preset.surfaceSecondary.copy(alpha = if (effectiveDark) 0.55f else 0.72f)
        effectiveDark -> Color(0x661E293B)
        else -> Color(0xFFF8FAFC)
    }
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val dividerColor = if (effectiveDark) Color(0x2E475569) else Color(0x30CBD5E1)

    val activeFilterCount = (if (selectedStatus != "ALL") 1 else 0) +
        (if (selectedRank != "ALL") 1 else 0) +
        (if (selectedSort != SortOption.RETURN_SOON) 1 else 0)

    val visibilityState = remember {
        MutableTransitionState(!animationsEnabled).apply { targetState = true }
    }

    Dialog(onDismissRequest = onDismiss) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        CompositionLocalProvider(
            LocalLayoutDirection provides if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            AnimatedVisibility(
                visibleState = visibilityState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(180)) + scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
                } else ExitTransition.None
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = dialogBg,
                    border = BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            if (effectiveDark) {
                                listOf(
                                    Color.White.copy(alpha = 0.28f),
                                    primaryColor.copy(alpha = 0.55f),
                                    secondaryColor.copy(alpha = 0.30f)
                                )
                            } else {
                                listOf(
                                    Color.White.copy(alpha = 0.96f),
                                    primaryColor.copy(alpha = 0.45f),
                                    Color(0x55CBD5E1)
                                )
                            }
                        )
                    ),
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                        .heightIn(max = 680.dp)
                        .testTag("filter_sort_dialog")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // 1) Sticky Gradient Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            primaryColor.copy(alpha = if (effectiveDark) 0.22f else 0.12f),
                                            secondaryColor.copy(alpha = if (effectiveDark) 0.14f else 0.06f)
                                        )
                                    )
                                )
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                Brush.linearGradient(listOf(primaryColor, secondaryColor))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = AppStrings.get("filter_sort_btn", currentLang),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (activeFilterCount > 0) {
                                                Surface(
                                                    shape = RoundedCornerShape(50.dp),
                                                    color = primaryColor
                                                ) {
                                                    Text(
                                                        text = "$activeFilterCount",
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${stats.total} ${AppStrings.get("total_personnel", currentLang)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .testTag("filter_sort_close_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = AppStrings.get("close", currentLang),
                                        tint = textSecondary
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = dividerColor)

                        // 2) Scrollable Structured Sections
                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .testTag("filter_sort_scroll")
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // SECTION A: Status Filter (2-Column Color-Coded Grid)
                            val statusItems = remember(stats, currentLang, primaryColor) {
                                listOf(
                                    StatusFilterItem(
                                        code = "ALL",
                                        label = cleanFilterLabel(AppStrings.get("status_all", currentLang)),
                                        count = stats.total,
                                        icon = Icons.Default.AllInclusive,
                                        accent = primaryColor
                                    ),
                                    StatusFilterItem(
                                        code = "AT_HOME",
                                        label = cleanFilterLabel(AppStrings.get("status_at_home", currentLang)),
                                        count = stats.atHome,
                                        icon = Icons.Default.Home,
                                        accent = Color(0xFF0284C7)
                                    ),
                                    StatusFilterItem(
                                        code = "RETURNING_TODAY",
                                        label = cleanFilterLabel(AppStrings.get("status_returning_today", currentLang)),
                                        count = stats.returningToday,
                                        icon = Icons.Default.NotificationsActive,
                                        accent = AmberWarning
                                    ),
                                    StatusFilterItem(
                                        code = "OVERDUE",
                                        label = cleanFilterLabel(AppStrings.get("status_overdue", currentLang)),
                                        count = stats.overdue,
                                        icon = Icons.Default.Warning,
                                        accent = RedDanger
                                    ),
                                    StatusFilterItem(
                                        code = "ON_DUTY",
                                        label = cleanFilterLabel(AppStrings.get("status_on_duty", currentLang)),
                                        count = stats.onDuty,
                                        icon = Icons.Default.Shield,
                                        accent = EmeraldSuccess
                                    ),
                                    StatusFilterItem(
                                        code = "ELIGIBLE",
                                        label = cleanFilterLabel(AppStrings.get("status_eligible", currentLang)),
                                        count = stats.eligible,
                                        icon = Icons.Default.Star,
                                        accent = Color(0xFF8B5CF6)
                                    )
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SectionHeaderRow(
                                    icon = Icons.Default.Shield,
                                    title = AppStrings.get("status_col_header", currentLang),
                                    accent = primaryColor,
                                    textPrimary = textPrimary
                                )

                                statusItems.chunked(2).forEach { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        pair.forEach { item ->
                                            val isSelected = selectedStatus == item.code
                                            StatusFilterTile(
                                                item = item,
                                                isSelected = isSelected,
                                                isDark = effectiveDark,
                                                textPrimary = textPrimary,
                                                textSecondary = textSecondary,
                                                surfaceBg = sectionBg,
                                                animationsEnabled = animationsEnabled,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("filter_status_${item.code}"),
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onSelectStatus(item.code)
                                                }
                                            )
                                        }
                                        if (pair.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }

                            // SECTION B: Sort Order (Clean Interactive Radio Rows)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SectionHeaderRow(
                                    icon = Icons.Default.Sort,
                                    title = AppStrings.get("sort", currentLang),
                                    accent = secondaryColor,
                                    textPrimary = textPrimary
                                )

                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = sectionBg,
                                    border = BorderStroke(1.dp, dividerColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        SortOption.entries.forEachIndexed { index, option ->
                                            val isSelected = selectedSort == option
                                            val (label, icon) = when (option) {
                                                SortOption.RETURN_SOON -> AppStrings.get("sort_return_soon", currentLang) to Icons.Default.Schedule
                                                SortOption.OVERDUE_FIRST -> AppStrings.get("sort_overdue_first", currentLang) to Icons.Default.PriorityHigh
                                                SortOption.START_DESC -> AppStrings.get("sort_start_desc", currentLang) to Icons.Default.Event
                                                SortOption.NAME_ASC -> AppStrings.get("sort_name_asc", currentLang) to Icons.Default.SortByAlpha
                                                SortOption.REG_ASC -> AppStrings.get("sort_reg_asc", currentLang) to Icons.Default.Badge
                                            }

                                            SortOptionRow(
                                                label = label,
                                                icon = icon,
                                                isSelected = isSelected,
                                                accent = primaryColor,
                                                isDark = effectiveDark,
                                                textPrimary = textPrimary,
                                                textSecondary = textSecondary,
                                                animationsEnabled = animationsEnabled,
                                                modifier = Modifier.testTag("filter_sort_${option.name}"),
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onSelectSort(option)
                                                }
                                            )

                                            if (index < SortOption.entries.lastIndex) {
                                                HorizontalDivider(
                                                    color = dividerColor,
                                                    modifier = Modifier.padding(horizontal = 14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // SECTION C: Military Rank Filter (Sleek Horizontal Capsule Strip)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val selectedRankLabel = if (selectedRank == "ALL") {
                                    AppStrings.get("all_ranks", currentLang)
                                } else {
                                    formatRank(selectedRank, currentLang)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SectionHeaderRow(
                                        icon = Icons.Default.MilitaryTech,
                                        title = AppStrings.get("all_ranks", currentLang),
                                        accent = AmberWarning,
                                        textPrimary = textPrimary
                                    )

                                    if (selectedRank != "ALL") {
                                        Surface(
                                            shape = RoundedCornerShape(50.dp),
                                            color = primaryColor.copy(alpha = if (effectiveDark) 0.20f else 0.12f)
                                        ) {
                                            Text(
                                                text = selectedRankLabel,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                val rankOptions = remember(uniqueRanks, currentLang) {
                                    listOf("ALL" to AppStrings.get("all_ranks", currentLang)) +
                                        uniqueRanks.map { it to formatRank(it, currentLang) }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rankOptions.forEach { (code, label) ->
                                        val isSelected = selectedRank == code
                                        val pillBg by animateColorAsState(
                                            targetValue = when {
                                                isSelected -> primaryColor
                                                effectiveDark -> Color(0x551E293B)
                                                else -> Color(0xFFFFFFFF)
                                            },
                                            animationSpec = if (animationsEnabled) {
                                                spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                                            } else tween(0),
                                            label = "rankPillBg"
                                        )

                                        Surface(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onSelectRank(code)
                                            },
                                            shape = RoundedCornerShape(50.dp),
                                            color = pillBg,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSelected) primaryColor else dividerColor
                                            ),
                                            modifier = Modifier
                                                .defaultMinSize(minHeight = 48.dp)
                                                .testTag("filter_rank_$code")
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    color = if (isSelected) Color.White else textPrimary,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = dividerColor)

                        // 3) Sticky Action Footer (Reset + Gradient Apply)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onResetFilters()
                                },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.1.dp, dividerColor),
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("filter_sort_reset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("reset_filters", currentLang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .weight(1.25f)
                                    .height(48.dp)
                                    .shadow(
                                        elevation = 6.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor = primaryColor.copy(alpha = 0.35f),
                                        spotColor = secondaryColor.copy(alpha = 0.35f)
                                    )
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(primaryColor, secondaryColor)),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .testTag("filter_sort_apply_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("apply", currentLang),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(
    icon: ImageVector,
    title: String,
    accent: Color,
    textPrimary: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(15.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = textPrimary
        )
    }
}

@Composable
private fun StatusFilterTile(
    item: StatusFilterItem,
    isSelected: Boolean,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    surfaceBg: Color,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            item.accent.copy(alpha = if (isDark) 0.24f else 0.14f)
        } else {
            surfaceBg
        },
        animationSpec = if (animationsEnabled) {
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 400f)
        } else tween(0),
        label = "statusTileBg"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.015f else 1f,
        animationSpec = if (animationsEnabled) {
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
        } else tween(0),
        label = "statusTileScale"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) item.accent else (if (isDark) Color(0x30475569) else Color(0x30CBD5E1))
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        if (isSelected) item.accent else item.accent.copy(alpha = if (isDark) 0.20f else 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else item.accent,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = item.label,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (isSelected) item.accent else textPrimary,
                maxLines = 2,
                lineHeight = 14.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (item.count != null) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (isSelected) {
                        item.accent.copy(alpha = 0.22f)
                    } else if (isDark) {
                        Color(0x33475569)
                    } else {
                        Color(0xFFE2E8F0)
                    }
                ) {
                    Text(
                        text = "${item.count}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) item.accent else textSecondary,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SortOptionRow(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    accent: Color,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val rowBg by animateColorAsState(
        targetValue = if (isSelected) accent.copy(alpha = if (isDark) 0.16f else 0.09f) else Color.Transparent,
        animationSpec = if (animationsEnabled) {
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 400f)
        } else tween(0),
        label = "sortRowBg"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .background(rowBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) accent else textSecondary,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) accent else textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected) accent else Color.Transparent)
                .border(
                    width = 1.5.dp,
                    color = if (isSelected) accent else textSecondary.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
