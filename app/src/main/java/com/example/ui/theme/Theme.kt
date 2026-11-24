package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalLivingLuxuryMode = staticCompositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = CyanGlow,
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF134E4A),
    onPrimaryContainer = Color(0xFFCCFBF1),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = AmberWarning,
    onTertiary = Color.Black,
    background = DarkNavyBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF263349),
    error = RedDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantCyan,
    onPrimary = Color.White,
    primaryContainer = CyanContainer,
    onPrimaryContainer = OnCyanContainer,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = SlateTextPrimary,
    tertiary = AmberWarning,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = NeumorphicBackgroundLight,
    onBackground = SlateTextPrimary,
    surface = NeumorphicCardLight,
    onSurface = SlateTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateTextSecondary,
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9),
    error = CoralRedAccent,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = OnRedContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    themePreset: AppThemePreset? = null,
    livingLuxuryMode: Boolean = false,
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

    CompositionLocalProvider(
        LocalThemePreset provides themePreset,
        LocalLivingLuxuryMode provides livingLuxuryMode
    ) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
