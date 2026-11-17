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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableView
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
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.ThemeEngine
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class SettingsRoute {
    HOME,
    LANGUAGE,
    APPEARANCE,
    SECURITY,
    NOTIFICATIONS,
    BACKUP_RESTORE
}

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
    onExportExcel: () -> Unit = {},
    onOpenBackup: () -> Unit,
    onOpenRoomDatabase: () -> Unit,
    onToggleTheme: () -> Unit,
    onOpenDeveloper: () -> Unit,
    onResetSampleData: () -> Unit,
    currentThemeMode: AppThemeMode = if (isDarkMode) AppThemeMode.DARK else AppThemeMode.LIGHT,
    onSelectThemeMode: (AppThemeMode) -> Unit = {},
    currentThemeStyle: AppThemeStyle = AppThemeStyle.GLASSMORPHISM,
    onSelectThemeStyle: (AppThemeStyle) -> Unit = {},
    selectedThemeEngineId: String = "",
    onSelectThemeEngine: (String) -> Unit = {},
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
    isLivingLuxuryMode: Boolean = false,
    onToggleLivingLuxuryMode: (Boolean) -> Unit = {},
    trashedCount: Int = 0,
    onOpenTrashBin: () -> Unit = {},
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
    var currentRoute by rememberSaveable { mutableStateOf(SettingsRoute.HOME) }
    var showThemeEngineDialog by remember { mutableStateOf(false) }
    val activeThemePreset = LocalThemePreset.current
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val accentCyan = activeThemePreset?.primary ?: Color(0xFF00BCD4)
    val dividerColor = if (isDarkMode) Color(0x28475569) else Color(0x30CBD5E1)

    if (showThemeEngineDialog) {
        ThemeEnginePickerDialog(
            selectedThemeId = selectedThemeEngineId,
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            onSelectTheme = { presetId -> onSelectThemeEngine(presetId) },
            onResetDefault = { onSelectThemeEngine("") },
            onDismiss = { showThemeEngineDialog = false }
        )
    }

    LaunchedEffect(drawerIsOpen) {
        if (!drawerIsOpen && currentRoute != SettingsRoute.HOME) {
            currentRoute = SettingsRoute.HOME
        }
    }

    BackHandler(enabled = drawerIsOpen && currentRoute != SettingsRoute.HOME) {
        currentRoute = SettingsRoute.HOME
    }

    AnimatedContent(
        targetState = currentRoute,
        transitionSpec = {
            if (targetState != SettingsRoute.HOME) {
                (slideInHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { it / 3 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(200)) { -it / 4 } + fadeOut(tween(180)))
            } else {
                (slideInHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { -it / 3 } + fadeIn(tween(220))) togetherWith
                    (slideOutHorizontally(tween(200)) { it / 4 } + fadeOut(tween(180)))
            }
        },
        label = "settingsScreenRouteTransition",
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                activeThemePreset?.backgroundMid
                    ?: if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC)
            )
            .statusBarsPadding()
            .testTag("side_navigation_drawer")
    ) { route ->
        when (route) {
            SettingsRoute.HOME -> {
                val homeScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(homeScroll)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // SECTION 1: Clickable Top Header Container (Logo + App Name ONLY, NO Version -> Opens Language Settings)
                    val headerInteraction = remember { MutableInteractionSource() }
                    val headerPressed by headerInteraction.collectIsPressedAsState()
                    val headerScale by animateFloatAsState(
                        targetValue = if (headerPressed) 0.975f else 1f,
                        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
                        label = "headerCardScale"
                    )
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            currentRoute = SettingsRoute.LANGUAGE
                        },
                        interactionSource = headerInteraction,
                        shape = RoundedCornerShape(22.dp),
                        color = activeThemePreset?.surfacePrimary
                            ?: if (isDarkMode) Color(0xFF1E293B) else Color.White,
                        border = BorderStroke(
                            1.2.dp,
                            Brush.linearGradient(
                                if (isDarkMode) listOf(Color(0x40FFFFFF), accentCyan.copy(alpha = 0.45f))
                                else listOf(Color.White, accentCyan.copy(alpha = 0.55f))
                            )
                        ),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 84.dp)
                            .graphicsLayer {
                                scaleX = headerScale
                                scaleY = headerScale
                            }
                            .testTag("drawer_header_language_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .liquidGlassInteractive(
                                            shape = RoundedCornerShape(14.dp),
                                            glowColor = MaterialTheme.colorScheme.primary
                                        )
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(Color(0xFF0A192F), Color(0xFF144266))
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.app_logo),
                                        contentDescription = AppStrings.get("app_icon_desc", currentLang),
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(11.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
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
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = AppStrings.get("language", currentLang),
                                    tint = accentCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = accentCyan.copy(alpha = if (isDarkMode) 0.14f else 0.08f),
                                border = BorderStroke(1.dp, accentCyan.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = null,
                                            tint = accentCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("settings_tap_for_language", currentLang),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF334155),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(50.dp),
                                        color = accentCyan.copy(alpha = if (isDarkMode) 0.24f else 0.16f)
                                    ) {
                                        Text(
                                            text = currentLang.displayName,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = accentCyan,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Conditional Undo pill only when an undo action exists
                    AnimatedVisibility(
                        visible = lastUndo != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
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
                    }

                    // SECTION 2: Appearance
                    DrawerNavigationItem(
                        icon = Icons.Default.Palette,
                        iconTint = Color(0xFF8B5CF6),
                        title = AppStrings.get("settings_cat_appearance", currentLang),
                        subtitle = AppStrings.get("settings_cat_appearance_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = { currentRoute = SettingsRoute.APPEARANCE },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        testTag = "settings_nav_appearance"
                    )

                    // SECTION 3: Security
                    DrawerNavigationItem(
                        icon = Icons.Default.Shield,
                        iconTint = Color(0xFF10B981),
                        title = AppStrings.get("settings_cat_security", currentLang),
                        subtitle = AppStrings.get("settings_cat_security_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = { currentRoute = SettingsRoute.SECURITY },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isBiometricEnabled || isNoScreenshotEnabled) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        testTag = "settings_nav_security"
                    )

                    // SECTION 4: Notifications & Leave Eligibility Threshold (Days)
                    DrawerNavigationItem(
                        icon = Icons.Default.NotificationsActive,
                        iconTint = Color(0xFF0284C7),
                        title = AppStrings.get("settings_cat_notifications", currentLang),
                        subtitle = AppStrings.get("settings_cat_notifications_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = { currentRoute = SettingsRoute.NOTIFICATIONS },
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = Color(0xFF0284C7).copy(alpha = if (isDarkMode) 0.20f else 0.12f)
                                ) {
                                    Text(
                                        text = "$dutyThreshold ${AppStrings.get("days", currentLang)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0284C7),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        testTag = "settings_nav_notifications"
                    )

                    // SECTION 5: Trash Bin
                    DrawerNavigationItem(
                        icon = Icons.Default.DeleteSweep,
                        iconTint = CoralRedAccent,
                        title = AppStrings.get("trash_bin_title", currentLang),
                        subtitle = AppStrings.get("trash_bin_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = onOpenTrashBin,
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (trashedCount > 0) {
                                    Surface(
                                        shape = CircleShape,
                                        color = CoralRedAccent
                                    ) {
                                        Text(
                                            text = if (trashedCount > 99) "99+" else "$trashedCount",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        testTag = "settings_nav_trash_bin"
                    )

                    // SECTION 6: Backup, Restore & Tools
                    DrawerNavigationItem(
                        icon = Icons.Default.CloudSync,
                        iconTint = Color(0xFF4285F4),
                        title = AppStrings.get("settings_cat_backup", currentLang),
                        subtitle = AppStrings.get("settings_cat_backup_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = { currentRoute = SettingsRoute.BACKUP_RESTORE },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        testTag = "settings_nav_backup_restore"
                    )

                    // App Update (directly below Backup & Restore on Settings root)
                    DrawerNavigationItem(
                        icon = Icons.Default.RocketLaunch,
                        iconTint = accentCyan,
                        title = AppStrings.get("update_menu_title", currentLang),
                        subtitle = if (availableUpdateVersion != null) {
                            "${AppStrings.get("update_available_sub", currentLang)} • v$availableUpdateVersion"
                        } else {
                            "${AppStrings.get("update_menu_sub", currentLang)} (v${BuildConfig.VERSION_NAME})"
                        },
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = onCheckUpdate,
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (availableUpdateVersion != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        testTag = "drawer_update_item"
                    )

                    // Developer Info (below App Update on Settings root)
                    DrawerNavigationItem(
                        icon = Icons.Default.Info,
                        iconTint = Color(0xFF0284C7),
                        title = AppStrings.get("developer_info", currentLang),
                        subtitle = "Developed by Charfeddine Ka",
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = onOpenDeveloper,
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        testTag = "drawer_developer_item"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    HorizontalDivider(color = dividerColor)

                    // Compact Unit Overview Strip
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = AppStrings.get("drawer_quick_stats", currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SidebarStatBadge(AppStrings.get("stat_total", currentLang), "${stats.total}", MaterialTheme.colorScheme.primary, isDarkMode, Modifier.weight(1f))
                            SidebarStatBadge(AppStrings.get("stat_on_leave", currentLang), "${stats.atHome}", Color(0xFF0284C7), isDarkMode, Modifier.weight(1f))
                            SidebarStatBadge(AppStrings.get("stat_returning_today", currentLang), "${stats.returningToday}", Color(0xFFD97706), isDarkMode, Modifier.weight(1f))
                            SidebarStatBadge(AppStrings.get("stat_overdue", currentLang), "${stats.overdue}", Color(0xFFDC2626), isDarkMode, Modifier.weight(1f))
                        }
                    }
                }
            }

            SettingsRoute.LANGUAGE -> {
                val langScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(langScroll)
                        .padding(16.dp)
                        .testTag("settings_language_page"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsSubPageHeader(
                        title = AppStrings.get("language", currentLang),
                        subtitle = AppStrings.get("select_language", currentLang),
                        icon = Icons.Default.Language,
                        accentColor = accentCyan,
                        isDarkMode = isDarkMode,
                        backContentDesc = AppStrings.get("back", currentLang),
                        haptic = haptic,
                        onBack = { currentRoute = SettingsRoute.HOME }
                    )

                    val languageChoices = listOf(
                        Triple(
                            AppLanguage.ARABIC,
                            AppStrings.get("lang_ar_title", currentLang),
                            AppStrings.get("lang_ar_sub", currentLang)
                        ),
                        Triple(
                            AppLanguage.TAMAZIGHT,
                            AppStrings.get("lang_tzm_title", currentLang),
                            AppStrings.get("lang_tzm_sub", currentLang)
                        ),
                        Triple(
                            AppLanguage.FRENCH,
                            AppStrings.get("lang_fr_title", currentLang),
                            AppStrings.get("lang_fr_sub", currentLang)
                        ),
                        Triple(
                            AppLanguage.ENGLISH,
                            AppStrings.get("lang_en_title", currentLang),
                            AppStrings.get("lang_en_sub", currentLang)
                        )
                    )

                    languageChoices.forEach { (lang, title, subtitle) ->
                        val isSelected = currentLang == lang
                        val badgeCode = when (lang) {
                            AppLanguage.ARABIC -> "AR"
                            AppLanguage.TAMAZIGHT -> "ⵜⵎ"
                            AppLanguage.FRENCH -> "FR"
                            AppLanguage.ENGLISH -> "EN"
                        }
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectLanguage(lang)
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) {
                                accentCyan.copy(alpha = if (isDarkMode) 0.20f else 0.12f)
                            } else {
                                activeThemePreset?.surfacePrimary
                                    ?: if (isDarkMode) Color(0xFF1E293B) else Color.White
                            },
                            border = BorderStroke(
                                if (isSelected) 1.6.dp else 1.dp,
                                if (isSelected) accentCyan
                                else if (isDarkMode) Color(0x33475569) else Color(0x38CBD5E1)
                            ),
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 64.dp)
                                .testTag("lang_option_${lang.code}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) accentCyan
                                            else accentCyan.copy(alpha = if (isDarkMode) 0.16f else 0.10f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badgeCode,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.White else accentCyan
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 11.sp,
                                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = AppStrings.get("theme_engine_active", currentLang),
                                        tint = accentCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SettingsRoute.APPEARANCE -> {
                val appScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(appScroll)
                        .padding(16.dp)
                        .testTag("settings_appearance_page"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsSubPageHeader(
                        title = AppStrings.get("settings_cat_appearance", currentLang),
                        subtitle = AppStrings.get("settings_cat_appearance_sub", currentLang),
                        icon = Icons.Default.Palette,
                        accentColor = Color(0xFF8B5CF6),
                        isDarkMode = isDarkMode,
                        backContentDesc = AppStrings.get("back", currentLang),
                        haptic = haptic,
                        onBack = { currentRoute = SettingsRoute.HOME }
                    )

                    // 2.1 Dark Mode Toggle + Display Theme Switcher
                    CleanSettingsCard(isDarkMode = isDarkMode) {
                        TactileSwitchSettingRow(
                            icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            iconTint = if (isDarkMode) Color(0xFFFBBF24) else MaterialTheme.colorScheme.primary,
                            title = if (isDarkMode) AppStrings.get("theme_dark", currentLang) else AppStrings.get("theme_light", currentLang),
                            subtitle = if (isDarkMode) AppStrings.get("drawer_dark_active", currentLang) else AppStrings.get("drawer_light_active", currentLang),
                            checked = isDarkMode,
                            trackColor = MaterialTheme.colorScheme.primary,
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            rowTag = "drawer_theme_toggle",
                            onToggle = { onToggleTheme() }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = dividerColor)
                        Spacer(modifier = Modifier.height(10.dp))

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
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("appearance_theme_light_btn")
                            )
                            TactileSegmentedOption(
                                label = AppStrings.get("theme_dark_btn", currentLang),
                                icon = Icons.Default.DarkMode,
                                selected = currentThemeMode == AppThemeMode.DARK,
                                accentColor = Color(0xFF38BDF8),
                                isDarkMode = isDarkMode,
                                haptic = haptic,
                                onClick = { onSelectThemeMode(AppThemeMode.DARK) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("appearance_theme_dark_btn")
                            )
                        }
                    }

                    // 2.2 Interface & Design Style + Theme Engine
                    CleanSettingsCard(isDarkMode = isDarkMode) {
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
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("appearance_style_glass_btn")
                            )
                            TactileSegmentedOption(
                                label = AppStrings.get("theme_style_neumorph", currentLang),
                                icon = Icons.Default.Layers,
                                selected = currentThemeStyle == AppThemeStyle.NEUMORPHISM,
                                accentColor = Color(0xFF6366F1),
                                isDarkMode = isDarkMode,
                                haptic = haptic,
                                onClick = { onSelectThemeStyle(AppThemeStyle.NEUMORPHISM) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("appearance_style_neumorph_btn")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = dividerColor)
                        Spacer(modifier = Modifier.height(10.dp))

                        val engineAccent = activeThemePreset?.primary ?: accentCyan
                        val engineInteraction = remember { MutableInteractionSource() }
                        val enginePressed by engineInteraction.collectIsPressedAsState()
                        val engineScale by animateFloatAsState(
                            targetValue = if (enginePressed) 0.97f else 1f,
                            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
                            label = "themeEngineRowScale"
                        )
                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showThemeEngineDialog = true
                            },
                            interactionSource = engineInteraction,
                            shape = RoundedCornerShape(14.dp),
                            color = engineAccent.copy(alpha = if (isDarkMode) 0.14f else 0.08f),
                            border = BorderStroke(
                                1.1.dp,
                                Brush.linearGradient(
                                    listOf(
                                        engineAccent.copy(alpha = 0.55f),
                                        (activeThemePreset?.secondary ?: Color(0xFF8B5CF6)).copy(alpha = 0.45f)
                                    )
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 56.dp)
                                .graphicsLayer {
                                    scaleX = engineScale
                                    scaleY = engineScale
                                }
                                .testTag("drawer_theme_engine_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    engineAccent.copy(alpha = 0.28f),
                                                    (activeThemePreset?.secondary ?: Color(0xFF8B5CF6)).copy(alpha = 0.24f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = engineAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = AppStrings.get("theme_engine_title", currentLang),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = activeThemePreset?.textPrimary
                                                ?: if (isDarkMode) Color.White else Color(0xFF0F172A)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(50.dp),
                                            color = engineAccent.copy(alpha = 0.20f)
                                        ) {
                                            Text(
                                                text = "${ThemeEngine.allThemes.size}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = engineAccent,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    val previewPreset = ThemeEngine.findById(selectedThemeEngineId)
                                    Text(
                                        text = previewPreset?.name ?: AppStrings.get("theme_engine_sub", currentLang),
                                        fontSize = 10.5.sp,
                                        color = if (previewPreset != null) engineAccent
                                        else (activeThemePreset?.textSecondary ?: if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)),
                                        fontWeight = if (previewPreset != null) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (previewPreset != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            previewPreset.previewSwatches.forEach { swatch ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(9.dp)
                                                        .clip(CircleShape)
                                                        .background(swatch)
                                                )
                                            }
                                        }
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = engineAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // 2.3 Dynamic Animations & Living UI (with Performance Toggle), Living Breathing Luxury & Focus Mode
                    CleanSettingsCard(isDarkMode = isDarkMode) {
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
                            switchTag = "drawer_dynamic_animations_toggle",
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
                        Text(
                            text = AppStrings.get("dynamic_animations_desc", currentLang),
                            fontSize = 10.5.sp,
                            lineHeight = 14.5.sp,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = dividerColor)
                        Spacer(modifier = Modifier.height(4.dp))

                        val luxuryAccent = activeThemePreset?.primary ?: Color(0xFFF59E0B)
                        TactileSwitchSettingRow(
                            icon = Icons.Default.AutoAwesome,
                            iconTint = luxuryAccent,
                            title = AppStrings.get("living_luxury_title", currentLang),
                            subtitle = AppStrings.get(
                                if (isLivingLuxuryMode) "living_luxury_active" else "living_luxury_inactive",
                                currentLang
                            ),
                            subtitleColor = if (isLivingLuxuryMode) luxuryAccent else null,
                            checked = isLivingLuxuryMode,
                            trackColor = luxuryAccent,
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            switchTag = "drawer_living_luxury_toggle",
                            onToggle = { enabled ->
                                onToggleLivingLuxuryMode(enabled)
                                Toast.makeText(
                                    context,
                                    AppStrings.get(
                                        if (enabled) "living_luxury_enabled_toast" else "living_luxury_disabled_toast",
                                        currentLang
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        HorizontalDivider(color = dividerColor)
                        Spacer(modifier = Modifier.height(4.dp))

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
                            switchTag = "drawer_focus_mode_toggle",
                            onToggle = { onToggleFocusMode() }
                        )
                    }
                }
            }

            SettingsRoute.SECURITY -> {
                val secScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(secScroll)
                        .padding(16.dp)
                        .testTag("settings_security_page"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsSubPageHeader(
                        title = AppStrings.get("settings_cat_security", currentLang),
                        subtitle = AppStrings.get("settings_cat_security_sub", currentLang),
                        icon = Icons.Default.Shield,
                        accentColor = Color(0xFF10B981),
                        isDarkMode = isDarkMode,
                        backContentDesc = AppStrings.get("back", currentLang),
                        haptic = haptic,
                        onBack = { currentRoute = SettingsRoute.HOME }
                    )

                    CleanSettingsCard(isDarkMode = isDarkMode) {
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
                    }
                }
            }

            SettingsRoute.NOTIFICATIONS -> {
                val notifScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(notifScroll)
                        .padding(16.dp)
                        .testTag("settings_notifications_page"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsSubPageHeader(
                        title = AppStrings.get("settings_cat_notifications", currentLang),
                        subtitle = AppStrings.get("settings_cat_notifications_sub", currentLang),
                        icon = Icons.Default.NotificationsActive,
                        accentColor = Color(0xFF0284C7),
                        isDarkMode = isDarkMode,
                        backContentDesc = AppStrings.get("back", currentLang),
                        haptic = haptic,
                        onBack = { currentRoute = SettingsRoute.HOME }
                    )

                    // 4.1 Check / Scan Notifications
                    DrawerNavigationItem(
                        icon = Icons.Default.NotificationsActive,
                        iconTint = Color(0xFF0284C7),
                        title = AppStrings.get("check_notifications", currentLang),
                        subtitle = AppStrings.get("drawer_notif_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = onCheckNotifications,
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        testTag = "drawer_notifications_item"
                    )

                    // 4.2 Leave Eligibility Threshold (Days) Slider & Quick Presets
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
                            ),
                            modifier = Modifier.testTag("threshold_slider")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(30, 45, 60, 90).forEach { preset ->
                                val selected = dutyThreshold == preset
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onSaveThreshold(preset)
                                    },
                                    label = {
                                        Text(
                                            text = "$preset",
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("threshold_preset_$preset"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDarkMode) Color(0x33334155) else Color(0xFFF1F5F9),
                                        selectedContainerColor = accentCyan,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            SettingsRoute.BACKUP_RESTORE -> {
                val backupScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(backupScroll)
                        .padding(16.dp)
                        .testTag("settings_backup_restore_page"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsSubPageHeader(
                        title = AppStrings.get("settings_cat_backup", currentLang),
                        subtitle = AppStrings.get("settings_cat_backup_sub", currentLang),
                        icon = Icons.Default.CloudSync,
                        accentColor = Color(0xFF4285F4),
                        isDarkMode = isDarkMode,
                        backContentDesc = AppStrings.get("back", currentLang),
                        haptic = haptic,
                        onBack = { currentRoute = SettingsRoute.HOME }
                    )

                    // 6.1 Backup & Data (JSON/CSV)
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

                    // 6.2 Export PDF Report
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

                    // 6.3 Export Database to Excel (.xlsx)
                    DrawerNavigationItem(
                        icon = Icons.Default.TableView,
                        iconTint = Color(0xFF059669),
                        title = AppStrings.get("room_export_xlsx_btn", currentLang),
                        subtitle = AppStrings.get("excel_export_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = onExportExcel,
                        testTag = "drawer_excel_item"
                    )

                    // 6.4 Google Drive Backup Card
                    CleanSettingsCard(isDarkMode = isDarkMode) {
                        TactileSwitchSettingRow(
                            icon = Icons.Default.CloudSync,
                            iconTint = Color(0xFF4285F4),
                            title = AppStrings.get("drive_backup_title", currentLang),
                            subtitle = "${AppStrings.get("drive_last_backup", currentLang)} ${if (lastDriveBackupTime > 0L) sdf.format(Date(lastDriveBackupTime)) else AppStrings.get("drive_never", currentLang)}",
                            checked = isDriveSyncEnabled,
                            trackColor = Color(0xFF4285F4),
                            isDarkMode = isDarkMode,
                            haptic = haptic,
                            switchTag = "drawer_drive_sync_toggle",
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
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("drawer_drive_backup_btn")
                            )
                            TactileActionPillButton(
                                text = AppStrings.get("drive_restore_btn", currentLang),
                                icon = Icons.Default.CloudDownload,
                                accentColor = Color(0xFF4285F4),
                                isFilled = false,
                                isDarkMode = isDarkMode,
                                haptic = haptic,
                                onClick = onRestoreFromDrive,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("drawer_drive_restore_btn")
                            )
                        }
                    }

                    // 6.5 Room DB Management
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

                    // 6.6 Restore Demo Data
                    DrawerNavigationItem(
                        icon = Icons.Default.Refresh,
                        iconTint = Color(0xFFF59E0B),
                        title = AppStrings.get("drawer_reset_title", currentLang),
                        subtitle = AppStrings.get("drawer_reset_sub", currentLang),
                        isDarkMode = isDarkMode,
                        haptic = haptic,
                        onClick = onResetSampleData,
                        testTag = "drawer_reset_item"
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSubPageHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isDarkMode: Boolean,
    backContentDesc: String,
    haptic: HapticFeedback,
    onBack: () -> Unit
) {
    val activeThemePreset = LocalThemePreset.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = activeThemePreset?.surfacePrimary ?: if (isDarkMode) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                if (isDarkMode) listOf(Color(0x38FFFFFF), accentColor.copy(alpha = 0.45f))
                else listOf(Color.White, accentColor.copy(alpha = 0.45f))
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
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onBack()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .testTag("settings_subpage_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = backContentDesc,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CleanSettingsCard(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    val activeThemePreset = LocalThemePreset.current
    val isLivingLuxury = LocalLivingLuxuryMode.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = activeThemePreset?.surfacePrimary ?: if (isDarkMode) Color(0xFF1E293B) else Color.White,
        border = BorderStroke(
            if (isLivingLuxury) 1.3.dp else 1.dp,
            activeThemePreset?.borderAccent?.copy(alpha = if (isLivingLuxury) 0.7f else 0.45f)
                ?: if (isLivingLuxury) Color(0x6638BDF8)
                else if (isDarkMode) Color(0x33475569)
                else Color(0x38CBD5E1)
        ),
        shadowElevation = if (isLivingLuxury) 5.dp else if (isDarkMode) 2.dp else 1.dp,
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
    rowTag: String? = null,
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
            .then(if (rowTag != null) Modifier.testTag(rowTag) else Modifier)
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
    val activeThemePreset = LocalThemePreset.current
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
            activeThemePreset?.surfacePrimary
                ?: if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.92f)
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
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(if (isPressed) 1.4.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
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
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569),
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
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
        shape = RoundedCornerShape(10.dp),
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
