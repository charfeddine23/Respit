package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppNotification
import com.example.data.Personnel
import com.example.ui.PersonnelViewModel
import com.example.ui.theme.DynamicAuroraBackground
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: PersonnelViewModel,
    onBack: () -> Unit,
    onSelectPersonnel: ((Long) -> Unit)? = null
) {
    // Intercept back navigation
    BackHandler { onBack() }

    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val pinnedIds by viewModel.pinnedNotificationIds.collectAsState()
    val dismissedIds by viewModel.dismissedNotificationIds.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "PINNED", "RETURNING_TODAY", "OVERDUE", "APPROACHING", "ELIGIBLE"
    var showClearAllConfirmDialog by remember { mutableStateOf(false) }

    // Filter list
    val filteredList = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            "PINNED" -> notifications.filter { it.isPinned }
            "RETURNING_TODAY" -> notifications.filter { it.type == "RETURNING_TODAY" }
            "OVERDUE" -> notifications.filter { it.type == "OVERDUE" }
            "APPROACHING" -> notifications.filter { it.type == "APPROACHING_RETURN" || it.type == "APPROACHING_START" }
            "ELIGIBLE" -> notifications.filter { it.type == "ELIGIBLE" }
            else -> notifications
        }
    }

    val pinnedCount = remember(notifications) { notifications.count { it.isPinned } }

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ambient Liquid Dynamic Aurora Mesh Background (Smooth 60fps drifting orbs)
            DynamicAuroraBackground(isDark = isDarkMode)

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("notifications_scaffold"),
                containerColor = Color.Transparent,
                topBar = {
                    Surface(
                        color = if (isDarkMode) Color(0xFC0F172A) else Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TopAppBar(
                            navigationIcon = {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .liquidGlassInteractive(
                                            shape = RoundedCornerShape(12.dp),
                                            glowColor = MaterialTheme.colorScheme.primary
                                        )
                                ) {
                                    IconButton(
                                        onClick = onBack,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .testTag("notifications_back_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = AppStrings.get("back", currentLang),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            },
                            title = {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = AppStrings.get("notifications_page_title", currentLang),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (notifications.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "${notifications.size}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "متابعة الإجازات، الدخول اليوم، والتأخر",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)
                                    )
                                }
                            },
                            actions = {
                                // Refresh Button
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .liquidGlassInteractive(
                                            shape = RoundedCornerShape(12.dp),
                                            glowColor = MaterialTheme.colorScheme.primary
                                        )
                                ) {
                                    IconButton(
                                        onClick = {
                                            val count = viewModel.checkUpcomingNotifications()
                                            Toast.makeText(
                                                context,
                                                "${AppStrings.get("notifications_checked", currentLang)} ($count)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .testTag("notifications_refresh_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "تحديث",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Clear All Button
                                if (notifications.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .liquidGlassInteractive(
                                                shape = RoundedCornerShape(12.dp),
                                                glowColor = Color(0xFFEF4444)
                                            )
                                    ) {
                                        IconButton(
                                            onClick = { showClearAllConfirmDialog = true },
                                            modifier = Modifier
                                                .size(38.dp)
                                                .testTag("notifications_clear_all_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ClearAll,
                                                contentDescription = AppStrings.get("clear_all", currentLang),
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 840.dp)
                    ) {
                    // Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NotificationFilterChip(
                            label = "الكل (${notifications.size})",
                            isSelected = selectedFilter == "ALL",
                            onClick = { selectedFilter = "ALL" },
                            isDark = isDarkMode
                        )
                        if (pinnedCount > 0) {
                            NotificationFilterChip(
                                label = "📌 المثبتة ($pinnedCount)",
                                isSelected = selectedFilter == "PINNED",
                                onClick = { selectedFilter = "PINNED" },
                                isDark = isDarkMode,
                                accentColor = Color(0xFFF59E0B)
                            )
                        }
                        NotificationFilterChip(
                            label = "🔔 الدخول اليوم",
                            isSelected = selectedFilter == "RETURNING_TODAY",
                            onClick = { selectedFilter = "RETURNING_TODAY" },
                            isDark = isDarkMode,
                            accentColor = Color(0xFF00BCD4)
                        )
                        NotificationFilterChip(
                            label = "⚠️ متأخرون",
                            isSelected = selectedFilter == "OVERDUE",
                            onClick = { selectedFilter = "OVERDUE" },
                            isDark = isDarkMode,
                            accentColor = Color(0xFFEF4444)
                        )
                        NotificationFilterChip(
                            label = "⏳ إجازات قادمة",
                            isSelected = selectedFilter == "APPROACHING",
                            onClick = { selectedFilter = "APPROACHING" },
                            isDark = isDarkMode,
                            accentColor = Color(0xFF10B981)
                        )
                        NotificationFilterChip(
                            label = "🌟 مستحقون",
                            isSelected = selectedFilter == "ELIGIBLE",
                            onClick = { selectedFilter = "ELIGIBLE" },
                            isDark = isDarkMode,
                            accentColor = Color(0xFF8B5CF6)
                        )
                    }

                    // Content
                    if (filteredList.isEmpty()) {
                        // Empty State
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlass(
                                        shape = LiquidGlassDefaults.CardShape,
                                        elevation = 6.dp,
                                        isDark = isDarkMode
                                    )
                                    .padding(28.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        )
                                        .border(
                                            1.5.dp,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                            CircleShape
                                        ),
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
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = AppStrings.get("notifications_empty_sub", currentLang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                if (dismissedIds.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.restoreDismissedNotifications()
                                            Toast.makeText(context, "تمت استعادة التنبيهات الممسوحة", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("restore_dismissed_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("استعادة التنبيهات الممسوحة (${dismissedIds.size})")
                                    }
                                }
                            }
                        }
                    } else {
                        // Notifications List
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("notifications_list"),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredList, key = { it.id }) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    isDark = isDarkMode,
                                    currentLang = currentLang,
                                    onTogglePin = { viewModel.togglePinNotification(notification.id) },
                                    onDismiss = {
                                        viewModel.dismissNotification(notification.id)
                                        Toast.makeText(context, "تم مسح الإشعار", Toast.LENGTH_SHORT).show()
                                    },
                                    onSelectPersonnel = {
                                        if (notification.personnelId != null && onSelectPersonnel != null) {
                                            onSelectPersonnel(notification.personnelId)
                                            onBack()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }

    // Confirmation dialog for clearing all notifications
    if (showClearAllConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirmDialog = false },
            title = {
                Text(
                    text = "مسح جميع التنبيهات",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("هل تريد بالتأكيد مسح كافة التنبيهات المعروضة؟ يمكنك استعادتها لاحقاً عند الحاجة.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllNotifications()
                        showClearAllConfirmDialog = false
                        Toast.makeText(context, "تم مسح جميع التنبيهات بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("مسح الكل", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirmDialog = false }) {
                    Text(AppStrings.get("cancel", currentLang))
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
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
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    accentColor.copy(alpha = if (isDark) 0.35f else 0.2f)
                } else {
                    if (isDark) Color(0x331E293B) else Color(0x66FFFFFF)
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) accentColor else (if (isDark) Color(0x3338BDF8) else Color(0x330284C7)),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) {
                if (isDark) Color.White else accentColor
            } else {
                if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
            }
        )
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    isDark: Boolean,
    currentLang: AppLanguage,
    onTogglePin: () -> Unit,
    onDismiss: () -> Unit,
    onSelectPersonnel: () -> Unit
) {
    // Determine colors and icon by notification type
    val (typeColor, typeIcon, typeLabel) = when (notification.type) {
        "OVERDUE" -> Triple(
            Color(0xFFEF4444),
            Icons.Default.Warning,
            "متأخر عن الدخول"
        )
        "RETURNING_TODAY" -> Triple(
            Color(0xFF0284C7),
            Icons.Default.NotificationsActive,
            "موعد الدخول اليوم"
        )
        "APPROACHING_RETURN" -> Triple(
            Color(0xFFF59E0B),
            Icons.Default.Event,
            "اقتراب موعد الدخول"
        )
        "APPROACHING_START" -> Triple(
            Color(0xFF10B981),
            Icons.Default.FlightTakeoff,
            "انطلاق إجازة"
        )
        "ELIGIBLE" -> Triple(
            Color(0xFF8B5CF6),
            Icons.Default.Star,
            "استحقاق إجازة"
        )
        else -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Default.Notifications,
            "تنبيه"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(18.dp),
                elevation = if (notification.isPinned) 7.dp else 4.dp,
                borderAlpha = if (notification.isPinned) 0.95f else 0.75f,
                isDark = isDark
            )
            .border(
                width = if (notification.isPinned) 1.5.dp else 0.8.dp,
                brush = if (notification.isPinned) {
                    Brush.linearGradient(
                        listOf(Color(0xFFF59E0B), Color(0xFFFBBF24), Color(0xFFF59E0B))
                    )
                } else {
                    Brush.linearGradient(
                        listOf(typeColor.copy(alpha = 0.5f), Color.Transparent)
                    )
                },
                shape = RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
            .testTag("notification_card_${notification.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Type Badge + Pin indicator + Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Type Badge with icon
                    Box(
                        modifier = Modifier
                            .background(
                                color = typeColor.copy(alpha = if (isDark) 0.25f else 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                typeColor.copy(alpha = 0.5f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = typeIcon,
                                contentDescription = null,
                                tint = typeColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = typeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = typeColor
                            )
                        }
                    }

                    // Pinned Indicator Badge
                    if (notification.isPinned) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    1.dp,
                                    Color(0xFFF59E0B).copy(alpha = 0.6f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "مثبت في الأعلى",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFF59E0B),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Action Buttons: Pin/Unpin + Dismiss
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Pin / Unpin Button (Rounded corner icon button)
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (notification.isPinned) {
                                    Color(0xFFF59E0B).copy(alpha = 0.2f)
                                } else {
                                    if (isDark) Color(0x331E293B) else Color(0x220284C7)
                                }
                            )
                            .border(
                                1.dp,
                                if (notification.isPinned) Color(0xFFF59E0B).copy(alpha = 0.6f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .testTag("pin_btn_${notification.id}")
                    ) {
                        Icon(
                            imageVector = if (notification.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (notification.isPinned) AppStrings.get("unpin_action", currentLang) else AppStrings.get("pin_action", currentLang),
                            tint = if (notification.isPinned) Color(0xFFF59E0B) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Delete / Dismiss Button (Rounded corner icon button)
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = if (isDark) Color(0x22EF4444) else Color(0x15EF4444)
                            )
                            .border(
                                1.dp,
                                Color(0xFFEF4444).copy(alpha = 0.25f),
                                RoundedCornerShape(8.dp)
                            )
                            .testTag("dismiss_btn_${notification.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.get("dismiss_action", currentLang),
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Notification Title
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Notification Message
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDarkMode(isDark)) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                lineHeight = 20.sp
            )

            // Footer Row: Formatted Date + View Personnel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (notification.formattedDate.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = notification.formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (notification.personnelId != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                            .clickable(onClick = onSelectPersonnel)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "عرض السجل",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun isDarkMode(isDark: Boolean): Boolean = isDark
