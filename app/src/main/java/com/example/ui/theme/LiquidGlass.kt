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
    val SmallShape = RoundedCornerShape(14.dp)
    val DialogShape = RoundedCornerShape(28.dp)
}

@Composable
fun Modifier.liquidGlass(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 4.dp,
    borderAlpha: Float = 0.85f,
    isDark: Boolean = false
): Modifier {
    // Serene, eye-comfortable liquid glass with soft specular sheen and no dark borders
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFC1E293B),
                Color(0xFF0F172A)
            )
        )
    } else {
        // Sculpted Ice Liquid Glass matching reference image
        Brush.verticalGradient(
            listOf(
                Color(0xF5FFFFFF),
                Color(0xE0F0F8FF),
                Color(0xD0E5F3FD)
            )
        )
    }

    val borderBrush = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color(0xB3FFFFFF).copy(alpha = borderAlpha * 0.5f),
                Color(0x9938BDF8).copy(alpha = borderAlpha),
                Color(0x660284C7).copy(alpha = borderAlpha * 0.7f)
            )
        )
    } else {
        // Crystalline specular highlight border
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.98f),
                Color(0x99BAE6FD).copy(alpha = borderAlpha),
                Color(0x667DD3FC).copy(alpha = borderAlpha * 0.8f),
                Color.White.copy(alpha = 0.92f)
            )
        )
    }

    val shadowAmbient = if (isDark) Color(0x1A0284C7) else Color(0x1F0284C7)
    val shadowSpot = if (isDark) Color(0x260369A1) else Color(0x2438BDF8)

    return this
        .shadow(elevation, shape, clip = false, ambientColor = shadowAmbient, spotColor = shadowSpot)
        .clip(shape)
        .background(bgBrush)
        .border(BorderStroke(1.3.dp, borderBrush), shape)
}

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
    glowColor: Color = Color(0xFF38BDF8)
): Modifier {
    val bg = if (isDark) Color(0xFC1E293B) else Color(0xEBFFFFFF)
    val borderBrush = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = if (isDark) 0.8f else 0.95f),
            glowColor.copy(alpha = if (isDark) 0.75f else 0.65f),
            Color.White.copy(alpha = if (isDark) 0.5f else 0.85f)
        )
    )
    return this
        .clip(shape)
        .background(bg)
        .border(BorderStroke(1.dp, borderBrush), shape)
}

