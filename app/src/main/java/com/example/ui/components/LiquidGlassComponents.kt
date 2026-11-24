package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.util.AppLanguage
import com.example.util.SyncDialogSecureWindow

internal fun contrastOnAccent(accent: Color): Color =
    if (accent.luminance() > 0.45f) Color(0xFF0F172A) else Color.White

/**
 * Clean, modern sticky header used across dialogs.
 */
@Composable
fun LiquidGlassDialogHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    primaryAccent: Color,
    secondaryAccent: Color,
    isDark: Boolean,
    closeContentDescription: String,
    onDismiss: () -> Unit,
    badgeText: String? = null,
    closeTestTag: String? = null,
    trailingExtra: (@Composable () -> Unit)? = null
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val haptic = LocalHapticFeedback.current
    val iconContainerBg = primaryAccent.copy(alpha = if (effectiveDark) 0.16f else 0.11f)
    val closeBg = if (effectiveDark) Color(0xFF1F2B40) else Color(0xFFF1F5F9)
    val dividerColor = if (effectiveDark) Color(0xFF283548) else Color(0xFFE2E8F0)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconContainerBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryAccent,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (!badgeText.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(primaryAccent.copy(alpha = if (effectiveDark) 0.18f else 0.12f))
                            ) {
                                Text(
                                    text = badgeText,
                                    color = primaryAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (trailingExtra != null) {
                    trailingExtra()
                }
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDismiss()
                    },
                    shape = CircleShape,
                    color = closeBg,
                    modifier = Modifier
                        .size(48.dp)
                        .then(if (closeTestTag != null) Modifier.testTag(closeTestTag) else Modifier)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = closeContentDescription,
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = dividerColor, thickness = 1.dp)
    }
}

/**
 * Clean, calm section header with subtle tinted icon badge (eliminates heavy boxed gradients).
 */
@Composable
fun LiquidGlassSectionHighlight(
    title: String,
    icon: ImageVector,
    accent: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color(0xFFE2E8F0) else SlateTextPrimary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = if (effectiveDark) 0.16f else 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(15.dp)
                )
            }

            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!badgeText.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(accent.copy(alpha = if (effectiveDark) 0.16f else 0.10f))
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Crisp 48dp+ tactile action button (solid filled primary or clean bordered secondary).
 */
@Composable
fun LiquidGlassActionButton(
    text: String,
    onClick: () -> Unit,
    isDark: Boolean,
    primaryAccent: Color,
    secondaryAccent: Color = primaryAccent,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
        label = "glassActionBtnScale"
    )
    val onPrimaryColor = contrastOnAccent(primaryAccent)
    val contentTint = if (isPrimary) onPrimaryColor else textPrimary
    val containerColor = if (isPrimary) {
        primaryAccent
    } else if (effectiveDark) {
        Color(0xFF1E293B)
    } else {
        Color(0xFFF8FAFC)
    }
    val borderStroke = if (isPrimary) null else BorderStroke(
        1.dp,
        if (effectiveDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    )

    Surface(
        onClick = {
            if (enabled) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
        },
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = borderStroke,
        shadowElevation = if (isPrimary && enabled) 2.dp else 0.dp,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.50f
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimary) onPrimaryColor else primaryAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.SemiBold,
                color = contentTint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Clean confirmation modal with calm elevation and restrained typography.
 */
@Composable
fun LiquidGlassConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    icon: ImageVector,
    accentColor: Color = RedDanger,
    secondaryColor: Color = accentColor,
    isDarkMode: Boolean,
    currentLang: AppLanguage,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false,
    dialogTestTag: String? = null,
    confirmTestTag: String? = null,
    cancelTestTag: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val dividerColor = if (effectiveDark) Color(0xFF283548) else Color(0xFFE2E8F0)
    val messageSurface = if (effectiveDark) Color(0xFF1A2538) else Color(0xFFF8FAFC)
    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(
            LocalLayoutDirection provides if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            AnimatedVisibility(
                visibleState = entranceState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(180)) + scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
                } else ExitTransition.None
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 400.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(24.dp),
                                cornerRadius = 24.dp,
                                isDark = effectiveDark,
                                primaryAccent = accentColor,
                                secondaryAccent = secondaryColor
                            )
                            .then(if (dialogTestTag != null) Modifier.testTag(dialogTestTag) else Modifier)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(accentColor.copy(alpha = if (effectiveDark) 0.18f else 0.11f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                textAlign = TextAlign.Center
                            )

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = messageSurface,
                                border = BorderStroke(1.dp, dividerColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.5.sp,
                                    lineHeight = 19.sp,
                                    color = textSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val cancelAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
                                LiquidGlassActionButton(
                                    text = cancelText,
                                    onClick = onDismiss,
                                    isDark = effectiveDark,
                                    primaryAccent = cancelAccent,
                                    isPrimary = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .then(if (cancelTestTag != null) Modifier.testTag(cancelTestTag) else Modifier)
                                )

                                LiquidGlassActionButton(
                                    text = confirmText,
                                    onClick = onConfirm,
                                    isDark = effectiveDark,
                                    primaryAccent = accentColor,
                                    secondaryAccent = secondaryColor,
                                    isPrimary = true,
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .then(if (confirmTestTag != null) Modifier.testTag(confirmTestTag) else Modifier)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
