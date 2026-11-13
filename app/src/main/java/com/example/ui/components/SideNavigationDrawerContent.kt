package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
    onLockNow: () -> Unit = {},
    isDynamicAnimationsEnabled: Boolean = true,
    onToggleDynamicAnimations: (Boolean) -> Unit = {},
    isDriveSyncEnabled: Boolean = false,
    onToggleDriveSync: (Boolean) -> Unit = {},
    lastDriveBackupTime: Long = 0L,
    onBackupToDrive: () -> Unit = {},
    onRestoreFromDrive: () -> Unit = {},
    drawerIsOpen: Boolean = true
) {
    val context = LocalContext.current
    var isSettingsPageOpen by rememberSaveable { mutableStateOf(false) }
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

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
                (slideInHorizontally(animationSpec = tween(250)) { it / 3 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(animationSpec = tween(220)) { -it / 4 } + fadeOut(tween(180)))
            } else {
                (slideInHorizontally(animationSpec = tween(250)) { -it / 3 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(animationSpec = tween(220)) { it / 4 } + fadeOut(tween(180)))
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
                            onClick = { onSelectLanguage(lang) },
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
            onClick = onOpenBackup,
            testTag = "drawer_backup_item"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Room Database Screen Item
        DrawerNavigationItem(
            icon = Icons.Default.Storage,
            iconTint = Color(0xFF0284C7),
            title = AppStrings.get("room_db_title", currentLang),
            subtitle = AppStrings.get("room_db_sub", currentLang),
            isDarkMode = isDarkMode,
            onClick = onOpenRoomDatabase,
            testTag = "drawer_room_db_item"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Theme Switch Item
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
            border = BorderStroke(
                1.dp,
                if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleTheme() }
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
                    onCheckedChange = { onToggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // General Settings Navigation Item -> opens dedicated Settings page inside the side panel
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
            border = BorderStroke(
                1.dp,
                if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isSettingsPageOpen = true }
                .testTag("drawer_settings_item")
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
                        .background(Color(0xFF059669).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.get("settings", currentLang),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = AppStrings.get("drawer_settings_sub", currentLang),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF334155),
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Developer Info Item
        DrawerNavigationItem(
            icon = Icons.Default.Info,
            iconTint = Color(0xFF0284C7),
            title = AppStrings.get("developer_info", currentLang),
            subtitle = AppStrings.get("drawer_developer_sub", currentLang),
            isDarkMode = isDarkMode,
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
            // Dedicated General Settings Page inside the Side Panel (same drawer frame & styling)
            val settingsScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .verticalScroll(settingsScrollState)
                    .padding(16.dp)
                    .testTag("drawer_settings_page"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Settings Page Header Card with Back Navigation
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
                    border = BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            if (isDarkMode) listOf(Color(0x40FFFFFF), Color(0x3310B981))
                            else listOf(Color.White, Color(0x6610B981))
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
                        IconButton(
                            onClick = { isSettingsPageOpen = false },
                            modifier = Modifier
                                .size(48.dp)
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
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // A. Theme Mode (Light / Dark) & Design Style (Glass / Neumorph)
                InlineSettingsSectionCard(isDarkMode = isDarkMode) {
                    Text(
                        text = AppStrings.get("theme_title", currentLang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.entries.forEach { mode ->
                            val selected = currentThemeMode == mode
                            Surface(
                                onClick = { onSelectThemeMode(mode) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) {
                                    if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA)
                                } else {
                                    if (isDarkMode) Color(0x220F172A) else Color(0xFFF8FAFC)
                                },
                                border = BorderStroke(
                                    if (selected) 1.5.dp else 1.dp,
                                    if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0x33475569) else Color(0x40CBD5E1))
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (mode == AppThemeMode.LIGHT) AppStrings.get("theme_light_btn", currentLang) else AppStrings.get("theme_dark_btn", currentLang),
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF0F172A))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = AppStrings.get("theme_style_title", currentLang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            AppThemeStyle.GLASSMORPHISM to AppStrings.get("theme_style_glass", currentLang),
                            AppThemeStyle.NEUMORPHISM to AppStrings.get("theme_style_neumorph", currentLang)
                        ).forEach { (style, label) ->
                            val selected = currentThemeStyle == style
                            Surface(
                                onClick = { onSelectThemeStyle(style) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) {
                                    if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA)
                                } else {
                                    if (isDarkMode) Color(0x220F172A) else Color(0xFFF8FAFC)
                                },
                                border = BorderStroke(
                                    if (selected) 1.5.dp else 1.dp,
                                    if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0x33475569) else Color(0x40CBD5E1))
                                )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF0F172A)),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // B. Focus Mode & Dynamic Animations Switches
                InlineSettingsSectionCard(isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("focus_mode_title", currentLang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = AppStrings.get(
                                        if (isFocusMode) "focus_mode_active" else "focus_mode_inactive",
                                        currentLang
                                    ),
                                    fontSize = 10.5.sp,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = isFocusMode,
                            onCheckedChange = { onToggleFocusMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00BCD4)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = if (isDarkMode) Color(0x22475569) else Color(0x22CBD5E1))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("dynamic_animations_title", currentLang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = AppStrings.get(
                                        if (isDynamicAnimationsEnabled) "dynamic_animations_active" else "dynamic_animations_disabled",
                                        currentLang
                                    ),
                                    fontSize = 10.5.sp,
                                    color = if (isDynamicAnimationsEnabled) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                            }
                        }
                        Switch(
                            checked = isDynamicAnimationsEnabled,
                            onCheckedChange = { enabled ->
                                onToggleDynamicAnimations(enabled)
                                Toast.makeText(
                                    context,
                                    AppStrings.get(
                                        if (enabled) "dynamic_animations_active" else "dynamic_animations_disabled",
                                        currentLang
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFA855F7)
                            )
                        )
                    }
                }

                // C. Duty Eligibility Threshold Slider
                InlineSettingsSectionCard(isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("duty_threshold_title", currentLang),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$dutyThreshold ${AppStrings.get("days", currentLang)}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00BCD4)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = AppStrings.get("duty_threshold_desc", currentLang),
                        fontSize = 10.5.sp,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Slider(
                        value = dutyThreshold.toFloat(),
                        onValueChange = { onSaveThreshold(it.toInt()) },
                        valueRange = 10f..180f,
                        steps = 33,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00BCD4),
                            activeTrackColor = Color(0xFF00BCD4)
                        )
                    )
                }

                // D. Biometric App Lock
                InlineSettingsSectionCard(isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("biometric_lock_title", currentLang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = AppStrings.get(
                                        if (isBiometricEnabled) "biometric_lock_active_sub" else "biometric_lock_inactive_sub",
                                        currentLang
                                    ),
                                    fontSize = 10.5.sp,
                                    color = if (isBiometricEnabled) Color(0xFF10B981) else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                                )
                            }
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { isChecked ->
                                onToggleBiometric(isChecked)
                                Toast.makeText(
                                    context,
                                    AppStrings.get(
                                        if (isChecked) "biometric_lock_enabled_toast" else "biometric_lock_disabled_toast",
                                        currentLang
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00BCD4)
                            ),
                            modifier = Modifier.testTag("drawer_biometric_lock_toggle")
                        )
                    }

                    if (isBiometricEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onLockNow,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF00BCD4)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("drawer_lock_now_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("lock_now_btn", currentLang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00BCD4)
                            )
                        }
                    }
                }

                // E. Google Drive Backup & Restore
                InlineSettingsSectionCard(isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color(0xFF4285F4),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("drive_backup_title", currentLang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${AppStrings.get("drive_last_backup", currentLang)} ${if (lastDriveBackupTime > 0L) sdf.format(Date(lastDriveBackupTime)) else AppStrings.get("drive_never", currentLang)}",
                                    fontSize = 10.5.sp,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = isDriveSyncEnabled,
                            onCheckedChange = { onToggleDriveSync(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4285F4)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onBackupToDrive,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.get("drive_backup_btn", currentLang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = onRestoreFromDrive,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.7f)),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(
                                Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = Color(0xFF4285F4),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.get("drive_restore_btn", currentLang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF8AB4F8) else Color(0xFF1967D2),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // F. Version & Developer Credit
                InlineSettingsSectionCard(isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("app_version_label", currentLang),
                            fontSize = 11.5.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                        Text(
                            text = "v${BuildConfig.VERSION_NAME} (#${BuildConfig.VERSION_CODE})",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00BCD4)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Developed by Charfeddine Ka",
                            fontSize = 11.5.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { openDeveloperWhatsApp(context, currentLang) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .testTag("drawer_developer_whatsapp_button")
                        ) {
                            Surface(
                                modifier = Modifier.size(26.dp),
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
            }
        }
    }
}

@Composable
private fun InlineSettingsSectionCard(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(
            0.8.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            content()
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
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
        border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
