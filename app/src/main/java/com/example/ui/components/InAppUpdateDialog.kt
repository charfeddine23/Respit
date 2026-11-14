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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppUpdateManager
import com.example.util.AppUpdateState
import kotlin.math.roundToInt

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
    val enterState = remember { MutableTransitionState(false).apply { targetState = true } }

    val infiniteTransition = rememberInfiniteTransition(label = "updateDialogMotion")
    val glowPulse by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.76f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowPulse"
        )
    } else {
        remember { mutableStateOf(0.88f) }
    }

    val haloOrbitAngle by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "haloOrbit"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val rocketFloatOffset by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = -2.5f,
            targetValue = 2.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rocketFloat"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val flamePulse by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.86f,
            targetValue = 1.22f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 520, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "flamePulse"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val accentCyan = Color(0xFF00BCD4)
    val cardShape = RoundedCornerShape(30.dp)

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
        AnimatedVisibility(
            visibleState = enterState,
            enter = fadeIn(tween(240)) + scaleIn(
                initialScale = 0.90f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        ) {
            // Centered floating dialog with smooth rounded gradient aura (NO full-screen layout, NO What's New page)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 370.dp)
                    .testTag("in_app_update_dialog"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .drawBehind {
                            val haloAlpha = (if (isDarkMode) 0.56f else 0.36f) * glowPulse
                            val sweepColors = listOf(
                                Color(0xFF00E5FF).copy(alpha = haloAlpha),
                                Color(0xFF38BDF8).copy(alpha = haloAlpha * 0.92f),
                                Color(0xFF818CF8).copy(alpha = haloAlpha * 0.88f),
                                Color(0xFFF472B6).copy(alpha = haloAlpha * 0.85f),
                                Color(0xFF10B981).copy(alpha = haloAlpha * 0.92f),
                                Color(0xFF00E5FF).copy(alpha = haloAlpha)
                            )

                            rotate(degrees = haloOrbitAngle * 0.25f) {
                                // Outer feathered rounded gradient glow
                                val outerExpand = 10.dp.toPx()
                                val outerRadius = 38.dp.toPx()
                                drawRoundRect(
                                    brush = Brush.sweepGradient(colors = sweepColors),
                                    topLeft = Offset(-outerExpand, -outerExpand),
                                    size = Size(size.width + outerExpand * 2f, size.height + outerExpand * 2f),
                                    cornerRadius = CornerRadius(outerRadius, outerRadius),
                                    style = Stroke(width = 18.dp.toPx())
                                )
                            }

                            // Mid-layer rounded gradient bloom hugging the dialog edges
                            val midExpand = 5.dp.toPx()
                            val midRadius = 34.dp.toPx()
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF00E5FF).copy(alpha = haloAlpha * 0.72f),
                                        Color(0xFF3B82F6).copy(alpha = haloAlpha * 0.68f),
                                        Color(0xFFA855F7).copy(alpha = haloAlpha * 0.64f),
                                        Color(0xFF10B981).copy(alpha = haloAlpha * 0.72f)
                                    )
                                ),
                                topLeft = Offset(-midExpand, -midExpand),
                                size = Size(size.width + midExpand * 2f, size.height + midExpand * 2f),
                                cornerRadius = CornerRadius(midRadius, midRadius),
                                style = Stroke(width = 10.dp.toPx())
                            )

                            // Crisp inner gradient rim just outside the rounded border
                            val rimExpand = 1.5.dp.toPx()
                            val rimRadius = 31.dp.toPx()
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8).copy(alpha = haloAlpha * 0.88f),
                                        Color(0xFFF472B6).copy(alpha = haloAlpha * 0.75f),
                                        Color(0xFF10B981).copy(alpha = haloAlpha * 0.88f)
                                    )
                                ),
                                topLeft = Offset(-rimExpand, -rimExpand),
                                size = Size(size.width + rimExpand * 2f, size.height + rimExpand * 2f),
                                cornerRadius = CornerRadius(rimRadius, rimRadius),
                                style = Stroke(width = 3.5.dp.toPx())
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val containerBrush = when {
                        isNeumorphic && isDarkMode -> Brush.verticalGradient(
                            listOf(Color(0xFF172136), Color(0xFF0F172A))
                        )
                        isNeumorphic && !isDarkMode -> Brush.verticalGradient(
                            listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
                        )
                        isDarkMode -> Brush.verticalGradient(
                            listOf(Color(0xFA111827), Color(0xFA0B1324))
                        )
                        else -> Brush.verticalGradient(
                            listOf(Color(0xFCFFFFFF), Color(0xF8EFF8FF))
                        )
                    }

                    val cardBorder = BorderStroke(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = if (isDarkMode) {
                                listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.78f),
                                    Color(0xFFA855F7).copy(alpha = 0.55f),
                                    Color(0xFF10B981).copy(alpha = 0.72f)
                                )
                            } else {
                                listOf(
                                    Color(0xFF00BCD4).copy(alpha = 0.68f),
                                    Color(0xFF3B82F6).copy(alpha = 0.52f),
                                    Color(0xFF10B981).copy(alpha = 0.65f)
                                )
                            }
                        )
                    )

                    Surface(
                        shape = cardShape,
                        color = Color.Transparent,
                        border = cardBorder,
                        shadowElevation = if (isNeumorphic) 12.dp else 18.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(containerBrush)
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            // Top Corner Close Icon (non-intrusive so content sits symmetrically centered)
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (updateState is AppUpdateState.Downloading) {
                                        onCancelDownload()
                                    } else {
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .testTag("update_dialog_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = AppStrings.get("close", currentLang),
                                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    modifier = Modifier.size(21.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                UpdateHeroEmblem(
                                    updateState = updateState,
                                    primaryColor = primaryColor,
                                    accentCyan = accentCyan,
                                    isDarkMode = isDarkMode,
                                    glowPulse = glowPulse,
                                    haloOrbitAngle = haloOrbitAngle,
                                    rocketFloatOffset = rocketFloatOffset,
                                    flamePulse = flamePulse
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                AnimatedContent(
                                    targetState = updateState,
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
    primaryColor: Color,
    accentCyan: Color,
    isDarkMode: Boolean,
    glowPulse: Float,
    haloOrbitAngle: Float,
    rocketFloatOffset: Float,
    flamePulse: Float
) {
    val coreAccent = when (updateState) {
        is AppUpdateState.ReadyToInstall,
        is AppUpdateState.UpToDate -> Color(0xFF10B981)
        is AppUpdateState.Error -> Color(0xFFF59E0B)
        else -> accentCyan
    }

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val auraAlpha = (if (isDarkMode) 0.46f else 0.30f) * glowPulse

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        coreAccent.copy(alpha = auraAlpha),
                        primaryColor.copy(alpha = auraAlpha * 0.55f),
                        Color.Transparent
                    ),
                    center = c,
                    radius = size.minDimension * 0.56f
                ),
                center = c,
                radius = size.minDimension * 0.56f
            )

            drawCircle(
                color = coreAccent.copy(alpha = if (isDarkMode) 0.10f else 0.08f),
                radius = size.minDimension * 0.48f,
                center = c
            )
            drawCircle(
                color = coreAccent.copy(alpha = if (isDarkMode) 0.18f else 0.13f),
                radius = size.minDimension * 0.42f,
                center = c
            )

            rotate(degrees = haloOrbitAngle, pivot = c) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            coreAccent,
                            primaryColor,
                            Color(0xFF38BDF8),
                            Color(0xFFF472B6).copy(alpha = 0.78f),
                            coreAccent
                        )
                    ),
                    radius = size.minDimension * 0.38f,
                    center = c,
                    style = Stroke(width = 2.6.dp.toPx())
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isDarkMode) {
                        listOf(Color(0xFF162238), Color(0xFF0A101E))
                    } else {
                        listOf(Color(0xFFFFFFFF), Color(0xFFF0F9FF))
                    },
                    center = c,
                    radius = size.minDimension * 0.36f
                ),
                radius = size.minDimension * 0.36f,
                center = c
            )
        }

        when (updateState) {
            is AppUpdateState.UpToDate -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(46.dp)
                )
            }
            is AppUpdateState.ReadyToInstall -> {
                Icon(
                    imageVector = if (updateState.requiresPermission) Icons.Default.Security else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(44.dp)
                )
            }
            is AppUpdateState.Error -> {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(46.dp)
                )
            }
            else -> {
                Canvas(
                    modifier = Modifier
                        .size(74.dp)
                        .graphicsLayer {
                            translationY = rocketFloatOffset.dp.toPx()
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f

                    val starAlpha = (0.65f + 0.35f * glowPulse).coerceIn(0f, 1f)
                    drawCircle(
                        color = Color(0xFFFBBF24).copy(alpha = starAlpha),
                        radius = 2.1.dp.toPx(),
                        center = Offset(w * 0.19f, h * 0.66f)
                    )
                    drawCircle(
                        color = accentCyan.copy(alpha = starAlpha),
                        radius = 1.8.dp.toPx(),
                        center = Offset(w * 0.18f, h * 0.28f)
                    )
                    drawCircle(
                        color = (if (isDarkMode) Color.White else primaryColor).copy(alpha = starAlpha),
                        radius = 2.2.dp.toPx(),
                        center = Offset(w * 0.81f, h * 0.26f)
                    )
                    drawCircle(
                        color = Color(0xFFF472B6).copy(alpha = starAlpha),
                        radius = 1.9.dp.toPx(),
                        center = Offset(w * 0.79f, h * 0.65f)
                    )

                    val flameLength = h * 0.20f * flamePulse
                    val outerFlamePath = Path().apply {
                        moveTo(cx - w * 0.072f, h * 0.67f)
                        lineTo(cx, h * 0.67f + flameLength)
                        lineTo(cx + w * 0.072f, h * 0.67f)
                        close()
                    }
                    drawPath(
                        path = outerFlamePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF97316), Color(0xFFEF4444))
                        )
                    )
                    val innerFlamePath = Path().apply {
                        moveTo(cx - w * 0.036f, h * 0.67f)
                        lineTo(cx, h * 0.67f + flameLength * 0.58f)
                        lineTo(cx + w * 0.036f, h * 0.67f)
                        close()
                    }
                    drawPath(innerFlamePath, color = Color(0xFFFEF08A))

                    val finBrush = Brush.verticalGradient(
                        listOf(primaryColor, Color(0xFF0284C7))
                    )
                    val leftFin = Path().apply {
                        moveTo(cx - w * 0.105f, h * 0.48f)
                        lineTo(cx - w * 0.26f, h * 0.66f)
                        lineTo(cx - w * 0.075f, h * 0.625f)
                        close()
                    }
                    drawPath(leftFin, brush = finBrush)

                    val rightFin = Path().apply {
                        moveTo(cx + w * 0.105f, h * 0.48f)
                        lineTo(cx + w * 0.26f, h * 0.66f)
                        lineTo(cx + w * 0.075f, h * 0.625f)
                        close()
                    }
                    drawPath(rightFin, brush = finBrush)

                    val bodyPath = Path().apply {
                        moveTo(cx, h * 0.13f)
                        cubicTo(
                            cx + w * 0.165f, h * 0.27f,
                            cx + w * 0.145f, h * 0.54f,
                            cx + w * 0.085f, h * 0.67f
                        )
                        lineTo(cx - w * 0.085f, h * 0.67f)
                        cubicTo(
                            cx - w * 0.145f, h * 0.54f,
                            cx - w * 0.165f, h * 0.27f,
                            cx, h * 0.13f
                        )
                        close()
                    }
                    drawPath(
                        path = bodyPath,
                        brush = Brush.horizontalGradient(
                            colors = if (isDarkMode) {
                                listOf(Color.White, Color(0xFFF1F5F9), Color(0xFFCBD5E1))
                            } else {
                                listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                            }
                        )
                    )

                    val nosePath = Path().apply {
                        moveTo(cx, h * 0.13f)
                        cubicTo(
                            cx + w * 0.088f, h * 0.205f,
                            cx + w * 0.108f, h * 0.265f,
                            cx + w * 0.108f, h * 0.295f
                        )
                        lineTo(cx - w * 0.108f, h * 0.295f)
                        cubicTo(
                            cx - w * 0.108f, h * 0.265f,
                            cx - w * 0.088f, h * 0.205f,
                            cx, h * 0.13f
                        )
                        close()
                    }
                    drawPath(
                        path = nosePath,
                        brush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), accentCyan))
                    )

                    val windowCenter = Offset(cx, h * 0.435f)
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = w * 0.088f,
                        center = windowCenter
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF7DD3FC), Color(0xFF0284C7)),
                            center = Offset(cx - w * 0.02f, h * 0.415f),
                            radius = w * 0.07f
                        ),
                        radius = w * 0.062f,
                        center = windowCenter
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.75f),
                        radius = w * 0.018f,
                        center = Offset(cx - w * 0.024f, h * 0.412f)
                    )

                    drawLine(
                        color = Color(0xFF475569),
                        start = Offset(cx - w * 0.065f, h * 0.67f),
                        end = Offset(cx + w * 0.065f, h * 0.67f),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionPillBadge(
    text: String,
    tint: Color,
    isDarkMode: Boolean
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = tint.copy(alpha = if (isDarkMode) 0.18f else 0.11f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.45f))
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

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.30f)),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(50.dp),
                ambientColor = gradientColors.first().copy(alpha = 0.45f),
                spotColor = gradientColors.last().copy(alpha = 0.55f)
            )
            .clip(RoundedCornerShape(50.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
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
