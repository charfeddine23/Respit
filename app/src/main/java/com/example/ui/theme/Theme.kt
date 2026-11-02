package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkyAccent,
    onPrimary = Color.White,
    primaryContainer = NavyPrimary,
    onPrimaryContainer = Color.White,
    secondary = SkyAccent,
    onSecondary = Color.White,
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
    primary = SkyAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = NavyPrimary,
    onSecondary = Color.White,
    background = SkyDayBackground,
    onBackground = SlateTextPrimary,
    surface = CardSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    outlineVariant = Color(0xFFE2E8F0),
    error = RedDanger,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false, // Keep professional custom palette consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
