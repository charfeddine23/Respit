package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VibrantCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF006064),
    onPrimaryContainer = Color(0xFFE0F7FA),
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4C1D95),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = AmberWarning,
    onTertiary = Color.Black,
    background = DarkNavyBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    error = RedDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlueAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = VibrantCyan,
    onSecondary = Color.White,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = OnCyanContainer,
    tertiary = AmberWarning,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = NeumorphicBackgroundLight,
    onBackground = SlateTextPrimary,
    surface = NeumorphicCardLight,
    onSurface = SlateTextPrimary,
    surfaceVariant = Color(0xFFEDF2F7),
    onSurfaceVariant = SlateTextSecondary,
    outline = Color(0x3394A3B8),
    outlineVariant = Color(0x22CBD5E1),
    error = CoralRedAccent,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = OnRedContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    themePreset: AppThemePreset? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (themePreset != null) {
        if (themePreset.isDark) {
            darkColorScheme(
                primary = themePreset.primary,
                onPrimary = themePreset.onPrimary,
                primaryContainer = themePreset.primary.copy(alpha = 0.24f),
                onPrimaryContainer = themePreset.textPrimary,
                secondary = themePreset.secondary,
                onSecondary = themePreset.onSecondary,
                secondaryContainer = themePreset.secondary.copy(alpha = 0.22f),
                onSecondaryContainer = themePreset.textPrimary,
                tertiary = themePreset.tertiary,
                onTertiary = themePreset.onTertiary,
                background = themePreset.backgroundMid,
                onBackground = themePreset.textPrimary,
                surface = themePreset.surfacePrimary,
                onSurface = themePreset.textPrimary,
                surfaceVariant = themePreset.surfaceVariant,
                onSurfaceVariant = themePreset.textSecondary,
                outline = themePreset.borderAccent,
                outlineVariant = themePreset.borderHighlight,
                error = RedDanger,
                onError = Color.White
            )
        } else {
            lightColorScheme(
                primary = themePreset.primary,
                onPrimary = themePreset.onPrimary,
                primaryContainer = themePreset.primary.copy(alpha = 0.14f),
                onPrimaryContainer = themePreset.textPrimary,
                secondary = themePreset.secondary,
                onSecondary = themePreset.onSecondary,
                secondaryContainer = themePreset.secondary.copy(alpha = 0.14f),
                onSecondaryContainer = themePreset.textPrimary,
                tertiary = themePreset.tertiary,
                onTertiary = themePreset.onTertiary,
                background = themePreset.backgroundMid,
                onBackground = themePreset.textPrimary,
                surface = themePreset.surfacePrimary,
                onSurface = themePreset.textPrimary,
                surfaceVariant = themePreset.surfaceVariant,
                onSurfaceVariant = themePreset.textSecondary,
                outline = themePreset.borderAccent,
                outlineVariant = themePreset.borderHighlight,
                error = CoralRedAccent,
                onError = Color.White,
                errorContainer = RedContainer,
                onErrorContainer = OnRedContainer
            )
        }
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }

    CompositionLocalProvider(LocalThemePreset provides themePreset) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
