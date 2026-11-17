package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.R
import com.example.data.AppThemeMode
import com.example.data.AppThemeStyle
import com.example.ui.DashboardStats
import com.example.ui.UndoAction
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.ThemeEngine
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal enum class SettingsDestination {
    HOME,
    LANGUAGE,
    APPEARANCE,
    SECURITY,
    NOTIFICATIONS,
    BACKUP_RESTORE,
    MORE
}

@Composable
internal fun SettingsSubpageScaffold(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    accentColor: Color,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    pageTag: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val activeThemePreset = LocalThemePreset.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag(pageTag),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = activeThemePreset?.surfacePrimary ?: if (isDarkMode) Color(0xFF1E293B) else Color.White,
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(
                    if (isDarkMode) listOf(Color(0x38FFFFFF), accentColor.copy(alpha = 0.45f))
                    else listOf(Color.White, accentColor.copy(alpha = 0.50f))
                )
            ),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val backInteraction = remember { MutableInteractionSource() }
                val backPressed by backInteraction.collectIsPressedAsState()
                val backScale by animateFloatAsState(
                    targetValue = if (backPressed) 0.90f else 1f,
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                    label = "subpageBackScale"
                )
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBack()
                    },
                    interactionSource = backInteraction,
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = backScale
                            scaleY = backScale
                        }
                        .clip(CircleShape)
                        .testTag("drawer_settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = AppStrings.get("back", currentLang),
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = activeThemePreset?.textPrimary ?: if (isDarkMode) Color.White else Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = activeThemePreset?.textSecondary ?: if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
internal fun LanguageSettingsSubpage(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    onSelectLanguage: (AppLanguage) -> Unit,
    onBack: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    SettingsSubpageScaffold(
        title = AppStrings.get("settings_cat_language", currentLang),
        subtitle = AppStrings.get("select_language", currentLang),
        icon = Icons.Default.Language,
        accentColor = accent,
        currentLang = currentLang,
        isDarkMode = isDarkMode,
        haptic = haptic,
        pageTag = "drawer_language_page",
        onBack = onBack
    ) {
        CleanSettingsCard(isDarkMode = isDarkMode) {
            Text(
                text = AppStrings.get("lang_page_hint", currentLang),
                fontSize = 11.5.sp,
                lineHeight = 17.sp,
                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
        }

        AppLanguage.entries.forEach { lang ->
            val selected = currentLang == lang
            val langBadge = when (lang) {
                AppLanguage.ARABIC -> "AR"
                AppLanguage.AMAZIGH -> "DZ"
                AppLanguage.FRENCH -> "FR"
                AppLanguage.ENGLISH -> "EN"
            }
            val langSubtitle = when (lang) {
                AppLanguage.ARABIC -> AppStrings.get("lang_arabic_subtitle", currentLang)
                AppLanguage.AMAZIGH -> AppStrings.get("lang_amazigh_subtitle", currentLang)
                AppLanguage.FRENCH -> AppStrings.get("lang_french_subtitle", currentLang)
                AppLanguage.ENGLISH -> AppStrings.get("lang_english_subtitle", currentLang)
            }
            val interaction = remember { MutableInteractionSource() }
            val pressed by interaction.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (pressed) 0.97f else 1f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
                label = "langOptionScale_${lang.code}"
            )
            val cardColor by animateColorAsState(
                targetValue = when {
                    selected -> accent.copy(alpha = if (isDarkMode) 0.20f else 0.10f)
                    isDarkMode -> Color(0xFF1E293B)
                    else -> Color.White
                },
                animationSpec = tween(180),
                label = "langOptionBg_${lang.code}"
            )

            Surface(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectLanguage(lang)
                },
                interactionSource = interaction,
                shape = RoundedCornerShape(16.dp),
                color = cardColor,
                border = BorderStroke(
                    if (selected) 1.5.dp else 1.dp,
                    if (selected) accent else if (isDarkMode) Color(0x33475569) else Color(0x38CBD5E1)
                ),
                shadowElevation = if (selected) 2.dp else 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .testTag("lang_option_${lang.code}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) accent else accent.copy(alpha = if (isDarkMode) 0.18f else 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = langBadge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selected) Color.White else accent
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lang.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                        Text(
                            text = langSubtitle,
                            fontSize = 11.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }

                    if (selected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = AppStrings.get("theme_engine_active", currentLang),
                            tint = accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AppearanceSettingsSubpage(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    currentThemeMode: AppThemeMode,
    currentThemeStyle: AppThemeStyle,
    selectedThemeEngineId: String,
    isDynamicAnimationsEnabled: Boolean,
    isLivingLuxuryMode: Boolean,
    isFocusMode: Boolean,
    haptic: HapticFeedback,
    onToggleTheme: () -> Unit,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSelectThemeStyle: (AppThemeStyle) -> Unit,
    onOpenThemeEngine: () -> Unit,
    onToggleDynamicAnimations: (Boolean) -> Unit,
    onToggleLivingLuxuryMode: (Boolean) -> Unit,
    onToggleFocusMode: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activeThemePreset = LocalThemePreset.current
    val accentCyan = Color(0xFF00BCD4)
    val dividerColor = if (isDarkMode) Color(0x28475569) else Color(0x30CBD5E1)

    SettingsSubpageScaffold(
        title = AppStrings.get("settings_cat_appearance", currentLang),
        subtitle = AppStrings.get("settings_cat_appearance_sub", currentLang),
        icon = Icons.Default.Palette,
        accentColor = accentCyan,
        currentLang = currentLang,
        isDarkMode = isDarkMode,
        haptic = haptic,
        pageTag = "drawer_appearance_page",
        onBack = onBack
    ) {
        // Group 1: Dark mode + Display Theme + Interface & Design Style + Theme Engine
        CleanSettingsCard(isDarkMode = isDarkMode) {
            // 1. Dark mode quick switch
            TactileSwitchSettingRow(
                icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                iconTint = if (isDarkMode) Color(0xFFFBBF24) else MaterialTheme.colorScheme.primary,
                title = AppStrings.get("dark_mode_title", currentLang),
                subtitle = AppStrings.get(
                    if (isDarkMode) "drawer_dark_active" else "drawer_light_active",
                    currentLang
                ),
                checked = isDarkMode,
                trackColor = MaterialTheme.colorScheme.primary,
                isDarkMode = isDarkMode,
                haptic = haptic,
                rowTag = "drawer_theme_toggle",
                onToggle = { onToggleTheme() }
            )

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            // 2. Display Theme (Light / Dark)
            Text(
                text = AppStrings.get("theme_title", currentLang),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileSegmentedOption(
                    label = AppStrings.get("theme_light_btn", currentLang),
                    icon = Icons.Default.LightMode,
                    selected = currentThemeMode == AppThemeMode.LIGHT,
                    accentColor = Color(0xFFF59E0B),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = { onSelectThemeMode(AppThemeMode.LIGHT) },
                    modifier = Modifier.weight(1f)
                )
                TactileSegmentedOption(
                    label = AppStrings.get("theme_dark_btn", currentLang),
                    icon = Icons.Default.DarkMode,
                    selected = currentThemeMode == AppThemeMode.DARK,
                    accentColor = Color(0xFF38BDF8),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = { onSelectThemeMode(AppThemeMode.DARK) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Interface & Design Style
            Text(
                text = AppStrings.get("theme_style_title", currentLang),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileSegmentedOption(
                    label = AppStrings.get("theme_style_glass", currentLang),
                    icon = Icons.Default.AutoAwesome,
                    selected = currentThemeStyle == AppThemeStyle.GLASSMORPHISM,
                    accentColor = accentCyan,
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = { onSelectThemeStyle(AppThemeStyle.GLASSMORPHISM) },
                    modifier = Modifier.weight(1f)
                )
                TactileSegmentedOption(
                    label = AppStrings.get("theme_style_neumorph", currentLang),
                    icon = Icons.Default.Layers,
                    selected = currentThemeStyle == AppThemeStyle.NEUMORPHISM,
                    accentColor = Color(0xFF6366F1),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = { onSelectThemeStyle(AppThemeStyle.NEUMORPHISM) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            // 4. Theme Engine (60 Pro Palettes)
            val engineAccent = activeThemePreset?.primary ?: accentCyan
            val engineInteraction = remember { MutableInteractionSource() }
            val enginePressed by engineInteraction.collectIsPressedAsState()
            val engineScale by animateFloatAsState(
                targetValue = if (enginePressed) 0.97f else 1f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
                label = "themeEngineRowScale"
            )
            Surface(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onOpenThemeEngine()
                },
                interactionSource = engineInteraction,
                shape = RoundedCornerShape(14.dp),
                color = engineAccent.copy(alpha = if (isDarkMode) 0.14f else 0.08f),
                border = BorderStroke(
                    1.1.dp,
                    Brush.linearGradient(
                        listOf(
                            engineAccent.copy(alpha = 0.55f),
                            (activeThemePreset?.secondary ?: Color(0xFF8B5CF6)).copy(alpha = 0.45f)
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp)
                    .graphicsLayer {
                        scaleX = engineScale
                        scaleY = engineScale
                    }
                    .testTag("drawer_theme_engine_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        engineAccent.copy(alpha = 0.28f),
                                        (activeThemePreset?.secondary ?: Color(0xFF8B5CF6)).copy(alpha = 0.24f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = engineAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = AppStrings.get("theme_engine_title", currentLang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = activeThemePreset?.textPrimary
                                    ?: if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = engineAccent.copy(alpha = 0.20f)
                            ) {
                                Text(
                                    text = "${ThemeEngine.allThemes.size}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = engineAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                        val previewPreset = ThemeEngine.findById(selectedThemeEngineId)
                        Text(
                            text = previewPreset?.name ?: AppStrings.get("theme_engine_sub", currentLang),
                            fontSize = 10.5.sp,
                            color = if (previewPreset != null) engineAccent
                            else (activeThemePreset?.textSecondary ?: if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)),
                            fontWeight = if (previewPreset != null) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (previewPreset != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                previewPreset.previewSwatches.forEach { swatch ->
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(swatch)
                                    )
                                }
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = engineAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Group 2: Dynamic Animations & Living UI + Living Breathing Luxury + Focus Mode
        CleanSettingsCard(isDarkMode = isDarkMode) {
            // 5. Dynamic Animations & Living UI
            TactileSwitchSettingRow(
                icon = Icons.Default.Speed,
                iconTint = Color(0xFFA855F7),
                title = AppStrings.get("dynamic_animations_title", currentLang),
                subtitle = AppStrings.get(
                    if (isDynamicAnimationsEnabled) "dynamic_animations_active" else "dynamic_animations_disabled",
                    currentLang
                ),
                subtitleColor = if (isDynamicAnimationsEnabled) Color(0xFF10B981) else Color(0xFFF59E0B),
                checked = isDynamicAnimationsEnabled,
                trackColor = Color(0xFFA855F7),
                isDarkMode = isDarkMode,
                haptic = haptic,
                switchTag = "drawer_dynamic_animations_toggle",
                onToggle = { enabled ->
                    onToggleDynamicAnimations(enabled)
                    Toast.makeText(
                        context,
                        AppStrings.get(
                            if (enabled) "dynamic_animations_active" else "dynamic_animations_disabled",
                            currentLang
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(4.dp))

            // 6. Living Breathing Luxury
            val luxuryAccent = activeThemePreset?.primary ?: Color(0xFFF59E0B)
            TactileSwitchSettingRow(
                icon = Icons.Default.AutoAwesome,
                iconTint = luxuryAccent,
                title = AppStrings.get("living_luxury_title", currentLang),
                subtitle = AppStrings.get(
                    if (isLivingLuxuryMode) "living_luxury_active" else "living_luxury_inactive",
                    currentLang
                ),
                subtitleColor = if (isLivingLuxuryMode) luxuryAccent else null,
                checked = isLivingLuxuryMode,
                trackColor = luxuryAccent,
                isDarkMode = isDarkMode,
                haptic = haptic,
                switchTag = "drawer_living_luxury_toggle",
                onToggle = { enabled ->
                    onToggleLivingLuxuryMode(enabled)
                    Toast.makeText(
                        context,
                        AppStrings.get(
                            if (enabled) "living_luxury_enabled_toast" else "living_luxury_disabled_toast",
                            currentLang
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(4.dp))

            // 7. Focus Mode
            TactileSwitchSettingRow(
                icon = Icons.Default.CenterFocusStrong,
                iconTint = accentCyan,
                title = AppStrings.get("focus_mode_title", currentLang),
                subtitle = AppStrings.get(
                    if (isFocusMode) "focus_mode_active" else "focus_mode_inactive",
                    currentLang
                ),
                checked = isFocusMode,
                trackColor = accentCyan,
                isDarkMode = isDarkMode,
                haptic = haptic,
                switchTag = "drawer_focus_mode_toggle",
                onToggle = { onToggleFocusMode() }
            )
        }
    }
}

@Composable
internal fun SecuritySettingsSubpage(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    isBiometricEnabled: Boolean,
    isNoScreenshotEnabled: Boolean,
    haptic: HapticFeedback,
    onToggleBiometric: (Boolean) -> Unit,
    onToggleNoScreenshot: (Boolean) -> Unit,
    onLockNow: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val accentBlue = Color(0xFF3B82F6)
    val accentCyan = Color(0xFF00BCD4)
    val dividerColor = if (isDarkMode) Color(0x28475569) else Color(0x30CBD5E1)

    SettingsSubpageScaffold(
        title = AppStrings.get("settings_cat_security", currentLang),
        subtitle = AppStrings.get("settings_cat_security_sub", currentLang),
        icon = Icons.Default.Security,
        accentColor = accentBlue,
        currentLang = currentLang,
        isDarkMode = isDarkMode,
        haptic = haptic,
        pageTag = "drawer_security_page",
        onBack = onBack
    ) {
        CleanSettingsCard(isDarkMode = isDarkMode) {
            TactileSwitchSettingRow(
                icon = Icons.Default.Fingerprint,
                iconTint = accentCyan,
                title = AppStrings.get("biometric_lock_title", currentLang),
                subtitle = AppStrings.get(
                    if (isBiometricEnabled) "biometric_lock_active_sub" else "biometric_lock_inactive_sub",
                    currentLang
                ),
                subtitleColor = if (isBiometricEnabled) Color(0xFF10B981) else null,
                checked = isBiometricEnabled,
                trackColor = accentCyan,
                isDarkMode = isDarkMode,
                haptic = haptic,
                switchTag = "drawer_biometric_lock_toggle",
                onToggle = { isChecked ->
                    onToggleBiometric(isChecked)
                    Toast.makeText(
                        context,
                        AppStrings.get(
                            if (isChecked) "biometric_lock_enabled_toast" else "biometric_lock_disabled_toast",
                            currentLang
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            AnimatedVisibility(
                visible = isBiometricEnabled,
                enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    TactileActionPillButton(
                        text = AppStrings.get("lock_now_btn", currentLang),
                        icon = Icons.Default.Lock,
                        accentColor = accentCyan,
                        isFilled = false,
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = onLockNow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drawer_lock_now_button")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(6.dp))

            TactileSwitchSettingRow(
                icon = Icons.Default.VisibilityOff,
                iconTint = Color(0xFF10B981),
                title = AppStrings.get("no_screenshot_title", currentLang),
                subtitle = AppStrings.get(
                    if (isNoScreenshotEnabled) "no_screenshot_active_sub" else "no_screenshot_inactive_sub",
                    currentLang
                ),
                subtitleColor = if (isNoScreenshotEnabled) Color(0xFF10B981) else null,
                checked = isNoScreenshotEnabled,
                trackColor = Color(0xFF10B981),
                isDarkMode = isDarkMode,
                haptic = haptic,
                switchTag = "drawer_no_screenshot_toggle",
                onToggle = { isChecked ->
                    onToggleNoScreenshot(isChecked)
                    Toast.makeText(
                        context,
                        AppStrings.get(
                            if (isChecked) "no_screenshot_enabled_toast" else "no_screenshot_disabled_toast",
                            currentLang
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
internal fun NotificationsSettingsSubpage(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    dutyThreshold: Int,
    haptic: HapticFeedback,
    onSaveThreshold: (Int) -> Unit,
    onCheckNotifications: () -> Unit,
    onBack: () -> Unit
) {
    val accent = Color(0xFF0284C7)

    SettingsSubpageScaffold(
        title = AppStrings.get("settings_cat_notifications", currentLang),
        subtitle = AppStrings.get("settings_cat_notifications_sub", currentLang),
        icon = Icons.Default.NotificationsActive,
        accentColor = accent,
        currentLang = currentLang,
        isDarkMode = isDarkMode,
        haptic = haptic,
        pageTag = "drawer_notifications_page",
        onBack = onBack
    ) {
        // 1. Leave Eligibility Threshold (Days)
        CleanSettingsCard(isDarkMode = isDarkMode) {
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.get("duty_threshold_title", currentLang),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = accent.copy(alpha = if (isDarkMode) 0.20f else 0.12f),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "$dutyThreshold ${AppStrings.get("days", currentLang)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = AppStrings.get("duty_threshold_desc", currentLang),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Slider(
                value = dutyThreshold.toFloat(),
                onValueChange = { newVal ->
                    val rounded = newVal.toInt()
                    if (rounded != dutyThreshold) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onSaveThreshold(rounded)
                },
                valueRange = 10f..180f,
                steps = 33,
                colors = SliderDefaults.colors(
                    thumbColor = accent,
                    activeTrackColor = accent,
                    inactiveTrackColor = if (isDarkMode) Color(0x33475569) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.testTag("drawer_threshold_slider")
            )
        }

        // 2. Check Notifications
        DrawerNavigationItem(
            icon = Icons.Default.NotificationsActive,
            iconTint = accent,
            title = AppStrings.get("check_notifications", currentLang),
            subtitle = AppStrings.get("drawer_notif_sub", currentLang),
            isDarkMode = isDarkMode,
            haptic = haptic,
            onClick = onCheckNotifications,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            },
            testTag = "drawer_check_notifications_btn"
        )
    }
}

@Composable
internal fun BackupRestoreSettingsSubpage(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    isDriveSyncEnabled: Boolean,
    lastDriveBackupTime: Long,
    haptic: HapticFeedback,
    onOpenBackup: () -> Unit,
    onExportPdf: () -> Unit,
    onOpenRoomDatabase: () -> Unit,
    onToggleDriveSync: (Boolean) -> Unit,
    onBackupToDrive: () -> Unit,
    onRestoreFromDrive: () -> Unit,
    onBack: () -> Unit
) {
    val accent = Color(0xFF7C3AED)
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    SettingsSubpageScaffold(
        title = AppStrings.get("settings_cat_backup_restore", currentLang),
        subtitle = AppStrings.get("settings_cat_backup_restore_sub", currentLang),
        icon = Icons.Default.CloudSync,
        accentColor = accent,
        currentLang = currentLang,
        isDarkMode = isDarkMode,
        haptic = haptic,
        pageTag = "drawer_backup_restore_page",
        onBack = onBack
    ) {
        // 1. Backup & Data
        DrawerNavigationItem(
            icon = Icons.Default.Share,
            iconTint = Color(0xFF7C3AED),
            title = AppStrings.get("export_data", currentLang),
            subtitle = AppStrings.get("drawer_data_sub", currentLang),
            isDarkMode = isDarkMode,
            haptic = haptic,
            onClick = onOpenBackup,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            },
            testTag = "drawer_backup_item"
        )

        // 2. Export PDF Report
        DrawerNavigationItem(
            icon = Icons.Default.PictureAsPdf,
            iconTint = Color(0xFFDC2626),
            title = AppStrings.get("export_pdf", currentLang),
            subtitle = AppStrings.get("drawer_pdf_sub", currentLang),
            isDarkMode = isDarkMode,
            haptic = haptic,
            onClick = onExportPdf,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            },
            testTag = "drawer_pdf_item"
        )

        // 3. Room DB
        DrawerNavigationItem(
            icon = Icons.Default.Storage,
            iconTint = Color(0xFF0284C7),
            title = AppStrings.get("nav_database", currentLang),
            subtitle = AppStrings.get("room_db_sub", currentLang),
            isDarkMode = isDarkMode,
            haptic = haptic,
            onClick = onOpenRoomDatabase,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            },
            testTag = "drawer_room_db_item"
        )

        // 4. Google Drive Backup
        CleanSettingsCard(isDarkMode = isDarkMode) {
            TactileSwitchSettingRow(
                icon = Icons.Default.CloudSync,
                iconTint = Color(0xFF4285F4),
                title = AppStrings.get("drive_backup_title", currentLang),
                subtitle = "${AppStrings.get("drive_last_backup", currentLang)} ${if (lastDriveBackupTime > 0L) sdf.format(Date(lastDriveBackupTime)) else AppStrings.get("drive_never", currentLang)}",
                checked = isDriveSyncEnabled,
                trackColor = Color(0xFF4285F4),
                isDarkMode = isDarkMode,
                haptic = haptic,
                switchTag = "drawer_drive_sync_toggle",
                onToggle = onToggleDriveSync
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileActionPillButton(
                    text = AppStrings.get("drive_backup_btn", currentLang),
                    icon = Icons.Default.CloudUpload,
                    accentColor = Color(0xFF4285F4),
                    isFilled = true,
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onBackupToDrive,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("drive_backup_btn")
                )
                TactileActionPillButton(
                    text = AppStrings.get("drive_restore_btn", currentLang),
                    icon = Icons.Default.CloudDownload,
                    accentColor = Color(0xFF4285F4),
                    isFilled = false,
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onRestoreFromDrive,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("drive_restore_btn")
                )
            }
        }
    }
}

@Composable
internal fun MoreSettingsSubpage(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    lastUndo: UndoAction?,
    stats: DashboardStats,
    availableUpdateVersion: String?,
    haptic: HapticFeedback,
    onUndo: () -> Unit,
    onCheckUpdate: () -> Unit,
    onOpenDeveloper: () -> Unit,
    onResetSampleData: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val accent = Color(0xFF6366F1)
    val dividerColor = if (isDarkMode) Color(0x28475569) else Color(0x30CBD5E1)

    SettingsSubpageScaffold(
        title = AppStrings.get("settings_cat_more", currentLang),
        subtitle = AppStrings.get("settings_cat_more_sub", currentLang),
        icon = Icons.Default.MoreHoriz,
        accentColor = accent,
        currentLang = currentLang,
        isDarkMode = isDarkMode,
        haptic = haptic,
        pageTag = "drawer_more_page",
        onBack = onBack
    ) {
        // Undo Last Action (when available)
        AnimatedVisibility(
            visible = lastUndo != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DrawerNavigationItem(
                icon = Icons.AutoMirrored.Filled.Undo,
                iconTint = Color(0xFFD97706),
                title = AppStrings.get("undo_btn", currentLang),
                subtitle = lastUndo?.getDescription(currentLang),
                isDarkMode = isDarkMode,
                haptic = haptic,
                onClick = onUndo,
                testTag = "drawer_undo_item"
            )
        }

        // Check for Updates
        DrawerNavigationItem(
            icon = Icons.Default.RocketLaunch,
            iconTint = if (availableUpdateVersion != null) Color(0xFF10B981) else Color(0xFF00BCD4),
            title = AppStrings.get("update_menu_title", currentLang),
            subtitle = if (availableUpdateVersion != null) {
                "${AppStrings.get("update_available_sub", currentLang)} • v$availableUpdateVersion"
            } else {
                "${AppStrings.get("update_menu_sub", currentLang)} (v${BuildConfig.VERSION_NAME})"
            },
            isDarkMode = isDarkMode,
            haptic = haptic,
            onClick = onCheckUpdate,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            },
            testTag = "drawer_update_item"
        )

        // Developer Info Dialog Item
        DrawerNavigationItem(
            icon = Icons.Default.Info,
            iconTint = Color(0xFF0284C7),
            title = AppStrings.get("developer_info", currentLang),
            subtitle = AppStrings.get("drawer_developer_sub", currentLang),
            isDarkMode = isDarkMode,
            haptic = haptic,
            onClick = onOpenDeveloper,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            },
            testTag = "drawer_developer_item"
        )

        // Reset Sample Data Item
        DrawerNavigationItem(
            icon = Icons.Default.Refresh,
            iconTint = Color(0xFF94A3B8),
            title = AppStrings.get("drawer_reset_title", currentLang),
            subtitle = AppStrings.get("drawer_reset_sub", currentLang),
            isDarkMode = isDarkMode,
            haptic = haptic,
            onClick = onResetSampleData,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            },
            testTag = "drawer_reset_item"
        )

        // Quick Stats & Developer WhatsApp Card
        CleanSettingsCard(isDarkMode = isDarkMode) {
            Text(
                text = AppStrings.get("drawer_quick_stats", currentLang),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SidebarStatBadge(AppStrings.get("stat_total", currentLang), "${stats.total}", MaterialTheme.colorScheme.primary, isDarkMode, Modifier.weight(1f))
                SidebarStatBadge(AppStrings.get("stat_on_leave", currentLang), "${stats.atHome}", Color(0xFF0284C7), isDarkMode, Modifier.weight(1f))
                SidebarStatBadge(AppStrings.get("stat_returning_today", currentLang), "${stats.returningToday}", Color(0xFFD97706), isDarkMode, Modifier.weight(1f))
                SidebarStatBadge(AppStrings.get("stat_overdue", currentLang), "${stats.overdue}", Color(0xFFDC2626), isDarkMode, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = dividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Developed by Charfeddine Ka",
                        fontSize = 11.5.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F)
                    )
                    Text(
                        text = "${AppStrings.get("certified_developer", currentLang)} • v${BuildConfig.VERSION_NAME}",
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Default,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }

                val waInteraction = remember { MutableInteractionSource() }
                val waPressed by waInteraction.collectIsPressedAsState()
                val waScale by animateFloatAsState(
                    targetValue = if (waPressed) 0.88f else 1f,
                    animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                    label = "moreWaScale"
                )
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        openDeveloperWhatsApp(context, currentLang)
                    },
                    interactionSource = waInteraction,
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = waScale
                            scaleY = waScale
                        }
                        .clip(CircleShape)
                        .testTag("drawer_developer_whatsapp_button")
                ) {
                    Surface(
                        modifier = Modifier.size(30.dp),
                        shape = CircleShape,
                        color = Color(0xFF25D366).copy(alpha = if (isDarkMode) 0.18f else 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.45f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                contentDescription = AppStrings.get("contact_whatsapp", currentLang),
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CleanSettingsCard(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    val activeThemePreset = LocalThemePreset.current
    val isLivingLuxury = LocalLivingLuxuryMode.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = activeThemePreset?.surfacePrimary ?: if (isDarkMode) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(
            if (isLivingLuxury) 1.3.dp else 1.dp,
            activeThemePreset?.borderAccent?.copy(alpha = if (isLivingLuxury) 0.7f else 0.45f)
                ?: if (isLivingLuxury) Color(0x6638BDF8)
                else if (isDarkMode) Color(0x33475569)
                else Color(0x38CBD5E1)
        ),
        shadowElevation = if (isLivingLuxury) 5.dp else if (isDarkMode) 2.dp else 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
internal fun TactileSegmentedOption(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    accentColor: Color,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "segmentScale"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            selected && isDarkMode -> accentColor.copy(alpha = 0.22f)
            selected && !isDarkMode -> accentColor.copy(alpha = 0.13f)
            isPressed -> accentColor.copy(alpha = 0.08f)
            isDarkMode -> Color(0x280F172A)
            else -> Color(0xFFF8FAFC)
        },
        animationSpec = tween(180),
        label = "segmentBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor else if (isDarkMode) Color(0x38475569) else Color(0x45CBD5E1),
        animationSpec = tween(180),
        label = "segmentBorder"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) accentColor else if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) {
                    if (isDarkMode) accentColor else accentColor.copy(alpha = 0.95f)
                } else {
                    if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF1E293B)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun TactileSwitchSettingRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    subtitleColor: Color? = null,
    checked: Boolean,
    trackColor: Color,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    rowTag: String? = null,
    switchTag: String? = null,
    onToggle: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "switchRowScale"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onToggle(!checked)
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = if (isPressed) {
            iconTint.copy(alpha = if (isDarkMode) 0.12f else 0.08f)
        } else {
            Color.Transparent
        },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .let { if (rowTag != null) it.testTag(rowTag) else it }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Text(
                        text = title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.5.sp,
                        color = subtitleColor ?: if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { newValue ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onToggle(newValue)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = trackColor
                ),
                modifier = if (switchTag != null) Modifier.testTag(switchTag) else Modifier
            )
        }
    }
}

@Composable
internal fun TactileActionPillButton(
    text: String,
    icon: ImageVector,
    accentColor: Color,
    isFilled: Boolean,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "actionPillScale"
    )

    if (isFilled) {
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
            interactionSource = interactionSource,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = modifier
                .height(48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
            interactionSource = interactionSource,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.65f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = accentColor.copy(alpha = if (isDarkMode) 0.10f else 0.06f),
                contentColor = accentColor
            ),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = modifier
                .height(48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode && accentColor == Color(0xFF4285F4)) Color(0xFF8AB4F8) else accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun DrawerNavigationItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String?,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null,
    testTag: String
) {
    val activeThemePreset = LocalThemePreset.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "drawerItemScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) {
            iconTint.copy(alpha = 0.75f)
        } else {
            activeThemePreset?.borderAccent?.copy(alpha = 0.45f)
                ?: if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
        },
        animationSpec = tween(150),
        label = "drawerItemBorder"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isPressed) {
            if (isDarkMode) Color(0xFF263449) else iconTint.copy(alpha = 0.08f)
        } else {
            activeThemePreset?.surfacePrimary
                ?: if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.90f)
        },
        animationSpec = tween(150),
        label = "drawerItemBg"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(if (isPressed) 1.4.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = activeThemePreset?.textPrimary ?: if (isDarkMode) Color.White else Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = activeThemePreset?.textSecondary ?: if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(6.dp))
                trailingContent()
            }
        }
    }
}

@Composable
internal fun SidebarStatBadge(
    label: String,
    value: String,
    color: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = if (isDarkMode) 0.18f else 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = color)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) color else Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
