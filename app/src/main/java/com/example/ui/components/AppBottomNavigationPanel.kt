package com.example.ui.components

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuroraCyanTop
import com.example.ui.theme.AuroraPurpleBottom
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlin.math.abs
import kotlin.math.roundToInt

enum class AppNavPage {
    DASHBOARD,
    RECORDS,
    DATABASE,
    ANALYTICS,
    SETTINGS,
    NOTIFICATIONS
}

private data class DockTabSpec(
    val page: AppNavPage,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelKey: String,
    val testTag: String
)

private val dockTabs = listOf(
    DockTabSpec(AppNavPage.DASHBOARD, Icons.Filled.GridView, Icons.Outlined.GridView, "nav_dashboard", "bottom_nav_dashboard"),
    DockTabSpec(AppNavPage.RECORDS, Icons.Filled.Group, Icons.Outlined.Group, "nav_records", "bottom_nav_records"),
    DockTabSpec(AppNavPage.DATABASE, Icons.AutoMirrored.Filled.FormatListBulleted, Icons.AutoMirrored.Filled.FormatListBulleted, "nav_database", "bottom_nav_database"),
    DockTabSpec(AppNavPage.SETTINGS, Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings", "bottom_nav_settings")
)

@Composable
fun AppBottomNavigationPanel(
    selectedPage: AppNavPage,
    onSelectPage: (AppNavPage) -> Unit,
    isDarkMode: Boolean,
    animationsEnabled: Boolean,
    currentLang: AppLanguage,
    modifier: Modifier = Modifier,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val isLivingLuxury = LocalLivingLuxuryMode.current && animationsEnabled
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val activeAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan

    val dockBreath = if (isLivingLuxury) {
        val inf = rememberInfiniteTransition(label = "livingDock")
        val p by inf.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dockBreath"
        )
        p
    } else 0f

    val tabBounds = remember { mutableStateMapOf<AppNavPage, Pair<Float, Float>>() }

    val selectedTabKey = if (selectedPage == AppNavPage.NOTIFICATIONS || selectedPage == AppNavPage.ANALYTICS) null else selectedPage
    val targetBounds = selectedTabKey?.let { tabBounds[it] }
    val hasMeasuredBounds = targetBounds != null && targetBounds.second > 0f

    val lensX by animateFloatAsState(
        targetValue = targetBounds?.first ?: 0f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = if (isLivingLuxury) 0.78f else Spring.DampingRatioNoBouncy,
                stiffness = if (isLivingLuxury) 360f else 420f
            )
        } else tween(0),
        label = "cleanNavPillX"
    )
    val lensWidth by animateFloatAsState(
        targetValue = targetBounds?.second ?: 0f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = if (isLivingLuxury) 0.78f else Spring.DampingRatioNoBouncy,
                stiffness = if (isLivingLuxury) 360f else 420f
            )
        } else tween(0),
        label = "cleanNavPillWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        BottomDockFadeToBlurOverlay(
            modifier = Modifier
                .matchParentSize()
                .testTag("bottom_dock_fade_to_blur"),
            isDarkMode = effectiveDark,
            backdropLayer = backdropLayer,
            backdropOffsetInRoot = backdropOffsetInRoot
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassFloatingCapsule(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .height(68.dp)
                    .testTag("bottom_nav_panel"),
                isDarkMode = effectiveDark,
                activeAccent = activeAccent,
                isLiving = isLivingLuxury,
                backdropLayer = backdropLayer,
                backdropOffsetInRoot = backdropOffsetInRoot
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    if (hasMeasuredBounds && lensWidth > 0f) {
                        val lensWidthDp = with(density) { lensWidth.toDp() }
                        val pillShape = RoundedCornerShape(16.dp)
                        val pillAlpha = if (effectiveDark) {
                            if (isLivingLuxury) 0.20f + 0.08f * dockBreath else 0.22f
                        } else {
                            if (isLivingLuxury) 0.11f + 0.06f * dockBreath else 0.12f
                        }
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(lensX.roundToInt(), 0) }
                                .width(lensWidthDp)
                                .fillMaxHeight()
                                .clip(pillShape)
                                .background(activeAccent.copy(alpha = pillAlpha))
                                .border(
                                    width = if (isLivingLuxury) 1.2.dp else 1.dp,
                                    color = activeAccent.copy(
                                        alpha = if (effectiveDark) {
                                            if (isLivingLuxury) 0.30f + 0.18f * dockBreath else 0.30f
                                        } else {
                                            if (isLivingLuxury) 0.16f + 0.14f * dockBreath else 0.14f
                                        }
                                    ),
                                    shape = pillShape
                                )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dockTabs.forEach { tab ->
                            val isSelected = selectedPage == tab.page
                            BottomNavItem(
                                icon = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                label = AppStrings.get(tab.labelKey, currentLang),
                                isSelected = isSelected,
                                badgeCount = 0,
                                isDarkMode = effectiveDark,
                                activeColor = activeAccent,
                                textSecondary = preset?.textSecondary,
                                animationsEnabled = animationsEnabled,
                                isLivingLuxury = isLivingLuxury,
                                dockBreath = dockBreath,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelectPage(tab.page)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .onGloballyPositioned { coords ->
                                        val rawX = coords.positionInParent().x
                                        val w = coords.size.width.toFloat()
                                        val parentW = coords.parentLayoutCoordinates?.size?.width?.toFloat() ?: 0f
                                        val startRelativeX = if (isRtl && parentW > 0f) {
                                            (parentW - (rawX + w)).coerceAtLeast(0f)
                                        } else {
                                            rawX
                                        }
                                        val existing = tabBounds[tab.page]
                                        if (existing == null || abs(existing.first - startRelativeX) > 0.5f || abs(existing.second - w) > 0.5f) {
                                            tabBounds[tab.page] = startRelativeX to w
                                        }
                                    },
                                testTag = tab.testTag
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Clean frosted floating capsule bar with subtle optional Android 12+ backdrop blur
 * and neutral hairline border (zero chromatic tint wash or lens distortion).
 */
@Composable
fun LiquidGlassFloatingCapsule(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    activeAccent: Color,
    isLiving: Boolean = false,
    showBorder: Boolean = true,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero,
    content: @Composable BoxScope.() -> Unit
) {
    val capsuleShape = RoundedCornerShape(24.dp)
    var capsulePositionInRoot by remember { mutableStateOf(Offset.Zero) }

    val supportsHardwareBlur = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !Build.FINGERPRINT.equals("robolectric", ignoreCase = true)
    }

    val fillColor = if (isDarkMode) {
        Color(0xFF13202B).copy(alpha = if (backdropLayer != null && supportsHardwareBlur) 0.78f else 0.92f)
    } else {
        Color.White.copy(alpha = if (backdropLayer != null && supportsHardwareBlur) 0.80f else 0.90f)
    }

    val capsuleBreath = if (isLiving) {
        val inf = rememberInfiniteTransition(label = "livingCapsule")
        val p by inf.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "capsuleBreath"
        )
        p
    } else 0f

    val borderBrush = if (isDarkMode) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (isLiving) 0.20f + 0.12f * capsuleBreath else 0.18f),
                if (isLiving) activeAccent.copy(alpha = 0.20f + 0.14f * capsuleBreath) else Color(0xFF253746)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.96f),
                if (isLiving) activeAccent.copy(alpha = 0.18f + 0.12f * capsuleBreath) else Color(0xFFD5E3E2)
            )
        )
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isLiving) 10.dp else 8.dp,
                shape = capsuleShape,
                clip = false,
                ambientColor = if (isDarkMode) Color(0x66000000) else Color(0x160F172A),
                spotColor = if (isLiving) activeAccent.copy(alpha = 0.28f) else if (isDarkMode) Color(0x77000000) else Color(0x1E0D9488)
            )
            .clip(capsuleShape)
            .onGloballyPositioned { coords ->
                capsulePositionInRoot = coords.positionInRoot()
            }
    ) {
        if (backdropLayer != null && supportsHardwareBlur) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .blur(radius = if (isLiving) 22.dp else 18.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
            ) {
                val layerSize = backdropLayer.size
                if (layerSize.width > 0 && layerSize.height > 0) {
                    val dx = backdropOffsetInRoot.x - capsulePositionInRoot.x
                    val dy = backdropOffsetInRoot.y - capsulePositionInRoot.y
                    withTransform({
                        translate(left = dx, top = dy)
                    }) {
                        drawLayer(backdropLayer)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(fillColor)
                .then(
                    if (isLiving) {
                        Modifier.drawWithContent {
                            drawContent()
                            val sheenAlpha = (if (isDarkMode) 0.14f else 0.36f) * (0.55f + 0.45f * capsuleBreath)
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = sheenAlpha),
                                        activeAccent.copy(alpha = sheenAlpha * 0.65f),
                                        Color.Transparent
                                    )
                                ),
                                start = Offset(size.width * 0.12f, 1.5.dp.toPx()),
                                end = Offset(size.width * 0.88f, 1.5.dp.toPx()),
                                strokeWidth = 1.6.dp.toPx()
                            )
                        }
                    } else Modifier
                )
                .then(
                    if (showBorder) {
                        Modifier.border(if (isLiving) 1.2.dp else 1.dp, borderBrush, capsuleShape)
                    } else {
                        Modifier
                    }
                )
        )

        content()
    }
}

private enum class FadeBlurEdge {
    TOP,
    BOTTOM
}

@Composable
fun TopBarFadeToBlurOverlay(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero
) {
    EdgeFadeToBlurOverlay(
        edge = FadeBlurEdge.TOP,
        modifier = modifier,
        isDarkMode = isDarkMode,
        backdropLayer = backdropLayer,
        backdropOffsetInRoot = backdropOffsetInRoot
    )
}

@Composable
fun BottomDockFadeToBlurOverlay(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero
) {
    EdgeFadeToBlurOverlay(
        edge = FadeBlurEdge.BOTTOM,
        modifier = modifier,
        isDarkMode = isDarkMode,
        backdropLayer = backdropLayer,
        backdropOffsetInRoot = backdropOffsetInRoot
    )
}

@Composable
private fun EdgeFadeToBlurOverlay(
    edge: FadeBlurEdge,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val mistColor = when (edge) {
        FadeBlurEdge.TOP -> preset?.backgroundTop ?: if (effectiveDark) Color(0xFF0B1220) else AuroraCyanTop
        FadeBlurEdge.BOTTOM -> preset?.backgroundBottom ?: if (effectiveDark) Color(0xFF111B2E) else AuroraPurpleBottom
    }

    var overlayPositionInRoot by remember { mutableStateOf(Offset.Zero) }
    val supportsHardwareBlur = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !Build.FINGERPRINT.equals("robolectric", ignoreCase = true)
    }

    val blurFadeMask = remember(edge) {
        if (edge == FadeBlurEdge.TOP) {
            Brush.verticalGradient(
                0.00f to Color.Black,
                0.42f to Color.Black.copy(alpha = 0.92f),
                0.72f to Color.Black.copy(alpha = 0.48f),
                1.00f to Color.Transparent
            )
        } else {
            Brush.verticalGradient(
                0.00f to Color.Transparent,
                0.28f to Color.Black.copy(alpha = 0.48f),
                0.58f to Color.Black.copy(alpha = 0.92f),
                1.00f to Color.Black
            )
        }
    }

    val atmosphericVeil = remember(edge, mistColor, effectiveDark, supportsHardwareBlur) {
        val peakAlpha = if (supportsHardwareBlur) {
            if (effectiveDark) 0.44f else 0.38f
        } else {
            if (effectiveDark) 0.80f else 0.76f
        }
        val midAlpha = if (supportsHardwareBlur) {
            if (effectiveDark) 0.22f else 0.18f
        } else {
            if (effectiveDark) 0.46f else 0.42f
        }
        if (edge == FadeBlurEdge.TOP) {
            Brush.verticalGradient(
                0.00f to mistColor.copy(alpha = peakAlpha),
                0.45f to mistColor.copy(alpha = midAlpha),
                1.00f to Color.Transparent
            )
        } else {
            Brush.verticalGradient(
                0.00f to Color.Transparent,
                0.55f to mistColor.copy(alpha = midAlpha),
                1.00f to mistColor.copy(alpha = peakAlpha)
            )
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onGloballyPositioned { coords ->
                overlayPositionInRoot = coords.positionInRoot()
            }
    ) {
        if (backdropLayer != null && supportsHardwareBlur) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = blurFadeMask,
                            blendMode = BlendMode.DstIn
                        )
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .blur(radius = 20.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
                ) {
                    val layerSize = backdropLayer.size
                    if (layerSize.width > 0 && layerSize.height > 0) {
                        val dx = backdropOffsetInRoot.x - overlayPositionInRoot.x
                        val dy = backdropOffsetInRoot.y - overlayPositionInRoot.y
                        withTransform({
                            translate(left = dx, top = dy)
                        }) {
                            drawLayer(backdropLayer)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(atmosphericVeil)
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int,
    isDarkMode: Boolean,
    activeColor: Color,
    textSecondary: Color?,
    animationsEnabled: Boolean,
    isLivingLuxury: Boolean = false,
    dockBreath: Float = 0f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val inactiveColor = textSecondary
        ?: if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary

    val itemColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = if (animationsEnabled) tween(180, easing = FastOutSlowInEasing) else tween(0),
        label = "navColor"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val baseScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else if (isSelected && isLivingLuxury) 1.04f else 1f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = if (isLivingLuxury) 0.72f else Spring.DampingRatioNoBouncy,
                stiffness = 420f
            )
        } else tween(0),
        label = "navScale"
    )
    val livingIconScale = if (isSelected && isLivingLuxury && !isPressed) {
        1f + 0.045f * dockBreath
    } else 1f

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer {
                scaleX = baseScale
                scaleY = baseScale
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = RedDanger,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (badgeCount > 99) "99+" else "$badgeCount",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = itemColor,
                        modifier = Modifier
                            .size(21.dp)
                            .graphicsLayer {
                                scaleX = livingIconScale
                                scaleY = livingIconScale
                            }
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = itemColor,
                    modifier = Modifier
                        .size(21.dp)
                        .graphicsLayer {
                            scaleX = livingIconScale
                            scaleY = livingIconScale
                        }
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = itemColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
