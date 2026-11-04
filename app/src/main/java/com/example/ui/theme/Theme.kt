package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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
    primary = VibrantCyan,
    onPrimary = Color.White,
    primaryContainer = CyanContainer,
    onPrimaryContainer = OnCyanContainer,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = PurpleContainer,
    onSecondaryContainer = OnPurpleContainer,
    tertiary = AmberWarning,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = Color(0xFFF8FAFC),
    onBackground = SlateTextPrimary,
    surface = GlassCardSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = Color(0xF2FFFFFF),
    onSurfaceVariant = SlateTextSecondary,
    outline = GlassCardBorder,
    outlineVariant = Color(0xFFE2E8F0),
    error = RedDanger,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = OnRedContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false, // Keep custom Light Mode Aurora palette strictly consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
