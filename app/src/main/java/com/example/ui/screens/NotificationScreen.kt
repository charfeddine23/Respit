package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppNotification
import com.example.data.AppThemeStyle
import com.example.data.Personnel
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.PersonnelViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun NotificationScreen(
    viewModel: PersonnelViewModel,
    onBack: () -> Unit,
    onSelectPersonnel: ((Long) -> Unit)? = null
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val themeStyle by viewModel.currentThemeStyle.collectAsState()
    val isNeumorphic = themeStyle == AppThemeStyle.NEUMORPHISM
    val notifications by viewModel.notifications.collectAsState()
    val allPersonnel by viewModel.allPersonnel.collectAsState()
    val dismissedIds by viewModel.dismissedNotificationIds.collectAsState()
    val isDynamicAnimationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()

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
                .testTag("notifications_scaffold")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = if (isDarkMode) Color(0xE8131D33) else Color(0xFAFFFFFF),
                            border = BorderStroke(
                                1.2.dp,
                                Brush.linearGradient(
                                    if (isDarkMode) listOf(Color(0x5538BDF8), Color(0x33A855F7), Color(0x4510B981))
                                    else listOf(Color(0x6600BCD4), Color(0x443B82F6), Color(0x5510B981))
                                )
                            ),
                            shadowElevation = if (isNeumorphic) 8.dp else 6.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 840.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TactileCircleActionButton(
                                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = AppStrings.get("back", currentLang),
                                    tint = MaterialTheme.colorScheme.primary,
                                    isDark = isDarkMode,
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
                                                    if (urgentCount > 0) Color(0xFFEF4444) else Color(0xFF00BCD4)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))
                                        Text(
                                            text = AppStrings.get("notifications_page_title", currentLang),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (notifications.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(50.dp),
                                                color = (if (urgentCount > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary)
                                                    .copy(alpha = if (isDarkMode) 0.24f else 0.14f),
                                                border = BorderStroke(
                                                    1.dp,
                                                    (if (urgentCount > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary)
                                                        .copy(alpha = 0.55f)
                                                )
                                            ) {
                                                Text(
                                                    text = "${notifications.size}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (urgentCount > 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
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
                                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                TactileCircleActionButton(
                                    icon = Icons.Default.Refresh,
                                    contentDescription = AppStrings.get("refresh", currentLang),
                                    tint = Color(0xFF00BCD4),
                                    isDark = isDarkMode,
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
                                        tint = Color(0xFFEF4444),
                                        isDark = isDarkMode,
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
                                accent = Color(0xFFEF4444),
                                isSelected = selectedFilter == "URGENT",
                                isDark = isDarkMode,
                                haptic = haptic,
                                onClick = {
                                    selectedFilter = if (selectedFilter == "URGENT") "ALL" else "URGENT"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AlertKpiTile(
                                label = AppStrings.get("notif_summary_upcoming", currentLang),
                                count = approachingCount,
                                accent = Color(0xFFF59E0B),
                                isSelected = selectedFilter == "APPROACHING",
                                isDark = isDarkMode,
                                haptic = haptic,
                                onClick = {
                                    selectedFilter = if (selectedFilter == "APPROACHING") "ALL" else "APPROACHING"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AlertKpiTile(
                                label = AppStrings.get("notif_summary_eligible", currentLang),
                                count = eligibleCount,
                                accent = Color(0xFF8B5CF6),
                                isSelected = selectedFilter == "ELIGIBLE",
                                isDark = isDarkMode,
                                haptic = haptic,
                                onClick = {
                                    selectedFilter = if (selectedFilter == "ELIGIBLE") "ALL" else "ELIGIBLE"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AlertKpiTile(
                                label = AppStrings.get("notif_summary_total", currentLang),
                                count = notifications.size,
                                accent = Color(0xFF00BCD4),
                                isSelected = selectedFilter == "ALL",
                                isDark = isDarkMode,
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
                                isDark = isDarkMode
                            )
                            if (pinnedCount > 0) {
                                NotificationFilterChip(
                                    label = "${AppStrings.get("filter_pinned", currentLang)} ($pinnedCount)",
                                    isSelected = selectedFilter == "PINNED",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedFilter = "PINNED"
                                    },
                                    isDark = isDarkMode,
                                    accentColor = Color(0xFFF59E0B)
                                )
                            }
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_returning_today", currentLang)} ($returningTodayCount)",
                                isSelected = selectedFilter == "RETURNING_TODAY",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "RETURNING_TODAY"
                                },
                                isDark = isDarkMode,
                                accentColor = Color(0xFF00BCD4)
                            )
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_overdue", currentLang)} ($overdueCount)",
                                isSelected = selectedFilter == "OVERDUE",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "OVERDUE"
                                },
                                isDark = isDarkMode,
                                accentColor = Color(0xFFEF4444)
                            )
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_approaching", currentLang)} ($approachingCount)",
                                isSelected = selectedFilter == "APPROACHING",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "APPROACHING"
                                },
                                isDark = isDarkMode,
                                accentColor = Color(0xFF10B981)
                            )
                            NotificationFilterChip(
                                label = "${AppStrings.get("filter_eligible", currentLang)} ($eligibleCount)",
                                isSelected = selectedFilter == "ELIGIBLE",
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedFilter = "ELIGIBLE"
                                },
                                isDark = isDarkMode,
                                accentColor = Color(0xFF8B5CF6)
                            )
                        }

                        if (filteredList.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(26.dp),
                                    color = if (isDarkMode) Color(0xE8131D33) else Color(0xFAFFFFFF),
                                    border = BorderStroke(
                                        1.2.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFF10B981).copy(alpha = 0.55f),
                                                Color(0xFF00BCD4).copy(alpha = 0.45f)
                                            )
                                        )
                                    ),
                                    shadowElevation = 8.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(14.dp),
                                        modifier = Modifier.padding(28.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        listOf(
                                                            Color(0xFF10B981).copy(alpha = 0.25f),
                                                            Color(0xFF10B981).copy(alpha = 0.08f)
                                                        )
                                                    )
                                                )
                                                .border(1.5.dp, Color(0xFF10B981).copy(alpha = 0.5f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(38.dp)
                                            )
                                        }

                                        Text(
                                            text = AppStrings.get("notifications_empty_title", currentLang),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                            textAlign = TextAlign.Center
                                        )

                                        Text(
                                            text = AppStrings.get("notifications_empty_sub", currentLang),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            textAlign = TextAlign.Center
                                        )

                                        if (dismissedIds.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            OutlinedButton(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    viewModel.restoreDismissedNotifications()
                                                    Toast.makeText(
                                                        context,
                                                        AppStrings.get("restore_dismissed_toast", currentLang),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                shape = RoundedCornerShape(50.dp),
                                                border = BorderStroke(1.dp, Color(0xFF00BCD4).copy(alpha = 0.6f)),
                                                modifier = Modifier
                                                    .height(48.dp)
                                                    .testTag("restore_dismissed_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00BCD4),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "${AppStrings.get("restore_dismissed_btn", currentLang)} (${dismissedIds.size})",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00BCD4)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("notifications_list"),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredList, key = { it.id }) { notification ->
                                    NotificationCard(
                                        notification = notification,
                                        isDark = isDarkMode,
                                        isNeumorphic = isNeumorphic,
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
            isDarkMode = isDarkMode,
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

    // Custom Floating Gradient-Halo Clear-All Confirmation Popup
    if (showClearAllConfirmDialog) {
        ClearAllNotificationsPopup(
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            haptic = haptic,
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
        color = tint.copy(alpha = if (isDark) 0.16f else 0.10f),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.35f)),
        modifier = modifier
            .size(44.dp)
            .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
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
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.70f, stiffness = Spring.StiffnessMedium),
        label = "kpiTileScale"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected && isDark -> accent.copy(alpha = 0.24f)
            isSelected -> accent.copy(alpha = 0.14f)
            isDark -> Color(0xCC131D33)
            else -> Color(0xF5FFFFFF)
        },
        animationSpec = tween(180),
        label = "kpiTileBg"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) accent else (if (isDark) Color(0x33475569) else Color(0x38CBD5E1))
        ),
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
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
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
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
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = if (isSelected) {
            accentColor.copy(alpha = if (isDark) 0.28f else 0.16f)
        } else {
            if (isDark) Color(0xAA1E293B) else Color(0xE6FFFFFF)
        },
        border = BorderStroke(
            width = if (isSelected) 1.4.dp else 1.dp,
            color = if (isSelected) accentColor else (if (isDark) Color(0x33475569) else Color(0x38CBD5E1))
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isSelected) {
                if (isDark) Color.White else accentColor
            } else {
                if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
            },
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
        )
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
                primaryColor = Color(0xFFEF4444),
                secondaryColor = Color(0xFFDC2626),
                icon = Icons.Default.Warning,
                label = AppStrings.get("notif_type_overdue", currentLang)
            )
            "RETURNING_TODAY" -> NotificationVisualSpec(
                primaryColor = Color(0xFF00BCD4),
                secondaryColor = Color(0xFF2563EB),
                icon = Icons.Default.NotificationsActive,
                label = AppStrings.get("notif_type_returning_today", currentLang)
            )
            "APPROACHING_RETURN" -> NotificationVisualSpec(
                primaryColor = Color(0xFFF59E0B),
                secondaryColor = Color(0xFFEA580C),
                icon = Icons.Default.Event,
                label = AppStrings.get("notif_type_approaching", currentLang)
            )
            "APPROACHING_START" -> NotificationVisualSpec(
                primaryColor = Color(0xFF10B981),
                secondaryColor = Color(0xFF059669),
                icon = Icons.Default.FlightTakeoff,
                label = AppStrings.get("notif_type_leave_start", currentLang)
            )
            "ELIGIBLE" -> NotificationVisualSpec(
                primaryColor = Color(0xFF8B5CF6),
                secondaryColor = Color(0xFF6366F1),
                icon = Icons.Default.Star,
                label = AppStrings.get("notif_type_eligible", currentLang)
            )
            else -> NotificationVisualSpec(
                primaryColor = fallbackPrimary,
                secondaryColor = Color(0xFF00BCD4),
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
    isNeumorphic: Boolean,
    currentLang: AppLanguage,
    haptic: HapticFeedback,
    onCardClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDismiss: () -> Unit,
    onSelectPersonnel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = rememberNotificationVisualSpec(notification.type, currentLang)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "notifCardScale"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onCardClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(22.dp),
        color = if (isDark) Color(0xEA131D33) else Color(0xFAFFFFFF),
        border = BorderStroke(
            width = if (notification.isPinned) 1.6.dp else 1.1.dp,
            brush = if (notification.isPinned) {
                Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24), Color(0xFFF59E0B)))
            } else {
                Brush.linearGradient(
                    listOf(
                        visual.primaryColor.copy(alpha = if (isDark) 0.55f else 0.40f),
                        visual.secondaryColor.copy(alpha = if (isDark) 0.25f else 0.16f)
                    )
                )
            }
        ),
        shadowElevation = if (notification.isPinned) 8.dp else if (isNeumorphic) 6.dp else 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
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
                            listOf(visual.primaryColor, visual.secondaryColor)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Row: Glowing Orb Icon + Type Pill + Pin Badge + Action Icons
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
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            visual.primaryColor.copy(alpha = if (isDark) 0.26f else 0.16f),
                                            visual.secondaryColor.copy(alpha = if (isDark) 0.14f else 0.08f)
                                        )
                                    )
                                )
                                .border(
                                    1.dp,
                                    visual.primaryColor.copy(alpha = 0.45f),
                                    RoundedCornerShape(12.dp)
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
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = visual.primaryColor.copy(alpha = if (isDark) 0.20f else 0.12f),
                                    border = BorderStroke(0.8.dp, visual.primaryColor.copy(alpha = 0.45f))
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
                                    Surface(
                                        shape = RoundedCornerShape(50.dp),
                                        color = Color(0xFFF59E0B).copy(alpha = if (isDark) 0.22f else 0.14f),
                                        border = BorderStroke(0.8.dp, Color(0xFFF59E0B).copy(alpha = 0.55f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PushPin,
                                                contentDescription = null,
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = AppStrings.get("pinned_badge", currentLang),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFF59E0B)
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
                                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = notification.formattedDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
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
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTogglePin()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (notification.isPinned) Color(0xFFF59E0B).copy(alpha = 0.22f)
                                    else if (isDark) Color(0x33334155) else Color(0xFFF1F5F9)
                                )
                                .border(
                                    1.dp,
                                    if (notification.isPinned) Color(0xFFF59E0B).copy(alpha = 0.55f)
                                    else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .testTag("pin_btn_${notification.id}")
                        ) {
                            Icon(
                                imageVector = if (notification.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (notification.isPinned) AppStrings.get("unpin_action", currentLang) else AppStrings.get("pin_action", currentLang),
                                tint = if (notification.isPinned) Color(0xFFF59E0B) else if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDismiss()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) Color(0x26EF4444) else Color(0x18EF4444))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.30f), RoundedCornerShape(10.dp))
                                .testTag("dismiss_btn_${notification.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = AppStrings.get("dismiss_action", currentLang),
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Title
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )

                // Inset Message Container
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0x450F172A) else Color(0xFFF8FAFC),
                    border = BorderStroke(
                        0.8.dp,
                        if (isDark) Color(0x28475569) else Color(0x33CBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }

                // Footer Quick Actions
                if (notification.personnelId != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = visual.primaryColor.copy(alpha = if (isDark) 0.16f else 0.10f),
                            border = BorderStroke(0.9.dp, visual.primaryColor.copy(alpha = 0.38f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onCardClick()
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = visual.primaryColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = AppStrings.get("notif_quick_preview", currentLang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = visual.primaryColor
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.18f else 0.12f),
                            border = BorderStroke(0.9.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelectPersonnel()
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = AppStrings.get("view_record", currentLang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
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
    currentLang: AppLanguage,
    haptic: HapticFeedback,
    onTogglePin: () -> Unit,
    onConfirmDuty: (() -> Unit)?,
    onEditRecord: (() -> Unit)?,
    onDismissPopup: () -> Unit
) {
    val visual = rememberNotificationVisualSpec(notification.type, currentLang)

    Dialog(
        onDismissRequest = onDismissPopup,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(200)) + scaleIn(
                initialScale = 0.90f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
            ),
            exit = fadeOut(tween(150))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .widthIn(max = 380.dp)
                    .testTag("notification_detail_popup"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .drawBehind {
                            val auraAlpha = if (isDarkMode) 0.50f else 0.30f
                            val outerR = 34.dp.toPx()
                            val expand = 8.dp.toPx()
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    listOf(
                                        visual.primaryColor.copy(alpha = auraAlpha),
                                        visual.secondaryColor.copy(alpha = auraAlpha * 0.85f),
                                        Color(0xFF00BCD4).copy(alpha = auraAlpha * 0.75f)
                                    )
                                ),
                                topLeft = Offset(-expand, -expand),
                                size = Size(size.width + expand * 2f, size.height + expand * 2f),
                                cornerRadius = CornerRadius(outerR, outerR),
                                style = Stroke(width = 14.dp.toPx())
                            )
                        }
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = if (isDarkMode) Color(0xFA111827) else Color(0xFCFFFFFF),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(visual.primaryColor.copy(alpha = 0.78f), visual.secondaryColor.copy(alpha = 0.65f))
                            )
                        ),
                        shadowElevation = 18.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Top Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(listOf(visual.primaryColor, visual.secondaryColor))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = visual.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = AppStrings.get("notif_detail_title", currentLang),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Black,
                                            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = visual.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = visual.primaryColor
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onTogglePin()
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (notification.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                            contentDescription = AppStrings.get(if (notification.isPinned) "unpin_action" else "pin_action", currentLang),
                                            tint = if (notification.isPinned) Color(0xFFF59E0B) else Color(0xFF94A3B8)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onDismissPopup()
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = AppStrings.get("close", currentLang),
                                            tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )

                            Text(
                                text = notification.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF334155),
                                lineHeight = 21.sp
                            )

                            if (personnel != null) {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (isDarkMode) Color(0x401E293B) else Color(0xFFF1F5F9),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isDarkMode) Color(0x3838BDF8) else Color(0x380284C7)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${formatRank(personnel.rank, currentLang)} / ${personnel.getLocalizedName(currentLang)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = personnel.registrationNumber,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00BCD4)
                                            )
                                        }

                                        HorizontalDivider(
                                            color = if (isDarkMode) Color(0x28475569) else Color(0x2894A3B8)
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
                                                    color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569)
                                                )
                                            }
                                            Text(
                                                text = "${personnel.formattedStartDate} → ${personnel.formattedReturnDate}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }

                            // Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (onConfirmDuty != null) {
                                    Button(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onConfirmDuty()
                                        },
                                        shape = RoundedCornerShape(50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF10B981),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(17.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("confirm_duty_btn", currentLang),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (onEditRecord != null) {
                                    OutlinedButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onEditRecord()
                                        },
                                        shape = RoundedCornerShape(50.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("edit", currentLang),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
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

@Composable
private fun ClearAllNotificationsPopup(
    isDarkMode: Boolean,
    currentLang: AppLanguage,
    haptic: HapticFeedback,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 360.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .drawBehind {
                        val expand = 7.dp.toPx()
                        val corner = 32.dp.toPx()
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFEF4444).copy(alpha = 0.45f),
                                    Color(0xFFF97316).copy(alpha = 0.38f)
                                )
                            ),
                            topLeft = Offset(-expand, -expand),
                            size = Size(size.width + expand * 2f, size.height + expand * 2f),
                            cornerRadius = CornerRadius(corner, corner),
                            style = Stroke(width = 12.dp.toPx())
                        )
                    }
            ) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = if (isDarkMode) Color(0xFA111827) else Color(0xFCFFFFFF),
                    border = BorderStroke(
                        1.4.dp,
                        Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFF97316)))
                    ),
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ClearAll,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Text(
                            text = AppStrings.get("clear_all_dialog_title", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = AppStrings.get("clear_all_dialog_desc", currentLang),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(50.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = AppStrings.get("cancel", currentLang),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onConfirm()
                                },
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = AppStrings.get("clear_all_confirm", currentLang),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
