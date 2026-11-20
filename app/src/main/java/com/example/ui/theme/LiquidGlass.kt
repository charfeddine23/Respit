package com.example.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LiquidGlassDefaults {
    val CardShape = RoundedCornerShape(24.dp)
    val PillShape = RoundedCornerShape(50.dp)
    val SmallShape = RoundedCornerShape(18.dp)
    val DialogShape = RoundedCornerShape(28.dp)
}

/**
 * Dynamic Aurora & Neumorphic background:
 * - In Neumorphism (without Living Mode): Clean solid GPU-fast canvas.
 * - When Living & Ultra-Luxury Mode is enabled: Organic breathing harmonic waves + floating luminous bokeh particles.
 */
@Composable
fun DynamicAuroraBackground(
    isDark: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val isLiving = LocalLivingLuxuryMode.current
    val effectiveDark = preset?.isDark ?: isDark
    val effectiveNeumorphic = isNeumorphic && (preset?.isLiquidGlass != true) && !isLiving

    if (effectiveNeumorphic) {
        val canvasBg = preset?.backgroundMid ?: if (effectiveDark) Color(0xFF131926) else Color(0xFFE6EEF8)
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(color = canvasBg)
        }
        return
    }

    val staticBrush = remember(effectiveDark, preset?.id, isLiving, isNeumorphic) {
        if (preset != null) {
            Brush.verticalGradient(
                listOf(
                    preset.backgroundTop,
                    preset.backgroundMid,
                    preset.backgroundBottom
                )
            )
        } else if (isLiving && isNeumorphic) {
            if (effectiveDark) {
                Brush.verticalGradient(listOf(Color(0xFF0A101F), Color(0xFF131D33), Color(0xFF0F172A)))
            } else {
                Brush.verticalGradient(listOf(Color(0xFFE4EEFC), Color(0xFFEDF4FF), Color(0xFFE1EAFA)))
            }
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

    if (!animationsEnabled && !isLiving) {
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(brush = staticBrush)
        }
        return
    }

    val orbBoost = if (isLiving) 1.42f else 1f
    val orb1Colors = remember(effectiveDark, preset?.id, isLiving) {
        if (preset != null) {
            val base = preset.orb1.copy(alpha = (preset.orb1.alpha * orbBoost).coerceAtMost(0.55f))
            listOf(base, base.copy(alpha = base.alpha * 0.45f), Color.Transparent)
        } else if (effectiveDark) {
            listOf(Color(0x3800E5FF).copy(alpha = (0.22f * orbBoost).coerceAtMost(0.45f)), Color(0x1800BCD4), Color.Transparent)
        } else {
            listOf(Color(0x3B00E5FF).copy(alpha = (0.24f * orbBoost).coerceAtMost(0.44f)), Color(0x1A00BCD4), Color.Transparent)
        }
    }
    val orb2Colors = remember(effectiveDark, preset?.id, isLiving) {
        if (preset != null) {
            val base = preset.orb2.copy(alpha = (preset.orb2.alpha * orbBoost).coerceAtMost(0.52f))
            listOf(base, base.copy(alpha = base.alpha * 0.45f), Color.Transparent)
        } else if (effectiveDark) {
            listOf(Color(0x2E7C4DFF).copy(alpha = (0.19f * orbBoost).coerceAtMost(0.42f)), Color(0x126366F1), Color.Transparent)
        } else {
            listOf(Color(0x308B5CF6).copy(alpha = (0.20f * orbBoost).coerceAtMost(0.40f)), Color(0x12A855F7), Color.Transparent)
        }
    }
    val orb3Colors = remember(effectiveDark, preset?.id, isLiving) {
        if (preset != null) {
            val base = preset.orb3.copy(alpha = (preset.orb3.alpha * orbBoost).coerceAtMost(0.48f))
            listOf(base, base.copy(alpha = base.alpha * 0.45f), Color.Transparent)
        } else if (effectiveDark) {
            listOf(Color(0x2810B981).copy(alpha = (0.17f * orbBoost).coerceAtMost(0.38f)), Color(0x10059669), Color.Transparent)
        } else {
            listOf(Color(0x2E00BCD4).copy(alpha = (0.19f * orbBoost).coerceAtMost(0.38f)), Color(0x120284C7), Color.Transparent)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "auroraTransition")

    val t1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isLiving) 7800 else 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1Anim"
    )

    val t2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isLiving) 10500 else 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2Anim"
    )

    val t3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isLiving) 9200 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb3Anim"
    )

    val breathWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "livingBreathWave"
    )

    val particleAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val particleSecondary = preset?.secondary ?: if (effectiveDark) Color(0xFF8B5CF6) else RoyalBlueAccent

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        val baseDim = sqrt(w * h).coerceAtLeast(1f)
        val breathExpand = if (isLiving) 1f + 0.14f * breathWave else 1f

        drawRect(brush = staticBrush)

        if (effectiveDark) {
            val orb1X = w * (0.65f + 0.22f * sin(t1 * Math.PI).toFloat())
            val orb1Y = h * (0.15f + 0.12f * cos(t1 * Math.PI).toFloat())
            val orb1Radius = (baseDim * (0.46f + 0.08f * sin(t2 * Math.PI).toFloat()) * breathExpand).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(orb1Colors, center = Offset(orb1X, orb1Y), radius = orb1Radius),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            )

            val orb2X = w * (0.18f + 0.16f * cos(t2 * Math.PI).toFloat())
            val orb2Y = h * (0.52f + 0.10f * sin(t2 * Math.PI).toFloat())
            val orb2Radius = (baseDim * (0.48f + 0.06f * cos(t3 * Math.PI).toFloat()) * breathExpand).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(orb2Colors, center = Offset(orb2X, orb2Y), radius = orb2Radius),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            )

            val orb3X = w * (0.80f - 0.20f * sin(t3 * Math.PI).toFloat())
            val orb3Y = h * (0.82f - 0.08f * cos(t3 * Math.PI).toFloat())
            val orb3Radius = (baseDim * (0.42f + 0.06f * sin(t1 * Math.PI).toFloat()) * breathExpand).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(orb3Colors, center = Offset(orb3X, orb3Y), radius = orb3Radius),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            )
        } else {
            val orb1X = w * (0.12f + 0.18f * sin(t1 * Math.PI).toFloat())
            val orb1Y = h * (0.14f + 0.10f * cos(t1 * Math.PI).toFloat())
            val orb1Radius = (baseDim * (0.50f + 0.06f * sin(t2 * Math.PI).toFloat()) * breathExpand).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(orb1Colors, center = Offset(orb1X, orb1Y), radius = orb1Radius),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            )

            val orb2X = w * (0.88f - 0.18f * cos(t2 * Math.PI).toFloat())
            val orb2Y = h * (0.40f + 0.12f * sin(t2 * Math.PI).toFloat())
            val orb2Radius = (baseDim * (0.46f + 0.06f * cos(t3 * Math.PI).toFloat()) * breathExpand).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(orb2Colors, center = Offset(orb2X, orb2Y), radius = orb2Radius),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            )

            val orb3X = w * (0.40f + 0.25f * sin(t3 * Math.PI).toFloat())
            val orb3Y = h * (0.82f - 0.08f * cos(t1 * Math.PI).toFloat())
            val orb3Radius = (baseDim * (0.46f + 0.06f * sin(t1 * Math.PI).toFloat()) * breathExpand).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(orb3Colors, center = Offset(orb3X, orb3Y), radius = orb3Radius),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            )
        }

        // Living Breathing Mode: 16 floating luminous bokeh micro-motes
        if (isLiving) {
            val phase = (t1 * 2f + t2 * 3f) * Math.PI
            for (i in 0 until 16) {
                val seed = (i + 1) * 0.6180339f
                val fracX = ((seed * 1.73f) % 1f) + 0.06f * sin(phase + i * 0.9).toFloat()
                val baseY = ((seed * 2.41f) % 1f)
                val driftY = (baseY - (t3 * 0.14f) + 0.04f * cos(phase + i * 1.1).toFloat() + 1f) % 1f
                val radius = ((3.5f + (i % 4) * 2.4f) * (0.85f + 0.35f * breathWave)).coerceAtLeast(1f)
                val alpha = (if (effectiveDark) 0.24f else 0.18f) * (0.55f + 0.45f * sin(phase * 0.5 + i).toFloat().let { (it + 1f) * 0.5f })
                val c = if (i % 2 == 0) particleAccent else particleSecondary
                val center = Offset(w * fracX.coerceIn(0.03f, 0.97f), h * driftY)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(c.copy(alpha = alpha), c.copy(alpha = alpha * 0.3f), Color.Transparent),
                        center = center,
                        radius = radius * 3.4f
                    ),
                    center = center,
                    radius = radius * 3.4f
                )
            }
        }
    }
}

/**
 * Hardware-accelerated Glassmorphism & Tactile Neumorphism container modifier.
 * Automatically adopts active ThemeEngine palette and Living & Ultra-Luxury specular effects.
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
    val isLiving = LocalLivingLuxuryMode.current
    val effectiveDark = preset?.isDark ?: isDark
    val effectiveNeumorphic = isNeumorphic && (preset?.isLiquidGlass != true)

    val shimmerPhase = if (isLiving) {
        val transition = rememberInfiniteTransition(label = "livingGlassShimmer")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glassSpecularShift"
        ).value
    } else 0f

    if (effectiveNeumorphic) {
        val surfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF161F30) else Color(0xFFE6EEF8)
        val borderBrush = if (isLiving) {
            val shift = shimmerPhase * 420f
            val accent = preset?.primary ?: if (effectiveDark) CyanGlow else RoyalBlueAccent
            Brush.linearGradient(
                colors = if (effectiveDark) {
                    listOf(
                        Color.White.copy(alpha = 0.28f),
                        accent.copy(alpha = 0.50f),
                        Color.White.copy(alpha = 0.14f),
                        Color.Black.copy(alpha = 0.45f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.98f),
                        accent.copy(alpha = 0.34f),
                        Color.White.copy(alpha = 0.72f),
                        Color(0x4564748B)
                    )
                },
                start = Offset(shift, 0f),
                end = Offset(shift + 520f, 520f)
            )
        } else {
            remember(effectiveDark, borderAlpha, preset?.id) {
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
        }
        val ambientShadow = if (isLiving) {
            (preset?.primary ?: if (effectiveDark) CyanGlow else RoyalBlueAccent).copy(alpha = if (effectiveDark) 0.26f else 0.18f)
        } else if (effectiveDark) Color(0xAA070B12) else Color(0x4D7A8FA8)
        val spotShadow = if (effectiveDark) Color(0xCC05080E) else Color(0x5A6C829C)

        return this
            .shadow(
                elevation = if (isLiving) elevation + 5.dp else elevation + 2.dp,
                shape = shape,
                clip = false,
                ambientColor = ambientShadow,
                spotColor = spotShadow
            )
            .clip(shape)
            .background(surfaceColor)
            .border(BorderStroke(if (isLiving) 1.35.dp else 1.dp, borderBrush), shape)
    }

    val bgBrush = remember(effectiveDark, preset?.id, isLiving) {
        if (preset != null) {
            Brush.verticalGradient(listOf(preset.cardTintTop, preset.cardTintBottom))
        } else if (effectiveDark) {
            if (isLiving) {
                Brush.verticalGradient(listOf(Color(0xF21E293B), Color(0xE80F172A)))
            } else {
                Brush.verticalGradient(listOf(Color(0xEB1E293B), Color(0xDF0F172A)))
            }
        } else {
            if (isLiving) {
                Brush.verticalGradient(listOf(Color(0xF8FFFFFF), Color(0xE6F8FAFC), Color(0xE0F1F5F9)))
            } else {
                Brush.verticalGradient(listOf(Color(0xF0FFFFFF), Color(0xD9FFFFFF), Color(0xD4FFFFFF)))
            }
        }
    }

    val borderBrush = if (isLiving) {
        val shift = shimmerPhase * 520f
        val accent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
        val secondary = preset?.secondary ?: if (effectiveDark) PurpleAccent else RoyalBlueAccent
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = if (effectiveDark) 0.45f else 0.98f),
                accent.copy(alpha = if (effectiveDark) 0.85f else 0.62f),
                secondary.copy(alpha = if (effectiveDark) 0.55f else 0.36f),
                Color.White.copy(alpha = if (effectiveDark) 0.25f else 0.92f)
            ),
            start = Offset(shift, 0f),
            end = Offset(shift + 540f, 540f)
        )
    } else {
        remember(effectiveDark, borderAlpha, preset?.id) {
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
    }

    val shadowAmbient = when {
        isLiving && preset != null -> preset.primary.copy(alpha = if (effectiveDark) 0.34f else 0.20f)
        isLiving -> (if (effectiveDark) CyanGlow else RoyalBlueAccent).copy(alpha = if (effectiveDark) 0.30f else 0.18f)
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
            elevation = if (isLiving) elevation + 3.dp else elevation,
            shape = shape,
            clip = false,
            ambientColor = shadowAmbient,
            spotColor = shadowSpot
        )
        .clip(shape)
        .background(bgBrush)
        .border(
            BorderStroke(
                if (isLiving) 1.55.dp else if (preset?.isLiquidGlass == true) 1.35.dp else 1.2.dp,
                borderBrush
            ),
            shape
        )
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
    val isLiving = LocalLivingLuxuryMode.current
    val effectiveDark = preset?.isDark ?: isDark
    val effectiveNeumorphic = isNeumorphic && (preset?.isLiquidGlass != true)

    val shimmerPhase = if (isLiving) {
        val transition = rememberInfiniteTransition(label = "interactiveLuxuryShimmer")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "interactiveSpecularShift"
        ).value
    } else 0f

    if (effectiveNeumorphic) {
        val surfaceColor = preset?.surfacePrimary ?: if (effectiveDark) Color(0xFF161F30) else Color(0xFFE6EEF8)
        val borderBrush = if (isLiving) {
            val shift = shimmerPhase * 160f
            val accent = preset?.primary ?: glowColor
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = if (effectiveDark) 0.25f else 0.98f),
                    accent.copy(alpha = 0.55f),
                    Color.Black.copy(alpha = if (effectiveDark) 0.45f else 0.18f)
                ),
                start = Offset(shift, 0f),
                end = Offset(shift + 180f, 180f)
            )
        } else {
            remember(effectiveDark, preset?.id) {
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
        }
        val ambientShadow = if (effectiveDark) Color(0xAA070B12) else Color(0x457A8FA8)
        val spotShadow = if (effectiveDark) Color(0xCC05080E) else Color(0x506C829C)

        return this
            .shadow(
                elevation = if (isLiving) 6.dp else 4.dp,
                shape = shape,
                clip = false,
                ambientColor = ambientShadow,
                spotColor = spotShadow
            )
            .clip(shape)
            .background(surfaceColor)
            .border(BorderStroke(if (isLiving) 1.25.dp else 0.9.dp, borderBrush), shape)
    }

    val bg = preset?.surfacePrimary?.copy(alpha = if (preset.isLiquidGlass) 0.78f else 0.92f)
        ?: if (effectiveDark) Color(0xEE1E293B) else Color(0xEBFFFFFF)
    val effectiveGlow = preset?.primary ?: glowColor
    val borderBrush = if (isLiving) {
        val shift = shimmerPhase * 180f
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = if (effectiveDark) 0.55f else 0.98f),
                effectiveGlow.copy(alpha = 0.85f),
                (preset?.secondary ?: Color(0xFF8B5CF6)).copy(alpha = 0.55f)
            ),
            start = Offset(shift, 0f),
            end = Offset(shift + 180f, 180f)
        )
    } else {
        remember(effectiveDark, effectiveGlow, preset?.id) {
            Brush.linearGradient(
                listOf(
                    preset?.borderHighlight ?: Color.White.copy(alpha = if (effectiveDark) 0.5f else 0.95f),
                    effectiveGlow.copy(alpha = if (effectiveDark) 0.72f else 0.5f),
                    (preset?.secondary ?: Color.White).copy(alpha = if (effectiveDark) 0.35f else 0.85f)
                )
            )
        }
    }
    return this
        .clip(shape)
        .background(bg)
        .border(BorderStroke(if (isLiving) 1.45.dp else 1.2.dp, borderBrush), shape)
}

/**
 * Enables genuine Android 12+ blur-behind + softer frosted dim on Compose Dialog windows
 * while safely no-oping on older SDKs or Robolectric test environments.
 */
@Composable
fun SyncDialogLiquidGlassWindow(
    dimAmount: Float = 0.30f,
    blurRadiusPx: Int = 38
) {
    val view = LocalView.current
    DisposableEffect(view, dimAmount, blurRadiusPx) {
        val dialogWindow = (view.parent as? DialogWindowProvider)?.window
        if (dialogWindow != null) {
            runCatching {
                dialogWindow.setDimAmount(dimAmount)
                val isRobolectric = Build.FINGERPRINT.equals("robolectric", ignoreCase = true)
                if (!isRobolectric && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    val params = dialogWindow.attributes
                    params.blurBehindRadius = blurRadiusPx
                    dialogWindow.attributes = params
                }
            }
        }
        onDispose { }
    }
}

/**
 * Full iOS 26-inspired Liquid Glass outer shell for Dialogs and Floating Sheets:
 * translucent frosted medium + chromatic refraction wash + top curved specular dome gloss +
 * bottom internal caustic bounce + luminous multi-stop glass rim border.
 */
@Composable
fun Modifier.liquidGlassDialogShell(
    shape: Shape = LiquidGlassDefaults.DialogShape,
    cornerRadius: Dp = 28.dp,
    elevation: Dp = 20.dp,
    isDark: Boolean = false,
    primaryAccent: Color = CyanGlow,
    secondaryAccent: Color = RoyalBlueAccent
): Modifier {
    val preset = LocalThemePreset.current
    val isLiving = LocalLivingLuxuryMode.current
    val effectiveDark = preset?.isDark ?: isDark
    val accent1 = preset?.primary ?: primaryAccent
    val accent2 = preset?.secondary ?: secondaryAccent

    val shimmerPhase = if (isLiving) {
        val transition = rememberInfiniteTransition(label = "dialogGlassShimmer")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dialogGlassShift"
        ).value
    } else 0f

    val borderBrush = if (isLiving) {
        val shift = shimmerPhase * 520f
        Brush.linearGradient(
            colors = if (effectiveDark) {
                listOf(
                    Color.White.copy(alpha = 0.56f),
                    accent1.copy(alpha = 0.72f),
                    accent2.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0.24f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.98f),
                    accent1.copy(alpha = 0.52f),
                    accent2.copy(alpha = 0.34f),
                    Color.White.copy(alpha = 0.90f)
                )
            },
            start = Offset(shift, 0f),
            end = Offset(shift + 580f, 580f)
        )
    } else {
        remember(effectiveDark, accent1, accent2) {
            Brush.linearGradient(
                colors = if (effectiveDark) {
                    listOf(
                        Color.White.copy(alpha = 0.44f),
                        accent1.copy(alpha = 0.48f),
                        Color.White.copy(alpha = 0.14f),
                        accent2.copy(alpha = 0.30f),
                        Color.White.copy(alpha = 0.26f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.96f),
                        accent1.copy(alpha = 0.36f),
                        Color.White.copy(alpha = 0.52f),
                        accent2.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.88f)
                    )
                }
            )
        }
    }

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = if (effectiveDark) Color(0xAA000000) else Color(0x380F172A),
            spotColor = accent1.copy(alpha = if (effectiveDark) 0.42f else 0.24f)
        )
        .clip(shape)
        .drawBehind {
            val radiusPx = cornerRadius.toPx()
            val cr = CornerRadius(radiusPx, radiusPx)
            // 1. Frosted translucent glass medium
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = if (effectiveDark) {
                        listOf(
                            Color(0xFF1E293B).copy(alpha = 0.76f),
                            Color(0xFF0F172A).copy(alpha = 0.68f),
                            Color(0xFF1E293B).copy(alpha = 0.74f)
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.78f),
                            Color(0xFFF1F5F9).copy(alpha = 0.66f),
                            Color.White.copy(alpha = 0.74f)
                        )
                    }
                ),
                cornerRadius = cr
            )
            // 2. Subtle chromatic refraction tint wash
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent1.copy(alpha = if (effectiveDark) 0.14f else 0.08f),
                        Color.Transparent,
                        accent2.copy(alpha = if (effectiveDark) 0.12f else 0.07f)
                    )
                ),
                cornerRadius = cr
            )
            // 3. Top specular dome gloss (signature Liquid Glass upper highlight)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (effectiveDark) 0.16f else 0.58f),
                        Color.White.copy(alpha = if (effectiveDark) 0.04f else 0.16f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height * 0.28f
                ),
                cornerRadius = cr
            )
            // 4. Bottom internal caustic bounce
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = if (effectiveDark) 0.06f else 0.24f)
                    ),
                    startY = size.height * 0.80f,
                    endY = size.height
                ),
                cornerRadius = cr
            )
        }
        .border(BorderStroke(if (isLiving) 1.6.dp else 1.4.dp, borderBrush), shape)
}

/**
 * Inner Liquid Glass panel / card / interactive tile / pill surface with specular top highlight,
 * caustic bottom glow, and multi-stop glass rim border.
 */
@Composable
fun Modifier.liquidGlassPanel(
    shape: Shape = LiquidGlassDefaults.SmallShape,
    cornerRadius: Dp = 18.dp,
    isDark: Boolean = false,
    accent: Color = VibrantCyan,
    secondaryAccent: Color = RoyalBlueAccent,
    isSelected: Boolean = false,
    softTintWhenSelected: Boolean = true,
    borderWidth: Dp = if (isSelected) 1.4.dp else 1.05.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val borderBrush = remember(effectiveDark, accent, secondaryAccent, isSelected, softTintWhenSelected) {
        when {
            isSelected && !softTintWhenSelected -> Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.72f),
                    accent.copy(alpha = 0.90f),
                    Color.White.copy(alpha = 0.45f)
                )
            )
            isSelected -> Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = if (effectiveDark) 0.50f else 0.94f),
                    accent.copy(alpha = if (effectiveDark) 0.92f else 0.82f),
                    accent.copy(alpha = if (effectiveDark) 0.62f else 0.55f)
                )
            )
            effectiveDark -> Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.26f),
                    accent.copy(alpha = 0.24f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
            else -> Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.94f),
                    Color(0xFFCBD5E1).copy(alpha = 0.48f),
                    Color.White.copy(alpha = 0.78f)
                )
            )
        }
    }

    return this
        .clip(shape)
        .drawBehind {
            val radiusPx = cornerRadius.toPx()
            val cr = CornerRadius(radiusPx, radiusPx)
            val baseBrush = when {
                isSelected && !softTintWhenSelected -> Brush.linearGradient(
                    listOf(
                        accent.copy(alpha = 0.90f),
                        secondaryAccent.copy(alpha = 0.84f)
                    )
                )
                isSelected -> Brush.verticalGradient(
                    if (effectiveDark) {
                        listOf(
                            accent.copy(alpha = 0.34f),
                            accent.copy(alpha = 0.20f)
                        )
                    } else {
                        listOf(
                            accent.copy(alpha = 0.22f),
                            accent.copy(alpha = 0.12f)
                        )
                    }
                )
                effectiveDark -> Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E293B).copy(alpha = 0.56f),
                        Color(0xFF0F172A).copy(alpha = 0.42f)
                    )
                )
                else -> Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.62f),
                        Color(0xFFF8FAFC).copy(alpha = 0.40f)
                    )
                )
            }
            drawRoundRect(brush = baseBrush, cornerRadius = cr)

            // Top specular dome sheen
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(
                            alpha = when {
                                isSelected && !softTintWhenSelected -> 0.34f
                                effectiveDark -> 0.13f
                                else -> 0.52f
                            }
                        ),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height * 0.46f
                ),
                cornerRadius = cr
            )
        }
        .border(BorderStroke(borderWidth, borderBrush), shape)
}
