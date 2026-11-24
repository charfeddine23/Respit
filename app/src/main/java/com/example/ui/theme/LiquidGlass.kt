package com.example.ui.theme

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
import kotlin.math.sqrt

object LiquidGlassDefaults {
    val CardShape = RoundedCornerShape(20.dp)
    val PillShape = RoundedCornerShape(50.dp)
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
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark

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
                    Color(0xFF0B1220),
                    Color(0xFF0E1627),
                    Color(0xFF111B2E)
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

    val softCornerTint = remember(effectiveDark, preset?.id) {
        val accent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
        listOf(
            accent.copy(alpha = if (effectiveDark) 0.06f else 0.04f),
            Color.Transparent
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(brush = staticBrush)
        val w = size.width
        val h = size.height
        if (w > 0f && h > 0f) {
            val radius = (sqrt(w * h) * 0.65f).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = softCornerTint,
                    center = Offset(w * 0.85f, h * 0.08f),
                    radius = radius
                ),
                center = Offset(w * 0.85f, h * 0.08f),
                radius = radius
            )
        }
    }
}

/**
 * Clean, modern elevated container surface with subtle frosted fill, soft neutral shadow,
 * and crisp 1.dp hairline border. Adopts ThemePreset automatically without neon glow clutter.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 3.dp,
    borderAlpha: Float = 0.85f,
    isDark: Boolean = false,
    isNeumorphic: Boolean = false,
    cornerRadius: Dp = 20.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark

    val surfaceColor = remember(effectiveDark, preset?.id) {
        preset?.surfacePrimary
            ?: if (effectiveDark) Color(0xFF151F31) else Color(0xFFFFFFFF)
    }

    val borderColor = remember(effectiveDark, borderAlpha, preset?.id) {
        preset?.borderAccent?.copy(alpha = (0.32f * borderAlpha).coerceIn(0.14f, 0.50f))
            ?: if (effectiveDark) {
                Color(0xFF283548)
            } else {
                Color(0xFFE2E8F0)
            }
    }

    val ambientShadow = if (effectiveDark) Color(0x55000000) else Color(0x120F172A)
    val spotShadow = if (effectiveDark) Color(0x66000000) else Color(0x160F172A)

    return this
        .shadow(
            elevation = elevation.coerceAtMost(4.dp),
            shape = shape,
            clip = false,
            ambientColor = ambientShadow,
            spotColor = spotShadow
        )
        .clip(shape)
        .background(surfaceColor)
        .border(BorderStroke(1.dp, borderColor), shape)
}

@Composable
fun Modifier.liquidGlassInteractive(
    shape: Shape = LiquidGlassDefaults.SmallShape,
    isDark: Boolean = false,
    glowColor: Color = VibrantCyan,
    isNeumorphic: Boolean = false,
    cornerRadius: Dp = 14.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark

    val bg = preset?.surfacePrimary
        ?: if (effectiveDark) Color(0xFF1A2538) else Color(0xFFFFFFFF)
    val borderColor = preset?.borderAccent?.copy(alpha = 0.35f)
        ?: if (effectiveDark) Color(0xFF314056) else Color(0xFFE2E8F0)

    return this
        .clip(shape)
        .background(bg)
        .border(BorderStroke(1.dp, borderColor), shape)
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
 * Clean, modern frosted dialog shell with 95% opaque calm background, neutral shadow,
 * and crisp 1.dp hairline border.
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
        preset?.surfacePrimary?.copy(alpha = 0.97f)
            ?: if (effectiveDark) Color(0xF5151F31) else Color(0xF8FFFFFF)
    }

    val borderColor = remember(effectiveDark, preset?.id) {
        preset?.borderAccent?.copy(alpha = 0.35f)
            ?: if (effectiveDark) Color(0xFF2D3C53) else Color(0xFFE2E8F0)
    }

    return this
        .shadow(
            elevation = elevation.coerceAtMost(14.dp),
            shape = shape,
            clip = false,
            ambientColor = if (effectiveDark) Color(0x88000000) else Color(0x240F172A),
            spotColor = if (effectiveDark) Color(0x88000000) else Color(0x280F172A)
        )
        .clip(shape)
        .background(bgColor)
        .border(BorderStroke(1.dp, borderColor), shape)
}

/**
 * Clean inner container / tile / pill surface with restrained tonal tint and hairline border.
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
            isSelected -> accent.copy(alpha = if (effectiveDark) 0.55f else 0.45f)
            effectiveDark -> Color(0xFF29364B)
            else -> Color(0xFFE2E8F0)
        }
    }

    return this
        .clip(shape)
        .drawBehind {
            val radiusPx = cornerRadius.toPx()
            val cr = CornerRadius(radiusPx, radiusPx)
            val fillColor = when {
                isSelected && !softTintWhenSelected -> accent
                isSelected -> accent.copy(alpha = if (effectiveDark) 0.16f else 0.10f)
                effectiveDark -> Color(0xFF1A2538)
                else -> Color(0xFFF8FAFC)
            }
            drawRoundRect(color = fillColor, cornerRadius = cr)
        }
        .border(BorderStroke(borderWidth, borderColor), shape)
}
