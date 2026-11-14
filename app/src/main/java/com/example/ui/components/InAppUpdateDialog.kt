package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val infiniteTransition = rememberInfiniteTransition(label = "updateGlowTransition")
    val glowPulse by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.72f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowPulse"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "staticGlow"
        )
    }

    val flamePulse by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "flamePulse"
        )
    } else {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(1000)),
            label = "staticFlame"
        )
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val accentCyan = Color(0xFF00BCD4)
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
        // Floating centered container — NEVER full-screen — with multi-color ambient glowing halo behind the card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .widthIn(max = 390.dp)
                .testTag("in_app_update_dialog"),
            contentAlignment = Alignment.Center
        ) {
            // Outer Box that paints the multi-color backlight halo bleeding softly around the floating card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .drawBehind {
                        val haloAlpha = (if (isDarkMode) 0.52f else 0.34f) * glowPulse
                        val cornerRadiusPx = 30.dp.toPx()

                        // Top-Left Blue / Cyan Halo
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = haloAlpha),
                                    Color(0xFF3B82F6).copy(alpha = haloAlpha * 0.45f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.14f, size.height * 0.14f),
                                radius = size.minDimension * 0.62f
                            ),
                            center = Offset(size.width * 0.14f, size.height * 0.14f),
                            radius = size.minDimension * 0.62f
                        )

                        // Top-Right Coral / Rose Halo
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFB7185).copy(alpha = haloAlpha * 0.9f),
                                    Color(0xFFEF4444).copy(alpha = haloAlpha * 0.38f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.88f, size.height * 0.16f),
                                radius = size.minDimension * 0.56f
                            ),
                            center = Offset(size.width * 0.88f, size.height * 0.16f),
                            radius = size.minDimension * 0.56f
                        )

                        // Bottom-Right Emerald / Amber Halo
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF10B981).copy(alpha = haloAlpha * 0.9f),
                                    Color(0xFFFBBF24).copy(alpha = haloAlpha * 0.35f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.84f, size.height * 0.86f),
                                radius = size.minDimension * 0.58f
                            ),
                            center = Offset(size.width * 0.84f, size.height * 0.86f),
                            radius = size.minDimension * 0.58f
                        )

                        // Bottom-Left Indigo / Cyan Halo
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF818CF8).copy(alpha = haloAlpha * 0.78f),
                                    Color(0xFF06B6D4).copy(alpha = haloAlpha * 0.32f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * 0.16f, size.height * 0.84f),
                                radius = size.minDimension * 0.55f
                            ),
                            center = Offset(size.width * 0.16f, size.height * 0.84f),
                            radius = size.minDimension * 0.55f
                        )

                        // Luminous prism border bloom hugging the card contour
                        drawRoundRect(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = haloAlpha * 0.85f),
                                    Color(0xFFFB7185).copy(alpha = haloAlpha * 0.75f),
                                    Color(0xFFFBBF24).copy(alpha = haloAlpha * 0.7f),
                                    Color(0xFF10B981).copy(alpha = haloAlpha * 0.8f),
                                    Color(0xFF818CF8).copy(alpha = haloAlpha * 0.75f),
                                    Color(0xFF38BDF8).copy(alpha = haloAlpha * 0.85f)
                                )
                            ),
                            topLeft = Offset(-3.dp.toPx(), -3.dp.toPx()),
                            size = Size(size.width + 6.dp.toPx(), size.height + 6.dp.toPx()),
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                            style = Stroke(width = 7.dp.toPx())
                        )
                    }
            ) {
                val containerBrush = when {
                    isNeumorphic && isDarkMode -> Brush.verticalGradient(
                        listOf(Color(0xFF162032), Color(0xFF0F172A))
                    )
                    isNeumorphic && !isDarkMode -> Brush.verticalGradient(
                        listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF))
                    )
                    isDarkMode -> Brush.verticalGradient(
                        listOf(Color(0xF5111827), Color(0xF50F172A))
                    )
                    else -> Brush.verticalGradient(
                        listOf(Color(0xFAFFFFFF), Color(0xF5F0F9FF))
                    )
                }

                val cardBorder = BorderStroke(
                    width = 1.25.dp,
                    brush = Brush.linearGradient(
                        colors = if (isDarkMode) {
                            listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.55f),
                                Color.White.copy(alpha = 0.22f),
                                Color(0xFF10B981).copy(alpha = 0.45f)
                            )
                        } else {
                            listOf(
                                Color.White,
                                Color(0xFF00BCD4).copy(alpha = 0.45f),
                                Color(0xFF3B82F6).copy(alpha = 0.35f)
                            )
                        }
                    )
                )

                Surface(
                    shape = cardShape,
                    color = Color.Transparent,
                    border = cardBorder,
                    shadowElevation = if (isNeumorphic) 12.dp else 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(containerBrush)
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top Row with Close button in corner (matching Reference Image 1)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (updateState is AppUpdateState.Downloading) {
                                            onCancelDownload()
                                        } else {
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .testTag("update_dialog_close_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = AppStrings.get("close", currentLang),
                                        tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Hero Emblem with Backlight Halo (fusing Reference Image 1 Rocket + Reference Image 2 Glowing Halo)
                            UpdateHeroEmblem(
                                updateState = updateState,
                                primaryColor = primaryColor,
                                accentCyan = accentCyan,
                                isDarkMode = isDarkMode,
                                glowPulse = glowPulse,
                                flamePulse = flamePulse
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Title, Accent Subtitle, Body & Action by State
                            when (updateState) {
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
                                    Spacer(modifier = Modifier.height(12.dp))
                                    CircularProgressIndicator(
                                        color = accentCyan,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(28.dp)
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
                                        text = "${AppStrings.get("update_available_sub", currentLang)} • v${updateState.versionName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = accentCyan,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Version & File Size Chip Row
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        VersionPillBadge(
                                            text = "v${BuildConfig.VERSION_NAME} → v${updateState.versionName}",
                                            tint = primaryColor,
                                            isDarkMode = isDarkMode
                                        )
                                        if (updateState.fileSizeBytes > 0L) {
                                            VersionPillBadge(
                                                text = AppUpdateManager.formatBytes(updateState.fileSizeBytes),
                                                tint = Color(0xFF10B981),
                                                isDarkMode = isDarkMode
                                            )
                                        }
                                    }

                                    if (updateState.releaseNotes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isDarkMode) Color(0x33334155) else Color(0xFFF1F5F9),
                                            border = BorderStroke(
                                                1.dp,
                                                if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 100.dp)
                                        ) {
                                            Text(
                                                text = updateState.releaseNotes,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF334155),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .verticalScroll(rememberScrollState())
                                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = AppStrings.get("update_available_title", currentLang),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    ThemedPillCtaButton(
                                        text = AppStrings.get("update_btn_now", currentLang),
                                        gradientColors = listOf(primaryColor, accentCyan),
                                        onClick = onStartDownload,
                                        testTag = "update_dialog_start_button"
                                    )
                                }

                                is AppUpdateState.Downloading -> {
                                    Text(
                                        text = AppStrings.get("update_downloading_title", currentLang),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    val percent = (updateState.progress * 100f).roundToInt().coerceIn(0, 100)
                                    Text(
                                        text = "v${updateState.versionName} • $percent%",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = accentCyan
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    if (updateState.totalBytes > 0L) {
                                        LinearProgressIndicator(
                                            progress = { updateState.progress.coerceIn(0f, 1f) },
                                            color = accentCyan,
                                            trackColor = if (isDarkMode) Color(0x33475569) else Color(0xFFE2E8F0),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "${AppUpdateManager.formatBytes(updateState.downloadedBytes)} / ${AppUpdateManager.formatBytes(updateState.totalBytes)}",
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

                                    OutlinedButton(
                                        onClick = onCancelDownload,
                                        shape = RoundedCornerShape(50.dp),
                                        border = BorderStroke(1.2.dp, Color(0xFFEF4444).copy(alpha = 0.65f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("update_dialog_cancel_button")
                                    ) {
                                        Text(
                                            text = AppStrings.get("update_btn_cancel_download", currentLang),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFEF4444)
                                        )
                                    }
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
                                        text = "v${updateState.versionName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF10B981)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = AppStrings.get(
                                            if (updateState.requiresPermission) "update_permission_desc" else "update_ready_desc",
                                            currentLang
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    ThemedPillCtaButton(
                                        text = AppStrings.get(
                                            if (updateState.requiresPermission) "update_btn_grant_install" else "update_btn_install",
                                            currentLang
                                        ),
                                        gradientColors = listOf(Color(0xFF059669), Color(0xFF10B981)),
                                        onClick = onInstallApk,
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
                                    Spacer(modifier = Modifier.height(6.dp))
                                    VersionPillBadge(
                                        text = "${AppStrings.get("update_current_version_tag", currentLang)}: v${updateState.currentVersion}",
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
                                        gradientColors = listOf(primaryColor, accentCyan),
                                        onClick = onDismiss,
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
                                        text = AppStrings.get(updateState.messageKey, currentLang),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                                        textAlign = TextAlign.Center
                                    )
                                    if (!updateState.detail.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = updateState.detail,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFF59E0B),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    ThemedPillCtaButton(
                                        text = AppStrings.get("update_btn_retry", currentLang),
                                        gradientColors = listOf(Color(0xFFEA580C), Color(0xFFF59E0B)),
                                        onClick = onRetryCheck,
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

@Composable
private fun UpdateHeroEmblem(
    updateState: AppUpdateState,
    primaryColor: Color,
    accentCyan: Color,
    isDarkMode: Boolean,
    glowPulse: Float,
    flamePulse: Float
) {
    val coreAccent = when (updateState) {
        is AppUpdateState.ReadyToInstall,
        is AppUpdateState.UpToDate -> Color(0xFF10B981)
        is AppUpdateState.Error -> Color(0xFFF59E0B)
        else -> accentCyan
    }

    Box(
        modifier = Modifier.size(116.dp),
        contentAlignment = Alignment.Center
    ) {
        // Multi-color prism squircle backlight aura behind the concentric rings (echoing Screenshot 2)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val auraAlpha = (if (isDarkMode) 0.42f else 0.28f) * glowPulse

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

            // Outer translucent concentric halo ring (echoing Screenshot 1)
            drawCircle(
                color = coreAccent.copy(alpha = if (isDarkMode) 0.16f else 0.12f),
                radius = size.minDimension * 0.46f,
                center = c
            )

            // Crisp luminous ring border
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        coreAccent,
                        primaryColor,
                        Color(0xFF38BDF8),
                        coreAccent
                    )
                ),
                radius = size.minDimension * 0.39f,
                center = c,
                style = Stroke(width = 2.4.dp.toPx())
            )

            // Inner deep disc
            drawCircle(
                color = if (isDarkMode) Color(0xFF0B1220) else Color.White,
                radius = size.minDimension * 0.37f,
                center = c
            )
        }

        when (updateState) {
            is AppUpdateState.UpToDate -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(44.dp)
                )
            }
            is AppUpdateState.ReadyToInstall -> {
                Icon(
                    imageVector = if (updateState.requiresPermission) Icons.Default.Security else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(42.dp)
                )
            }
            is AppUpdateState.Error -> {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(44.dp)
                )
            }
            else -> {
                // Custom crisp Rocket Launch artwork styled with app theme colors & orbiting stars (inspired by Screenshot 1)
                Canvas(modifier = Modifier.size(72.dp)) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f

                    // Orbiting pixel sparkles inside the disc (matching Screenshot 1)
                    drawRect(
                        color = Color(0xFFFBBF24),
                        topLeft = Offset(w * 0.18f, h * 0.65f),
                        size = Size(3.dp.toPx(), 3.dp.toPx())
                    )
                    drawRect(
                        color = accentCyan,
                        topLeft = Offset(w * 0.17f, h * 0.28f),
                        size = Size(2.5.dp.toPx(), 2.5.dp.toPx())
                    )
                    drawRect(
                        color = if (isDarkMode) Color.White else primaryColor,
                        topLeft = Offset(w * 0.80f, h * 0.25f),
                        size = Size(3.dp.toPx(), 3.dp.toPx())
                    )
                    drawRect(
                        color = Color(0xFFFB7185),
                        topLeft = Offset(w * 0.78f, h * 0.66f),
                        size = Size(2.5.dp.toPx(), 2.5.dp.toPx())
                    )

                    // Thruster flame
                    val flameLength = h * 0.18f * flamePulse
                    val flamePath = Path().apply {
                        moveTo(cx - w * 0.065f, h * 0.68f)
                        lineTo(cx, h * 0.68f + flameLength)
                        lineTo(cx + w * 0.065f, h * 0.68f)
                        close()
                    }
                    drawPath(
                        path = flamePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFBBF24), Color(0xFFF97316), Color(0xFFEF4444))
                        )
                    )

                    // Left Fin
                    val leftFin = Path().apply {
                        moveTo(cx - w * 0.11f, h * 0.50f)
                        lineTo(cx - w * 0.25f, h * 0.66f)
                        lineTo(cx - w * 0.08f, h * 0.63f)
                        close()
                    }
                    drawPath(leftFin, color = primaryColor)

                    // Right Fin
                    val rightFin = Path().apply {
                        moveTo(cx + w * 0.11f, h * 0.50f)
                        lineTo(cx + w * 0.25f, h * 0.66f)
                        lineTo(cx + w * 0.08f, h * 0.63f)
                        close()
                    }
                    drawPath(rightFin, color = primaryColor)

                    // Fuselage body
                    val bodyPath = Path().apply {
                        moveTo(cx, h * 0.14f)
                        cubicTo(
                            cx + w * 0.16f, h * 0.28f,
                            cx + w * 0.14f, h * 0.54f,
                            cx + w * 0.08f, h * 0.68f
                        )
                        lineTo(cx - w * 0.08f, h * 0.68f)
                        cubicTo(
                            cx - w * 0.14f, h * 0.54f,
                            cx - w * 0.16f, h * 0.28f,
                            cx, h * 0.14f
                        )
                        close()
                    }
                    drawPath(
                        path = bodyPath,
                        color = if (isDarkMode) Color.White else Color(0xFFE2E8F0)
                    )

                    // Nose cone accent cap
                    val nosePath = Path().apply {
                        moveTo(cx, h * 0.14f)
                        cubicTo(
                            cx + w * 0.085f, h * 0.21f,
                            cx + w * 0.105f, h * 0.27f,
                            cx + w * 0.105f, h * 0.30f
                        )
                        lineTo(cx - w * 0.105f, h * 0.30f)
                        cubicTo(
                            cx - w * 0.105f, h * 0.27f,
                            cx - w * 0.085f, h * 0.21f,
                            cx, h * 0.14f
                        )
                        close()
                    }
                    drawPath(nosePath, color = accentCyan)

                    // Circular Porthole Window
                    val windowCenter = Offset(cx, h * 0.44f)
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = w * 0.082f,
                        center = windowCenter
                    )
                    drawCircle(
                        color = Color(0xFF38BDF8),
                        radius = w * 0.058f,
                        center = windowCenter
                    )

                    // Thruster nozzle bar
                    drawLine(
                        color = Color(0xFF475569),
                        start = Offset(cx - w * 0.06f, h * 0.68f),
                        end = Offset(cx + w * 0.06f, h * 0.68f),
                        strokeWidth = 2.5.dp.toPx(),
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ThemedPillCtaButton(
    text: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .testTag(testTag)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}
