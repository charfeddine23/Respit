package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardStats
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun DashboardPillSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSubmitSearch: () -> Unit,
    onOpenFilter: () -> Unit,
    recentSearches: List<String> = emptyList(),
    onSelectRecentSearch: (String) -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    activeFilterCount: Int = 0,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    searchFieldTestTag: String = "search_field",
    filterButtonTestTag: String = "filter_sort_dialog_button",
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

    val innerFieldBg = if (effectiveDark) Color(0xB8101C26) else Color(0xD9FFFFFF)
    val innerFieldBorder = if (effectiveDark) Color(0xFF253746) else Color(0xFFDDE7E6)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(18.dp),
                    elevation = 4.dp,
                    isDark = effectiveDark,
                    cornerRadius = 18.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    color = innerFieldBg,
                    border = BorderStroke(1.dp, innerFieldBorder),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = AppStrings.get("search_label", currentLang),
                            tint = primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 46.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = AppStrings.get("search_placeholder", currentLang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 13.5.sp,
                                    color = textSecondary.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                cursorBrush = SolidColor(primaryAccent),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { onSubmitSearch() }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(searchFieldTestTag)
                            )
                        }

                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = AppStrings.get("clear_search", currentLang),
                                    tint = textSecondary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }

                // Mint-tinted frosted filter square button matching mockup
                Surface(
                    onClick = onOpenFilter,
                    shape = RoundedCornerShape(13.dp),
                    color = if (activeFilterCount > 0) {
                        primaryAccent
                    } else {
                        primaryAccent.copy(alpha = if (effectiveDark) 0.18f else 0.11f)
                    },
                    border = BorderStroke(
                        1.dp,
                        if (activeFilterCount > 0) primaryAccent else primaryAccent.copy(alpha = if (effectiveDark) 0.30f else 0.18f)
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag(filterButtonTestTag)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        BadgedBox(
                            badge = {
                                if (activeFilterCount > 0) {
                                    Badge(
                                        containerColor = CoralRedAccent,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = "$activeFilterCount",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = AppStrings.get("filter_sort_btn", currentLang),
                                tint = if (activeFilterCount > 0) contrastOnAccent(primaryAccent) else primaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        if (recentSearches.isNotEmpty()) {
            val chipSurface = preset?.surfacePrimary ?: if (effectiveDark) Color(0xCC152430) else Color(0xDCFFFFFF)
            val chipBorder = preset?.borderAccent?.copy(alpha = 0.28f)
                ?: if (effectiveDark) Color(0xFF253746) else Color(0xFFD5E3E2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = AppStrings.get("recent_searches_title", currentLang),
                    tint = textSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = AppStrings.get("recent_searches_title", currentLang),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = textSecondary,
                    fontSize = 11.sp
                )
                recentSearches.forEach { queryItem ->
                    val isMatched = searchQuery == queryItem
                    Surface(
                        shape = CircleShape,
                        color = if (isMatched) {
                            primaryAccent.copy(alpha = if (effectiveDark) 0.20f else 0.12f)
                        } else {
                            chipSurface
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isMatched) primaryAccent.copy(alpha = 0.55f) else chipBorder
                        ),
                        onClick = { onSelectRecentSearch(queryItem) },
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = queryItem,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = if (isMatched) primaryAccent else textPrimary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { onRemoveRecentSearch(queryItem) },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = AppStrings.get("delete", currentLang),
                                    tint = textSecondary,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
                TextButton(
                    onClick = onClearRecentSearches,
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text = AppStrings.get("clear_btn", currentLang),
                        style = MaterialTheme.typography.labelSmall,
                        color = CoralRedAccent,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardQuickActionRow(
    urgentCount: Int,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    onAddPersonnel: () -> Unit,
    onOpenUrgentAlerts: () -> Unit,
    onOpenDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val alertColor = if (effectiveDark) Color(0xFFF87171) else RedDanger

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Primary Teal Glossy Glass CTA: "+ Add person"
        val primaryShape = RoundedCornerShape(16.dp)
        val onPrimary = contrastOnAccent(primaryAccent)
        val primaryGrad = Brush.verticalGradient(
            listOf(
                primaryAccent,
                primaryAccent.copy(alpha = 0.88f)
            )
        )
        Box(
            modifier = Modifier
                .weight(1.06f)
                .defaultMinSize(minHeight = 52.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = primaryShape,
                    clip = false,
                    ambientColor = primaryAccent.copy(alpha = 0.25f),
                    spotColor = primaryAccent.copy(alpha = 0.35f)
                )
                .clip(primaryShape)
                .background(primaryGrad)
                .border(
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                    primaryShape
                )
                .clickable(onClick = onAddPersonnel)
                .testTag("action_btn_add_personnel"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = AppStrings.get("add_person_btn", currentLang),
                    color = onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 2. Frosted Glass Button: "Urgent alerts"
        QuickActionGlassTile(
            label = AppStrings.get("urgent_alerts_short", currentLang),
            icon = Icons.Outlined.Notifications,
            iconTint = alertColor,
            textColor = alertColor,
            effectiveDark = effectiveDark,
            badgeCount = urgentCount,
            testTag = "action_btn_urgent_alerts",
            onClick = onOpenUrgentAlerts,
            modifier = Modifier.weight(1f)
        )

        // 3. Frosted Glass Button: "Room DB"
        QuickActionGlassTile(
            label = AppStrings.get("nav_database", currentLang),
            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
            iconTint = textPrimary,
            textColor = textPrimary,
            effectiveDark = effectiveDark,
            badgeCount = 0,
            testTag = "action_btn_room_db",
            onClick = onOpenDatabase,
            modifier = Modifier.weight(0.94f)
        )
    }
}

@Composable
private fun QuickActionGlassTile(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    textColor: Color,
    effectiveDark: Boolean,
    badgeCount: Int,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileShape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .liquidGlass(
                shape = tileShape,
                elevation = 3.dp,
                isDark = effectiveDark,
                cornerRadius = 16.dp
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .background(iconTint, CircleShape)
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AllPersonsSummaryBanner(
    stats: DashboardStats,
    currentLang: AppLanguage,
    animationsEnabled: Boolean = true,
    onOpenAllPersons: () -> Unit,
    modifier: Modifier = Modifier
) {
    val onDutyFraction = if (stats.total > 0) {
        (stats.onDuty.toFloat() / stats.total.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgress = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = onDutyFraction,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            label = "bannerProgressAnim"
        ).value
    } else {
        onDutyFraction
    }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val bannerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF064E3B),
            Color(0xFF065F46)
        )
    )
    val urgentCount = stats.returningToday + stats.overdue
    val summaryDescription = "${stats.onDuty} ${AppStrings.get("banner_on_duty_short", currentLang)} • " +
        "${stats.atHome} ${AppStrings.get("banner_on_leave_short", currentLang)} • " +
        "$urgentCount ${AppStrings.get("banner_alerts_short", currentLang)}"

    Surface(
        onClick = onOpenAllPersons,
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Color(0x336EE7B7)),
        modifier = modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 88.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(bannerBrush, RoundedCornerShape(22.dp))
            .testTag("all_persons_summary_banner")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = AppStrings.get("all_persons_title", currentLang),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.18f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${stats.total}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = AppStrings.get("all_persons_title", currentLang),
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("all_persons_dotted_route"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatusEndpointBadge(
                    count = stats.onDuty,
                    dotColor = Color(0xFF34D399)
                )

                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                ) {
                    val y = size.height / 2f
                    val startX = if (isRtl) size.width - 4.dp.toPx() else 4.dp.toPx()
                    val endX = if (isRtl) 4.dp.toPx() else size.width - 4.dp.toPx()
                    val span = endX - startX
                    val dashStep = 6.dp.toPx()
                    val dashGap = 5.dp.toPx()

                    drawLine(
                        color = Color.White.copy(alpha = 0.30f),
                        start = Offset(startX, y),
                        end = Offset(endX, y),
                        strokeWidth = 2.2.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashStep, dashGap), 0f)
                    )

                    val activeEndX = startX + span * animatedProgress
                    if (animatedProgress > 0.01f) {
                        drawLine(
                            color = Color(0xFF6EE7B7),
                            start = Offset(startX, y),
                            end = Offset(activeEndX, y),
                            strokeWidth = 2.8.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashStep, dashGap), 0f)
                        )
                    }

                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(activeEndX, y)
                    )
                }

                StatusEndpointBadge(
                    count = stats.atHome,
                    dotColor = Color(0xFFA7F3D0)
                )
            }

            Text(
                text = summaryDescription,
                color = Color(0xFFD1FAE5),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("all_persons_summary_text")
            )
        }
    }
}

@Composable
private fun StatusEndpointBadge(
    count: Int,
    dotColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = "$count",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
