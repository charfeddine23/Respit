package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AppThemePreset
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.ThemeCategory
import com.example.ui.theme.ThemeEngine
import com.example.util.AppLanguage
import com.example.util.AppStrings

private enum class ThemeFilterTab {
    ALL,
    CLASSIC_DARK,
    CLASSIC_LIGHT,
    LIQUID_GLASS
}

@Composable
fun ThemeEnginePickerDialog(
    selectedThemeId: String,
    isDarkMode: Boolean,
    currentLang: AppLanguage,
    onSelectTheme: (String) -> Unit,
    onResetDefault: () -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val activePreset = LocalThemePreset.current
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(ThemeFilterTab.ALL) }

    val filteredThemes = remember(searchQuery, activeFilter) {
        val q = searchQuery.trim()
        ThemeEngine.allThemes.filter { preset ->
            val matchesCategory = when (activeFilter) {
                ThemeFilterTab.ALL -> true
                ThemeFilterTab.CLASSIC_DARK -> preset.category == ThemeCategory.CLASSIC_DARK
                ThemeFilterTab.CLASSIC_LIGHT -> preset.category == ThemeCategory.CLASSIC_LIGHT
                ThemeFilterTab.LIQUID_GLASS -> preset.category == ThemeCategory.LIQUID_GLASS
            }
            val matchesQuery = q.isEmpty() || preset.name.contains(q, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val dialogSurface = activePreset?.surfacePrimary
        ?: if (isDarkMode) Color(0xFF141B2D) else Color(0xFFF4F8FC)
    val textPrimary = activePreset?.textPrimary
        ?: if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = activePreset?.textSecondary
        ?: if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val accent = activePreset?.primary ?: MaterialTheme.colorScheme.primary
    val borderBrush = Brush.linearGradient(
        listOf(
            activePreset?.borderHighlight ?: Color.White.copy(alpha = if (isDarkMode) 0.25f else 0.95f),
            accent.copy(alpha = 0.65f),
            (activePreset?.secondary ?: accent).copy(alpha = 0.40f)
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = dialogSurface,
            border = BorderStroke(1.3.dp, borderBrush),
            shadowElevation = 14.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 620.dp)
                .fillMaxHeight(0.88f)
                .testTag("theme_engine_picker_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(accent.copy(alpha = 0.16f))
                                .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = AppStrings.get("theme_engine_title", currentLang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = accent.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, accent.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "${ThemeEngine.allThemes.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = accent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = activePreset?.name ?: AppStrings.get("theme_engine_sub", currentLang),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (activePreset != null) accent else textSecondary,
                                fontWeight = if (activePreset != null) FontWeight.Bold else FontWeight.Normal,
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
                            .testTag("theme_engine_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.get("close", currentLang),
                            tint = textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = AppStrings.get("theme_engine_search_hint", currentLang),
                            fontSize = 12.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = accent
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(48.dp)
                            ) {
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
                        focusedBorderColor = accent,
                        unfocusedBorderColor = textSecondary.copy(alpha = 0.28f),
                        focusedContainerColor = if (isDarkMode) Color(0x26000000) else Color.White.copy(alpha = 0.72f),
                        unfocusedContainerColor = if (isDarkMode) Color(0x1C000000) else Color.White.copy(alpha = 0.55f),
                        focusedPlaceholderColor = textSecondary,
                        unfocusedPlaceholderColor = textSecondary,
                        cursorColor = accent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("theme_engine_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val chips = listOf(
                        Triple(ThemeFilterTab.ALL, AppStrings.get("theme_engine_cat_all", currentLang), Icons.Default.Palette),
                        Triple(ThemeFilterTab.CLASSIC_DARK, AppStrings.get("theme_engine_cat_dark", currentLang), Icons.Default.DarkMode),
                        Triple(ThemeFilterTab.CLASSIC_LIGHT, AppStrings.get("theme_engine_cat_light", currentLang), Icons.Default.LightMode),
                        Triple(ThemeFilterTab.LIQUID_GLASS, AppStrings.get("theme_engine_cat_glass", currentLang), Icons.Default.AutoAwesome)
                    )
                    chips.forEach { (tab, label, icon) ->
                        val isSelected = activeFilter == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                activeFilter = tab
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accent.copy(alpha = 0.18f),
                                selectedLabelColor = accent,
                                selectedLeadingIconColor = accent,
                                labelColor = textSecondary,
                                iconColor = textSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = accent,
                                borderColor = textSecondary.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.defaultMinSize(minHeight = 40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 60-Theme Visual Preview Grid
                if (filteredThemes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = textSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = AppStrings.get("theme_engine_no_results", currentLang),
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 148.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .testTag("theme_engine_grid")
                    ) {
                        items(
                            items = filteredThemes,
                            key = { it.id }
                        ) { preset ->
                            ThemePresetPreviewCard(
                                preset = preset,
                                isSelected = selectedThemeId == preset.id,
                                activeLabel = AppStrings.get("theme_engine_active", currentLang),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelectTheme(preset.id)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Row: Reset Default + Done
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onResetDefault()
                        },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, textSecondary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("theme_engine_reset_default_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = textPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("theme_engine_default", currentLang),
                            color = textPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent,
                            contentColor = activePreset?.onPrimary ?: Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("theme_engine_done_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("theme_engine_done", currentLang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemePresetPreviewCard(
    preset: AppThemePreset,
    isSelected: Boolean,
    activeLabel: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "themeCardScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) preset.primary else preset.borderAccent.copy(alpha = 0.45f),
        animationSpec = tween(180),
        label = "themeCardBorder"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = BorderStroke(if (isSelected) 2.2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 112.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag("theme_preset_${preset.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(preset.backgroundTop, preset.backgroundMid, preset.backgroundBottom)
                    )
                )
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Top Mini UI Surface Mockup + Glass/Mode Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 4 Signature Color Swatches
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(preset.primary, preset.secondary, preset.tertiary).forEach { swatch ->
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(swatch)
                                    .border(0.6.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                            )
                        }
                    }

                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = preset.primary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = activeLabel,
                                    tint = preset.onPrimary,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    } else if (preset.isLiquidGlass) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = preset.primary,
                            modifier = Modifier.size(13.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (preset.isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = preset.textSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Miniature Card Preview Strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.verticalGradient(listOf(preset.cardTintTop, preset.cardTintBottom)))
                        .border(0.8.dp, preset.borderHighlight, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(preset.textPrimary.copy(alpha = 0.85f))
                        )
                        Box(
                            modifier = Modifier
                                .size(width = 18.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(preset.primary)
                        )
                    }
                }

                // Theme Exact Name
                Text(
                    text = preset.name,
                    fontSize = 11.5.sp,
                    lineHeight = 14.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    color = preset.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
