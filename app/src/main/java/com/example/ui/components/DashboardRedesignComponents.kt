package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
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
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
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
    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else Color(0xFF059669)
    val surfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF151E30) else Color(0xFFFEFFFF)
    val borderColor = preset?.borderAccent?.copy(alpha = 0.28f)
        ?: if (effectiveDark) Color(0x3338BDF8) else Color(0xFFE2E8F0)
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = surfaceColor,
            shadowElevation = if (effectiveDark) 4.dp else 6.dp,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(primaryAccent.copy(alpha = if (effectiveDark) 0.18f else 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = AppStrings.get("search_label", currentLang),
                        tint = primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp),
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
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = AppStrings.get("clear_search", currentLang),
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    onClick = onOpenFilter,
                    shape = CircleShape,
                    color = if (activeFilterCount > 0) {
                        primaryAccent
                    } else if (effectiveDark) {
                        Color.White.copy(alpha = 0.08f)
                    } else {
                        Color(0xFFF1F5F9)
                    },
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
                                tint = if (activeFilterCount > 0) Color.White else primaryAccent,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }
                }
            }
        }

        if (recentSearches.isNotEmpty()) {
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
                    tint = primaryAccent,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = AppStrings.get("recent_searches_title", currentLang),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary,
                    fontSize = 11.sp
                )
                recentSearches.forEach { queryItem ->
                    val isMatched = searchQuery == queryItem
                    Surface(
                        shape = CircleShape,
                        color = if (isMatched) {
                            primaryAccent.copy(alpha = if (effectiveDark) 0.28f else 0.14f)
                        } else {
                            surfaceColor
                        },
                        border = BorderStroke(
                            0.8.dp,
                            if (isMatched) primaryAccent.copy(alpha = 0.65f) else borderColor
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
                                fontWeight = FontWeight.SemiBold,
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
                        fontWeight = FontWeight.Bold
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
    val surfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF151E30) else Color(0xFFFEFFFF)
    val borderColor = preset?.borderHighlight?.copy(alpha = 0.45f)
        ?: if (effectiveDark) Color(0x2838BDF8) else Color(0xFFE2E8F0)
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickActionPillTile(
            label = AppStrings.get("add_personnel", currentLang),
            icon = Icons.Default.Add,
            accent = EmeraldSuccess,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            textColor = textPrimary,
            effectiveDark = effectiveDark,
            badgeCount = 0,
            testTag = "action_btn_add_personnel",
            onClick = onAddPersonnel,
            modifier = Modifier.weight(1f)
        )

        QuickActionPillTile(
            label = AppStrings.get("urgent_alerts_title", currentLang).split("(").first().trim(),
            icon = Icons.Default.NotificationsActive,
            accent = CoralRedAccent,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            textColor = textPrimary,
            effectiveDark = effectiveDark,
            badgeCount = urgentCount,
            testTag = "action_btn_urgent_alerts",
            onClick = onOpenUrgentAlerts,
            modifier = Modifier.weight(1f)
        )

        QuickActionPillTile(
            label = AppStrings.get("nav_database", currentLang),
            icon = Icons.Default.Storage,
            accent = preset?.primary ?: if (effectiveDark) CyanGlow else RoyalBlueAccent,
            surfaceColor = surfaceColor,
            borderColor = borderColor,
            textColor = textPrimary,
            effectiveDark = effectiveDark,
            badgeCount = 0,
            testTag = "action_btn_room_db",
            onClick = onOpenDatabase,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionPillTile(
    label: String,
    icon: ImageVector,
    accent: Color,
    surfaceColor: Color,
    borderColor: Color,
    textColor: Color,
    effectiveDark: Boolean,
    badgeCount: Int,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = surfaceColor,
        shadowElevation = if (effectiveDark) 3.dp else 4.dp,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = if (effectiveDark) 0.22f else 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .background(accent, CircleShape)
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        color = Color.White,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold
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
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            label = "bannerProgressAnim"
        ).value
    } else {
        onDutyFraction
    }

    val pulsePhase: Float = if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "bannerRoutePulse")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "routePulsePhase"
        ).value
    } else {
        0f
    }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val bannerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF064E3B),
            Color(0xFF065F46),
            Color(0xFF047857)
        )
    )
    val urgentCount = stats.returningToday + stats.overdue
    val summaryDescription = "${stats.onDuty} ${AppStrings.get("banner_on_duty_short", currentLang)} • " +
        "${stats.atHome} ${AppStrings.get("banner_on_leave_short", currentLang)} • " +
        "$urgentCount ${AppStrings.get("banner_alerts_short", currentLang)}"

    Surface(
        onClick = onOpenAllPersons,
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0x3D6EE7B7)),
        modifier = modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 92.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bannerBrush, RoundedCornerShape(24.dp))
            .testTag("all_persons_summary_banner")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Header: "All persons" + total pill + circular arrow indicator
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
                            .background(Color.White.copy(alpha = 0.16f)),
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
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.20f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${stats.total}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
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

            // Middle: Horizontal dotted progress/route indicator between two status endpoints
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
                        .height(14.dp)
                ) {
                    val y = size.height / 2f
                    val startX = if (isRtl) size.width - 4.dp.toPx() else 4.dp.toPx()
                    val endX = if (isRtl) 4.dp.toPx() else size.width - 4.dp.toPx()
                    val span = endX - startX
                    val dashStep = 6.dp.toPx()
                    val dashGap = 5.dp.toPx()

                    // Background dotted track
                    drawLine(
                        color = Color.White.copy(alpha = 0.32f),
                        start = Offset(startX, y),
                        end = Offset(endX, y),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashStep, dashGap), 0f)
                    )

                    // Active dotted segment up to onDutyFraction
                    val activeEndX = startX + span * animatedProgress
                    if (animatedProgress > 0.01f) {
                        drawLine(
                            color = Color(0xFF6EE7B7),
                            start = Offset(startX, y),
                            end = Offset(activeEndX, y),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashStep, dashGap), 0f)
                        )
                    }

                    // Progress anchor node
                    drawCircle(
                        color = Color.White,
                        radius = 4.2.dp.toPx(),
                        center = Offset(activeEndX, y)
                    )

                    // Subtle traveling pulse dot along active route when animations enabled
                    if (animationsEnabled && animatedProgress > 0.08f) {
                        val travelX = startX + span * animatedProgress * pulsePhase
                        drawCircle(
                            color = Color(0xFFA7F3D0).copy(alpha = 1f - pulsePhase * 0.7f),
                            radius = 3.dp.toPx(),
                            center = Offset(travelX, y)
                        )
                    }
                }

                StatusEndpointBadge(
                    count = stats.atHome,
                    dotColor = Color(0xFFA7F3D0)
                )
            }

            // Bottom concise summary description line
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
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp
        )
    }
}
