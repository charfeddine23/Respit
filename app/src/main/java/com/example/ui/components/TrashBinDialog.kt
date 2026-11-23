package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Personnel
import com.example.data.formatRank
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrashBinDialog(
    trashedPersonnel: List<Personnel>,
    isDarkMode: Boolean,
    currentLang: AppLanguage,
    onRestore: (Personnel) -> Unit,
    onRestoreAll: () -> Unit,
    onDeleteForever: (Personnel) -> Unit,
    onEmptyTrash: () -> Unit,
    onDismiss: () -> Unit,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryAccent = preset?.primary ?: CoralRedAccent
    val secondaryAccent = preset?.secondary ?: if (effectiveDark) CyanGlow else RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.62f)
    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US) }

    var showEmptyConfirm by remember { mutableStateOf(false) }

    if (showEmptyConfirm) {
        LiquidGlassConfirmDialog(
            title = AppStrings.get("trash_empty_confirm_title", currentLang),
            message = AppStrings.get("trash_empty_confirm_desc", currentLang),
            confirmText = AppStrings.get("trash_empty_all_btn", currentLang),
            cancelText = AppStrings.get("cancel", currentLang),
            icon = Icons.Default.Warning,
            accentColor = RedDanger,
            secondaryColor = CoralRedAccent,
            isDarkMode = effectiveDark,
            currentLang = currentLang,
            animationsEnabled = animationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled,
            confirmTestTag = "trash_confirm_empty_button",
            onConfirm = {
                showEmptyConfirm = false
                onEmptyTrash()
            },
            onDismiss = { showEmptyConfirm = false }
        )
    }

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
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 560.dp)
                            .heightIn(min = 260.dp, max = 640.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = CoralRedAccent,
                                secondaryAccent = secondaryAccent
                            )
                            .testTag("trash_bin_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LiquidGlassDialogHeader(
                                title = AppStrings.get("trash_bin_title", currentLang),
                                subtitle = AppStrings.get("trash_bin_sub", currentLang),
                                icon = Icons.Default.DeleteSweep,
                                primaryAccent = CoralRedAccent,
                                secondaryAccent = RedDanger,
                                isDark = effectiveDark,
                                closeContentDescription = AppStrings.get("close", currentLang),
                                onDismiss = onDismiss,
                                badgeText = if (trashedPersonnel.isNotEmpty()) "${trashedPersonnel.size}" else null,
                                closeTestTag = "trash_bin_close_button"
                            )

                            HorizontalDivider(color = glassDividerColor)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Action Bar when non-empty
                                AnimatedVisibility(
                                    visible = trashedPersonnel.isNotEmpty(),
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        LiquidGlassActionButton(
                                            text = AppStrings.get("trash_restore_all_btn", currentLang),
                                            icon = Icons.Default.RestoreFromTrash,
                                            isDark = effectiveDark,
                                            primaryAccent = EmeraldSuccess,
                                            secondaryAccent = VibrantCyan,
                                            isPrimary = true,
                                            onClick = onRestoreAll,
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("trash_restore_all_button")
                                        )

                                        LiquidGlassActionButton(
                                            text = AppStrings.get("trash_empty_all_btn", currentLang),
                                            icon = Icons.Default.DeleteForever,
                                            isDark = effectiveDark,
                                            primaryAccent = RedDanger,
                                            secondaryAccent = CoralRedAccent,
                                            isPrimary = false,
                                            onClick = { showEmptyConfirm = true },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("trash_empty_all_button")
                                        )
                                    }
                                }

                                // Content List or Empty State
                                if (trashedPersonnel.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(20.dp),
                                                cornerRadius = 20.dp,
                                                isDark = effectiveDark,
                                                accent = EmeraldSuccess,
                                                secondaryAccent = primaryAccent
                                            )
                                            .padding(vertical = 28.dp, horizontal = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(58.dp)
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(18.dp),
                                                        cornerRadius = 18.dp,
                                                        isDark = effectiveDark,
                                                        accent = EmeraldSuccess,
                                                        secondaryAccent = primaryAccent,
                                                        isSelected = true,
                                                        softTintWhenSelected = true
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteSweep,
                                                    contentDescription = null,
                                                    tint = EmeraldSuccess,
                                                    modifier = Modifier.size(30.dp)
                                                )
                                            }
                                            Text(
                                                text = AppStrings.get("trash_bin_empty_title", currentLang),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary
                                            )
                                            Text(
                                                text = AppStrings.get("trash_bin_empty_desc", currentLang),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = textSecondary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f, fill = false),
                                        contentPadding = PaddingValues(bottom = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(
                                            items = trashedPersonnel,
                                            key = { it.id },
                                            contentType = { "trashed_personnel" }
                                        ) { person ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(18.dp),
                                                        cornerRadius = 18.dp,
                                                        isDark = effectiveDark,
                                                        accent = CoralRedAccent,
                                                        secondaryAccent = secondaryAccent
                                                    )
                                                    .padding(12.dp)
                                                    .testTag("trash_item_${person.id}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(
                                                        modifier = Modifier.weight(1f),
                                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                                    ) {
                                                        Text(
                                                            text = person.getLocalizedName(currentLang),
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 14.sp,
                                                            color = textPrimary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = formatRank(person.rank, currentLang),
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = preset?.primary ?: Color(0xFF0284C7),
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = "• ${person.registrationNumber}",
                                                                fontSize = 10.5.sp,
                                                                fontFamily = FontFamily.Monospace,
                                                                color = textSecondary
                                                            )
                                                        }
                                                        if (person.deletedAtMillis > 0L) {
                                                            Text(
                                                                text = "${AppStrings.get("trash_deleted_at", currentLang)} \u200E${dateFmt.format(Date(person.deletedAtMillis))}\u200E",
                                                                fontSize = 10.sp,
                                                                color = textSecondary
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Surface(
                                                            onClick = { onRestore(person) },
                                                            shape = RoundedCornerShape(14.dp),
                                                            color = Color.Transparent,
                                                            modifier = Modifier
                                                                .height(48.dp)
                                                                .liquidGlassPanel(
                                                                    shape = RoundedCornerShape(14.dp),
                                                                    cornerRadius = 14.dp,
                                                                    isDark = effectiveDark,
                                                                    accent = EmeraldSuccess,
                                                                    secondaryAccent = VibrantCyan,
                                                                    isSelected = true,
                                                                    softTintWhenSelected = true
                                                                )
                                                                .testTag("trash_restore_${person.id}")
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.padding(horizontal = 10.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.RestoreFromTrash,
                                                                    contentDescription = AppStrings.get("trash_restore_btn", currentLang),
                                                                    tint = EmeraldSuccess,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text(
                                                                    text = AppStrings.get("trash_restore_btn", currentLang),
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = EmeraldSuccess
                                                                )
                                                            }
                                                        }

                                                        Surface(
                                                            onClick = { onDeleteForever(person) },
                                                            shape = RoundedCornerShape(14.dp),
                                                            color = Color.Transparent,
                                                            modifier = Modifier
                                                                .size(48.dp)
                                                                .liquidGlassPanel(
                                                                    shape = RoundedCornerShape(14.dp),
                                                                    cornerRadius = 14.dp,
                                                                    isDark = effectiveDark,
                                                                    accent = RedDanger,
                                                                    secondaryAccent = CoralRedAccent,
                                                                    isSelected = true,
                                                                    softTintWhenSelected = true
                                                                )
                                                                .testTag("trash_delete_forever_${person.id}")
                                                        ) {
                                                            Box(
                                                                contentAlignment = Alignment.Center,
                                                                modifier = Modifier.fillMaxSize()
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.DeleteForever,
                                                                    contentDescription = AppStrings.get("trash_delete_forever", currentLang),
                                                                    tint = RedDanger,
                                                                    modifier = Modifier.size(18.dp)
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
            }
        }
    }
}
