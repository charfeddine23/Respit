package com.example.ui.theme

import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LiquidGlassDefaults {
    val CardShape = RoundedCornerShape(20.dp)
    val SmallShape = RoundedCornerShape(14.dp)
    val DialogShape = RoundedCornerShape(24.dp)
}

/**
 * Clean, calm ambient canvas background:
 * Smooth gradient tinted subtly by the active ThemePreset with one very soft top highlight
 * (zero distracting moving orbs, bokeh confetti, or heavy animations).
 */
@Composable
fun DynamicAuroraBackground(
    isDark: Boolean = false,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val isLivingLuxury = LocalLivingLuxuryMode.current && animationsEnabled

    val breathPhase: Float
    val driftAngle: Float
    if (isLivingLuxury) {
        val infiniteTransition = rememberInfiniteTransition(label = "livingLuxuryAurora")
        val breath by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "auroraBreath"
        )
        val drift by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2.0 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 15000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "auroraDrift"
        )
        breathPhase = breath
        driftAngle = drift
    } else {
        breathPhase = 0f
        driftAngle = 0f
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
                    Color(0xFF0A141A),
                    Color(0xFF0E1A22),
                    Color(0xFF12212B)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    AuroraCyanTop,
                    AuroraWhiteMid,
                    AuroraPurpleBottom
                )
            )
        }
    }

    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryAccent = preset?.secondary ?: if (effectiveDark) SkyLeaveBlue else TealPrimary
    val tertiaryAccent = preset?.tertiary ?: if (effectiveDark) Color(0xFF38BDF8) else Color(0xFF14B8A6)

    val topAlphaMultiplier = if (isLivingLuxury) 1f + 0.58f * breathPhase else 1f
    val bottomAlphaMultiplier = if (isLivingLuxury) 1f + 0.55f * (1f - breathPhase) else 1f

    val topGlow = listOf(
        primaryAccent.copy(alpha = (if (effectiveDark) 0.10f else 0.068f) * topAlphaMultiplier),
        Color.Transparent
    )
    val bottomGlow = listOf(
        secondaryAccent.copy(alpha = (if (effectiveDark) 0.085f else 0.050f) * bottomAlphaMultiplier),
        Color.Transparent
    )
    val midVeilGlow = if (isLivingLuxury) {
        listOf(
            tertiaryAccent.copy(alpha = (if (effectiveDark) 0.085f else 0.052f) * (0.65f + 0.35f * breathPhase)),
            Color.Transparent
        )
    } else null

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(brush = staticBrush)
        val w = size.width
        val h = size.height
        if (w > 0f && h > 0f) {
            val baseRadius = (sqrt(w * h) * 0.7f).coerceAtLeast(1f)
            val topX = if (isLivingLuxury) w * (0.16f + 0.09f * cos(driftAngle)) else w * 0.15f
            val topY = if (isLivingLuxury) h * (0.08f + 0.05f * sin(driftAngle)) else h * 0.06f
            val topR = baseRadius * (if (isLivingLuxury) 0.96f + 0.18f * breathPhase else 1f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = topGlow,
                    center = Offset(topX, topY),
                    radius = topR
                ),
                center = Offset(topX, topY),
                radius = topR
            )

            val botX = if (isLivingLuxury) w * (0.84f - 0.09f * cos(driftAngle * 0.85f)) else w * 0.88f
            val botY = if (isLivingLuxury) h * (0.82f + 0.06f * sin(driftAngle * 1.1f)) else h * 0.85f
            val botR = baseRadius * (if (isLivingLuxury) 0.85f + 0.16f * (1f - breathPhase) else 0.85f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = bottomGlow,
                    center = Offset(botX, botY),
                    radius = botR
                ),
                center = Offset(botX, botY),
                radius = botR
            )

            if (midVeilGlow != null) {
                val midX = w * (0.50f + 0.14f * sin(driftAngle))
                val midY = h * (0.44f + 0.07f * cos(driftAngle))
                val midR = baseRadius * (0.66f + 0.10f * breathPhase)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = midVeilGlow,
                        center = Offset(midX, midY),
                        radius = midR
                    ),
                    center = Offset(midX, midY),
                    radius = midR
                )
            }
        }
    }
}

/**
 * Frosted glassmorphic card container with translucent fill, luminous top specular highlight,
 * and soft neutral shadow. Adapts cleanly to ThemePreset, Living Breathing Luxury, and Light/Dark modes.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 4.dp,
    borderAlpha: Float = 0.9f,
    isDark: Boolean = false,
    cornerRadius: Dp = 20.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val isLivingLuxury = LocalLivingLuxuryMode.current && LocalAnimationsEnabled.current
    val accent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan

    val breathPhase = if (isLivingLuxury) {
        val transition = rememberInfiniteTransition(label = "livingGlassCard")
        val breath by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glassCardBreath"
        )
        breath
    } else 0f

    val fillBrush = remember(effectiveDark, preset?.id) {
        if (preset != null) {
            Brush.verticalGradient(
                listOf(
                    preset.surfacePrimary.copy(alpha = if (effectiveDark) 0.86f else 0.88f),
                    preset.surfacePrimary.copy(alpha = if (effectiveDark) 0.78f else 0.78f)
                )
            )
        } else if (effectiveDark) {
            Brush.verticalGradient(
                listOf(
                    Color(0xD9152430),
                    Color(0xC9101C26)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xEBFFFFFF),
                    Color(0xD4F8FCFB)
                )
            )
        }
    }

    val topBorderAlpha = if (isLivingLuxury) {
        if (effectiveDark) (0.20f + 0.14f * breathPhase) * borderAlpha else 0.94f + 0.06f * breathPhase
    } else {
        if (effectiveDark) 0.18f * borderAlpha else 0.95f
    }

    val borderBrush = if (preset != null) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = topBorderAlpha.coerceIn(0f, 1f)),
                if (isLivingLuxury) {
                    accent.copy(alpha = 0.24f + 0.16f * breathPhase)
                } else {
                    preset.borderAccent.copy(alpha = (0.34f * borderAlpha).coerceIn(0.14f, 0.50f))
                }
            )
        )
    } else if (effectiveDark) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = topBorderAlpha.coerceIn(0f, 1f)),
                if (isLivingLuxury) accent.copy(alpha = 0.18f + 0.14f * breathPhase) else Color(0xFF233645)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = topBorderAlpha.coerceIn(0f, 1f)),
                if (isLivingLuxury) accent.copy(alpha = 0.18f + 0.12f * breathPhase) else Color(0xFFD5E3E2)
            )
        )
    }

    val ambientShadow = if (effectiveDark) Color(0x55000000) else Color(0x120F172A)
    val spotShadow = if (effectiveDark) Color(0x66000000) else Color(0x180D9488)

    val sheenModifier = if (isLivingLuxury) {
        Modifier.drawWithContent {
            drawContent()
            val sheenAlpha = (if (effectiveDark) 0.12f else 0.34f) * (0.55f + 0.45f * breathPhase)
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = sheenAlpha),
                        accent.copy(alpha = sheenAlpha * 0.65f),
                        Color.Transparent
                    )
                ),
                start = Offset(size.width * 0.10f, 1.5.dp.toPx()),
                end = Offset(size.width * 0.90f, 1.5.dp.toPx()),
                strokeWidth = 1.6.dp.toPx()
            )
        }
    } else Modifier

    return this
        .shadow(
            elevation = elevation.coerceAtMost(6.dp),
            shape = shape,
            clip = false,
            ambientColor = ambientShadow,
            spotColor = spotShadow
        )
        .clip(shape)
        .background(fillBrush)
        .then(sheenModifier)
        .border(BorderStroke(if (isLivingLuxury) 1.15.dp else 1.dp, borderBrush), shape)
}

@Composable
fun Modifier.liquidGlassInteractive(
    shape: Shape = LiquidGlassDefaults.SmallShape,
    isDark: Boolean = false,
    glowColor: Color = VibrantCyan,
    cornerRadius: Dp = 14.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark

    val bg = preset?.surfacePrimary?.copy(alpha = if (effectiveDark) 0.84f else 0.86f)
        ?: if (effectiveDark) Color(0xD4162531) else Color(0xDCFFFFFF)
    val borderBrush = if (effectiveDark) {
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.16f), Color(0xFF273A4A)))
    } else {
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.95f), Color(0xFFD6E4E3)))
    }

    return this
        .clip(shape)
        .background(bg)
        .border(BorderStroke(1.dp, borderBrush), shape)
}

/**
 * Enables Android 12+ gentle blur-behind + subtle dim on Compose Dialog windows
 * while safely no-oping on older SDKs or Robolectric test environments.
 */
@Composable
fun SyncDialogLiquidGlassWindow(
    dimAmount: Float = 0.28f,
    blurRadiusPx: Int = 28
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
 * Clean frosted dialog shell with subtle translucent glass tint and specular edge highlight.
 */
@Composable
fun Modifier.liquidGlassDialogShell(
    shape: Shape = LiquidGlassDefaults.DialogShape,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 12.dp,
    isDark: Boolean = false,
    primaryAccent: Color = CyanGlow,
    secondaryAccent: Color = RoyalBlueAccent
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark

    val bgColor = remember(effectiveDark, preset?.id) {
        preset?.surfacePrimary?.copy(alpha = 0.95f)
            ?: if (effectiveDark) Color(0xF013202B) else Color(0xF5FFFFFF)
    }

    val borderBrush = remember(effectiveDark, preset?.id) {
        if (preset != null) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (effectiveDark) 0.18f else 0.9f),
                    preset.borderAccent.copy(alpha = 0.35f)
                )
            )
        } else if (effectiveDark) {
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.18f), Color(0xFF283B4A)))
        } else {
            Brush.verticalGradient(listOf(Color.White, Color(0xFFD5E3E2)))
        }
    }

    return this
        .shadow(
            elevation = elevation.coerceAtMost(14.dp),
            shape = shape,
            clip = false,
            ambientColor = if (effectiveDark) Color(0x88000000) else Color(0x220F172A),
            spotColor = if (effectiveDark) Color(0x88000000) else Color(0x240D9488)
        )
        .clip(shape)
        .background(bgColor)
        .border(BorderStroke(1.dp, borderBrush), shape)
}

/**
 * Frosted inner container / tile / pill surface with restrained tonal tint and specular hairline border.
 */
@Composable
fun Modifier.liquidGlassPanel(
    shape: Shape = LiquidGlassDefaults.SmallShape,
    cornerRadius: Dp = 14.dp,
    isDark: Boolean = false,
    accent: Color = VibrantCyan,
    secondaryAccent: Color = RoyalBlueAccent,
    isSelected: Boolean = false,
    softTintWhenSelected: Boolean = true,
    borderWidth: Dp = if (isSelected) 1.2.dp else 1.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark

    val borderColor = remember(effectiveDark, accent, isSelected, softTintWhenSelected) {
        when {
            isSelected && !softTintWhenSelected -> accent
            isSelected -> accent.copy(alpha = if (effectiveDark) 0.55f else 0.42f)
            effectiveDark -> Color(0xFF253746)
            else -> Color(0xFFD9E6E5)
        }
    }

    return this
        .clip(shape)
        .drawBehind {
            val radiusPx = cornerRadius.toPx()
            val cr = CornerRadius(radiusPx, radiusPx)
            val fillColor = when {
                isSelected && !softTintWhenSelected -> accent
                isSelected -> accent.copy(alpha = if (effectiveDark) 0.18f else 0.11f)
                effectiveDark -> Color(0xBF172633)
                else -> Color(0xCCF6FBFB)
            }
            drawRoundRect(color = fillColor, cornerRadius = cr)
        }
        .border(BorderStroke(borderWidth, borderColor), shape)
}
