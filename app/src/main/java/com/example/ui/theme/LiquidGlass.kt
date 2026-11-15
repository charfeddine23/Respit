package com.example.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object LiquidGlassDefaults {
    val CardShape = RoundedCornerShape(24.dp)
    val PillShape = RoundedCornerShape(50.dp)
    val SmallShape = RoundedCornerShape(18.dp)
    val DialogShape = RoundedCornerShape(28.dp)
}

/**
 * Dynamic Aurora & Neumorphic background:
 * - In Neumorphism: Clean solid GPU-fast canvas (#E6EEF8 in light, #131926 in dark).
 * - In Glassmorphism: Smoothly drifting aurora ambient glowing mesh with remembered gradients.
 */
@Composable
fun DynamicAuroraBackground(
    isDark: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val effectiveNeumorphic = isNeumorphic && (preset?.isLiquidGlass != true)

    if (effectiveNeumorphic) {
        val canvasBg = preset?.backgroundMid ?: if (effectiveDark) Color(0xFF131926) else Color(0xFFE6EEF8)
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(color = canvasBg)
        }
        return
    }

    val staticBrush = remember(effectiveDark, preset?.id) {
        if (preset != null) {
            Brush.verticalGradient(
                listOf(
                    preset.backgroundTop,
                    preset.backgroundMid,
                    preset.backgroundBottom
                )
            )
        } else if (effectiveDark) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF090D1A),
                    Color(0xFF0F172A),
                    Color(0xFF1E293B)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    AuroraCyanTop,
                    AuroraCyanLight,
                    AuroraWhiteMid,
                    AuroraPurpleLight,
                    AuroraPurpleBottom
                )
            )
        }
    }

    if (!animationsEnabled) {
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(brush = staticBrush)
        }
        return
    }

    val orb1Colors = remember(effectiveDark, preset?.id) {
        if (preset != null) {
            listOf(preset.orb1, preset.orb1.copy(alpha = preset.orb1.alpha * 0.45f), Color.Transparent)
        } else if (effectiveDark) {
            listOf(Color(0x3800E5FF), Color(0x1800BCD4), Color.Transparent)
        } else {
            listOf(Color(0x3B00E5FF), Color(0x1A00BCD4), Color.Transparent)
        }
    }
    val orb2Colors = remember(effectiveDark, preset?.id) {
        if (preset != null) {
            listOf(preset.orb2, preset.orb2.copy(alpha = preset.orb2.alpha * 0.45f), Color.Transparent)
        } else if (effectiveDark) {
            listOf(Color(0x2E7C4DFF), Color(0x126366F1), Color.Transparent)
        } else {
            listOf(Color(0x308B5CF6), Color(0x12A855F7), Color.Transparent)
        }
    }
    val orb3Colors = remember(effectiveDark, preset?.id) {
        if (preset != null) {
            listOf(preset.orb3, preset.orb3.copy(alpha = preset.orb3.alpha * 0.45f), Color.Transparent)
        } else if (effectiveDark) {
            listOf(Color(0x2810B981), Color(0x10059669), Color.Transparent)
        } else {
            listOf(Color(0x2E00BCD4), Color(0x120284C7), Color.Transparent)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "auroraTransition")

    val t1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1Anim"
    )

    val t2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2Anim"
    )

    val t3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb3Anim"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val baseDim = kotlin.math.sqrt(w * h)

        drawRect(brush = staticBrush)

        if (effectiveDark) {
            val orb1X = w * (0.65f + 0.22f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())
            val orb1Y = h * (0.15f + 0.12f * kotlin.math.cos(t1 * kotlin.math.PI).toFloat())
            val orb1Radius = baseDim * (0.46f + 0.08f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())
            drawCircle(
                brush = Brush.radialGradient(orb1Colors, center = Offset(orb1X, orb1Y), radius = orb1Radius),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            )

            val orb2X = w * (0.18f + 0.16f * kotlin.math.cos(t2 * kotlin.math.PI).toFloat())
            val orb2Y = h * (0.52f + 0.10f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())
            val orb2Radius = baseDim * (0.48f + 0.06f * kotlin.math.cos(t3 * kotlin.math.PI).toFloat())
            drawCircle(
                brush = Brush.radialGradient(orb2Colors, center = Offset(orb2X, orb2Y), radius = orb2Radius),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            )

            val orb3X = w * (0.80f - 0.20f * kotlin.math.sin(t3 * kotlin.math.PI).toFloat())
            val orb3Y = h * (0.82f - 0.08f * kotlin.math.cos(t3 * kotlin.math.PI).toFloat())
            val orb3Radius = baseDim * (0.42f + 0.06f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())
            drawCircle(
                brush = Brush.radialGradient(orb3Colors, center = Offset(orb3X, orb3Y), radius = orb3Radius),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            )
        } else {
            val orb1X = w * (0.12f + 0.18f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())
            val orb1Y = h * (0.14f + 0.10f * kotlin.math.cos(t1 * kotlin.math.PI).toFloat())
            val orb1Radius = baseDim * (0.50f + 0.06f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())
            drawCircle(
                brush = Brush.radialGradient(orb1Colors, center = Offset(orb1X, orb1Y), radius = orb1Radius),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            )

            val orb2X = w * (0.88f - 0.18f * kotlin.math.cos(t2 * kotlin.math.PI).toFloat())
            val orb2Y = h * (0.40f + 0.12f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())
            val orb2Radius = baseDim * (0.46f + 0.06f * kotlin.math.cos(t3 * kotlin.math.PI).toFloat())
            drawCircle(
                brush = Brush.radialGradient(orb2Colors, center = Offset(orb2X, orb2Y), radius = orb2Radius),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            )

            val orb3X = w * (0.40f + 0.25f * kotlin.math.sin(t3 * kotlin.math.PI).toFloat())
            val orb3Y = h * (0.82f - 0.08f * kotlin.math.cos(t1 * kotlin.math.PI).toFloat())
            val orb3Radius = baseDim * (0.46f + 0.06f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())
            drawCircle(
                brush = Brush.radialGradient(orb3Colors, center = Offset(orb3X, orb3Y), radius = orb3Radius),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            )
        }
    }
}

/**
 * Hardware-accelerated Glassmorphism & Tactile Neumorphism container modifier.
 * Automatically adopts active ThemeEngine palette when a preset is selected.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 5.dp,
    borderAlpha: Float = 0.85f,
    isDark: Boolean = false,
    isNeumorphic: Boolean = false,
    cornerRadius: Dp = 24.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val effectiveNeumorphic = isNeumorphic && (preset?.isLiquidGlass != true)

    if (effectiveNeumorphic) {
        val surfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF161F30) else Color(0xFFE6EEF8)
        val borderBrush = remember(effectiveDark, borderAlpha, preset?.id) {
            Brush.linearGradient(
                if (preset != null) {
                    if (effectiveDark) {
                        listOf(
                            preset.borderHighlight,
                            preset.borderAccent.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.50f)
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.96f),
                            preset.borderHighlight,
                            preset.borderAccent.copy(alpha = 0.35f)
                        )
                    }
                } else if (effectiveDark) {
                    listOf(
                        Color.White.copy(alpha = 0.17f),
                        Color(0xFF334155).copy(alpha = 0.28f),
                        Color.Black.copy(alpha = 0.55f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.96f),
                        Color.White.copy(alpha = 0.48f),
                        Color(0x3894A3B8),
                        Color(0x4564748B)
                    )
                },
                start = Offset.Zero,
                end = Offset(500f, 500f)
            )
        }
        val ambientShadow = if (effectiveDark) Color(0xAA070B12) else Color(0x4D7A8FA8)
        val spotShadow = if (effectiveDark) Color(0xCC05080E) else Color(0x5A6C829C)

        return this
            .shadow(
                elevation = (elevation + 2.dp),
                shape = shape,
                clip = false,
                ambientColor = ambientShadow,
                spotColor = spotShadow
            )
            .clip(shape)
            .background(surfaceColor)
            .border(BorderStroke(1.dp, borderBrush), shape)
    }

    val bgBrush = remember(effectiveDark, preset?.id) {
        if (preset != null) {
            Brush.verticalGradient(listOf(preset.cardTintTop, preset.cardTintBottom))
        } else if (effectiveDark) {
            Brush.verticalGradient(listOf(Color(0xEB1E293B), Color(0xDF0F172A)))
        } else {
            Brush.verticalGradient(listOf(Color(0xF0FFFFFF), Color(0xD9FFFFFF), Color(0xD4FFFFFF)))
        }
    }

    val borderBrush = remember(effectiveDark, borderAlpha, preset?.id) {
        if (preset != null) {
            if (preset.isLiquidGlass) {
                Brush.linearGradient(
                    listOf(
                        preset.borderHighlight,
                        preset.borderAccent,
                        preset.secondary.copy(alpha = 0.45f),
                        preset.borderHighlight.copy(alpha = 0.6f)
                    )
                )
            } else if (effectiveDark) {
                Brush.linearGradient(
                    listOf(
                        preset.borderHighlight,
                        preset.borderAccent,
                        Color.White.copy(alpha = 0.12f)
                    )
                )
            } else {
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.95f),
                        preset.borderAccent,
                        Color.White.copy(alpha = 0.85f)
                    )
                )
            }
        } else if (effectiveDark) {
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = borderAlpha * 0.35f),
                    Color(0xFF00E5FF).copy(alpha = borderAlpha * 0.6f),
                    Color.White.copy(alpha = borderAlpha * 0.15f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.95f),
                    Color(0x66CBD5E1).copy(alpha = borderAlpha * 0.8f),
                    VibrantCyan.copy(alpha = borderAlpha * 0.25f),
                    Color.White.copy(alpha = 0.90f)
                )
            )
        }
    }

    val shadowAmbient = when {
        preset != null -> if (effectiveDark) preset.primary.copy(alpha = 0.22f) else Color(0x140F172A)
        effectiveDark -> Color(0x3300BCD4)
        else -> Color(0x140F172A)
    }
    val shadowSpot = when {
        preset != null -> if (effectiveDark) Color(0x44000000) else preset.primary.copy(alpha = 0.14f)
        effectiveDark -> Color(0x33000000)
        else -> Color(0x1A00BCD4)
    }

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
        .border(BorderStroke(if (preset?.isLiquidGlass == true) 1.35.dp else 1.2.dp, borderBrush), shape)
}

@Composable
fun Modifier.glassCard(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 4.dp,
    isDark: Boolean = false,
    isNeumorphic: Boolean = false
): Modifier = this.liquidGlass(
    shape = shape,
    elevation = elevation,
    borderAlpha = 0.85f,
    isDark = isDark,
    isNeumorphic = isNeumorphic
)

@Composable
fun Modifier.liquidGlassPill(
    isDark: Boolean = false,
    elevation: Dp = 2.dp,
    isNeumorphic: Boolean = false
): Modifier = this.liquidGlass(
    shape = LiquidGlassDefaults.PillShape,
    elevation = elevation,
    borderAlpha = 0.85f,
    isDark = isDark,
    isNeumorphic = isNeumorphic,
    cornerRadius = 50.dp
)

@Composable
fun Modifier.liquidGlassInteractive(
    shape: Shape = LiquidGlassDefaults.SmallShape,
    isDark: Boolean = false,
    glowColor: Color = VibrantCyan,
    isNeumorphic: Boolean = false,
    cornerRadius: Dp = 18.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val effectiveNeumorphic = isNeumorphic && (preset?.isLiquidGlass != true)

    if (effectiveNeumorphic) {
        val surfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF161F30) else Color(0xFFE6EEF8)
        val borderBrush = remember(effectiveDark, preset?.id) {
            Brush.linearGradient(
                if (effectiveDark) {
                    listOf(
                        preset?.borderHighlight ?: Color.White.copy(alpha = 0.16f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.50f)
                    )
                } else {
                    listOf(Color.White.copy(alpha = 0.96f), Color.White.copy(alpha = 0.45f), Color(0x3094A3B8), Color(0x4064748B))
                },
                start = Offset.Zero,
                end = Offset(160f, 160f)
            )
        }
        val ambientShadow = if (effectiveDark) Color(0xAA070B12) else Color(0x457A8FA8)
        val spotShadow = if (effectiveDark) Color(0xCC05080E) else Color(0x506C829C)

        return this
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = false,
                ambientColor = ambientShadow,
                spotColor = spotShadow
            )
            .clip(shape)
            .background(surfaceColor)
            .border(BorderStroke(0.9.dp, borderBrush), shape)
    }

    val bg = preset?.surfacePrimary?.copy(alpha = if (preset.isLiquidGlass) 0.78f else 0.92f)
        ?: if (effectiveDark) Color(0xEE1E293B) else Color(0xEBFFFFFF)
    val effectiveGlow = preset?.primary ?: glowColor
    val borderBrush = remember(effectiveDark, effectiveGlow, preset?.id) {
        Brush.linearGradient(
            listOf(
                preset?.borderHighlight ?: Color.White.copy(alpha = if (effectiveDark) 0.5f else 0.95f),
                effectiveGlow.copy(alpha = if (effectiveDark) 0.72f else 0.5f),
                (preset?.secondary ?: Color.White).copy(alpha = if (effectiveDark) 0.35f else 0.85f)
            )
        )
    }
    return this
        .clip(shape)
        .background(bg)
        .border(BorderStroke(1.2.dp, borderBrush), shape)
}
