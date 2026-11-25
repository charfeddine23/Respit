package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppUpdateManager
import com.example.util.AppUpdateState
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun InAppUpdateDialog(
    updateState: AppUpdateState,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean,
    currentLang: AppLanguage,
    onStartDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    onInstallApk: () -> Unit,
    onRetryCheck: () -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val enterState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }

    val themePreset = LocalThemePreset.current
    val effectiveDark = themePreset?.isDark ?: isDarkMode
    val primaryColor = themePreset?.primary ?: MaterialTheme.colorScheme.primary
    val accentCyan = themePreset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val cardShape = RoundedCornerShape(28.dp)

    Dialog(
        onDismissRequest = {
            if (updateState !is AppUpdateState.Downloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnBackPress = updateState !is AppUpdateState.Downloading,
            dismissOnClickOutside = updateState !is AppUpdateState.Downloading
        )
    ) {
        SyncDialogLiquidGlassWindow()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (updateState !is AppUpdateState.Downloading) {
                        onDismiss()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val cardWidth = (maxWidth - 40.dp).coerceIn(260.dp, 360.dp)

            AnimatedVisibility(
                visibleState = enterState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(200)) + scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                } else androidx.compose.animation.EnterTransition.None,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("in_app_update_dialog"),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = cardShape,
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .width(cardWidth)
                            .liquidGlassDialogShell(
                                shape = cardShape,
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = accentCyan,
                                secondaryAccent = RoyalBlueAccent
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 18.dp)
                        ) {
                            // Top Corner Frosted Close Icon (48dp touch target)
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (updateState is AppUpdateState.Downloading) {
                                        onCancelDownload()
                                    } else {
                                        onDismiss()
                                    }
                                },
                                shape = CircleShape,
                                color = Color.Transparent,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(48.dp)
                                    .liquidGlassPanel(
                                        shape = CircleShape,
                                        cornerRadius = 24.dp,
                                        isDark = effectiveDark,
                                        accent = accentCyan,
                                        secondaryAccent = RoyalBlueAccent
                                    )
                                    .testTag("update_dialog_close_button")
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = AppStrings.get("close", currentLang),
                                        tint = if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                UpdateHeroEmblem(
                                    updateState = updateState,
                                    accentCyan = accentCyan,
                                    isDarkMode = effectiveDark
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                AnimatedContent(
                                    targetState = updateState,
                                    contentAlignment = Alignment.Center,
                                    transitionSpec = {
                                        fadeIn(tween(240)) togetherWith fadeOut(tween(180))
                                    },
                                    label = "updateStateBodyTransition"
                                ) { state ->
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        when (state) {
                                            is AppUpdateState.Idle,
                                            is AppUpdateState.Checking -> {
                                                Text(
                                                    text = AppStrings.get("update_checking_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "v${BuildConfig.VERSION_NAME}",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = accentCyan
                                                )
                                                Spacer(modifier = Modifier.height(14.dp))
                                                CircularProgressIndicator(
                                                    color = accentCyan,
                                                    strokeWidth = 3.dp,
                                                    modifier = Modifier.size(30.dp)
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = AppStrings.get("update_checking_desc", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            is AppUpdateState.UpdateAvailable -> {
                                                Text(
                                                    text = AppStrings.get("update_available_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                Text(
                                                    text = "${AppStrings.get("update_available_sub", currentLang)} • v${state.versionName}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = accentCyan,
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(14.dp))

                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    VersionPillBadge(
                                                        text = "v${BuildConfig.VERSION_NAME} → v${state.versionName}",
                                                        tint = primaryColor,
                                                        isDarkMode = isDarkMode
                                                    )
                                                    if (state.fileSizeBytes > 0L) {
                                                        VersionPillBadge(
                                                            text = AppUpdateManager.formatBytes(state.fileSizeBytes),
                                                            tint = Color(0xFF10B981),
                                                            isDarkMode = isDarkMode
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(22.dp))

                                                ThemedPillCtaButton(
                                                    text = AppStrings.get("update_btn_now", currentLang),
                                                    icon = Icons.Default.RocketLaunch,
                                                    gradientColors = listOf(primaryColor, accentCyan),
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        onStartDownload()
                                                    },
                                                    testTag = "update_dialog_start_button"
                                                )
                                            }

                                            is AppUpdateState.Downloading -> {
                                                val animatedProgress by animateFloatAsState(
                                                    targetValue = state.progress.coerceIn(0f, 1f),
                                                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                                                    label = "downloadProgressSmooth"
                                                )

                                                Text(
                                                    text = AppStrings.get("update_downloading_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                val percent = (animatedProgress * 100f).roundToInt().coerceIn(0, 100)
                                                Text(
                                                    text = "v${state.versionName} • $percent%",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = accentCyan
                                                )

                                                Spacer(modifier = Modifier.height(14.dp))

                                                if (state.totalBytes > 0L) {
                                                    LinearProgressIndicator(
                                                        progress = { animatedProgress },
                                                        color = accentCyan,
                                                        trackColor = if (isDarkMode) Color(0x33475569) else Color(0xFFE2E8F0),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(10.dp)
                                                            .clip(CircleShape)
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = "${AppUpdateManager.formatBytes(state.downloadedBytes)} / ${AppUpdateManager.formatBytes(state.totalBytes)}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                                    )
                                                } else {
                                                    LinearProgressIndicator(
                                                        color = accentCyan,
                                                        trackColor = if (isDarkMode) Color(0x33475569) else Color(0xFFE2E8F0),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(10.dp)
                                                            .clip(CircleShape)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = AppStrings.get("update_downloading_sub", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(18.dp))

                                                TactileSecondaryPillButton(
                                                    text = AppStrings.get("update_btn_cancel_download", currentLang),
                                                    accentColor = Color(0xFFEF4444),
                                                    isDarkMode = isDarkMode,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        onCancelDownload()
                                                    },
                                                    testTag = "update_dialog_cancel_button"
                                                )
                                            }

                                            is AppUpdateState.ReadyToInstall -> {
                                                Text(
                                                    text = AppStrings.get("update_ready_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "v${state.versionName}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = Color(0xFF10B981)
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = AppStrings.get(
                                                        if (state.requiresPermission) "update_permission_desc" else "update_ready_desc",
                                                        currentLang
                                                    ),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(20.dp))
                                                ThemedPillCtaButton(
                                                    text = AppStrings.get(
                                                        if (state.requiresPermission) "update_btn_grant_install" else "update_btn_install",
                                                        currentLang
                                                    ),
                                                    icon = if (state.requiresPermission) Icons.Default.Security else Icons.Default.DownloadDone,
                                                    gradientColors = listOf(Color(0xFF059669), Color(0xFF10B981)),
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        onInstallApk()
                                                    },
                                                    testTag = "update_dialog_install_button"
                                                )
                                            }

                                            is AppUpdateState.UpToDate -> {
                                                Text(
                                                    text = AppStrings.get("update_uptodate_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                VersionPillBadge(
                                                    text = "${AppStrings.get("update_current_version_tag", currentLang)}: v${state.currentVersion}",
                                                    tint = Color(0xFF10B981),
                                                    isDarkMode = isDarkMode
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = AppStrings.get("update_uptodate_desc", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(20.dp))
                                                ThemedPillCtaButton(
                                                    text = AppStrings.get("update_btn_ok", currentLang),
                                                    icon = Icons.Default.CheckCircle,
                                                    gradientColors = listOf(primaryColor, accentCyan),
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        onDismiss()
                                                    },
                                                    testTag = "update_dialog_ok_button"
                                                )
                                            }

                                            is AppUpdateState.Error -> {
                                                Text(
                                                    text = AppStrings.get("update_error_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = AppStrings.get(state.messageKey, currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                                                    textAlign = TextAlign.Center
                                                )
                                                if (!state.detail.isNullOrBlank()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = state.detail,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = Color(0xFFF59E0B),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(20.dp))
                                                ThemedPillCtaButton(
                                                    text = AppStrings.get("update_btn_retry", currentLang),
                                                    icon = Icons.Default.Refresh,
                                                    gradientColors = listOf(Color(0xFFEA580C), Color(0xFFF59E0B)),
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        onRetryCheck()
                                                    },
                                                    testTag = "update_dialog_retry_button"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateHeroEmblem(
    updateState: AppUpdateState,
    accentCyan: Color,
    isDarkMode: Boolean
) {
    val coreAccent = when (updateState) {
        is AppUpdateState.ReadyToInstall,
        is AppUpdateState.UpToDate -> Color(0xFF059669)
        is AppUpdateState.Error -> Color(0xFFD97706)
        else -> accentCyan
    }

    Box(
        modifier = Modifier
            .size(84.dp)
            .liquidGlassPanel(
                shape = CircleShape,
                cornerRadius = 42.dp,
                isDark = isDarkMode,
                accent = coreAccent,
                secondaryAccent = coreAccent,
                isSelected = true,
                softTintWhenSelected = true
            ),
        contentAlignment = Alignment.Center
    ) {
        val icon = when (updateState) {
            is AppUpdateState.UpToDate -> Icons.Default.CheckCircle
            is AppUpdateState.ReadyToInstall ->
                if (updateState.requiresPermission) Icons.Default.Security else Icons.Default.DownloadDone
            is AppUpdateState.Error -> Icons.Default.ErrorOutline
            else -> Icons.Default.RocketLaunch
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = coreAccent,
            modifier = Modifier.size(38.dp)
        )
    }
}

@Composable
private fun VersionPillBadge(
    text: String,
    tint: Color,
    isDarkMode: Boolean
) {
    Box(
        modifier = Modifier.liquidGlassPanel(
            shape = RoundedCornerShape(50.dp),
            cornerRadius = 50.dp,
            isDark = isDarkMode,
            accent = tint,
            secondaryAccent = tint,
            isSelected = true,
            softTintWhenSelected = true
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (isDarkMode) tint else tint.copy(alpha = 0.95f),
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ThemedPillCtaButton(
    text: String,
    icon: ImageVector? = null,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ctaPressScale"
    )

    val ctaFill = gradientColors.first()
    val ctaContent = contrastOnAccent(ctaFill)

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ctaFill,
            contentColor = ctaContent
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ctaContent,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ctaContent
        )
    }
}

@Composable
private fun TactileSecondaryPillButton(
    text: String,
    icon: ImageVector? = null,
    accentColor: Color,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "secondaryPressScale"
    )

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.1.dp, accentColor.copy(alpha = if (isDarkMode) 0.50f else 0.42f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = accentColor.copy(alpha = if (isDarkMode) 0.12f else 0.08f),
            contentColor = accentColor
        ),
        contentPadding = PaddingValues(horizontal = 18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkMode) accentColor else accentColor.copy(alpha = 0.95f),
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) accentColor else accentColor.copy(alpha = 0.95f)
        )
    }
}
