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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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
 * - In Neumorphism: Clean tactile solid canvas (#E8EEF5 in light, #141923 in dark) matching the user reference design.
 * - In Glassmorphism: Smoothly drifting aurora ambient glowing mesh.
 */
@Composable
fun DynamicAuroraBackground(
    isDark: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (isNeumorphic) {
        val canvasBg = if (isDark) Color(0xFF141923) else Color(0xFFE8EEF5)
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(color = canvasBg)
        }
        return
    }

    if (!animationsEnabled) {
        val staticBrush = if (isDark) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF0F172A),
                    Color(0xFF131D33),
                    Color(0xFF101828)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFE0F7FA),
                    Color(0xFFFFFFFF),
                    Color(0xFFF3E5F5)
                )
            )
        }
        Canvas(modifier = modifier.fillMaxSize()) {
            drawRect(brush = staticBrush)
        }
        return
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

        if (isDark) {
            // Base Gradient: Deep Rich Slate / Midnight
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF090D1A),
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            )

            // Orb 1: Glowing Neon Cyan (floats top right)
            val orb1X = w * (0.65f + 0.22f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())
            val orb1Y = h * (0.15f + 0.12f * kotlin.math.cos(t1 * kotlin.math.PI).toFloat())
            val orb1Radius = baseDim * (0.46f + 0.08f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3800E5FF),
                        Color(0x1800BCD4),
                        Color.Transparent
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                ),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            )

            // Orb 2: Luminous Purple / Indigo (floats middle left)
            val orb2X = w * (0.18f + 0.16f * kotlin.math.cos(t2 * kotlin.math.PI).toFloat())
            val orb2Y = h * (0.52f + 0.10f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())
            val orb2Radius = baseDim * (0.48f + 0.06f * kotlin.math.cos(t3 * kotlin.math.PI).toFloat())

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2E7C4DFF),
                        Color(0x126366F1),
                        Color.Transparent
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                ),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            )

            // Orb 3: Soft Emerald Green (floats bottom right)
            val orb3X = w * (0.80f - 0.20f * kotlin.math.sin(t3 * kotlin.math.PI).toFloat())
            val orb3Y = h * (0.82f - 0.08f * kotlin.math.cos(t3 * kotlin.math.PI).toFloat())
            val orb3Radius = baseDim * (0.42f + 0.06f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2810B981),
                        Color(0x10059669),
                        Color.Transparent
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = orb3Radius
                ),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            )
        } else {
            // Base Gradient: Soft Light Aurora Mesh
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        AuroraCyanTop,        // #E0F7FA (Light soft cyan/blue)
                        AuroraCyanLight,      // Soft blend
                        AuroraWhiteMid,       // #FFFFFF (Clean white)
                        AuroraPurpleLight,    // Soft blend
                        AuroraPurpleBottom    // #F3E5F5 (Gentle muted purple)
                    )
                )
            )

            // Orb 1: Vibrant Cyan / Sky Glow (floats top left)
            val orb1X = w * (0.12f + 0.18f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())
            val orb1Y = h * (0.14f + 0.10f * kotlin.math.cos(t1 * kotlin.math.PI).toFloat())
            val orb1Radius = baseDim * (0.50f + 0.06f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3B00E5FF),
                        Color(0x1A00BCD4),
                        Color.Transparent
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                ),
                center = Offset(orb1X, orb1Y),
                radius = orb1Radius
            )

            // Orb 2: Lavender / Lilac Glow (floats middle right)
            val orb2X = w * (0.88f - 0.18f * kotlin.math.cos(t2 * kotlin.math.PI).toFloat())
            val orb2Y = h * (0.40f + 0.12f * kotlin.math.sin(t2 * kotlin.math.PI).toFloat())
            val orb2Radius = baseDim * (0.46f + 0.06f * kotlin.math.cos(t3 * kotlin.math.PI).toFloat())

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x308B5CF6),
                        Color(0x12A855F7),
                        Color.Transparent
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                ),
                center = Offset(orb2X, orb2Y),
                radius = orb2Radius
            )

            // Orb 3: Fresh Cyan / Turquoise (floats bottom center)
            val orb3X = w * (0.40f + 0.25f * kotlin.math.sin(t3 * kotlin.math.PI).toFloat())
            val orb3Y = h * (0.82f - 0.08f * kotlin.math.cos(t1 * kotlin.math.PI).toFloat())
            val orb3Radius = baseDim * (0.46f + 0.06f * kotlin.math.sin(t1 * kotlin.math.PI).toFloat())

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2E00BCD4),
                        Color(0x120284C7),
                        Color.Transparent
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = orb3Radius
                ),
                center = Offset(orb3X, orb3Y),
                radius = orb3Radius
            )
        }
    }
}

/**
 * Glassmorphism Cards & Containers:
 * - In Dark Mode: Deep frosted glass surface (rgba(30, 41, 59, 0.85) / rgba(15, 23, 42, 0.85))
 *   with thin luminous specular border.
 * - In Light Mode: Semi-transparent white background (rgba(255, 255, 255, 0.85 - 0.90))
 *   with polished translucent white border.
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
    if (isNeumorphic) {
        val surfaceColor = if (isDark) Color(0xFF1A2232) else Color(0xFFE8EEF5)
        val lightShadow = if (isDark) Color(0x1AFFFFFF) else Color(0xF2FFFFFF)
        val darkShadow = if (isDark) Color(0x750A0E17) else Color(0x559CB0C6)
        val isCircle = shape == CircleShape
        val borderBrush = Brush.linearGradient(
            if (isDark) {
                listOf(
                    Color.White.copy(alpha = 0.12f),
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.45f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.90f),
                    Color.Transparent,
                    Color(0x2094A3B8)
                )
            }
        )

        return this
            .drawBehind {
                val elevationPx = (elevation + 2.dp).toPx()
                val crPx = cornerRadius.toPx()

                drawIntoCanvas { canvas ->
                    val nativeCanvas = canvas.nativeCanvas

                    if (isCircle) {
                        val radius = size.minDimension / 2f
                        val cx = size.width / 2f
                        val cy = size.height / 2f

                        val lightPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.3f, -elevationPx * 0.75f, -elevationPx * 0.75f, lightShadow.toArgb())
                        }
                        nativeCanvas.drawCircle(cx, cy, radius, lightPaint)

                        val darkPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.5f, elevationPx * 0.85f, elevationPx * 0.85f, darkShadow.toArgb())
                        }
                        nativeCanvas.drawCircle(cx, cy, radius, darkPaint)
                    } else {
                        val lightPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.3f, -elevationPx * 0.75f, -elevationPx * 0.75f, lightShadow.toArgb())
                        }
                        nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, crPx, crPx, lightPaint)

                        val darkPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.5f, elevationPx * 0.85f, elevationPx * 0.85f, darkShadow.toArgb())
                        }
                        nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, crPx, crPx, darkPaint)
                    }
                }
            }
            .clip(shape)
            .background(surfaceColor)
            .border(BorderStroke(0.8.dp, borderBrush), shape)
    }

    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xEB1E293B),
                Color(0xDF0F172A)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xF0FFFFFF),
                Color(0xD9FFFFFF),
                Color(0xD4FFFFFF)
            )
        )
    }

    val borderBrush = if (isDark) {
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
    if (isNeumorphic) {
        val surfaceColor = if (isDark) Color(0xFF1A2232) else Color(0xFFE8EEF5)
        val lightShadow = if (isDark) Color(0x1AFFFFFF) else Color(0xF2FFFFFF)
        val darkShadow = if (isDark) Color(0x750A0E17) else Color(0x559CB0C6)
        val isCircle = shape == CircleShape
        val borderBrush = Brush.linearGradient(
            if (isDark) {
                listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, Color.Black.copy(alpha = 0.40f))
            } else {
                listOf(Color.White.copy(alpha = 0.90f), Color.Transparent, Color(0x2094A3B8))
            }
        )

        return this
            .drawBehind {
                val elevationPx = 3.5.dp.toPx()
                val crPx = cornerRadius.toPx()

                drawIntoCanvas { canvas ->
                    val nativeCanvas = canvas.nativeCanvas

                    if (isCircle) {
                        val radius = size.minDimension / 2f
                        val cx = size.width / 2f
                        val cy = size.height / 2f

                        val lightPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.3f, -elevationPx * 0.75f, -elevationPx * 0.75f, lightShadow.toArgb())
                        }
                        nativeCanvas.drawCircle(cx, cy, radius, lightPaint)

                        val darkPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.5f, elevationPx * 0.85f, elevationPx * 0.85f, darkShadow.toArgb())
                        }
                        nativeCanvas.drawCircle(cx, cy, radius, darkPaint)
                    } else {
                        val lightPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.3f, -elevationPx * 0.75f, -elevationPx * 0.75f, lightShadow.toArgb())
                        }
                        nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, crPx, crPx, lightPaint)

                        val darkPaint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = surfaceColor.toArgb()
                            setShadowLayer(elevationPx * 1.5f, elevationPx * 0.85f, elevationPx * 0.85f, darkShadow.toArgb())
                        }
                        nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, crPx, crPx, darkPaint)
                    }
                }
            }
            .clip(shape)
            .background(surfaceColor)
            .border(BorderStroke(0.8.dp, borderBrush), shape)
    }

    val bg = if (isDark) Color(0xEE1E293B) else Color(0xEBFFFFFF)
    val borderBrush = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = if (isDark) 0.5f else 0.95f),
            glowColor.copy(alpha = if (isDark) 0.7f else 0.5f),
            Color.White.copy(alpha = if (isDark) 0.25f else 0.85f)
        )
    )
    return this
        .clip(shape)
        .background(bg)
        .border(BorderStroke(1.2.dp, borderBrush), shape)
}
