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

    val topGlow = remember(effectiveDark, preset?.id) {
        val accent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
        listOf(
            accent.copy(alpha = if (effectiveDark) 0.10f else 0.065f),
            Color.Transparent
        )
    }

    val bottomGlow = remember(effectiveDark, preset?.id) {
        val accent = preset?.secondary ?: if (effectiveDark) Color(0xFF0284C7) else Color(0xFF0D9488)
        listOf(
            accent.copy(alpha = if (effectiveDark) 0.08f else 0.045f),
            Color.Transparent
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(brush = staticBrush)
        val w = size.width
        val h = size.height
        if (w > 0f && h > 0f) {
            val radius = (sqrt(w * h) * 0.7f).coerceAtLeast(1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = topGlow,
                    center = Offset(w * 0.15f, h * 0.06f),
                    radius = radius
                ),
                center = Offset(w * 0.15f, h * 0.06f),
                radius = radius
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = bottomGlow,
                    center = Offset(w * 0.88f, h * 0.85f),
                    radius = radius * 0.85f
                ),
                center = Offset(w * 0.88f, h * 0.85f),
                radius = radius * 0.85f
            )
        }
    }
}

/**
 * Frosted glassmorphic card container with translucent fill, luminous top specular highlight,
 * and soft neutral shadow. Adapts cleanly to ThemePreset and Light/Dark modes.
 */
@Composable
fun Modifier.liquidGlass(
    shape: Shape = LiquidGlassDefaults.CardShape,
    elevation: Dp = 4.dp,
    borderAlpha: Float = 0.9f,
    isDark: Boolean = false,
    isNeumorphic: Boolean = false,
    cornerRadius: Dp = 20.dp
): Modifier {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark

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

    val borderBrush = remember(effectiveDark, borderAlpha, preset?.id) {
        if (preset != null) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (effectiveDark) 0.18f else 0.85f),
                    preset.borderAccent.copy(alpha = (0.34f * borderAlpha).coerceIn(0.14f, 0.50f))
                )
            )
        } else if (effectiveDark) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.18f * borderAlpha),
                    Color(0xFF233645)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.95f),
                    Color(0xFFD5E3E2)
                )
            )
        }
    }

    val ambientShadow = if (effectiveDark) Color(0x55000000) else Color(0x120F172A)
    val spotShadow = if (effectiveDark) Color(0x66000000) else Color(0x180D9488)

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
        .border(BorderStroke(1.dp, borderBrush), shape)
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
