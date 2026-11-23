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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.SyncDialogSecureWindow

/**
 * Shared sticky Liquid Glass header used across redesigned dialogs.
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
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        primaryAccent.copy(alpha = if (effectiveDark) 0.22f else 0.12f),
                        secondaryAccent.copy(alpha = if (effectiveDark) 0.14f else 0.06f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        .size(44.dp)
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(14.dp),
                            cornerRadius = 14.dp,
                            isDark = effectiveDark,
                            accent = primaryAccent,
                            secondaryAccent = secondaryAccent,
                            isSelected = true,
                            softTintWhenSelected = false
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (!badgeText.isNullOrBlank()) {
                            Box(
                                modifier = Modifier.liquidGlassPanel(
                                    shape = RoundedCornerShape(50.dp),
                                    cornerRadius = 50.dp,
                                    isDark = effectiveDark,
                                    accent = primaryAccent,
                                    secondaryAccent = secondaryAccent,
                                    isSelected = true,
                                    softTintWhenSelected = false
                                )
                            ) {
                                Text(
                                    text = badgeText,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
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
                    color = Color.Transparent,
                    modifier = Modifier
                        .size(48.dp)
                        .liquidGlassPanel(
                            shape = CircleShape,
                            cornerRadius = 24.dp,
                            isDark = effectiveDark,
                            accent = primaryAccent,
                            secondaryAccent = secondaryAccent
                        )
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
    }
}

/**
 * Highlighted section header pill matching the design language across dialogs and screens.
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
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlassPanel(
                shape = RoundedCornerShape(14.dp),
                cornerRadius = 14.dp,
                isDark = effectiveDark,
                accent = accent,
                secondaryAccent = accent,
                isSelected = true,
                softTintWhenSelected = true
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        .size(28.dp)
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(9.dp),
                            cornerRadius = 9.dp,
                            isDark = effectiveDark,
                            accent = accent,
                            secondaryAccent = accent,
                            isSelected = true,
                            softTintWhenSelected = false
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!badgeText.isNullOrBlank()) {
                Box(
                    modifier = Modifier.liquidGlassPanel(
                        shape = RoundedCornerShape(50.dp),
                        cornerRadius = 50.dp,
                        isDark = effectiveDark,
                        accent = accent,
                        secondaryAccent = accent,
                        isSelected = true,
                        softTintWhenSelected = true
                    )
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tactile 48dp+ Liquid Glass action button (either glowing primary gradient or frosted neutral glass).
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
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "glassActionBtnScale"
    )
    val contentTint = if (isPrimary) Color.White else textPrimary

    val buttonModifier = modifier
        .defaultMinSize(minHeight = 48.dp)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            alpha = if (enabled) 1f else 0.55f
        }
        .then(
            if (isPrimary) {
                Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = primaryAccent.copy(alpha = 0.35f),
                    spotColor = secondaryAccent.copy(alpha = 0.35f)
                )
            } else Modifier
        )
        .liquidGlassPanel(
            shape = RoundedCornerShape(16.dp),
            cornerRadius = 16.dp,
            isDark = effectiveDark,
            accent = primaryAccent,
            secondaryAccent = secondaryAccent,
            isSelected = isPrimary,
            softTintWhenSelected = !isPrimary
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
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = buttonModifier
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
                    tint = if (isPrimary) Color.White else primaryAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isPrimary) FontWeight.ExtraBold else FontWeight.Bold,
                color = contentTint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Reusable 5%-transparency Liquid Glass confirmation modal replacing stock AlertDialogs.
 */
@Composable
fun LiquidGlassConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    icon: ImageVector,
    accentColor: Color = RedDanger,
    secondaryColor: Color = Color(0xFFF97316),
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
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.62f)
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
                        initialScale = 0.93f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(120)) + scaleOut(targetScale = 0.95f)
                } else ExitTransition.None
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 420.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
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
                                    .size(58.dp)
                                    .shadow(
                                        elevation = 10.dp,
                                        shape = RoundedCornerShape(18.dp),
                                        ambientColor = accentColor.copy(alpha = 0.35f),
                                        spotColor = secondaryColor.copy(alpha = 0.35f)
                                    )
                                    .liquidGlassPanel(
                                        shape = RoundedCornerShape(18.dp),
                                        cornerRadius = 18.dp,
                                        isDark = effectiveDark,
                                        accent = accentColor,
                                        secondaryAccent = secondaryColor,
                                        isSelected = true,
                                        softTintWhenSelected = false
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = textPrimary,
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlassPanel(
                                        shape = RoundedCornerShape(16.dp),
                                        cornerRadius = 16.dp,
                                        isDark = effectiveDark,
                                        accent = accentColor,
                                        secondaryAccent = secondaryColor
                                    )
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.5.sp,
                                    lineHeight = 19.sp,
                                    color = textSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            HorizontalDivider(color = glassDividerColor)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                LiquidGlassActionButton(
                                    text = cancelText,
                                    onClick = onDismiss,
                                    isDark = effectiveDark,
                                    primaryAccent = if (effectiveDark) CyanGlow else VibrantCyan,
                                    secondaryAccent = RoyalBlueAccent,
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
                                        .weight(1.15f)
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
