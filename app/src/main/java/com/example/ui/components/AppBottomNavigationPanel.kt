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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuroraCyanTop
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RoyalBlueAccent
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
    val icon: ImageVector,
    val labelKey: String,
    val testTag: String
)

private val dockTabs = listOf(
    DockTabSpec(AppNavPage.DASHBOARD, Icons.Default.GridView, "nav_dashboard", "bottom_nav_dashboard"),
    DockTabSpec(AppNavPage.RECORDS, Icons.Default.People, "nav_records", "bottom_nav_records"),
    DockTabSpec(AppNavPage.DATABASE, Icons.Default.Storage, "nav_database", "bottom_nav_database"),
    DockTabSpec(AppNavPage.SETTINGS, Icons.Default.Settings, "nav_settings", "bottom_nav_settings")
)

@Composable
fun AppBottomNavigationPanel(
    selectedPage: AppNavPage,
    onSelectPage: (AppNavPage) -> Unit,
    notificationsCount: Int,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean,
    currentLang: AppLanguage,
    modifier: Modifier = Modifier,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero
) {
    val preset = LocalThemePreset.current
    val isLiving = LocalLivingLuxuryMode.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val activeAccent = preset?.primary ?: if (effectiveDark) CyanGlow else RoyalBlueAccent
    val secondaryAccent = preset?.secondary ?: if (effectiveDark) Color(0xFF8B5CF6) else VibrantCyan

    val tabBounds = remember { mutableStateMapOf<AppNavPage, Pair<Float, Float>>() }

    val selectedTabKey = if (selectedPage == AppNavPage.NOTIFICATIONS || selectedPage == AppNavPage.ANALYTICS) null else selectedPage
    val targetBounds = selectedTabKey?.let { tabBounds[it] }
    val hasMeasuredBounds = targetBounds != null && targetBounds.second > 0f

    val lensX by animateFloatAsState(
        targetValue = targetBounds?.first ?: 0f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = 0.78f,
                stiffness = 380f
            )
        } else tween(0),
        label = "liquidGlassLensX"
    )
    val lensWidth by animateFloatAsState(
        targetValue = targetBounds?.second ?: 0f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = 0.80f,
                stiffness = 400f
            )
        } else tween(0),
        label = "liquidGlassLensWidth"
    )

    // Subtle liquid droplet stretch while gliding between tabs
    val glideDelta = if (targetBounds != null) abs(lensX - targetBounds.first) else 0f
    val stretchFactor = (1f + (glideDelta / 380f).coerceIn(0f, 0.14f))

    // Outer container is 100% transparent — zero scrim, zero opaque bar, full-bleed content passes right behind it
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        LiquidGlassFloatingCapsule(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .height(66.dp)
                .testTag("bottom_nav_panel"),
            isDarkMode = effectiveDark,
            activeAccent = activeAccent,
            secondaryAccent = secondaryAccent,
            isLiving = isLiving,
            backdropLayer = backdropLayer,
            backdropOffsetInRoot = backdropOffsetInRoot
        ) {
            // 3. Tabs Row + Fluid Sliding Liquid Glass Lens Pill
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 5.dp)
            ) {
                if (hasMeasuredBounds && lensWidth > 0f) {
                    val lensWidthDp = with(density) { lensWidth.toDp() }
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(lensX.roundToInt(), 0) }
                            .width(lensWidthDp)
                            .fillMaxHeight()
                            .graphicsLayer {
                                scaleX = stretchFactor
                                scaleY = if (glideDelta > 4f) 1.03f else 1f
                            }
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    if (effectiveDark) {
                                        listOf(
                                            Color.White.copy(alpha = 0.20f),
                                            activeAccent.copy(alpha = 0.16f),
                                            Color.White.copy(alpha = 0.09f)
                                        )
                                    } else {
                                        listOf(
                                            Color.White.copy(alpha = 0.82f),
                                            Color.White.copy(alpha = 0.56f),
                                            activeAccent.copy(alpha = 0.14f)
                                        )
                                    }
                                )
                            )
                            .drawBehind {
                                // Crisp lens top specular highlight
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = if (effectiveDark) 0.24f else 0.72f),
                                            Color.Transparent
                                        ),
                                        startY = 0f,
                                        endY = size.height * 0.45f
                                    ),
                                    cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                                )
                                // Delicate specular rim around the sliding glass lens
                                drawRoundRect(
                                    brush = Brush.linearGradient(
                                        if (effectiveDark) {
                                            listOf(
                                                Color.White.copy(alpha = 0.42f),
                                                activeAccent.copy(alpha = 0.50f),
                                                Color.White.copy(alpha = 0.14f)
                                            )
                                        } else {
                                            listOf(
                                                Color.White.copy(alpha = 0.98f),
                                                activeAccent.copy(alpha = 0.34f),
                                                Color.White.copy(alpha = 0.75f)
                                            )
                                        }
                                    ),
                                    cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
                                    style = Stroke(width = 1.15.dp.toPx())
                                )
                            }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dockTabs.forEach { tab ->
                        val isSelected = selectedPage == tab.page
                        BottomNavItem(
                            icon = tab.icon,
                            label = AppStrings.get(tab.labelKey, currentLang),
                            isSelected = isSelected,
                            badgeCount = if (tab.page == AppNavPage.NOTIFICATIONS) notificationsCount else 0,
                            isDarkMode = effectiveDark,
                            activeColor = activeAccent,
                            textSecondary = preset?.textSecondary,
                            animationsEnabled = animationsEnabled,
                            isLiving = isLiving,
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

@Composable
fun LiquidGlassFloatingCapsule(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    activeAccent: Color,
    secondaryAccent: Color,
    isLiving: Boolean = false,
    showBorder: Boolean = true,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero,
    content: @Composable BoxScope.() -> Unit
) {
    val capsuleShape = CircleShape
    var capsulePositionInRoot by remember { mutableStateOf(Offset.Zero) }

    val supportsHardwareBlur = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !Build.FINGERPRINT.equals("robolectric", ignoreCase = true)
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isLiving) 18.dp else 14.dp,
                shape = capsuleShape,
                clip = false,
                ambientColor = if (isDarkMode) Color(0x99000000) else Color(0x380F172A),
                spotColor = if (isDarkMode) {
                    activeAccent.copy(alpha = 0.35f)
                } else {
                    activeAccent.copy(alpha = 0.22f)
                }
            )
            .clip(capsuleShape)
            .onGloballyPositioned { coords ->
                capsulePositionInRoot = coords.positionInRoot()
            }
    ) {
        // 1. Live Optical Backdrop Refraction & Frosted Blur (Real content flowing underneath)
        if (backdropLayer != null) {
            val blurModifier = if (supportsHardwareBlur) {
                Modifier.blur(radius = 22.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
            } else {
                Modifier
            }
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .then(blurModifier)
            ) {
                val layerSize = backdropLayer.size
                if (layerSize.width > 0 && layerSize.height > 0) {
                    val dx = backdropOffsetInRoot.x - capsulePositionInRoot.x
                    val dy = backdropOffsetInRoot.y - capsulePositionInRoot.y
                    withTransform({
                        // Subtle convex lens magnification of the underlying content
                        scale(scaleX = 1.045f, scaleY = 1.045f, pivot = center)
                        translate(left = dx, top = dy)
                    }) {
                        drawLayer(backdropLayer)
                    }
                }
            }
        }

        // 2. iOS 26 Liquid Glass Translucent Medium + Specular Dome Gloss + Caustic Rim
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        if (isDarkMode) {
                            listOf(
                                Color(0xFF1E293B).copy(alpha = if (backdropLayer != null) 0.44f else 0.72f),
                                Color(0xFF0F172A).copy(alpha = if (backdropLayer != null) 0.34f else 0.64f),
                                Color(0xFF1E293B).copy(alpha = if (backdropLayer != null) 0.42f else 0.70f)
                            )
                        } else {
                            listOf(
                                Color.White.copy(alpha = if (backdropLayer != null) 0.44f else 0.72f),
                                Color(0xFFF8FAFC).copy(alpha = if (backdropLayer != null) 0.24f else 0.56f),
                                Color.White.copy(alpha = if (backdropLayer != null) 0.36f else 0.66f)
                            )
                        }
                    )
                )
                .drawBehind {
                    // Subtle chromatic glass tint wash
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            listOf(
                                activeAccent.copy(alpha = if (isDarkMode) 0.08f else 0.05f),
                                Color.Transparent,
                                secondaryAccent.copy(alpha = if (isDarkMode) 0.07f else 0.04f)
                            )
                        ),
                        cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                    )
                    // Top curved specular dome gloss (signature iOS Liquid Glass highlight)
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDarkMode) 0.16f else 0.54f),
                                Color.White.copy(alpha = if (isDarkMode) 0.04f else 0.14f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.48f
                        ),
                        cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                    )
                    // Subtle bottom internal caustic bounce
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = if (isDarkMode) 0.06f else 0.22f)
                            ),
                            startY = size.height * 0.72f,
                            endY = size.height
                        ),
                        cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                    )
                }
                .then(
                    if (showBorder) {
                        Modifier.border(
                            width = 1.25.dp,
                            brush = Brush.linearGradient(
                                colors = if (isDarkMode) {
                                    listOf(
                                        Color.White.copy(alpha = 0.48f),
                                        activeAccent.copy(alpha = 0.34f),
                                        Color.White.copy(alpha = 0.10f),
                                        Color.White.copy(alpha = 0.26f)
                                    )
                                } else {
                                    listOf(
                                        Color.White.copy(alpha = 0.95f),
                                        activeAccent.copy(alpha = 0.26f),
                                        Color.White.copy(alpha = 0.42f),
                                        Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            ),
                            shape = capsuleShape
                        )
                    } else {
                        Modifier
                    }
                )
        )

        content()
    }
}

/**
 * Progressive Fade-to-Blur atmospheric curtain rendered above and behind the floating Top Bar:
 * - Above the top bar (status bar / top margin), scrolling content smoothly fades into a 24.dp frosted blur.
 * - Moving downward behind the floating capsule, the blur and mist feather cleanly down to 0% opacity with no hard edge.
 */
@Composable
fun TopBarFadeToBlurOverlay(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val mistColor = preset?.backgroundTop ?: if (effectiveDark) Color(0xFF090D1A) else AuroraCyanTop

    var overlayPositionInRoot by remember { mutableStateOf(Offset.Zero) }
    val supportsHardwareBlur = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !Build.FINGERPRINT.equals("robolectric", ignoreCase = true)
    }

    val blurFadeMask = remember {
        Brush.verticalGradient(
            0.00f to Color.Black,
            0.38f to Color.Black.copy(alpha = 0.96f),
            0.68f to Color.Black.copy(alpha = 0.56f),
            0.88f to Color.Black.copy(alpha = 0.16f),
            1.00f to Color.Transparent
        )
    }

    val atmosphericVeil = remember(mistColor, effectiveDark, supportsHardwareBlur) {
        val topAlpha = if (supportsHardwareBlur) {
            if (effectiveDark) 0.40f else 0.34f
        } else {
            if (effectiveDark) 0.78f else 0.72f
        }
        val midAlpha = if (supportsHardwareBlur) {
            if (effectiveDark) 0.22f else 0.18f
        } else {
            if (effectiveDark) 0.48f else 0.42f
        }
        val tailAlpha = if (supportsHardwareBlur) {
            if (effectiveDark) 0.06f else 0.04f
        } else {
            if (effectiveDark) 0.14f else 0.10f
        }
        Brush.verticalGradient(
            0.00f to mistColor.copy(alpha = topAlpha),
            0.38f to mistColor.copy(alpha = midAlpha),
            0.78f to mistColor.copy(alpha = tailAlpha),
            1.00f to Color.Transparent
        )
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
                        .blur(radius = 24.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
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
    isLiving: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val inactiveColor = textSecondary
        ?: if (isDarkMode) Color(0xFFD1D5DB).copy(alpha = 0.78f) else Color(0xFF334155)

    val itemColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = if (animationsEnabled || isLiving) tween(200, easing = FastOutSlowInEasing) else tween(0),
        label = "navColor"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val livingPulse = if (isLiving && isSelected) {
        val t = rememberInfiniteTransition(label = "navLivingPulse")
        t.animateFloat(
            initialValue = 1.02f,
            targetValue = 1.07f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "navBreathScale"
        ).value
    } else 1f

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            isSelected -> 1.04f
            else -> 1f
        },
        animationSpec = if (animationsEnabled || isLiving) {
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
        } else tween(0),
        label = "navScale"
    )

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer {
                val s = if (isLiving && isSelected && !isPressed) livingPulse else scale
                scaleX = s
                scaleY = s
            }
            .clip(CircleShape)
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
                            containerColor = Color(0xFFEF4444),
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
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = itemColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = itemColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
