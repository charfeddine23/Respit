package com.example.ui.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KufiArabicBrandFontFamily
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppUpdateState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AppTopLiquidGlassBar(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean,
    notificationsCount: Int,
    isNotificationsOpen: Boolean,
    appUpdateState: AppUpdateState,
    onOpenNotifications: () -> Unit,
    onToggleTheme: () -> Unit,
    onOpenUpdateDialog: () -> Unit,
    modifier: Modifier = Modifier,
    backdropLayer: GraphicsLayer? = null,
    backdropOffsetInRoot: Offset = Offset.Zero
) {
    val preset = LocalThemePreset.current
    val isLiving = LocalLivingLuxuryMode.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val activeAccent = preset?.primary ?: if (effectiveDark) CyanGlow else RoyalBlueAccent
    val secondaryAccent = preset?.secondary ?: if (effectiveDark) Color(0xFF8B5CF6) else VibrantCyan

    val capsuleShape = CircleShape
    var capsulePositionInRoot by remember { mutableStateOf(Offset.Zero) }

    val supportsHardwareBlur = remember {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !Build.FINGERPRINT.equals("robolectric", ignoreCase = true)
    }

    val hasAvailableUpdate = appUpdateState is AppUpdateState.UpdateAvailable ||
        appUpdateState is AppUpdateState.Downloading ||
        appUpdateState is AppUpdateState.ReadyToInstall

    // Outer container is 100% transparent — zero scrim, zero opaque bar, full-bleed content passes right behind it
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Apple iOS 26 Liquid Glass Capsule (matching bottom dock architecture)
        Box(
            modifier = Modifier
                .widthIn(max = 980.dp)
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = if (isLiving) 18.dp else 14.dp,
                    shape = capsuleShape,
                    clip = false,
                    ambientColor = if (effectiveDark) {
                        Color(0x99000000)
                    } else {
                        Color(0x380F172A)
                    },
                    spotColor = if (effectiveDark) {
                        activeAccent.copy(alpha = 0.35f)
                    } else {
                        activeAccent.copy(alpha = 0.22f)
                    }
                )
                .clip(capsuleShape)
                .onGloballyPositioned { coords ->
                    capsulePositionInRoot = coords.positionInRoot()
                }
                .testTag("top_nav_panel")
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
                            if (effectiveDark) {
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
                                    activeAccent.copy(alpha = if (effectiveDark) 0.08f else 0.05f),
                                    Color.Transparent,
                                    secondaryAccent.copy(alpha = if (effectiveDark) 0.07f else 0.04f)
                                )
                            ),
                            cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                        )
                        // Top curved specular dome gloss (signature iOS Liquid Glass highlight)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (effectiveDark) 0.16f else 0.54f),
                                    Color.White.copy(alpha = if (effectiveDark) 0.04f else 0.14f),
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
                                    Color.White.copy(alpha = if (effectiveDark) 0.06f else 0.22f)
                                ),
                                startY = size.height * 0.72f,
                                endY = size.height
                            ),
                            cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                        )
                    }
                    .border(
                        width = 1.25.dp,
                        brush = Brush.linearGradient(
                            colors = if (effectiveDark) {
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
            )

            // 3. Inner Controls Row respecting LocalLayoutDirection (RTL for Arabic, LTR for EN/FR/TZM)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Start Side:
                // Arabic (RTL, Left->Right): [Update?] [Theme] [Notifications (Far Right)]
                // English (LTR, Left->Right): [Notifications (Far Left)] [Theme] [Update?]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val orbShape = CircleShape

                    // Notifications Button (Far Right in Arabic, Far Left in English)
                    Box(
                        modifier = Modifier.liquidGlassInteractive(
                            shape = orbShape,
                            glowColor = if (isNotificationsOpen || notificationsCount > 0) {
                                Color(0xFFEF4444)
                            } else {
                                activeAccent
                            },
                            isDark = effectiveDark,
                            isNeumorphic = isNeumorphic
                        )
                    ) {
                        IconButton(
                            onClick = onOpenNotifications,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(orbShape)
                                .testTag("header_notifications_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (notificationsCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFEF4444),
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = if (notificationsCount > 99) "99+" else "$notificationsCount",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isNotificationsOpen) {
                                        Icons.Default.NotificationsActive
                                    } else {
                                        Icons.Default.Notifications
                                    },
                                    contentDescription = AppStrings.get("header_notifications_desc", currentLang),
                                    tint = if (isNotificationsOpen) Color(0xFFEF4444) else activeAccent,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                    }

                    // Light / Dark Mode Toggle Button
                    Box(
                        modifier = Modifier.liquidGlassInteractive(
                            shape = orbShape,
                            glowColor = if (effectiveDark) Color(0xFFFBBF24) else activeAccent,
                            isDark = effectiveDark,
                            isNeumorphic = isNeumorphic
                        )
                    ) {
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(orbShape)
                                .testTag("header_theme_toggle")
                        ) {
                            Icon(
                                imageVector = if (effectiveDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = AppStrings.get(if (effectiveDark) "theme_light" else "theme_dark", currentLang),
                                tint = if (effectiveDark) Color(0xFFFBBF24) else activeAccent,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }

                    // Dynamic Update Button when an update exists
                    AnimatedVisibility(
                        visible = hasAvailableUpdate,
                        enter = fadeIn(tween(260)) + scaleIn(
                            initialScale = 0.72f,
                            animationSpec = spring(
                                dampingRatio = 0.65f,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ),
                        exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.75f)
                    ) {
                        HeaderUpdateDownloadButton(
                            updateState = appUpdateState,
                            isDarkMode = effectiveDark,
                            isNeumorphic = isNeumorphic,
                            animationsEnabled = animationsEnabled,
                            contentDescription = AppStrings.get("update_top_banner_desc", currentLang),
                            onClick = onOpenUpdateDialog
                        )
                    }
                }

                // End Side (Far Left in Arabic/RTL -> [Logo] [ريسبيت], Far Right in English/LTR -> [Respite] [Logo])
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("app_title_row")
                ) {
                    val isArabicTitle = currentLang == AppLanguage.ARABIC
                    Text(
                        text = AppStrings.get("app_title", currentLang),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = if (isArabicTitle) KufiArabicBrandFontFamily else FontFamily.Default,
                            fontSize = if (isArabicTitle) 19.sp else 18.sp,
                            lineHeight = 24.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = if (effectiveDark) Color.White else Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = AppStrings.get("app_icon_desc", currentLang),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            if (effectiveDark) Color.White else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(width = 48.dp, height = 36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderUpdateDownloadButton(
    updateState: AppUpdateState,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "headerDownloadScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "headerDownloadMotion")
    val arrowFloatY by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = -2.2f,
            targetValue = 2.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1050, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "arrowFloatY"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val ringRotation by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "headerRingRotation"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val haloPulse by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.55f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "headerHaloPulse"
        )
    } else {
        remember { mutableStateOf(0.8f) }
    }

    val shape = CircleShape
    val isReady = updateState is AppUpdateState.ReadyToInstall
    val isDownloading = updateState is AppUpdateState.Downloading
    val accentGlow = if (isReady) Color(0xFF10B981) else Color(0xFF00E5FF)

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .liquidGlassInteractive(
                shape = shape,
                glowColor = accentGlow,
                isDark = isDarkMode,
                isNeumorphic = isNeumorphic
            )
            .drawWithContent {
                drawContent()
                if (size.width <= 0f || size.height <= 0f) return@drawWithContent
                val strokePx = 1.6.dp.toPx()
                val insetPx = 1.4.dp.toPx()
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = (size.minDimension / 2f).coerceAtLeast(1f)
                val rad = Math.toRadians(ringRotation.toDouble())
                val dx = (cos(rad) * radius).toFloat()
                val dy = (sin(rad) * radius).toFloat()

                val ringColors = if (isReady) {
                    listOf(
                        Color(0xFF10B981).copy(alpha = 0.92f * haloPulse),
                        Color(0xFF34D399).copy(alpha = 0.55f * haloPulse),
                        Color(0xFF00E5FF).copy(alpha = 0.85f * haloPulse)
                    )
                } else {
                    listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.92f * haloPulse),
                        Color(0xFF3B82F6).copy(alpha = 0.55f * haloPulse),
                        Color(0xFF10B981).copy(alpha = 0.85f * haloPulse)
                    )
                }
                val ringBrush = Brush.linearGradient(
                    colors = ringColors,
                    start = Offset(cx + dx, cy + dy),
                    end = Offset(cx - dx, cy - dy)
                )

                drawCircle(
                    brush = ringBrush,
                    radius = radius - insetPx,
                    center = Offset(cx, cy),
                    style = Stroke(width = strokePx)
                )
            }
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClickLabel = contentDescription
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .testTag("header_update_download_button"),
        contentAlignment = Alignment.Center
    ) {
        when {
            isDownloading -> {
                val dlState = updateState as AppUpdateState.Downloading
                if (dlState.totalBytes > 0L) {
                    CircularProgressIndicator(
                        progress = { dlState.progress.coerceIn(0f, 1f) },
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFF00E5FF).copy(alpha = 0.22f),
                        strokeWidth = 2.4.dp,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        color = Color(0xFF00E5FF),
                        strokeWidth = 2.4.dp,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = contentDescription,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer { translationY = arrowFloatY.dp.toPx() * 0.5f }
                )
            }
            isReady -> {
                Icon(
                    imageVector = Icons.Default.DownloadDone,
                    contentDescription = contentDescription,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(21.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = contentDescription,
                    tint = if (isDarkMode) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(21.dp)
                        .graphicsLayer { translationY = arrowFloatY.dp.toPx() }
                )
            }
        }
    }
}
