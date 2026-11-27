package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
