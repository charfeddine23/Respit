package com.example

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppThemeMode
import com.example.data.Personnel
import com.example.data.formatRank
import com.example.ui.PersonnelViewModel
import com.example.ui.SortOption
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.AddEditPersonnelDialog
import com.example.ui.components.AppBottomNavigationPanel
import com.example.ui.components.AppNavPage
import com.example.ui.components.BiometricLockScreen
import com.example.ui.components.DeveloperInfoDialog
import com.example.ui.components.ExportDialog
import com.example.ui.components.FilterSortDialog
import com.example.ui.components.PersonnelCard
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SideNavigationDrawerContent
import com.example.ui.components.StatisticsSection
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.RoomDatabaseScreen
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.DynamicAuroraBackground
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    private val viewModel: PersonnelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set application animation and refresh frame rate up to 60 FPS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes.preferredRefreshRate = 60f
        } else {
            @Suppress("DEPRECATION")
            window.attributes.preferredRefreshRate = 60f
        }

        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.currentThemeMode.collectAsState()
            val isDarkTheme = themeMode == AppThemeMode.DARK

            LaunchedEffect(isDarkTheme) {
                viewModel.isDarkMode.value = isDarkTheme
            }

            val currentLang by viewModel.currentLanguage.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    LeaveTrackerMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveTrackerMainApp(viewModel: PersonnelViewModel) {
    val context = LocalContext.current
    val isLocked by viewModel.isLocked.collectAsState()
    val currentThemeMode by viewModel.currentThemeMode.collectAsState()
    val currentThemeStyle by viewModel.currentThemeStyle.collectAsState()
    val securityLockType by viewModel.securityLockType.collectAsState()
    val patternCode by viewModel.patternCode.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val isPasskeyEnabled by viewModel.isPasskeyEnabled.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isFocusMode by viewModel.isFocusMode.collectAsState()
    val dutyThreshold by viewModel.dutyThresholdDays.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val currentPin by viewModel.pinCode.collectAsState()
    val lastUndo by viewModel.lastUndoAction.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isNeumorphic = currentThemeStyle == com.example.data.AppThemeStyle.NEUMORPHISM

    val personnelList by viewModel.filteredPersonnel.collectAsState()
    val allPersonnelList by viewModel.allPersonnel.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val uniqueRanks by viewModel.uniqueRanks.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val selectedRank by viewModel.selectedRank.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedWilaya by viewModel.selectedWilaya.collectAsState()
    val uniqueWilayas by viewModel.uniqueWilayas.collectAsState()
    val currentSort by viewModel.sortOption.collectAsState()

    val notificationsCount by viewModel.unreadNotificationsCount.collectAsState()
    val isDriveSyncEnabled by viewModel.isDriveSyncEnabled.collectAsState()
    val lastDriveBackupTime by viewModel.lastDriveBackupTime.collectAsState()

    // Google Drive SAF Document Launchers
    val driveBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(viewModel.generateJsonContent().toByteArray(Charsets.UTF_8))
                }
                viewModel.updateLastDriveBackup()
                Toast.makeText(context, AppStrings.get("drive_backup_success", currentLang), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "${AppStrings.get("drive_backup_failed", currentLang)}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val driveRestoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val json = stream.bufferedReader(Charsets.UTF_8).readText()
                    val result = viewModel.importPersonnelFromJson(json, replaceAll = false)
                    if (result.first) {
                        viewModel.updateLastDriveBackup()
                        Toast.makeText(context, AppStrings.get("drive_restore_success", currentLang), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "${AppStrings.get("drive_restore_failed", currentLang)}: ${result.second}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "${AppStrings.get("drive_restore_failed", currentLang)}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var personnelToEdit by remember { mutableStateOf<Personnel?>(null) }
    var personnelToDelete by remember { mutableStateOf<Personnel?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showFilterSortDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var selectedNavPage by remember { mutableStateOf(AppNavPage.DASHBOARD) }
    val isDynamicAnimationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Notification permission launcher for Android 13+
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.checkUpcomingNotifications()
        }
    }

    LaunchedEffect(isLocked) {
        if (!isLocked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    viewModel.checkUpcomingNotifications()
                } else {
                    notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                viewModel.checkUpcomingNotifications()
            }
        }
    }

    // If locked with Biometric Lock, present modern full screen Biometric Lock Screen
    if (isLocked) {
        BiometricLockScreen(
            onUnlockSuccess = {
                viewModel.unlockWithBiometric()
                Toast.makeText(context, AppStrings.get("unlock_success", currentLang), Toast.LENGTH_SHORT).show()
            },
            isDarkMode = isDarkMode,
            currentLang = currentLang
        )
        return
    }

    // Back navigation: Pop sub-screens back to Dashboard
    BackHandler(enabled = selectedNavPage != AppNavPage.DASHBOARD) {
        selectedNavPage = AppNavPage.DASHBOARD
    }

    // ModalNavigationDrawer anchored on the RIGHT edge for right-side slide in
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true, // right-edge swipe gesture enabled
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.widthIn(max = 340.dp),
                    drawerContainerColor = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                    drawerTonalElevation = 6.dp
                ) {
                    CompositionLocalProvider(
                        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
                    ) {
                        SideNavigationDrawerContent(
                            currentLang = currentLang,
                            isDarkMode = isDarkMode,
                            lastUndo = lastUndo,
                            stats = stats,
                            onSelectLanguage = { lang -> viewModel.setLanguage(lang) },
                            onUndo = {
                                viewModel.undo()
                                Toast.makeText(context, AppStrings.get("undo_success", currentLang), Toast.LENGTH_SHORT).show()
                                scope.launch { drawerState.close() }
                            },
                            onCheckNotifications = {
                                selectedNavPage = AppNavPage.NOTIFICATIONS
                                scope.launch { drawerState.close() }
                            },
                            onExportPdf = {
                                viewModel.exportPdf(context)
                                scope.launch { drawerState.close() }
                            },
                            onOpenBackup = {
                                showExportDialog = true
                                scope.launch { drawerState.close() }
                            },
                            onOpenRoomDatabase = {
                                selectedNavPage = AppNavPage.DATABASE
                                scope.launch { drawerState.close() }
                            },
                            onToggleTheme = {
                                viewModel.toggleTheme()
                            },
                            onOpenDeveloper = {
                                showDeveloperDialog = true
                                scope.launch { drawerState.close() }
                            },
                            onResetSampleData = {
                                showResetConfirmDialog = true
                                scope.launch { drawerState.close() }
                            },
                            currentThemeMode = currentThemeMode,
                            onSelectThemeMode = { viewModel.setThemeMode(it) },
                            currentThemeStyle = currentThemeStyle,
                            onSelectThemeStyle = { viewModel.setThemeStyle(it) },
                            isFocusMode = isFocusMode,
                            onToggleFocusMode = { viewModel.toggleFocusMode() },
                            dutyThreshold = dutyThreshold,
                            onSaveThreshold = { viewModel.setDutyThreshold(it) },
                            isBiometricEnabled = isBiometricEnabled,
                            onToggleBiometric = { enabled -> viewModel.setBiometricLock(enabled) },
                            onLockNow = {
                                scope.launch { drawerState.close() }
                                viewModel.lockApp()
                            },
                            isDynamicAnimationsEnabled = isDynamicAnimationsEnabled,
                            onToggleDynamicAnimations = { viewModel.setDynamicAnimationsEnabled(it) },
                            isDriveSyncEnabled = isDriveSyncEnabled,
                            onToggleDriveSync = { viewModel.setDriveSyncEnabled(it) },
                            lastDriveBackupTime = lastDriveBackupTime,
                            onBackupToDrive = {
                                driveBackupLauncher.launch("respite_pulse_backup_${System.currentTimeMillis()}.json")
                            },
                            onRestoreFromDrive = {
                                driveRestoreLauncher.launch(arrayOf("application/json", "text/*"))
                            }
                        )
                    }
                }
            }
        ) {
            CompositionLocalProvider(
                LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Ambient Liquid Dynamic Aurora Mesh Background (Smooth 60fps drifting orbs)
                    DynamicAuroraBackground(
                        isDark = isDarkMode,
                        isNeumorphic = currentThemeStyle == com.example.data.AppThemeStyle.NEUMORPHISM,
                        animationsEnabled = isDynamicAnimationsEnabled
                    )

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_scaffold"),
                        containerColor = Color.Transparent,
                        topBar = {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Surface(
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left Side (الجهة اليسرى تماماً): App Title Row with Logo & Version (Non-clickable for theme)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .padding(vertical = 4.dp, horizontal = 2.dp)
                                                .testTag("app_title_row")
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .liquidGlassInteractive(
                                                        shape = if (isNeumorphic) RoundedCornerShape(12.dp) else RoundedCornerShape(10.dp),
                                                        glowColor = MaterialTheme.colorScheme.primary,
                                                        isDark = isDarkMode,
                                                        isNeumorphic = isNeumorphic
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.ic_respite_pulse_logo),
                                                    contentDescription = AppStrings.get("app_icon_desc", currentLang),
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = AppStrings.get("app_title", currentLang),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        // Right Side (الجهة اليمنى): Quick Actions (Settings, Notifications, Drawer Menu)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val iconShape = if (isNeumorphic) CircleShape else RoundedCornerShape(12.dp)

                                            // Settings Dialog Button
                                            Box(
                                                modifier = Modifier.liquidGlassInteractive(
                                                    shape = iconShape,
                                                    glowColor = MaterialTheme.colorScheme.primary,
                                                    isDark = isDarkMode,
                                                    isNeumorphic = isNeumorphic
                                                )
                                            ) {
                                                IconButton(
                                                    onClick = { showSettingsDialog = true },
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(iconShape)
                                                        .testTag("header_settings_button")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Settings,
                                                        contentDescription = AppStrings.get("settings", currentLang),
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }

                                            // Drawer Menu Button
                                            Box(
                                                modifier = Modifier.liquidGlassInteractive(
                                                    shape = iconShape,
                                                    glowColor = MaterialTheme.colorScheme.primary,
                                                    isDark = isDarkMode,
                                                    isNeumorphic = isNeumorphic
                                                )
                                            ) {
                                                IconButton(
                                                    onClick = { scope.launch { drawerState.open() } },
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(iconShape)
                                                        .testTag("header_menu_button")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Menu,
                                                        contentDescription = AppStrings.get("toolbar_menu_desc", currentLang),
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        floatingActionButton = {
                            if (selectedNavPage == AppNavPage.DASHBOARD || selectedNavPage == AppNavPage.RECORDS) {
                                if (isNeumorphic) {
                                    Surface(
                                        onClick = {
                                            personnelToEdit = null
                                            showAddEditDialog = true
                                        },
                                        modifier = Modifier
                                            .size(58.dp)
                                            .shadow(
                                                elevation = 10.dp,
                                                shape = CircleShape,
                                                ambientColor = Color(0x602563EB),
                                                spotColor = Color(0x901D4ED8)
                                            )
                                            .clip(CircleShape)
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                                )
                                            )
                                            .testTag("add_personnel_fab"),
                                        shape = CircleShape,
                                        color = Color(0xFF2563EB)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = AppStrings.get("add_personnel", currentLang),
                                                tint = Color.White,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        onClick = {
                                            personnelToEdit = null
                                            showAddEditDialog = true
                                        },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(18.dp),
                                                ambientColor = Color(0xFF00BCD4).copy(alpha = 0.45f),
                                                spotColor = Color(0xFF00E5FF).copy(alpha = 0.55f)
                                            )
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF00BCD4), Color(0xFF00E5FF))
                                                )
                                            )
                                            .border(
                                                BorderStroke(
                                                    1.5.dp,
                                                    Brush.linearGradient(
                                                        listOf(
                                                            Color.White.copy(alpha = 0.85f),
                                                            Color(0xFFE0F7FA).copy(alpha = 0.7f),
                                                            Color.White.copy(alpha = 0.4f)
                                                        )
                                                    )
                                                ),
                                                RoundedCornerShape(18.dp)
                                            )
                                            .testTag("add_personnel_fab"),
                                        shape = RoundedCornerShape(18.dp),
                                        color = Color.Transparent
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = AppStrings.get("add_personnel", currentLang),
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        bottomBar = {
                            AppBottomNavigationPanel(
                                selectedPage = selectedNavPage,
                                onSelectPage = { selectedNavPage = it },
                                notificationsCount = notificationsCount,
                                isDarkMode = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                animationsEnabled = isDynamicAnimationsEnabled,
                                currentLang = currentLang
                            )
                        }
                    ) { innerPadding ->
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            val isTablet = maxWidth >= 600.dp
                            val contentMaxWidth = if (isTablet) 980.dp else 680.dp
                            val horizontalPadding = if (isTablet) 24.dp else 16.dp
                            val contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 14.dp)

                            when (selectedNavPage) {
                                AppNavPage.DATABASE -> {
                                    RoomDatabaseScreen(
                                        viewModel = viewModel,
                                        onBack = { selectedNavPage = AppNavPage.DASHBOARD }
                                    )
                                }
                                AppNavPage.NOTIFICATIONS -> {
                                    NotificationScreen(
                                        viewModel = viewModel,
                                        onBack = { selectedNavPage = AppNavPage.DASHBOARD },
                                        onSelectPersonnel = { personnelId ->
                                            val p = allPersonnelList.find { it.id == personnelId }
                                            if (p != null) {
                                                personnelToEdit = p
                                                showAddEditDialog = true
                                            }
                                        }
                                    )
                                }
                                AppNavPage.DASHBOARD -> {
                                    val previewList = remember(personnelList) { personnelList.take(4) }
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .widthIn(max = contentMaxWidth)
                                            .testTag("dashboard_lazy_column"),
                                        contentPadding = contentPadding,
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // Unified Interactive KPI Dashboard (with Pulse & living animation support)
                                        item {
                                            StatisticsSection(
                                                stats = stats,
                                                currentLang = currentLang,
                                                selectedStatus = selectedStatus,
                                                onSelectStatus = {
                                                    viewModel.selectedStatus.value = it
                                                    selectedNavPage = AppNavPage.RECORDS
                                                },
                                                isDarkMode = isDarkMode,
                                                isNeumorphic = isNeumorphic,
                                                animationsEnabled = isDynamicAnimationsEnabled
                                            )
                                        }

                                        // 3 Prominent Tactile Action Buttons (Matching Reference Design: Green, Coral, Royal Blue)
                                        item {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // 1. Green Button: "+ إضافة فرد" (Emerald Green)
                                                Surface(
                                                    onClick = {
                                                        personnelToEdit = null
                                                        showAddEditDialog = true
                                                    },
                                                    shape = RoundedCornerShape(16.dp),
                                                    color = EmeraldSuccess,
                                                    shadowElevation = if (isNeumorphic) 6.dp else 4.dp,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp)
                                                        .testTag("action_btn_add_personnel")
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = AppStrings.get("add_personnel", currentLang),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }

                                                // 2. Coral Red Button: "تنبيهات عاجلة"
                                                Surface(
                                                    onClick = {
                                                        selectedNavPage = AppNavPage.NOTIFICATIONS
                                                    },
                                                    shape = RoundedCornerShape(16.dp),
                                                    color = CoralRedAccent,
                                                    shadowElevation = if (isNeumorphic) 6.dp else 4.dp,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp)
                                                        .testTag("action_btn_urgent_alerts")
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = AppStrings.get("urgent_alerts_title", currentLang).split("(").first().trim(),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }

                                                // 3. Royal Blue Button: "قاعدة البيانات"
                                                Surface(
                                                    onClick = {
                                                        selectedNavPage = AppNavPage.DATABASE
                                                    },
                                                    shape = RoundedCornerShape(16.dp),
                                                    color = RoyalBlueAccent,
                                                    shadowElevation = if (isNeumorphic) 6.dp else 4.dp,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(48.dp)
                                                        .testTag("action_btn_room_db")
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(Icons.Default.Storage, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = AppStrings.get("nav_database", currentLang),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Quick Search and Filter Trigger Box
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .liquidGlass(
                                                        shape = LiquidGlassDefaults.CardShape,
                                                        elevation = 5.dp,
                                                        borderAlpha = 0.85f,
                                                        isDark = isDarkMode,
                                                        isNeumorphic = isNeumorphic,
                                                        cornerRadius = 24.dp
                                                    )
                                                    .padding(12.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                    ) {
                                                        OutlinedTextField(
                                                            value = searchQuery,
                                                            onValueChange = {
                                                                viewModel.searchQuery.value = it
                                                                if (it.trim().length >= 3) {
                                                                    viewModel.addRecentSearch(it)
                                                                }
                                                            },
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .testTag("search_field"),
                                                            placeholder = {
                                                                Text(
                                                                    AppStrings.get("search_placeholder", currentLang),
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                                                )
                                                            },
                                                            leadingIcon = {
                                                                Icon(
                                                                    imageVector = Icons.Default.Search,
                                                                    contentDescription = AppStrings.get("search_label", currentLang),
                                                                    tint = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4)
                                                                )
                                                            },
                                                            trailingIcon = {
                                                                if (searchQuery.isNotEmpty()) {
                                                                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Clear,
                                                                            contentDescription = AppStrings.get("clear_search", currentLang),
                                                                            tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                                                        )
                                                                    }
                                                                }
                                                            },
                                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                                            keyboardActions = KeyboardActions(
                                                                onSearch = {
                                                                    if (searchQuery.isNotBlank()) {
                                                                        viewModel.addRecentSearch(searchQuery)
                                                                    }
                                                                }
                                                            ),
                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                focusedTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                                unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                                focusedContainerColor = if (isDarkMode) Color(0x331E293B) else Color(0xF5FFFFFF),
                                                                unfocusedContainerColor = if (isDarkMode) Color(0x221E293B) else Color(0xD9FFFFFF),
                                                                unfocusedBorderColor = if (isDarkMode) Color(0x4000E5FF) else Color(0x4000BCD4),
                                                                focusedBorderColor = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4),
                                                                focusedPlaceholderColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                                                unfocusedPlaceholderColor = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8),
                                                                cursorColor = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4)
                                                            ),
                                                            singleLine = true,
                                                            shape = RoundedCornerShape(50.dp)
                                                        )

                                                        Box(
                                                            modifier = Modifier.liquidGlassInteractive(
                                                                shape = RoundedCornerShape(50.dp),
                                                                glowColor = MaterialTheme.colorScheme.primary,
                                                                isDark = isDarkMode,
                                                                isNeumorphic = isNeumorphic
                                                            )
                                                        ) {
                                                            IconButton(
                                                                onClick = {
                                                                    selectedNavPage = AppNavPage.RECORDS
                                                                    showFilterSortDialog = true
                                                                },
                                                                modifier = Modifier
                                                                    .size(52.dp)
                                                                    .testTag("filter_sort_dialog_button")
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Tune,
                                                                    contentDescription = AppStrings.get("filter_sort_btn", currentLang),
                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(26.dp)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    // Recent Searches
                                                    if (recentSearches.isNotEmpty()) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .horizontalScroll(rememberScrollState()),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.History,
                                                                contentDescription = AppStrings.get("recent_searches_title", currentLang),
                                                                tint = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Text(
                                                                text = AppStrings.get("recent_searches_title", currentLang),
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF64748B),
                                                                fontSize = 11.sp
                                                            )
                                                            recentSearches.forEach { queryItem ->
                                                                Surface(
                                                                    shape = RoundedCornerShape(50.dp),
                                                                    color = if (searchQuery == queryItem) {
                                                                        if (isDarkMode) Color(0xFF00BCD4) else Color(0xFFE0F7FA)
                                                                    } else {
                                                                        if (isDarkMode) Color(0xFF334155) else Color(0xF2FFFFFF)
                                                                    },
                                                                    border = BorderStroke(
                                                                        0.8.dp,
                                                                        if (isDarkMode) Color(0x6600E5FF) else Color(0x4000BCD4)
                                                                    ),
                                                                    onClick = {
                                                                        viewModel.searchQuery.value = queryItem
                                                                        viewModel.addRecentSearch(queryItem)
                                                                    },
                                                                    modifier = Modifier.height(28.dp)
                                                                ) {
                                                                    Row(
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        modifier = Modifier.padding(horizontal = 8.dp)
                                                                    ) {
                                                                        Text(
                                                                            text = queryItem,
                                                                            style = MaterialTheme.typography.labelSmall,
                                                                            fontWeight = FontWeight.SemiBold,
                                                                            color = if (searchQuery == queryItem) {
                                                                                if (isDarkMode) Color.White else Color(0xFF00838F)
                                                                            } else {
                                                                                if (isDarkMode) Color(0xFFF1F5F9) else Color(0xFF0F172A)
                                                                            },
                                                                            fontSize = 11.sp
                                                                        )
                                                                        Spacer(modifier = Modifier.width(4.dp))
                                                                        IconButton(
                                                                            onClick = { viewModel.removeRecentSearch(queryItem) },
                                                                            modifier = Modifier.size(16.dp)
                                                                        ) {
                                                                            Icon(
                                                                                imageVector = Icons.Default.Close,
                                                                                contentDescription = AppStrings.get("delete", currentLang),
                                                                                tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                                                                modifier = Modifier.size(11.dp)
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            TextButton(
                                                                onClick = { viewModel.clearRecentSearches() },
                                                                modifier = Modifier.height(28.dp),
                                                                contentPadding = PaddingValues(horizontal = 6.dp)
                                                            ) {
                                                                Text(
                                                                    text = AppStrings.get("clear_btn", currentLang),
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    color = if (isDarkMode) Color(0xFFF87171) else Color(0xFFEF4444),
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Quick Navigation Header to Records
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${AppStrings.get("total_personnel", currentLang)} (${personnelList.size} / ${allPersonnelList.size})",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                TextButton(
                                                    onClick = { selectedNavPage = AppNavPage.RECORDS },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = AppStrings.get("view_all", currentLang),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Recent / Filtered Records Preview (up to 4 cards)
                                        if (previewList.isEmpty()) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .liquidGlass(
                                                            shape = LiquidGlassDefaults.CardShape,
                                                            elevation = 4.dp,
                                                            isDark = isDarkMode
                                                        )
                                                        .padding(28.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(
                                                            imageVector = Icons.Default.FilterList,
                                                            contentDescription = null,
                                                            tint = if (isDarkMode) Color(0xFF00E5FF).copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                            modifier = Modifier.size(44.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Text(
                                                            text = AppStrings.get("no_results", currentLang),
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = AppStrings.get("no_results_desc", currentLang),
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            items(previewList, key = { it.id }) { person ->
                                                PersonnelCard(
                                                    personnel = person,
                                                    dutyThresholdDays = dutyThreshold,
                                                    isFocusMode = isFocusMode,
                                                    isDarkMode = isDarkMode,
                                                    isNeumorphic = isNeumorphic,
                                                    animationsEnabled = isDynamicAnimationsEnabled,
                                                    currentLang = currentLang,
                                                    onConfirmDuty = {
                                                        viewModel.confirmReturnToDuty(person.id)
                                                        Toast.makeText(
                                                            context,
                                                            "${AppStrings.get("confirm_duty_btn", currentLang)}: ${formatRank(person.rank, currentLang)} / ${person.fullName}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                    onEdit = {
                                                        personnelToEdit = person
                                                        showAddEditDialog = true
                                                    },
                                                    onDelete = {
                                                        personnelToDelete = person
                                                    }
                                                )
                                            }
                                        }

                                        item {
                                            Spacer(modifier = Modifier.height(80.dp))
                                        }
                                    }
                                }
                                AppNavPage.RECORDS -> {
                                    val activeFilterCount = (if (selectedStatus != "ALL") 1 else 0) +
                                            (if (selectedRank != "ALL") 1 else 0) +
                                            (if (currentSort != SortOption.RETURN_SOON) 1 else 0)

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .widthIn(max = contentMaxWidth)
                                            .testTag("records_lazy_column"),
                                        contentPadding = contentPadding,
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        // Full Search Bar with Filter & Sort dialog trigger
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .liquidGlass(
                                                        shape = LiquidGlassDefaults.CardShape,
                                                        elevation = 5.dp,
                                                        borderAlpha = 0.85f,
                                                        isDark = isDarkMode,
                                                        isNeumorphic = isNeumorphic,
                                                        cornerRadius = 24.dp
                                                    )
                                                    .padding(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = searchQuery,
                                                        onValueChange = {
                                                            viewModel.searchQuery.value = it
                                                            if (it.trim().length >= 3) {
                                                                viewModel.addRecentSearch(it)
                                                            }
                                                        },
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("search_field_records"),
                                                        placeholder = {
                                                            Text(
                                                                AppStrings.get("search_placeholder", currentLang),
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                                            )
                                                        },
                                                        leadingIcon = {
                                                            Icon(
                                                                imageVector = Icons.Default.Search,
                                                                contentDescription = AppStrings.get("search_label", currentLang),
                                                                tint = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4)
                                                            )
                                                        },
                                                        trailingIcon = {
                                                            if (searchQuery.isNotEmpty()) {
                                                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Clear,
                                                                        contentDescription = AppStrings.get("clear_search", currentLang),
                                                                        tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                                        keyboardActions = KeyboardActions(
                                                            onSearch = {
                                                                if (searchQuery.isNotBlank()) {
                                                                    viewModel.addRecentSearch(searchQuery)
                                                                }
                                                            }
                                                        ),
                                                        colors = OutlinedTextFieldDefaults.colors(
                                                            focusedTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                            unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                            focusedContainerColor = if (isDarkMode) Color(0x331E293B) else Color(0xF5FFFFFF),
                                                            unfocusedContainerColor = if (isDarkMode) Color(0x221E293B) else Color(0xD9FFFFFF),
                                                            unfocusedBorderColor = if (isDarkMode) Color(0x4000E5FF) else Color(0x4000BCD4),
                                                            focusedBorderColor = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4),
                                                            focusedPlaceholderColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                                            unfocusedPlaceholderColor = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8),
                                                            cursorColor = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4)
                                                        ),
                                                        singleLine = true,
                                                        shape = RoundedCornerShape(14.dp)
                                                    )

                                                    Box(
                                                        modifier = Modifier.liquidGlassInteractive(
                                                            shape = RoundedCornerShape(14.dp),
                                                            glowColor = if (activeFilterCount > 0) Color(0xFF00BCD4) else MaterialTheme.colorScheme.primary,
                                                            isDark = isDarkMode,
                                                            isNeumorphic = isNeumorphic
                                                        )
                                                    ) {
                                                        BadgedBox(
                                                            badge = {
                                                                if (activeFilterCount > 0) {
                                                                    Badge(
                                                                        containerColor = Color(0xFF00BCD4),
                                                                        contentColor = Color.White
                                                                    ) {
                                                                        Text(
                                                                            text = "$activeFilterCount",
                                                                            fontWeight = FontWeight.Bold,
                                                                            fontSize = 11.sp
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        ) {
                                                            IconButton(
                                                                onClick = { showFilterSortDialog = true },
                                                                modifier = Modifier
                                                                    .size(52.dp)
                                                                    .testTag("filter_sort_dialog_button_records")
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Tune,
                                                                    contentDescription = AppStrings.get("filter_sort_btn", currentLang),
                                                                    tint = if (activeFilterCount > 0) Color(0xFF00BCD4) else MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(26.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Results Count Indicator
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${AppStrings.get("total_personnel", currentLang)} (${personnelList.size} / ${allPersonnelList.size})",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "${AppStrings.get("duty_threshold_label", currentLang)}: $dutyThreshold ${AppStrings.get("days", currentLang)}  |  ",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = currentSort.getTitle(currentLang),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Personnel List Cards
                                        if (personnelList.isEmpty()) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .liquidGlass(
                                                            shape = LiquidGlassDefaults.CardShape,
                                                            elevation = 4.dp,
                                                            isDark = isDarkMode
                                                        )
                                                        .padding(32.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(
                                                            imageVector = Icons.Default.FilterList,
                                                            contentDescription = null,
                                                            tint = if (isDarkMode) Color(0xFF00E5FF).copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                            modifier = Modifier.size(48.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            text = AppStrings.get("no_results", currentLang),
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = AppStrings.get("no_results_desc", currentLang),
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        } else if (isTablet) {
                                            // Adaptive 2-column grid layout for tablets and wide screens
                                            items(personnelList.chunked(2), key = { pair -> pair.joinToString("-") { it.id.toString() } }) { rowItems ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                                ) {
                                                    for (person in rowItems) {
                                                        Box(modifier = Modifier.weight(1f)) {
                                                            PersonnelCard(
                                                                personnel = person,
                                                                dutyThresholdDays = dutyThreshold,
                                                                isFocusMode = isFocusMode,
                                                                isDarkMode = isDarkMode,
                                                                isNeumorphic = isNeumorphic,
                                                                animationsEnabled = isDynamicAnimationsEnabled,
                                                                currentLang = currentLang,
                                                                onConfirmDuty = {
                                                                    viewModel.confirmReturnToDuty(person.id)
                                                                    Toast.makeText(
                                                                        context,
                                                                        "${AppStrings.get("confirm_duty_btn", currentLang)}: ${formatRank(person.rank, currentLang)} / ${person.fullName}",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                },
                                                                onEdit = {
                                                                    personnelToEdit = person
                                                                    showAddEditDialog = true
                                                                },
                                                                onDelete = {
                                                                    personnelToDelete = person
                                                                }
                                                            )
                                                        }
                                                    }
                                                    if (rowItems.size == 1) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                    }
                                                }
                                            }
                                        } else {
                                            items(personnelList, key = { it.id }) { person ->
                                                PersonnelCard(
                                                    personnel = person,
                                                    dutyThresholdDays = dutyThreshold,
                                                    isFocusMode = isFocusMode,
                                                    isDarkMode = isDarkMode,
                                                    isNeumorphic = isNeumorphic,
                                                    animationsEnabled = isDynamicAnimationsEnabled,
                                                    currentLang = currentLang,
                                                    onConfirmDuty = {
                                                        viewModel.confirmReturnToDuty(person.id)
                                                        Toast.makeText(
                                                            context,
                                                            "${AppStrings.get("confirm_duty_btn", currentLang)}: ${formatRank(person.rank, currentLang)} / ${person.fullName}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                    onEdit = {
                                                        personnelToEdit = person
                                                        showAddEditDialog = true
                                                    },
                                                    onDelete = {
                                                        personnelToDelete = person
                                                    }
                                                )
                                            }
                                        }

                                        item {
                                            Spacer(modifier = Modifier.height(90.dp))
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

    // Add / Edit Personnel Dialog
    if (showAddEditDialog) {
        AddEditPersonnelDialog(
            personnelToEdit = personnelToEdit,
            onDismiss = {
                showAddEditDialog = false
                personnelToEdit = null
            },
            onSave = { id, fullName, rank, regNumber, startMillis, returnMillis, isDuty, leaveType, wilaya, notes ->
                viewModel.savePersonnel(
                    id = id,
                    fullName = fullName,
                    rank = rank,
                    registrationNumber = regNumber,
                    startDateMillis = startMillis,
                    returnDateMillis = returnMillis,
                    isReturnedToDuty = isDuty,
                    leaveType = leaveType,
                    wilaya = wilaya,
                    notes = notes
                )
                showAddEditDialog = false
                personnelToEdit = null
                Toast.makeText(
                    context,
                    AppStrings.get("personnel_saved_success", currentLang),
                    Toast.LENGTH_SHORT
                ).show()
            },
            isDarkMode = isDarkMode,
            currentLang = currentLang
        )
    }

    // Delete Confirmation Dialog
    if (personnelToDelete != null) {
        val target = personnelToDelete!!
        AlertDialog(
            onDismissRequest = { personnelToDelete = null },
            title = {
                Text(AppStrings.get("confirm_delete_title", currentLang), fontWeight = FontWeight.Bold)
            },
            text = {
                val qMark = if (currentLang == AppLanguage.ARABIC) "؟" else "?"
                Text("${AppStrings.get("confirm_delete_message", currentLang)} ${formatRank(target.rank, currentLang)} / ${target.fullName} (${target.registrationNumber})$qMark")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePersonnel(target)
                        personnelToDelete = null
                        Toast.makeText(context, AppStrings.get("delete_success", currentLang), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(AppStrings.get("delete", currentLang), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { personnelToDelete = null }) {
                    Text(AppStrings.get("cancel", currentLang))
                }
            }
        )
    }

    // Unified Filter & Sort Dialog
    if (showFilterSortDialog) {
        FilterSortDialog(
            selectedSort = currentSort,
            onSelectSort = { viewModel.sortOption.value = it },
            selectedStatus = selectedStatus,
            onSelectStatus = { viewModel.selectedStatus.value = it },
            selectedRank = selectedRank,
            onSelectRank = { viewModel.selectedRank.value = it },
            uniqueRanks = uniqueRanks,
            stats = stats,
            onResetFilters = {
                viewModel.selectedStatus.value = "ALL"
                viewModel.selectedRank.value = "ALL"
                viewModel.selectedWilaya.value = "ALL"
                viewModel.sortOption.value = SortOption.RETURN_SOON
            },
            onDismiss = { showFilterSortDialog = false },
            isDarkMode = isDarkMode,
            currentLang = currentLang
        )
    }

    // Developer Info Dialog (From Drawer)
    if (showDeveloperDialog) {
        DeveloperInfoDialog(
            currentLang = currentLang,
            onDismiss = { showDeveloperDialog = false }
        )
    }

    // Settings Dialog with Google Drive Backup & Restore wired
    if (showSettingsDialog) {
        SettingsDialog(
            isDarkMode = isDarkMode,
            currentThemeMode = currentThemeMode,
            onSelectThemeMode = { viewModel.setThemeMode(it) },
            currentThemeStyle = currentThemeStyle,
            onSelectThemeStyle = { viewModel.setThemeStyle(it) },
            onToggleTheme = { viewModel.toggleTheme() },
            isFocusMode = isFocusMode,
            onToggleFocusMode = { viewModel.toggleFocusMode() },
            currentThreshold = dutyThreshold,
            onSaveThreshold = { viewModel.setDutyThreshold(it) },
            isPinEnabled = isPinEnabled,
            currentPin = currentPin,
            onSavePin = { pin, enabled -> viewModel.setPinCode(pin, enabled) },
            securityLockType = securityLockType,
            currentPattern = patternCode,
            isBiometricEnabled = isBiometricEnabled,
            isPasskeyEnabled = isPasskeyEnabled,
            onSaveSecurity = { type, pin, pattern, bio, passkey, enabled ->
                viewModel.setSecuritySettings(enabled, type, pin, pattern, bio, passkey)
            },
            onLockNow = { viewModel.lockApp() },
            currentLanguage = currentLang,
            onSelectLanguage = { viewModel.setLanguage(it) },
            isDriveSyncEnabled = isDriveSyncEnabled,
            onToggleDriveSync = { viewModel.setDriveSyncEnabled(it) },
            lastDriveBackupTime = lastDriveBackupTime,
            onBackupToDrive = {
                driveBackupLauncher.launch("respite_pulse_backup_${System.currentTimeMillis()}.json")
            },
            onRestoreFromDrive = {
                driveRestoreLauncher.launch(arrayOf("application/json", "text/*"))
            },
            isDynamicAnimationsEnabled = isDynamicAnimationsEnabled,
            onToggleDynamicAnimations = { viewModel.setDynamicAnimationsEnabled(it) },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // Export & Import Dialog
    if (showExportDialog) {
        ExportDialog(
            csvContent = viewModel.generateCsvContent(),
            jsonContent = viewModel.generateJsonContent(),
            onExportPdf = {
                viewModel.exportPdf(context)
            },
            onImportJson = { jsonStr, replaceAll ->
                viewModel.importPersonnelFromJson(jsonStr, replaceAll)
            },
            onDismiss = { showExportDialog = false },
            currentLang = currentLang
        )
    }

    // Reset Sample Data Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(AppStrings.get("reset_sample_dialog_title", currentLang), fontWeight = FontWeight.Bold)
            },
            text = {
                Text(AppStrings.get("reset_sample_dialog_desc", currentLang))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToSampleData()
                        showResetConfirmDialog = false
                        Toast.makeText(context, AppStrings.get("reset_sample_success", currentLang), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(AppStrings.get("drawer_reset_title", currentLang), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text(AppStrings.get("cancel", currentLang))
                }
            }
        )
    }
}
