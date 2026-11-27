package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppNotification
import com.example.data.Personnel
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.PersonnelViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow

@Composable
fun NotificationScreen(
    viewModel: PersonnelViewModel,
    onBack: () -> Unit,
    onSelectPersonnel: ((Long) -> Unit)? = null,
    topBarClearance: Dp = 0.dp,
    bottomDockClearance: Dp = 0.dp
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val activePreset = LocalThemePreset.current
    val effectiveDark = activePreset?.isDark ?: isDarkMode
    val primaryAccent = activePreset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryAccent = activePreset?.secondary ?: RoyalBlueAccent
    val textPrimary = activePreset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = activePreset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val notifications by viewModel.notifications.collectAsState()
    val allPersonnel by viewModel.allPersonnel.collectAsState()
    val dismissedIds by viewModel.dismissedNotificationIds.collectAsState()
    val isDynamicAnimationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()
    val isNoScreenshotEnabled by viewModel.isNoScreenshotEnabled.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var showClearAllConfirmDialog by remember { mutableStateOf(false) }
    var selectedNotificationForPopup by remember { mutableStateOf<AppNotification?>(null) }

    val overdueCount = remember(notifications) { notifications.count { it.type == "OVERDUE" } }
    val returningTodayCount = remember(notifications) { notifications.count { it.type == "RETURNING_TODAY" } }
    val urgentCount = overdueCount + returningTodayCount
    val approachingCount = remember(notifications) {
        notifications.count { it.type == "APPROACHING_RETURN" || it.type == "APPROACHING_START" }
    }
    val eligibleCount = remember(notifications) { notifications.count { it.type == "ELIGIBLE" } }
    val pinnedCount = remember(notifications) { notifications.count { it.isPinned } }

    val filteredList = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            "PINNED" -> notifications.filter { it.isPinned }
            "URGENT" -> notifications.filter { it.type == "OVERDUE" || it.type == "RETURNING_TODAY" }
            "RETURNING_TODAY" -> notifications.filter { it.type == "RETURNING_TODAY" }
            "OVERDUE" -> notifications.filter { it.type == "OVERDUE" }
            "APPROACHING" -> notifications.filter { it.type == "APPROACHING_RETURN" || it.type == "APPROACHING_START" }
            "ELIGIBLE" -> notifications.filter { it.type == "ELIGIBLE" }
            else -> notifications
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topBarClearance)
                .testTag("notifications_scaffold")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 840.dp)
                        .liquidGlass(
                            shape = RoundedCornerShape(24.dp),
                            elevation = 5.dp,
                            borderAlpha = 0.9f,
                            isDark = effectiveDark,
                            cornerRadius = 24.dp
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TactileCircleActionButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = AppStrings.get("back", currentLang),
                            tint = primaryAccent,
                            isDark = effectiveDark,
                            haptic = haptic,
                            onClick = onBack,
                            modifier = Modifier.testTag("notifications_back_button")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (urgentCount > 0) RedDanger else primaryAccent
                                        )
                                )
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = AppStrings.get("notifications_page_title", currentLang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (notifications.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val badgeTint = if (urgentCount > 0) RedDanger else primaryAccent
                                    Box(
                                        modifier = Modifier.liquidGlassPanel(
                                            shape = RoundedCornerShape(50.dp),
                                            cornerRadius = 50.dp,
                                            isDark = effectiveDark,
                                            accent = badgeTint,
                                            secondaryAccent = badgeTint,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                    ) {
                                        Text(
                                            text = "${notifications.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = badgeTint,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = AppStrings.get("notifications_subtitle", currentLang),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                color = textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        TactileCircleActionButton(
                            icon = Icons.Default.Refresh,
                            contentDescription = AppStrings.get("refresh", currentLang),
                            tint = primaryAccent,
                            isDark = effectiveDark,
                            haptic = haptic,
                            onClick = {
                                val count = viewModel.checkUpcomingNotifications()
                                Toast.makeText(
                                    context,
                                    "${AppStrings.get("notifications_checked", currentLang)} ($count)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("notifications_refresh_button")
                        )

                        if (notifications.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            TactileCircleActionButton(
                                icon = Icons.Default.ClearAll,
                                contentDescription = AppStrings.get("clear_all", currentLang),
                                tint = RedDanger,
                                isDark = effectiveDark,
                                haptic = haptic,
                                onClick = { showClearAllConfirmDialog = true },
                                modifier = Modifier.testTag("notifications_clear_all_button")
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 840.dp)
                    ) {
                        // Interactive Alert Summary Strip (4 tactile metric tiles)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AlertKpiTile(
                                label = AppStrings.get("notif_summary_urgent", currentLang),
                                count = urgentCount,
                                accent = RedDanger,
                                isSelected = selectedFilter == "URGENT",
                                isDark = effectiveDark,
                                haptic = haptic,
                                onClick = {
                                    selectedFilter = if (selectedFilter == "URGENT") "ALL" else "URGENT"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AlertKpiTile(
                                label = AppStrings.get("notif_summary_upcoming", currentLang),
                                count = approachingCount,
                                accent = AmberWarning,
                                isSelected = selectedFilter == "APPROACHING",
                                isDark = effectiveDark,
                                haptic = haptic,
                                onClick = {
                                    selectedFilter = if (selectedFilter == "APPROACHING") "ALL" else "APPROACHING"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AlertKpiTile(
                                label = AppStrings.get("notif_summary_eligible", currentLang),
                                count = eligibleCount,
                                accent = EmeraldSuccess,
                                isSelected = selectedFilter == "ELIGIBLE",
                                isDark = effectiveDark,
                                haptic = haptic,
                                onClick = {
                                    selectedFilter = if (selectedFilter == "ELIGIBLE") "ALL" else "ELIGIBLE"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AlertKpiTile(
                                label = AppStrings.get("notif_summary_total", currentLang),
                                count = notifications.size,
                                accent = primaryAccent,
                                isSelected = selectedFilter == "ALL",
                                isDark = effectiveDark,
                                haptic = haptic,
                                onClick = { selectedFilter = "ALL" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Secondary Category Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NotificationFilterChip(
                                label = "${AppStrings.get("all_count", currentLang)} (${notifications.size})",
                                isSelected = selectedFilter == "ALL",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "ALL"
                                },
                                isDark = effectiveDark,
                                accentColor = primaryAccent
                            )
                            if (pinnedCount > 0) {
                                NotificationFilterChip(
                                    label = "${AppStrings.get("filter_pinned", currentLang)} ($pinnedCount)",
                                    isSelected = selectedFilter == "PINNED",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedFilter = "PINNED"
                                    },
                                    isDark = effectiveDark,
                                    accentColor = AmberWarning
                                )
                            }
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_returning_today", currentLang)} ($returningTodayCount)",
                                isSelected = selectedFilter == "RETURNING_TODAY",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "RETURNING_TODAY"
                                },
                                isDark = effectiveDark,
                                accentColor = primaryAccent
                            )
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_overdue", currentLang)} ($overdueCount)",
                                isSelected = selectedFilter == "OVERDUE",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "OVERDUE"
                                },
                                isDark = effectiveDark,
                                accentColor = RedDanger
                            )
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_approaching", currentLang)} ($approachingCount)",
                                isSelected = selectedFilter == "APPROACHING",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "APPROACHING"
                                },
                                isDark = effectiveDark,
                                accentColor = AmberWarning
                            )
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_eligible", currentLang)} ($eligibleCount)",
                                isSelected = selectedFilter == "ELIGIBLE",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "ELIGIBLE"
                                },
                                isDark = effectiveDark,
                                accentColor = EmeraldSuccess
                            )
                        }

                        if (filteredList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp, vertical = 20.dp)
                                    .padding(bottom = bottomDockClearance),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .liquidGlass(
                                            shape = RoundedCornerShape(26.dp),
                                            elevation = 6.dp,
                                            borderAlpha = 0.9f,
                                            isDark = effectiveDark,
                                            cornerRadius = 26.dp
                                        )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(14.dp),
                                        modifier = Modifier.padding(28.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .liquidGlassPanel(
                                                    shape = CircleShape,
                                                    cornerRadius = 36.dp,
                                                    isDark = effectiveDark,
                                                    accent = EmeraldSuccess,
                                                    secondaryAccent = VibrantCyan,
                                                    isSelected = true,
                                                    softTintWhenSelected = true
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = EmeraldSuccess,
                                                modifier = Modifier.size(38.dp)
                                            )
                                        }

                                        Text(
                                            text = AppStrings.get("notifications_empty_title", currentLang),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textPrimary,
                                            textAlign = TextAlign.Center
                                        )

                                        Text(
                                            text = AppStrings.get("notifications_empty_sub", currentLang),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textSecondary,
                                            textAlign = TextAlign.Center
                                        )

                                        if (dismissedIds.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            com.example.ui.components.LiquidGlassActionButton(
                                                text = "${AppStrings.get("restore_dismissed_btn", currentLang)} (${dismissedIds.size})",
                                                onClick = {
                                                    viewModel.restoreDismissedNotifications()
                                                    Toast.makeText(
                                                        context,
                                                        AppStrings.get("restore_dismissed_toast", currentLang),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                isDark = effectiveDark,
                                                primaryAccent = VibrantCyan,
                                                secondaryAccent = RoyalBlueAccent,
                                                icon = Icons.Default.Refresh,
                                                isPrimary = false,
                                                modifier = Modifier.testTag("restore_dismissed_button")
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("notifications_list"),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 10.dp,
                                    bottom = maxOf(bottomDockClearance + 24.dp, 116.dp)
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredList, key = { it.id }) { notification ->
                                    NotificationCard(
                                        notification = notification,
                                        isDark = effectiveDark,
                                        currentLang = currentLang,
                                        haptic = haptic,
                                        onCardClick = { selectedNotificationForPopup = notification },
                                        onTogglePin = { viewModel.togglePinNotification(notification.id) },
                                        onDismiss = {
                                            viewModel.dismissNotification(notification.id)
                                            Toast.makeText(
                                                context,
                                                AppStrings.get("dismiss_notification_toast", currentLang),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onSelectPersonnel = {
                                            if (notification.personnelId != null && onSelectPersonnel != null) {
                                                onSelectPersonnel(notification.personnelId)
                                                onBack()
                                            }
                                        },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
        }
    }

    // Floating Gradient-Halo Popup when tapping a notification card
    val activePopupNotification = selectedNotificationForPopup
    if (activePopupNotification != null) {
        val linkedPersonnel = remember(activePopupNotification.personnelId, allPersonnel) {
            allPersonnel.find { it.id == activePopupNotification.personnelId }
        }
        NotificationDetailPopup(
            notification = activePopupNotification,
            personnel = linkedPersonnel,
            isDarkMode = effectiveDark,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled,
            currentLang = currentLang,
            haptic = haptic,
            onTogglePin = {
                viewModel.togglePinNotification(activePopupNotification.id)
                selectedNotificationForPopup = activePopupNotification.copy(isPinned = !activePopupNotification.isPinned)
            },
            onConfirmDuty = if (linkedPersonnel != null && !linkedPersonnel.isReturnedToDuty) {
                {
                    viewModel.confirmReturnToDuty(linkedPersonnel.id)
                    selectedNotificationForPopup = null
                    Toast.makeText(
                        context,
                        "${AppStrings.get("confirm_duty_btn", currentLang)}: ${formatRank(linkedPersonnel.rank, currentLang)} / ${linkedPersonnel.getLocalizedName(currentLang)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else null,
            onEditRecord = if (linkedPersonnel != null && onSelectPersonnel != null) {
                {
                    selectedNotificationForPopup = null
                    onSelectPersonnel(linkedPersonnel.id)
                    onBack()
                }
            } else null,
            onDismissPopup = { selectedNotificationForPopup = null }
        )
    }

    // 5%-Transparency Liquid Glass Clear-All Confirmation Dialog
    if (showClearAllConfirmDialog) {
        com.example.ui.components.LiquidGlassConfirmDialog(
            title = AppStrings.get("clear_all_dialog_title", currentLang),
            message = AppStrings.get("clear_all_dialog_desc", currentLang),
            confirmText = AppStrings.get("clear_all_confirm", currentLang),
            cancelText = AppStrings.get("cancel", currentLang),
            icon = Icons.Default.ClearAll,
            accentColor = RedDanger,
            secondaryColor = RedDanger,
            isDarkMode = effectiveDark,
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled,
            onConfirm = {
                viewModel.clearAllNotifications()
                showClearAllConfirmDialog = false
                Toast.makeText(
                    context,
                    AppStrings.get("clear_all_success_toast", currentLang),
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { showClearAllConfirmDialog = false }
        )
    }
}

@Composable
private fun TactileCircleActionButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    isDark: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "notifActionBtnScale"
    )
    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        modifier = modifier
            .size(48.dp)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .liquidGlassPanel(
                shape = RoundedCornerShape(14.dp),
                cornerRadius = 14.dp,
                isDark = effectiveDark,
                accent = tint,
                secondaryAccent = tint,
                isSelected = true,
                softTintWhenSelected = true
            )
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AlertKpiTile(
    label: String,
    count: Int,
    accent: Color,
    isSelected: Boolean,
    isDark: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF334155)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.70f, stiffness = Spring.StiffnessMedium),
        label = "kpiTileScale"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .liquidGlassPanel(
                shape = RoundedCornerShape(16.dp),
                cornerRadius = 16.dp,
                isDark = effectiveDark,
                accent = accent,
                secondaryAccent = accent,
                isSelected = isSelected,
                softTintWhenSelected = true
            )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = accent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NotificationFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val textColor = when {
        isSelected && effectiveDark -> Color.White
        isSelected -> accentColor
        else -> preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF334155)
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = Color.Transparent,
        modifier = Modifier
            .defaultMinSize(minHeight = 40.dp)
            .liquidGlassPanel(
                shape = RoundedCornerShape(50.dp),
                cornerRadius = 50.dp,
                isDark = effectiveDark,
                accent = accentColor,
                secondaryAccent = accentColor,
                isSelected = isSelected,
                softTintWhenSelected = true
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

private data class NotificationVisualSpec(
    val primaryColor: Color,
    val secondaryColor: Color,
    val icon: ImageVector,
    val label: String
)

@Composable
private fun rememberNotificationVisualSpec(
    type: String,
    currentLang: AppLanguage
): NotificationVisualSpec {
    val fallbackPrimary = MaterialTheme.colorScheme.primary
    return remember(type, currentLang, fallbackPrimary) {
        when (type) {
            "OVERDUE" -> NotificationVisualSpec(
                primaryColor = Color(0xFFDC2626),
                secondaryColor = Color(0xFFB91C1C),
                icon = Icons.Default.Warning,
                label = AppStrings.get("notif_type_overdue", currentLang)
            )
            "RETURNING_TODAY" -> NotificationVisualSpec(
                primaryColor = fallbackPrimary,
                secondaryColor = fallbackPrimary,
                icon = Icons.Default.NotificationsActive,
                label = AppStrings.get("notif_type_returning_today", currentLang)
            )
            "APPROACHING_RETURN" -> NotificationVisualSpec(
                primaryColor = Color(0xFFD97706),
                secondaryColor = Color(0xFFB45309),
                icon = Icons.Default.Event,
                label = AppStrings.get("notif_type_approaching", currentLang)
            )
            "APPROACHING_START" -> NotificationVisualSpec(
                primaryColor = Color(0xFF059669),
                secondaryColor = Color(0xFF047857),
                icon = Icons.Default.FlightTakeoff,
                label = AppStrings.get("notif_type_leave_start", currentLang)
            )
            "ELIGIBLE" -> NotificationVisualSpec(
                primaryColor = Color(0xFF059669),
                secondaryColor = Color(0xFF0D9488),
                icon = Icons.Default.Star,
                label = AppStrings.get("notif_type_eligible", currentLang)
            )
            else -> NotificationVisualSpec(
                primaryColor = fallbackPrimary,
                secondaryColor = fallbackPrimary,
                icon = Icons.Default.Notifications,
                label = AppStrings.get("notif_type_alert", currentLang)
            )
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    isDark: Boolean,
    currentLang: AppLanguage,
    haptic: HapticFeedback,
    onCardClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDismiss: () -> Unit,
    onSelectPersonnel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDark
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val visual = rememberNotificationVisualSpec(notification.type, currentLang)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "notifCardScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .liquidGlass(
                shape = RoundedCornerShape(22.dp),
                elevation = if (notification.isPinned) 7.dp else 4.dp,
                borderAlpha = if (notification.isPinned) 0.95f else 0.85f,
                isDark = effectiveDark,
                cornerRadius = 22.dp
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCardClick()
            }
            .testTag("notification_card_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Vertical Gradient Accent Spine
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            if (notification.isPinned) {
                                listOf(AmberWarning, Color(0xFFFBBF24), visual.primaryColor)
                            } else {
                                listOf(visual.primaryColor, visual.secondaryColor)
                            }
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Row: Glowing Glass Icon + Type Pill + Pin Badge + 48dp Action Icons
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
                                .size(40.dp)
                                .liquidGlassPanel(
                                    shape = RoundedCornerShape(12.dp),
                                    cornerRadius = 12.dp,
                                    isDark = effectiveDark,
                                    accent = visual.primaryColor,
                                    secondaryAccent = visual.secondaryColor,
                                    isSelected = true,
                                    softTintWhenSelected = true
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = visual.icon,
                                contentDescription = null,
                                tint = visual.primaryColor,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier.liquidGlassPanel(
                                        shape = RoundedCornerShape(50.dp),
                                        cornerRadius = 50.dp,
                                        isDark = effectiveDark,
                                        accent = visual.primaryColor,
                                        secondaryAccent = visual.secondaryColor,
                                        isSelected = true,
                                        softTintWhenSelected = true
                                    )
                                ) {
                                    Text(
                                        text = visual.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = visual.primaryColor,
                                        fontSize = 10.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                if (notification.isPinned) {
                                    Box(
                                        modifier = Modifier.liquidGlassPanel(
                                            shape = RoundedCornerShape(50.dp),
                                            cornerRadius = 50.dp,
                                            isDark = effectiveDark,
                                            accent = AmberWarning,
                                            secondaryAccent = AmberWarning,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = null,
                                                tint = AmberWarning,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = AppStrings.get("pinned_badge", currentLang),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AmberWarning
                                            )
                                        }
                                    }
                                }
                            }

                            if (notification.formattedDate.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = textSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = notification.formattedDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = textSecondary,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTogglePin()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(48.dp)
                                .liquidGlassPanel(
                                    shape = RoundedCornerShape(12.dp),
                                    cornerRadius = 12.dp,
                                    isDark = effectiveDark,
                                    accent = AmberWarning,
                                    secondaryAccent = AmberWarning,
                                    isSelected = notification.isPinned,
                                    softTintWhenSelected = true
                                )
                                .testTag("pin_btn_${notification.id}")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = if (notification.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = if (notification.isPinned) AppStrings.get("unpin_action", currentLang) else AppStrings.get("pin_action", currentLang),
                                    tint = if (notification.isPinned) AmberWarning else textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(48.dp)
                                .liquidGlassPanel(
                                    shape = RoundedCornerShape(12.dp),
                                    cornerRadius = 12.dp,
                                    isDark = effectiveDark,
                                    accent = RedDanger,
                                    secondaryAccent = RedDanger,
                                    isSelected = true,
                                    softTintWhenSelected = true
                                )
                                .testTag("dismiss_btn_${notification.id}")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = AppStrings.get("dismiss_action", currentLang),
                                    tint = RedDanger,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Title
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )

                // Inset Liquid Glass Message Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(14.dp),
                            cornerRadius = 14.dp,
                            isDark = effectiveDark,
                            accent = visual.primaryColor,
                            secondaryAccent = visual.secondaryColor
                        )
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (effectiveDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        lineHeight = 20.sp
                    )
                }

                // Footer Quick Actions
                if (notification.personnelId != null) {
                    val quickPreviewAccent = visual.primaryColor
                    val viewRecordAccent = MaterialTheme.colorScheme.primary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onCardClick()
                            },
                            shape = RoundedCornerShape(50.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .defaultMinSize(minHeight = 40.dp)
                                .liquidGlassPanel(
                                    shape = RoundedCornerShape(50.dp),
                                    cornerRadius = 50.dp,
                                    isDark = effectiveDark,
                                    accent = quickPreviewAccent,
                                    secondaryAccent = visual.secondaryColor,
                                    isSelected = true,
                                    softTintWhenSelected = true
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = quickPreviewAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = AppStrings.get("notif_quick_preview", currentLang),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = quickPreviewAccent
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectPersonnel()
                            },
                            shape = RoundedCornerShape(50.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .defaultMinSize(minHeight = 40.dp)
                                .liquidGlassPanel(
                                    shape = RoundedCornerShape(50.dp),
                                    cornerRadius = 50.dp,
                                    isDark = effectiveDark,
                                    accent = viewRecordAccent,
                                    secondaryAccent = RoyalBlueAccent,
                                    isSelected = true,
                                    softTintWhenSelected = true
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = viewRecordAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = AppStrings.get("view_record", currentLang),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = viewRecordAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationDetailPopup(
    notification: AppNotification,
    personnel: Personnel?,
    isDarkMode: Boolean,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false,
    currentLang: AppLanguage,
    haptic: HapticFeedback,
    onTogglePin: () -> Unit,
    onConfirmDuty: (() -> Unit)?,
    onEditRecord: (() -> Unit)?,
    onDismissPopup: () -> Unit
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else SlateTextSecondary
    val visual = rememberNotificationVisualSpec(notification.type, currentLang)

    Dialog(
        onDismissRequest = onDismissPopup,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(
            LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = if (animationsEnabled) {
                    fadeIn(tween(200)) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                    )
                } else androidx.compose.animation.EnterTransition.None,
                exit = fadeOut(tween(150))
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
                                primaryAccent = visual.primaryColor,
                                secondaryAccent = visual.secondaryColor
                            )
                            .testTag("notification_detail_popup")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Sticky Frosted Top Bar with 48dp touch targets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(14.dp),
                                                cornerRadius = 14.dp,
                                                isDark = effectiveDark,
                                                accent = visual.primaryColor,
                                                secondaryAccent = visual.secondaryColor,
                                                isSelected = true,
                                                softTintWhenSelected = false
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = visual.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(21.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = AppStrings.get("notif_detail_title", currentLang),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Black,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = visual.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = visual.primaryColor
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onTogglePin()
                                        },
                                        shape = CircleShape,
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .liquidGlassPanel(
                                                shape = CircleShape,
                                                cornerRadius = 24.dp,
                                                isDark = effectiveDark,
                                                accent = AmberWarning,
                                                secondaryAccent = AmberWarning,
                                                isSelected = notification.isPinned,
                                                softTintWhenSelected = true
                                            )
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Icon(
                                                imageVector = if (notification.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                                contentDescription = AppStrings.get(if (notification.isPinned) "unpin_action" else "pin_action", currentLang),
                                                tint = if (notification.isPinned) AmberWarning else textSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onDismissPopup()
                                        },
                                        shape = CircleShape,
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .liquidGlassPanel(
                                                shape = CircleShape,
                                                cornerRadius = 24.dp,
                                                isDark = effectiveDark,
                                                accent = visual.primaryColor,
                                                secondaryAccent = visual.secondaryColor
                                            )
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
                                }
                            }

                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlassPanel(
                                        shape = RoundedCornerShape(16.dp),
                                        cornerRadius = 16.dp,
                                        isDark = effectiveDark,
                                        accent = visual.primaryColor,
                                        secondaryAccent = visual.secondaryColor
                                    )
                                    .padding(horizontal = 13.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = notification.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (effectiveDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                                    lineHeight = 21.sp
                                )
                            }

                            if (personnel != null) {
                                val popupAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(18.dp),
                                            cornerRadius = 18.dp,
                                            isDark = effectiveDark,
                                            accent = popupAccent,
                                            secondaryAccent = RoyalBlueAccent,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${formatRank(personnel.rank, currentLang)} / ${personnel.getLocalizedName(currentLang)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = personnel.registrationNumber,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = popupAccent
                                            )
                                        }

                                        HorizontalDivider(
                                            color = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.6f)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = visual.primaryColor,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = formatWilaya(personnel.wilaya, currentLang, includeCode = false),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = textSecondary
                                                )
                                            }
                                            Text(
                                                text = "${personnel.formattedStartDate} → ${personnel.formattedReturnDate}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = textSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            if (onConfirmDuty != null || onEditRecord != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (onConfirmDuty != null) {
                                        com.example.ui.components.LiquidGlassActionButton(
                                            text = AppStrings.get("confirm_duty_btn", currentLang),
                                            onClick = onConfirmDuty,
                                            isDark = effectiveDark,
                                            primaryAccent = EmeraldSuccess,
                                            secondaryAccent = Color(0xFF0D9488),
                                            icon = Icons.Default.CheckCircle,
                                            isPrimary = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    if (onEditRecord != null) {
                                        com.example.ui.components.LiquidGlassActionButton(
                                            text = AppStrings.get("edit", currentLang),
                                            onClick = onEditRecord,
                                            isDark = effectiveDark,
                                            primaryAccent = if (effectiveDark) CyanGlow else RoyalBlueAccent,
                                            secondaryAccent = VibrantCyan,
                                            icon = Icons.Default.Edit,
                                            isPrimary = false,
                                            modifier = Modifier.weight(1f)
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
