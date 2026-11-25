package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppUpdateManager
import com.example.util.AppUpdateState
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
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
    val secondaryAccent = themePreset?.secondary ?: RoyalBlueAccent
    val textPrimary = themePreset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = themePreset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val cardShape = RoundedCornerShape(28.dp)

    Dialog(
        onDismissRequest = {
            if (updateState !is AppUpdateState.Downloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = updateState !is AppUpdateState.Downloading,
            dismissOnClickOutside = updateState !is AppUpdateState.Downloading
        )
    ) {
        SyncDialogLiquidGlassWindow()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            val cardWidth = maxWidth.coerceIn(270.dp, 390.dp)
            val maxCardHeight = (maxHeight - 48.dp).coerceAtLeast(320.dp)

            AnimatedVisibility(
                visibleState = enterState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(220)) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                } else EnterTransition.None,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Surface(
                    shape = cardShape,
                    color = Color.Transparent,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .width(cardWidth)
                        .heightIn(max = maxCardHeight)
                        .liquidGlassDialogShell(
                            shape = cardShape,
                            cornerRadius = 28.dp,
                            isDark = effectiveDark,
                            primaryAccent = accentCyan,
                            secondaryAccent = secondaryAccent
                        )
                        .testTag("in_app_update_dialog")
                ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp)
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
                                        secondaryAccent = secondaryAccent
                                    )
                                    .testTag("update_dialog_close_button")
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = AppStrings.get("close", currentLang),
                                        tint = textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(top = 12.dp, bottom = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                UpdateHeroEmblem(
                                    updateState = updateState,
                                    accentCyan = accentCyan,
                                    secondaryAccent = secondaryAccent,
                                    isDarkMode = effectiveDark,
                                    animationsEnabled = animationsEnabled
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
                                                    color = textPrimary,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                VersionPillBadge(
                                                    text = "${AppStrings.get("update_current_version_tag", currentLang)}: v${BuildConfig.VERSION_NAME}",
                                                    tint = accentCyan,
                                                    isDarkMode = effectiveDark
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                CircularProgressIndicator(
                                                    color = accentCyan,
                                                    strokeWidth = 3.dp,
                                                    modifier = Modifier.size(30.dp)
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = AppStrings.get("update_checking_desc", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            is AppUpdateState.UpdateAvailable -> {
                                                var showWhatsNew by remember(state.versionName) { mutableStateOf(false) }

                                                Text(
                                                    text = AppStrings.get("update_available_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = textPrimary,
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

                                                FlowRow(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    VersionPillBadge(
                                                        text = "${AppStrings.get("update_current_version_tag", currentLang)}: v${BuildConfig.VERSION_NAME}",
                                                        tint = textSecondary,
                                                        isDarkMode = effectiveDark
                                                    )
                                                    VersionPillBadge(
                                                        text = "${AppStrings.get("update_new_version_tag", currentLang)}: v${state.versionName}",
                                                        tint = accentCyan,
                                                        isDarkMode = effectiveDark
                                                    )
                                                    if (state.fileSizeBytes > 0L) {
                                                        VersionPillBadge(
                                                            text = AppUpdateManager.formatBytes(state.fileSizeBytes),
                                                            tint = EmeraldSuccess,
                                                            isDarkMode = effectiveDark
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(14.dp))

                                                // Expandable "What's New" (الإضافات) toggle & panel
                                                TactileSecondaryPillButton(
                                                    text = AppStrings.get("update_whats_new_btn", currentLang),
                                                    icon = Icons.Default.AutoAwesome,
                                                    trailingIcon = if (showWhatsNew) {
                                                        Icons.Default.KeyboardArrowUp
                                                    } else {
                                                        Icons.Default.KeyboardArrowDown
                                                    },
                                                    accentColor = accentCyan,
                                                    isDarkMode = effectiveDark,
                                                    animationsEnabled = animationsEnabled,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        showWhatsNew = !showWhatsNew
                                                    },
                                                    testTag = "update_dialog_whats_new_button"
                                                )

                                                AnimatedVisibility(
                                                    visible = showWhatsNew,
                                                    enter = if (animationsEnabled) {
                                                        expandVertically(
                                                            animationSpec = spring(
                                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                                stiffness = 380f
                                                            )
                                                        ) + fadeIn(tween(200))
                                                    } else EnterTransition.None,
                                                    exit = if (animationsEnabled) {
                                                        shrinkVertically(
                                                            animationSpec = spring(
                                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                                stiffness = 420f
                                                            )
                                                        ) + fadeOut(tween(150))
                                                    } else ExitTransition.None
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(top = 10.dp)
                                                            .liquidGlassPanel(
                                                                shape = RoundedCornerShape(16.dp),
                                                                cornerRadius = 16.dp,
                                                                isDark = effectiveDark,
                                                                accent = accentCyan,
                                                                secondaryAccent = secondaryAccent,
                                                                isSelected = true,
                                                                softTintWhenSelected = true
                                                            )
                                                            .padding(14.dp)
                                                            .testTag("update_dialog_whats_new_panel")
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Text(
                                                                text = AppStrings.get("update_whats_new_title", currentLang),
                                                                style = MaterialTheme.typography.labelLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                color = accentCyan
                                                            )
                                                            Text(
                                                                text = state.releaseNotes.ifBlank {
                                                                    AppStrings.get("update_whats_new_empty", currentLang)
                                                                },
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = textPrimary
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(16.dp))

                                                ThemedPillCtaButton(
                                                    text = AppStrings.get("update_btn_now", currentLang),
                                                    icon = Icons.Default.RocketLaunch,
                                                    gradientColors = listOf(primaryColor, accentCyan),
                                                    animationsEnabled = animationsEnabled,
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
                                                    animationSpec = if (animationsEnabled) {
                                                        tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                                    } else tween(0),
                                                    label = "downloadProgressSmooth"
                                                )

                                                Text(
                                                    text = AppStrings.get("update_downloading_title", currentLang),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Black,
                                                    color = textPrimary,
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
                                                        trackColor = if (effectiveDark) Color(0x33475569) else Color(0xFFE2E8F0),
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
                                                        color = textSecondary
                                                    )
                                                } else {
                                                    LinearProgressIndicator(
                                                        color = accentCyan,
                                                        trackColor = if (effectiveDark) Color(0x33475569) else Color(0xFFE2E8F0),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(10.dp)
                                                            .clip(CircleShape)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = AppStrings.get("update_downloading_sub", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textSecondary,
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(18.dp))

                                                TactileSecondaryPillButton(
                                                    text = AppStrings.get("update_btn_cancel_download", currentLang),
                                                    icon = Icons.Default.Close,
                                                    accentColor = RedDanger,
                                                    isDarkMode = effectiveDark,
                                                    animationsEnabled = animationsEnabled,
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
                                                    color = textPrimary,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                VersionPillBadge(
                                                    text = "v${state.versionName}",
                                                    tint = EmeraldSuccess,
                                                    isDarkMode = effectiveDark
                                                )
                                                Spacer(modifier = Modifier.height(12.dp))
                                                Text(
                                                    text = AppStrings.get(
                                                        if (state.requiresPermission) "update_permission_desc" else "update_ready_desc",
                                                        currentLang
                                                    ),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(20.dp))
                                                ThemedPillCtaButton(
                                                    text = AppStrings.get(
                                                        if (state.requiresPermission) "update_btn_grant_install" else "update_btn_install",
                                                        currentLang
                                                    ),
                                                    icon = if (state.requiresPermission) Icons.Default.Security else Icons.Default.DownloadDone,
                                                    gradientColors = listOf(EmeraldSuccess, Color(0xFF10B981)),
                                                    animationsEnabled = animationsEnabled,
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
                                                    color = textPrimary,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                VersionPillBadge(
                                                    text = "${AppStrings.get("update_current_version_tag", currentLang)}: v${state.currentVersion}",
                                                    tint = EmeraldSuccess,
                                                    isDarkMode = effectiveDark
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = AppStrings.get("update_uptodate_desc", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(20.dp))
                                                ThemedPillCtaButton(
                                                    text = AppStrings.get("update_btn_ok", currentLang),
                                                    icon = Icons.Default.CheckCircle,
                                                    gradientColors = listOf(primaryColor, accentCyan),
                                                    animationsEnabled = animationsEnabled,
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
                                                    color = textPrimary,
                                                    textAlign = TextAlign.Center
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = AppStrings.get(state.messageKey, currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textSecondary,
                                                    textAlign = TextAlign.Center
                                                )
                                                if (!state.detail.isNullOrBlank()) {
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = state.detail,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = AmberWarning,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(20.dp))
                                                ThemedPillCtaButton(
                                                    text = AppStrings.get("update_btn_retry", currentLang),
                                                    icon = Icons.Default.Refresh,
                                                    gradientColors = listOf(Color(0xFFEA580C), AmberWarning),
                                                    animationsEnabled = animationsEnabled,
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

@Composable
private fun UpdateHeroEmblem(
    updateState: AppUpdateState,
    accentCyan: Color,
    secondaryAccent: Color,
    isDarkMode: Boolean,
    animationsEnabled: Boolean
) {
    val coreAccent = when (updateState) {
        is AppUpdateState.ReadyToInstall,
        is AppUpdateState.UpToDate -> EmeraldSuccess
        is AppUpdateState.Error -> AmberWarning
        else -> accentCyan
    }

    val isLiving = LocalLivingLuxuryMode.current && animationsEnabled
    val isOrbitalActive = animationsEnabled &&
        (updateState is AppUpdateState.Idle ||
            updateState is AppUpdateState.Checking ||
            updateState is AppUpdateState.Downloading ||
            isLiving)

    val orbitAngle = if (isOrbitalActive) {
        val infinite = rememberInfiniteTransition(label = "updateEmblemOrbit")
        val angle by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = if (updateState is AppUpdateState.Checking || updateState is AppUpdateState.Downloading) 1800 else 5200,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "orbitAngle"
        )
        angle
    } else 0f

    val haloPulse = if (isLiving) {
        val infinite = rememberInfiniteTransition(label = "updateEmblemPulse")
        val p by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "haloPulse"
        )
        p
    } else 0.4f

    val downloadProgress = (updateState as? AppUpdateState.Downloading)?.progress?.coerceIn(0f, 1f) ?: 0f

    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val haloAlpha = (if (isDarkMode) 0.24f else 0.16f) + 0.10f * haloPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        coreAccent.copy(alpha = haloAlpha),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.minDimension / 2f
                )
            )

            val strokePx = 3.dp.toPx()
            val ringInset = strokePx / 2f + 2.dp.toPx()
            val arcSize = androidx.compose.ui.geometry.Size(
                size.width - ringInset * 2f,
                size.height - ringInset * 2f
            )

            // Base subtle ring
            drawArc(
                color = coreAccent.copy(alpha = if (isDarkMode) 0.18f else 0.14f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(ringInset, ringInset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            if (updateState is AppUpdateState.Downloading && updateState.totalBytes > 0L) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(coreAccent, secondaryAccent, coreAccent)),
                    startAngle = -90f,
                    sweepAngle = (downloadProgress * 360f).coerceAtLeast(12f),
                    useCenter = false,
                    topLeft = Offset(ringInset, ringInset),
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            } else if (isOrbitalActive) {
                rotate(degrees = orbitAngle) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color.Transparent,
                                coreAccent.copy(alpha = 0.85f),
                                secondaryAccent.copy(alpha = 0.70f),
                                Color.Transparent
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(ringInset, ringInset),
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(76.dp)
                .liquidGlassPanel(
                    shape = CircleShape,
                    cornerRadius = 38.dp,
                    isDark = isDarkMode,
                    accent = coreAccent,
                    secondaryAccent = secondaryAccent,
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
                modifier = Modifier.size(34.dp)
            )
        }
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
    animationsEnabled: Boolean = true,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && animationsEnabled) 0.965f else 1f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        } else tween(0),
        label = "ctaPressScale"
    )

    val ctaFill = gradientColors.first()
    val ctaEnd = gradientColors.lastOrNull() ?: ctaFill
    val ctaContent = contrastOnAccent(ctaFill)
    val gradientBrush = remember(ctaFill, ctaEnd) {
        Brush.horizontalGradient(listOf(ctaFill, ctaEnd))
    }

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = ctaContent
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(gradientBrush)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
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
}

@Composable
private fun TactileSecondaryPillButton(
    text: String,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    accentColor: Color,
    isDarkMode: Boolean,
    animationsEnabled: Boolean = true,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && animationsEnabled) 0.965f else 1f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        } else tween(0),
        label = "secondaryPressScale"
    )

    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = CircleShape,
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
        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = if (isDarkMode) accentColor else accentColor.copy(alpha = 0.95f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
