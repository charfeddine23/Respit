package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.R
import com.example.data.AppThemeMode
import com.example.data.AppThemeStyle
import com.example.ui.DashboardStats
import com.example.ui.UndoAction
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SideNavigationDrawerContent(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    lastUndo: UndoAction?,
    stats: DashboardStats,
    onSelectLanguage: (AppLanguage) -> Unit,
    onUndo: () -> Unit,
    onCheckNotifications: () -> Unit,
    onExportPdf: () -> Unit,
    onOpenBackup: () -> Unit,
    onOpenRoomDatabase: () -> Unit,
    onToggleTheme: () -> Unit,
    onOpenDeveloper: () -> Unit,
    onResetSampleData: () -> Unit,
    currentThemeMode: AppThemeMode = if (isDarkMode) AppThemeMode.DARK else AppThemeMode.LIGHT,
    onSelectThemeMode: (AppThemeMode) -> Unit = {},
    currentThemeStyle: AppThemeStyle = AppThemeStyle.GLASSMORPHISM,
    onSelectThemeStyle: (AppThemeStyle) -> Unit = {},
    isFocusMode: Boolean = false,
    onToggleFocusMode: () -> Unit = {},
    dutyThreshold: Int = 45,
    onSaveThreshold: (Int) -> Unit = {},
    isBiometricEnabled: Boolean = false,
    onToggleBiometric: (Boolean) -> Unit = {},
    isNoScreenshotEnabled: Boolean = true,
    onToggleNoScreenshot: (Boolean) -> Unit = {},
    onLockNow: () -> Unit = {},
    isDynamicAnimationsEnabled: Boolean = true,
    onToggleDynamicAnimations: (Boolean) -> Unit = {},
    isDriveSyncEnabled: Boolean = false,
    onToggleDriveSync: (Boolean) -> Unit = {},
    lastDriveBackupTime: Long = 0L,
    onBackupToDrive: () -> Unit = {},
    onRestoreFromDrive: () -> Unit = {},
    drawerIsOpen: Boolean = true,
    onCheckUpdate: () -> Unit = {},
    availableUpdateVersion: String? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var isSettingsPageOpen by rememberSaveable { mutableStateOf(false) }
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    LaunchedEffect(drawerIsOpen) {
        if (!drawerIsOpen && isSettingsPageOpen) {
            isSettingsPageOpen = false
        }
    }

    BackHandler(enabled = drawerIsOpen && isSettingsPageOpen) {
        isSettingsPageOpen = false
    }

    AnimatedContent(
        targetState = isSettingsPageOpen,
        transitionSpec = {
            if (targetState) {
                (slideInHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { it / 3 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(200)) { -it / 4 } + fadeOut(tween(180)))
            } else {
                (slideInHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { -it / 3 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(200)) { it / 4 } + fadeOut(tween(180)))
            }
        },
        label = "drawerSettingsPageTransition",
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC))
            .testTag("side_navigation_drawer")
    ) { inSettings ->
        if (!inSettings) {
            val menuScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .verticalScroll(menuScrollState)
                    .padding(16.dp)
            ) {
                // 1. App Header with Glass Branding & Version Info
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            if (isDarkMode) listOf(Color(0x40FFFFFF), Color(0x3338BDF8))
                            else listOf(Color.White, Color(0x6638BDF8))
                        )
                    ),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .liquidGlassInteractive(
                                        shape = RoundedCornerShape(12.dp),
                                        glowColor = MaterialTheme.colorScheme.primary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = AppStrings.get("app_icon_desc", currentLang),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = AppStrings.get("app_title", currentLang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = AppStrings.get("app_subtitle", currentLang),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = AppStrings.get("app_version", currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Language Selection Card (Français / English / العربية)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
                    border = BorderStroke(
                        1.dp,
                        if (isDarkMode) Color(0x33475569) else Color(0x40BAE6FD)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("language", currentLang),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AppLanguage.entries.forEach { lang ->
                                val isSelected = currentLang == lang
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onSelectLanguage(lang)
                                    },
                                    label = {
                                        Text(
                                            text = lang.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF0F172A),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDarkMode) Color(0x33334155) else Color.White.copy(alpha = 0.7f),
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else (if (isDarkMode) Color(0x4D475569) else Color(0x3338BDF8)),
                                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                                        enabled = true,
                                        selected = isSelected
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Navigation Actions
                Text(
                    text = AppStrings.get("drawer_nav_title", currentLang),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Undo Action (if available)
                AnimatedVisibility(
                    visible = lastUndo != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        DrawerNavigationItem(
                            icon = Icons.AutoMirrored.Filled.Undo,
                            iconTint = Color(0xFFD97706),
                            title = AppStrings.get("undo_btn", currentLang),
                            subtitle = lastUndo?.getDescription(currentLang),
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            onClick = onUndo,
                            testTag = "drawer_undo_item"
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                // Notification Check Item
                DrawerNavigationItem(
                    icon = Icons.Default.NotificationsActive,
                    iconTint = Color(0xFF0284C7),
                    title = AppStrings.get("check_notifications", currentLang),
                    subtitle = AppStrings.get("drawer_notif_sub", currentLang),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onCheckNotifications,
                    testTag = "drawer_notifications_item"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Export PDF Report Item
                DrawerNavigationItem(
                    icon = Icons.Default.PictureAsPdf,
                    iconTint = Color(0xFFDC2626),
                    title = AppStrings.get("export_pdf", currentLang),
                    subtitle = AppStrings.get("drawer_pdf_sub", currentLang),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onExportPdf,
                    testTag = "drawer_pdf_item"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Backup & Restore Item
                DrawerNavigationItem(
                    icon = Icons.Default.Share,
                    iconTint = Color(0xFF7C3AED),
                    title = AppStrings.get("export_data", currentLang),
                    subtitle = AppStrings.get("drawer_data_sub", currentLang),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onOpenBackup,
                    testTag = "drawer_backup_item"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Room Database Screen Item
                DrawerNavigationItem(
                    icon = Icons.Default.Storage,
                    iconTint = Color(0xFF0284C7),
                    title = AppStrings.get("nav_database", currentLang),
                    subtitle = AppStrings.get("room_db_sub", currentLang),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onOpenRoomDatabase,
                    testTag = "drawer_room_db_item"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Theme Switch Item with Tactile Press Feedback
                val themeInteraction = remember { MutableInteractionSource() }
                val themePressed by themeInteraction.collectIsPressedAsState()
                val themeScale by animateFloatAsState(
                    targetValue = if (themePressed) 0.97f else 1f,
                    animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
                    label = "drawerThemeScale"
                )
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleTheme()
                    },
                    interactionSource = themeInteraction,
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
                    border = BorderStroke(
                        if (themePressed) 1.4.dp else 1.dp,
                        if (themePressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        else if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = themeScale
                            scaleY = themeScale
                        }
                        .testTag("drawer_theme_toggle")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDarkMode) Color(0xFFFBBF24).copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFFFBBF24) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = if (isDarkMode) AppStrings.get("theme_dark", currentLang) else AppStrings.get("theme_light", currentLang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isDarkMode) AppStrings.get("drawer_dark_active", currentLang) else AppStrings.get("drawer_light_active", currentLang),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleTheme()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // General Settings Navigation Item -> opens dedicated Settings page inside the side panel
                DrawerNavigationItem(
                    icon = Icons.Default.Settings,
                    iconTint = Color(0xFF059669),
                    title = AppStrings.get("settings", currentLang),
                    subtitle = AppStrings.get("drawer_settings_sub", currentLang),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = { isSettingsPageOpen = true },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    testTag = "drawer_settings_item"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // In-App Update Item
                DrawerNavigationItem(
                    icon = Icons.Default.RocketLaunch,
                    iconTint = if (availableUpdateVersion != null) Color(0xFF10B981) else Color(0xFF00BCD4),
                    title = AppStrings.get("update_menu_title", currentLang),
                    subtitle = if (availableUpdateVersion != null) {
                        "${AppStrings.get("update_available_sub", currentLang)} • v$availableUpdateVersion"
                    } else {
                        "${AppStrings.get("update_menu_sub", currentLang)} (v${BuildConfig.VERSION_NAME})"
                    },
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onCheckUpdate,
                    testTag = "drawer_update_item"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Developer Info Item
                DrawerNavigationItem(
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF0284C7),
                    title = AppStrings.get("developer_info", currentLang),
                    subtitle = AppStrings.get("drawer_developer_sub", currentLang),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onOpenDeveloper,
                    testTag = "drawer_developer_item"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Reset Sample Data Item
                DrawerNavigationItem(
                    icon = Icons.Default.Refresh,
                    iconTint = Color(0xFF94A3B8),
                    title = AppStrings.get("drawer_reset_title", currentLang),
                    subtitle = AppStrings.get("drawer_reset_sub", currentLang),
                    isDarkMode = isDarkMode,
                    haptic = haptic,
                    onClick = onResetSampleData,
                    testTag = "drawer_reset_item"
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = if (isDarkMode) Color(0x33475569) else Color(0x2638BDF8))

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Stats Overview in Sidebar
                Text(
                    text = AppStrings.get("drawer_quick_stats", currentLang),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SidebarStatBadge(AppStrings.get("stat_total", currentLang), "${stats.total}", MaterialTheme.colorScheme.primary, isDarkMode, Modifier.weight(1f))
                    SidebarStatBadge(AppStrings.get("stat_on_leave", currentLang), "${stats.atHome}", Color(0xFF0284C7), isDarkMode, Modifier.weight(1f))
                    SidebarStatBadge(AppStrings.get("stat_returning_today", currentLang), "${stats.returningToday}", Color(0xFFD97706), isDarkMode, Modifier.weight(1f))
                    SidebarStatBadge(AppStrings.get("stat_overdue", currentLang), "${stats.overdue}", Color(0xFFDC2626), isDarkMode, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Respite Pulse",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        } else {
            // Redesigned, clean, organized General Settings Page inside the Side Panel
            val settingsScrollState = rememberScrollState()
            val accentCyan = Color(0xFF00BCD4)
            val dividerColor = if (isDarkMode) Color(0x28475569) else Color(0x30CBD5E1)

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .verticalScroll(settingsScrollState)
                    .padding(16.dp)
                    .testTag("drawer_settings_page"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Header Card with Tactile Back Navigation
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            if (isDarkMode) listOf(Color(0x38FFFFFF), Color(0x4000BCD4))
                            else listOf(Color.White, Color(0x6600BCD4))
                        )
                    ),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val backInteraction = remember { MutableInteractionSource() }
                        val backPressed by backInteraction.collectIsPressedAsState()
                        val backScale by animateFloatAsState(
                            targetValue = if (backPressed) 0.90f else 1f,
                            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                            label = "backBtnScale"
                        )
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isSettingsPageOpen = false
                            },
                            interactionSource = backInteraction,
                            modifier = Modifier
                                .size(48.dp)
                                .graphicsLayer {
                                    scaleX = backScale
                                    scaleY = backScale
                                }
                                .clip(CircleShape)
                                .testTag("drawer_settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppStrings.get("back", currentLang),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppStrings.get("settings", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = AppStrings.get("drawer_settings_sub", currentLang),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // SECTION 1: المظهر والعرض (Appearance & Display)
                SettingsSectionHeader(
                    title = AppStrings.get("settings_sec_appearance", currentLang),
                    accentColor = accentCyan
                )

                CleanSettingsCard(isDarkMode = isDarkMode) {
                    // 1.1 Theme Mode Segmented Control
                    Text(
                        text = AppStrings.get("theme_title", currentLang),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TactileSegmentedOption(
                            label = AppStrings.get("theme_light_btn", currentLang),
                            icon = Icons.Default.LightMode,
                            selected = currentThemeMode == AppThemeMode.LIGHT,
                            accentColor = Color(0xFFF59E0B),
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            onClick = { onSelectThemeMode(AppThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        TactileSegmentedOption(
                            label = AppStrings.get("theme_dark_btn", currentLang),
                            icon = Icons.Default.DarkMode,
                            selected = currentThemeMode == AppThemeMode.DARK,
                            accentColor = Color(0xFF38BDF8),
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            onClick = { onSelectThemeMode(AppThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1.2 Interface Design Style Segmented Control
                    Text(
                        text = AppStrings.get("theme_style_title", currentLang),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TactileSegmentedOption(
                            label = AppStrings.get("theme_style_glass", currentLang),
                            icon = Icons.Default.AutoAwesome,
                            selected = currentThemeStyle == AppThemeStyle.GLASSMORPHISM,
                            accentColor = accentCyan,
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            onClick = { onSelectThemeStyle(AppThemeStyle.GLASSMORPHISM) },
                            modifier = Modifier.weight(1f)
                        )
                        TactileSegmentedOption(
                            label = AppStrings.get("theme_style_neumorph", currentLang),
                            icon = Icons.Default.Layers,
                            selected = currentThemeStyle == AppThemeStyle.NEUMORPHISM,
                            accentColor = Color(0xFF6366F1),
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            onClick = { onSelectThemeStyle(AppThemeStyle.NEUMORPHISM) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = dividerColor)
                    Spacer(modifier = Modifier.height(4.dp))

                    // 1.3 Focus Mode Toggle Row (full-row clickable with press feedback)
                    TactileSwitchSettingRow(
                        icon = Icons.Default.CenterFocusStrong,
                        iconTint = accentCyan,
                        title = AppStrings.get("focus_mode_title", currentLang),
                        subtitle = AppStrings.get(
                            if (isFocusMode) "focus_mode_active" else "focus_mode_inactive",
                            currentLang
                        ),
                        checked = isFocusMode,
                        trackColor = accentCyan,
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onToggle = { onToggleFocusMode() }
                    )

                    HorizontalDivider(color = dividerColor)

                    // 1.4 Dynamic Animations Toggle Row
                    TactileSwitchSettingRow(
                        icon = Icons.Default.Speed,
                        iconTint = Color(0xFFA855F7),
                        title = AppStrings.get("dynamic_animations_title", currentLang),
                        subtitle = AppStrings.get(
                            if (isDynamicAnimationsEnabled) "dynamic_animations_active" else "dynamic_animations_disabled",
                            currentLang
                        ),
                        subtitleColor = if (isDynamicAnimationsEnabled) Color(0xFF10B981) else Color(0xFFF59E0B),
                        checked = isDynamicAnimationsEnabled,
                        trackColor = Color(0xFFA855F7),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onToggle = { enabled ->
                            onToggleDynamicAnimations(enabled)
                            Toast.makeText(
                                context,
                                AppStrings.get(
                                    if (enabled) "dynamic_animations_active" else "dynamic_animations_disabled",
                                    currentLang
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }

                // SECTION 2: نظام الإجازات (Leave Rules)
                SettingsSectionHeader(
                    title = AppStrings.get("settings_sec_leave", currentLang),
                    accentColor = Color(0xFF10B981)
                )

                CleanSettingsCard(isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventAvailable,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = AppStrings.get("duty_threshold_title", currentLang),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = accentCyan.copy(alpha = if (isDarkMode) 0.20f else 0.12f),
                            border = BorderStroke(1.dp, accentCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "$dutyThreshold ${AppStrings.get("days", currentLang)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accentCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = AppStrings.get("duty_threshold_desc", currentLang),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )

                    Slider(
                        value = dutyThreshold.toFloat(),
                        onValueChange = { newVal ->
                            val rounded = newVal.toInt()
                            if (rounded != dutyThreshold) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onSaveThreshold(rounded)
                        },
                        valueRange = 10f..180f,
                        steps = 33,
                        colors = SliderDefaults.colors(
                            thumbColor = accentCyan,
                            activeTrackColor = accentCyan,
                            inactiveTrackColor = if (isDarkMode) Color(0x33475569) else Color(0xFFE2E8F0)
                        )
                    )
                }

                // SECTION 3: الأمان والنسخ الاحتياطي (Security & Backup)
                SettingsSectionHeader(
                    title = AppStrings.get("settings_sec_security", currentLang),
                    accentColor = Color(0xFF3B82F6)
                )

                CleanSettingsCard(isDarkMode = isDarkMode) {
                    // 3.1 Biometric / Device Credential Lock Row
                    TactileSwitchSettingRow(
                        icon = Icons.Default.Fingerprint,
                        iconTint = accentCyan,
                        title = AppStrings.get("biometric_lock_title", currentLang),
                        subtitle = AppStrings.get(
                            if (isBiometricEnabled) "biometric_lock_active_sub" else "biometric_lock_inactive_sub",
                            currentLang
                        ),
                        subtitleColor = if (isBiometricEnabled) Color(0xFF10B981) else null,
                        checked = isBiometricEnabled,
                        trackColor = accentCyan,
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        switchTag = "drawer_biometric_lock_toggle",
                        onToggle = { isChecked ->
                            onToggleBiometric(isChecked)
                            Toast.makeText(
                                context,
                                AppStrings.get(
                                    if (isChecked) "biometric_lock_enabled_toast" else "biometric_lock_disabled_toast",
                                    currentLang
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    AnimatedVisibility(
                        visible = isBiometricEnabled,
                        enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(6.dp))
                            TactileActionPillButton(
                                text = AppStrings.get("lock_now_btn", currentLang),
                                icon = Icons.Default.Lock,
                                accentColor = accentCyan,
                                isFilled = false,
                                isDarkMode = isDarkMode,
                                haptic = haptic,
                                onClick = onLockNow,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("drawer_lock_now_button")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    HorizontalDivider(color = dividerColor)
                    Spacer(modifier = Modifier.height(4.dp))

                    // 3.15 Maximum Security: Block Screenshots & Screen Recording (Enabled by default)
                    TactileSwitchSettingRow(
                        icon = Icons.Default.VisibilityOff,
                        iconTint = Color(0xFF10B981),
                        title = AppStrings.get("no_screenshot_title", currentLang),
                        subtitle = AppStrings.get(
                            if (isNoScreenshotEnabled) "no_screenshot_active_sub" else "no_screenshot_inactive_sub",
                            currentLang
                        ),
                        subtitleColor = if (isNoScreenshotEnabled) Color(0xFF10B981) else null,
                        checked = isNoScreenshotEnabled,
                        trackColor = Color(0xFF10B981),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        switchTag = "drawer_no_screenshot_toggle",
                        onToggle = { isChecked ->
                            onToggleNoScreenshot(isChecked)
                            Toast.makeText(
                                context,
                                AppStrings.get(
                                    if (isChecked) "no_screenshot_enabled_toast" else "no_screenshot_disabled_toast",
                                    currentLang
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    HorizontalDivider(color = dividerColor)
                    Spacer(modifier = Modifier.height(4.dp))

                    // 3.2 Cloud Backup & Restore
                    TactileSwitchSettingRow(
                        icon = Icons.Default.CloudSync,
                        iconTint = Color(0xFF4285F4),
                        title = AppStrings.get("drive_backup_title", currentLang),
                        subtitle = "${AppStrings.get("drive_last_backup", currentLang)} ${if (lastDriveBackupTime > 0L) sdf.format(Date(lastDriveBackupTime)) else AppStrings.get("drive_never", currentLang)}",
                        checked = isDriveSyncEnabled,
                        trackColor = Color(0xFF4285F4),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onToggle = { onToggleDriveSync(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TactileActionPillButton(
                            text = AppStrings.get("drive_backup_btn", currentLang),
                            icon = Icons.Default.CloudUpload,
                            accentColor = Color(0xFF4285F4),
                            isFilled = true,
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            onClick = onBackupToDrive,
                            modifier = Modifier.weight(1f)
                        )
                        TactileActionPillButton(
                            text = AppStrings.get("drive_restore_btn", currentLang),
                            icon = Icons.Default.CloudDownload,
                            accentColor = Color(0xFF4285F4),
                            isFilled = false,
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            onClick = onRestoreFromDrive,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // SECTION 4: التطبيق والتحديثات (App & Updates)
                SettingsSectionHeader(
                    title = AppStrings.get("settings_sec_about", currentLang),
                    accentColor = Color(0xFF8B5CF6)
                )

                CleanSettingsCard(isDarkMode = isDarkMode) {
                    // 4.1 Clickable In-App Update Row
                    val updateInteraction = remember { MutableInteractionSource() }
                    val updatePressed by updateInteraction.collectIsPressedAsState()
                    val updateScale by animateFloatAsState(
                        targetValue = if (updatePressed) 0.97f else 1f,
                        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
                        label = "settingsUpdateRowScale"
                    )
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCheckUpdate()
                        },
                        interactionSource = updateInteraction,
                        shape = RoundedCornerShape(12.dp),
                        color = if (updatePressed) {
                            accentCyan.copy(alpha = if (isDarkMode) 0.16f else 0.10f)
                        } else {
                            Color.Transparent
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .graphicsLayer {
                                scaleX = updateScale
                                scaleY = updateScale
                            }
                            .testTag("drawer_settings_check_update_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        (if (availableUpdateVersion != null) Color(0xFF10B981) else accentCyan)
                                            .copy(alpha = 0.14f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = if (availableUpdateVersion != null) Color(0xFF10B981) else accentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppStrings.get("update_check_btn", currentLang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (availableUpdateVersion != null) {
                                        "${AppStrings.get("update_available_title", currentLang)} (v$availableUpdateVersion)"
                                    } else {
                                        "${AppStrings.get("app_version_label", currentLang)}: v${BuildConfig.VERSION_NAME}"
                                    },
                                    fontSize = 11.sp,
                                    fontFamily = if (availableUpdateVersion == null) FontFamily.Monospace else FontFamily.Default,
                                    color = if (availableUpdateVersion != null) Color(0xFF10B981) else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = dividerColor)
                    Spacer(modifier = Modifier.height(4.dp))

                    // 4.2 Developer Credit + Tactile WhatsApp Contact Icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Developed by Charfeddine Ka",
                                fontSize = 11.5.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F)
                            )
                            Text(
                                text = AppStrings.get("certified_developer", currentLang),
                                fontSize = 10.5.sp,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }

                        val waInteraction = remember { MutableInteractionSource() }
                        val waPressed by waInteraction.collectIsPressedAsState()
                        val waScale by animateFloatAsState(
                            targetValue = if (waPressed) 0.88f else 1f,
                            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                            label = "settingsWaScale"
                        )
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                openDeveloperWhatsApp(context, currentLang)
                            },
                            interactionSource = waInteraction,
                            modifier = Modifier
                                .size(48.dp)
                                .graphicsLayer {
                                    scaleX = waScale
                                    scaleY = waScale
                                }
                                .clip(CircleShape)
                                .testTag("drawer_developer_whatsapp_button")
                        ) {
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = Color(0xFF25D366).copy(alpha = if (isDarkMode) 0.18f else 0.12f),
                                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.45f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_whatsapp),
                                        contentDescription = AppStrings.get("contact_whatsapp", currentLang),
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 14.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor
        )
    }
}

@Composable
private fun CleanSettingsCard(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x38CBD5E1)
        ),
        shadowElevation = if (isDarkMode) 2.dp else 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
private fun TactileSegmentedOption(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    accentColor: Color,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "segmentScale"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            selected && isDarkMode -> accentColor.copy(alpha = 0.22f)
            selected && !isDarkMode -> accentColor.copy(alpha = 0.13f)
            isPressed -> accentColor.copy(alpha = 0.08f)
            isDarkMode -> Color(0x280F172A)
            else -> Color(0xFFF8FAFC)
        },
        animationSpec = tween(180),
        label = "segmentBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accentColor else if (isDarkMode) Color(0x38475569) else Color(0x45CBD5E1),
        animationSpec = tween(180),
        label = "segmentBorder"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) accentColor else if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) {
                    if (isDarkMode) accentColor else accentColor.copy(alpha = 0.95f)
                } else {
                    if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF1E293B)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TactileSwitchSettingRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    subtitleColor: Color? = null,
    checked: Boolean,
    trackColor: Color,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    switchTag: String? = null,
    onToggle: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "switchRowScale"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onToggle(!checked)
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = if (isPressed) {
            iconTint.copy(alpha = if (isDarkMode) 0.12f else 0.08f)
        } else {
            Color.Transparent
        },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Text(
                        text = title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.5.sp,
                        color = subtitleColor ?: if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { newValue ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onToggle(newValue)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = trackColor
                ),
                modifier = if (switchTag != null) Modifier.testTag(switchTag) else Modifier
            )
        }
    }
}

@Composable
private fun TactileActionPillButton(
    text: String,
    icon: ImageVector,
    accentColor: Color,
    isFilled: Boolean,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "actionPillScale"
    )

    if (isFilled) {
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
            interactionSource = interactionSource,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = modifier
                .height(48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
            interactionSource = interactionSource,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.65f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = accentColor.copy(alpha = if (isDarkMode) 0.10f else 0.06f),
                contentColor = accentColor
            ),
            contentPadding = PaddingValues(horizontal = 10.dp),
            modifier = modifier
                .height(48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode && accentColor == Color(0xFF4285F4)) Color(0xFF8AB4F8) else accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DrawerNavigationItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String?,
    isDarkMode: Boolean,
    haptic: HapticFeedback,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "drawerItemScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) {
            iconTint.copy(alpha = 0.75f)
        } else {
            if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
        },
        animationSpec = tween(150),
        label = "drawerItemBorder"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isPressed) {
            if (isDarkMode) Color(0xFF263449) else iconTint.copy(alpha = 0.08f)
        } else {
            if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f)
        },
        animationSpec = tween(150),
        label = "drawerItemBg"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(if (isPressed) 1.4.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF334155),
                        fontSize = 11.sp
                    )
                }
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(6.dp))
                trailingContent()
            }
        }
    }
}

@Composable
private fun SidebarStatBadge(
    label: String,
    value: String,
    color: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = if (isDarkMode) 0.18f else 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = color)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) color else Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
