package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object LiquidGlassDefaults {
    val CardShape = RoundedCornerShape(22.dp)
    val PillShape = RoundedCornerShape(50.dp)
    val SmallShape = RoundedCornerShape(16.dp)
    val DialogShape = RoundedCornerShape(26.dp)
}

/**
 * Applies the smooth Aurora mesh gradient background across screens:
 * Flows seamlessly from light soft cyan/blue (#E0F7FA) at the top,
 * through clean white (#FFFFFF) in the center,
 * to a gentle muted purple/blue (#F3E5F5) at the bottom.
 */
@Composable
fun Modifier.auroraBackground(isDark: Boolean = false): Modifier {
    val brush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF0F172A),
                Color(0xFF1E293B),
                Color(0xFF0B1120)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                AuroraCyanTop,        // #E0F7FA (Light soft cyan/blue)
                AuroraCyanLight,      // Soft intermediate blend
                AuroraWhiteMid,       // #FFFFFF (Clean white)
                AuroraPurpleLight,    // Soft intermediate lilac
                AuroraPurpleBottom    // #F3E5F5 (Gentle muted purple/blue)
            )
        )
    }
    return this.background(brush)
}

/**
 * Glassmorphism Cards & Containers:
 * - Semi-transparent white background (rgba(255, 255, 255, 0.85))
 * - Smooth rounded corners (20-24dp)
 * - Soft, polished translucent white borders with subtle specular highlight
 * - Subtle ambient shadow depths
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 4.dp,
    borderAlpha: Float = 0.85f,
    isDark: Boolean = false
): Modifier {
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFC1E293B),
                Color(0xFF0F172A)
            )
        )
    } else {
        // Semi-transparent white background (rgba(255, 255, 255, 0.85 - 0.90))
        Brush.verticalGradient(
            listOf(
                Color(0xF0FFFFFF), // Slightly more opaque at the top for specular rim
                Color(0xD9FFFFFF), // rgba(255, 255, 255, 0.85) in the body
                Color(0xD4FFFFFF)
            )
        )
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = borderAlpha * 0.4f),
                VibrantCyan.copy(alpha = borderAlpha * 0.7f),
                Color.White.copy(alpha = borderAlpha * 0.2f)
            )
        )
    } else {
        // Soft, polished translucent white border with subtle cyan/sky specular shimmer
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.95f),
                Color(0x66CBD5E1).copy(alpha = borderAlpha * 0.8f),
                VibrantCyan.copy(alpha = borderAlpha * 0.25f),
                Color.White.copy(alpha = 0.90f)
            )
        )
    }

    val shadowAmbient = if (isDark) Color(0x3300BCD4) else Color(0x140F172A)
    val shadowSpot = if (isDark) Color(0x33000000) else Color(0x1A00BCD4)

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = shadowAmbient,
            spotColor = shadowSpot
        )
        .clip(shape)
        .background(bgBrush)
        .border(BorderStroke(1.2.dp, borderBrush), shape)
}

@Composable
fun Modifier.glassCard(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 4.dp,
    isDark: Boolean = false
): Modifier = this.liquidGlass(
    shape = shape,
    elevation = elevation,
    borderAlpha = 0.85f,
    isDark = isDark
)

@Composable
fun Modifier.liquidGlassPill(
    isDark: Boolean = false,
    elevation: Dp = 2.dp
): Modifier = this.liquidGlass(
    shape = LiquidGlassDefaults.PillShape,
    elevation = elevation,
    borderAlpha = 0.85f,
    isDark = isDark
)

@Composable
fun Modifier.liquidGlassInteractive(
    shape: Shape = LiquidGlassDefaults.SmallShape,
    isDark: Boolean = false,
    glowColor: Color = VibrantCyan
): Modifier {
    val bg = if (isDark) Color(0xFC1E293B) else Color(0xEBFFFFFF)
    val borderBrush = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = if (isDark) 0.8f else 0.95f),
            glowColor.copy(alpha = if (isDark) 0.65f else 0.5f),
            Color.White.copy(alpha = if (isDark) 0.4f else 0.85f)
        )
    )
    return this
        .clip(shape)
        .background(bg)
        .border(BorderStroke(1.2.dp, borderBrush), shape)
}
