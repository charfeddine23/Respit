package com.example

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
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
import com.example.ui.components.AddNewLeaveDialog
import com.example.ui.components.AppBottomNavigationPanel
import com.example.ui.components.PersonnelLeaveHistoryDialog
import com.example.ui.components.AppNavPage
import com.example.ui.components.BiometricLockScreen
import com.example.ui.components.DashboardPillSearchBar
import com.example.ui.components.DashboardQuickActionRow
import com.example.ui.components.DeveloperInfoDialog
import com.example.ui.components.ExportDialog
import com.example.ui.components.FilterSortDialog
import com.example.ui.components.InAppUpdateDialog
import com.example.ui.components.LiquidGlassConfirmDialog
import com.example.ui.components.PersonnelCard
import com.example.ui.components.SideNavigationDrawerContent
import com.example.ui.components.StatisticsSection
import com.example.ui.components.TopBarFadeToBlurOverlay
import com.example.ui.components.TrashBinDialog
import com.example.ui.screens.AnalyticsDashboardScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.RoomDatabaseScreen
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DynamicAuroraBackground
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.KufiArabicBrandFontFamily
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppUpdateState
import com.example.util.ExcelXlsxExporter
import com.example.util.WindowSecurityHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : FragmentActivity() {
    private val viewModel: PersonnelViewModel by viewModels()
    private val openNotificationsSignal = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.getBooleanExtra("open_notifications", false) == true) {
            openNotificationsSignal.intValue++
        }

        // Unlock maximum supported display refresh rate (90Hz / 120Hz / 144Hz+) for silky fluidity
        try {
            val activeDisplay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay
            }
            val currentMode = activeDisplay?.mode
            val supportedModes = activeDisplay?.supportedModes?.toList().orEmpty()
            val bestMode = supportedModes
                .filter {
                    currentMode == null ||
                        (it.physicalWidth == currentMode.physicalWidth &&
                            it.physicalHeight == currentMode.physicalHeight)
                }
                .maxByOrNull { it.refreshRate }
                ?: supportedModes.maxByOrNull { it.refreshRate }

            val params = window.attributes
            if (bestMode != null) {
                params.preferredDisplayModeId = bestMode.modeId
                params.preferredRefreshRate = bestMode.refreshRate
            } else {
                params.preferredRefreshRate = 120f
            }
            window.attributes = params
        } catch (_: Exception) {
            // Safe fallback on headless/Robolectric test environments
        }

        enableEdgeToEdge()
        if (viewModel.isFitToScreenEnabled.value) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.statusBars())
        }
        // Enforce Maximum Security (No Screenshot) from very first frame when enabled
        WindowSecurityHelper.applySecureFlag(this, viewModel.isNoScreenshotEnabled.value)
        setContent {
            val themeMode by viewModel.currentThemeMode.collectAsState()
            val selectedThemeEngineId by viewModel.selectedThemeEngineId.collectAsState()
            val isLivingLuxuryMode by viewModel.isLivingLuxuryMode.collectAsState()
            val isDynamicAnimationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()
            val activeThemePreset = remember(selectedThemeEngineId) {
                com.example.ui.theme.ThemeEngine.findById(selectedThemeEngineId)
            }
            val isDarkTheme = activeThemePreset?.isDark ?: (themeMode == AppThemeMode.DARK)

            LaunchedEffect(isDarkTheme) {
                viewModel.isDarkMode.value = isDarkTheme
            }

            val currentLang by viewModel.currentLanguage.collectAsState()
            val navSignal by openNotificationsSignal
            MyApplicationTheme(
                darkTheme = isDarkTheme,
                themePreset = activeThemePreset,
                livingLuxuryMode = isLivingLuxuryMode,
                animationsEnabled = isDynamicAnimationsEnabled
            ) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    LeaveTrackerMainApp(
                        viewModel = viewModel,
                        openNotificationsSignal = navSignal
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("open_notifications", false)) {
            openNotificationsSignal.intValue++
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && viewModel.isFitToScreenEnabled.value) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.statusBars())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LeaveTrackerMainApp(
    viewModel: PersonnelViewModel,
    openNotificationsSignal: Int = 0
) {
    val context = LocalContext.current
    val isLocked by viewModel.isLocked.collectAsState()
    val currentThemeMode by viewModel.currentThemeMode.collectAsState()
    val selectedThemeEngineId by viewModel.selectedThemeEngineId.collectAsState()
    val activeThemePreset = com.example.ui.theme.LocalThemePreset.current
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val dutyThreshold by viewModel.dutyThresholdDays.collectAsState()
    val lastUndo by viewModel.lastUndoAction.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isNoScreenshotEnabled by viewModel.isNoScreenshotEnabled.collectAsState()
    val isFitToScreenEnabled by viewModel.isFitToScreenEnabled.collectAsState()

    LaunchedEffect(isNoScreenshotEnabled) {
        WindowSecurityHelper.applySecureFlag(context as? android.app.Activity, isNoScreenshotEnabled)
    }

    LaunchedEffect(isFitToScreenEnabled) {
        val window = (context as? android.app.Activity)?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isFitToScreenEnabled) {
                controller.hide(WindowInsetsCompat.Type.statusBars())
            } else {
                controller.show(WindowInsetsCompat.Type.statusBars())
            }
        }
    }

    val personnelList by viewModel.filteredPersonnel.collectAsState()
    val allPersonnelList by viewModel.allPersonnel.collectAsState()
    val allLeavesList by viewModel.allLeaves.collectAsState()
    val dutyReferenceMap by viewModel.dutyReferenceMap.collectAsState()
    val lastShortLeaveMap by viewModel.lastShortLeaveMap.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val uniqueRanks by viewModel.uniqueRanks.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val selectedRank by viewModel.selectedRank.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val currentSort by viewModel.sortOption.collectAsState()

    val notificationsList by viewModel.notifications.collectAsState()
    val notificationsCount = notificationsList.size
    val todayMillis by viewModel.todayMillis.collectAsState()
    val isDriveSyncEnabled by viewModel.isDriveSyncEnabled.collectAsState()
    val lastDriveBackupTime by viewModel.lastDriveBackupTime.collectAsState()
    val appUpdateState by viewModel.appUpdateState.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()

    val scope = rememberCoroutineScope()

    // Google Drive SAF Document Launchers
    val driveBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val jsonPayload = viewModel.generateJsonContent()
                val writeResult = runCatching {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { stream ->
                            stream.write(jsonPayload.toByteArray(Charsets.UTF_8))
                        } ?: error("Cannot open output stream")
                    }
                }
                writeResult.onSuccess {
                    viewModel.updateLastDriveBackup()
                    Toast.makeText(context, AppStrings.get("drive_backup_success", currentLang), Toast.LENGTH_LONG).show()
                }.onFailure { e ->
                    Toast.makeText(context, "${AppStrings.get("drive_backup_failed", currentLang)}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val driveRestoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val readResult = runCatching {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            stream.bufferedReader(Charsets.UTF_8).readText()
                        } ?: error("Cannot open input stream")
                    }
                }
                readResult.onSuccess { json ->
                    viewModel.importPersonnelFromJson(json, replaceAll = false) { ok, msg ->
                        if (ok) {
                            viewModel.updateLastDriveBackup()
                            Toast.makeText(context, AppStrings.get("drive_restore_success", currentLang), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "${AppStrings.get("drive_restore_failed", currentLang)}: $msg", Toast.LENGTH_LONG).show()
                        }
                    }
                }.onFailure { e ->
                    Toast.makeText(context, "${AppStrings.get("drive_restore_failed", currentLang)}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val excelExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val result = runCatching {
                    withContext(Dispatchers.IO) {
                        ExcelXlsxExporter.writeWorkbookToUri(
                            context = context,
                            uri = uri,
                            personnelList = allPersonnelList,
                            leaveRecords = allLeavesList,
                            lang = currentLang
                        )
                    }
                }
                if (result.isSuccess) {
                    Toast.makeText(
                        context,
                        AppStrings.get("excel_export_success", currentLang),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "${AppStrings.get("excel_export_failed", currentLang)}: ${result.exceptionOrNull()?.localizedMessage.orEmpty()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var showAddLeaveDialog by remember { mutableStateOf(false) }
    var quickLeavePersonnelId by remember { mutableStateOf<Long?>(null) }
    var personnelToEdit by remember { mutableStateOf<Personnel?>(null) }
    var personnelToDelete by remember { mutableStateOf<Personnel?>(null) }
    var personnelForHistory by remember { mutableStateOf<Personnel?>(null) }
    var personnelToExtend by remember { mutableStateOf<Personnel?>(null) }
    var historyRecordToExtend by remember { mutableStateOf<Pair<Personnel, com.example.data.LeaveRecord>?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showFilterSortDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showTrashBinDialog by remember { mutableStateOf(false) }
    var lastSwipeDeletedPerson by remember { mutableStateOf<Personnel?>(null) }
    var swipeUndoTicket by remember { mutableIntStateOf(0) }
    var selectedNavPage by remember { mutableStateOf(AppNavPage.DASHBOARD) }
    var previousNavPage by remember { mutableStateOf(AppNavPage.DASHBOARD) }
    val isDynamicAnimationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()
    val isLivingLuxuryMode by viewModel.isLivingLuxuryMode.collectAsState()
    val trashedPersonnel by viewModel.trashedPersonnel.collectAsState()
    val trashedCount by viewModel.trashedCount.collectAsState()

    LaunchedEffect(swipeUndoTicket) {
        if (lastSwipeDeletedPerson != null) {
            kotlinx.coroutines.delay(4500L)
            lastSwipeDeletedPerson = null
        }
    }

    val openNotificationsPage: () -> Unit = {
        if (selectedNavPage != AppNavPage.NOTIFICATIONS) {
            previousNavPage = selectedNavPage
            selectedNavPage = AppNavPage.NOTIFICATIONS
        } else {
            selectedNavPage = previousNavPage
        }
    }

    // Notification permission launcher for Android 13+
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.checkUpcomingNotifications()
        }
    }

    LaunchedEffect(openNotificationsSignal) {
        if (openNotificationsSignal > 0) {
            if (selectedNavPage != AppNavPage.NOTIFICATIONS) {
                previousNavPage = selectedNavPage
            }
            selectedNavPage = AppNavPage.NOTIFICATIONS
        }
    }

    var resumeRefreshCount by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, isLocked) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (!isLocked) {
                        viewModel.checkForAppUpdate(manual = false)
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    resumeRefreshCount++
                    viewModel.refreshDateSensitiveState()
                    viewModel.refreshInstallPermissionState()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
            viewModel.checkForAppUpdate(manual = false)
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
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled
        )
        return
    }

    // Back navigation: Pop secondary pages (including Settings/Analytics/Notifications) back to previous or Dashboard
    BackHandler(enabled = selectedNavPage != AppNavPage.DASHBOARD) {
        selectedNavPage = if (selectedNavPage == AppNavPage.NOTIFICATIONS) previousNavPage else AppNavPage.DASHBOARD
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        val backdropLayer = rememberGraphicsLayer()
        var backdropOffsetInRoot by remember { mutableStateOf(Offset.Zero) }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_scaffold"),
            containerColor = Color.Transparent,
            topBar = {
                CompositionLocalProvider(
                    LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
                ) {
                    val topPreset = LocalThemePreset.current
                    val effectiveTopDark = topPreset?.isDark ?: isDarkMode
                    val topActiveAccent = topPreset?.primary ?: if (effectiveTopDark) CyanGlow else VibrantCyan

                    // Keep exact status bar top spacing even when system status bar is hidden (Fit to Screen)
                    val localDensity = androidx.compose.ui.platform.LocalDensity.current
                    val liveStatusBarTopPx = WindowInsets.statusBars.getTop(localDensity)
                    val ignoringVisStatusBarTopPx = WindowInsets.statusBarsIgnoringVisibility.getTop(localDensity)
                    val cutoutTopPx = WindowInsets.displayCutout.getTop(localDensity)
                    var cachedStatusBarTopPx by remember { mutableIntStateOf(0) }
                    val observedStatusBarTopPx = maxOf(liveStatusBarTopPx, ignoringVisStatusBarTopPx)
                    if (observedStatusBarTopPx > cachedStatusBarTopPx) {
                        cachedStatusBarTopPx = observedStatusBarTopPx
                    }
                    val preservedStatusBarTopDp = remember(observedStatusBarTopPx, cachedStatusBarTopPx, cutoutTopPx, isFitToScreenEnabled, localDensity) {
                        val fallbackTopPx = if (observedStatusBarTopPx == 0 && cachedStatusBarTopPx == 0 && isFitToScreenEnabled) {
                            with(localDensity) { 24.dp.roundToPx() }
                        } else {
                            0
                        }
                        val effectiveTopPx = maxOf(observedStatusBarTopPx, cachedStatusBarTopPx, cutoutTopPx, fallbackTopPx)
                        with(localDensity) { effectiveTopPx.toDp() }
                    }

                    // Progressive Fade-to-Blur curtain above & behind the floating Top Bar capsule
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    ) {
                        TopBarFadeToBlurOverlay(
                            modifier = Modifier
                                .matchParentSize()
                                .testTag("top_bar_fade_to_blur"),
                            isDarkMode = effectiveTopDark,
                            backdropLayer = backdropLayer,
                            backdropOffsetInRoot = backdropOffsetInRoot
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = preservedStatusBarTopDp)
                                .padding(horizontal = 18.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .widthIn(max = 680.dp)
                                    .fillMaxWidth()
                                    .testTag("top_bar_glass_capsule"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Start Side: Large Frameless App Logo + Bold Brand Name
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .padding(vertical = 2.dp)
                                        .testTag("app_title_row")
                                ) {
                                    val isLivingHeader = isLivingLuxuryMode && isDynamicAnimationsEnabled
                                    val headerBreath = if (isLivingHeader) {
                                        val inf = rememberInfiniteTransition(label = "livingHeaderLogo")
                                        val p by inf.animateFloat(
                                            initialValue = 0f,
                                            targetValue = 1f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
                                                repeatMode = RepeatMode.Reverse
                                            ),
                                            label = "headerLogoBreath"
                                        )
                                        p
                                    } else 0f

                                    Image(
                                        painter = painterResource(id = R.drawable.app_logo),
                                        contentDescription = AppStrings.get("app_icon_desc", currentLang),
                                        contentScale = ContentScale.Fit,
                                        colorFilter = ColorFilter.tint(topActiveAccent),
                                        modifier = Modifier
                                            .size(width = 56.dp, height = 46.dp)
                                            .graphicsLayer {
                                                if (isLivingHeader) {
                                                    val s = 1f + 0.042f * headerBreath
                                                    scaleX = s
                                                    scaleY = s
                                                }
                                            }
                                            .testTag("top_bar_app_logo")
                                    )

                                    val isArabicTitle = currentLang == AppLanguage.ARABIC
                                    Text(
                                        text = AppStrings.get("app_title", currentLang),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = if (isArabicTitle) KufiArabicBrandFontFamily else androidx.compose.ui.text.font.FontFamily.Default,
                                            fontSize = if (isArabicTitle) 22.sp else 21.sp,
                                            lineHeight = if (isArabicTitle) 27.sp else 26.sp
                                        ),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = topPreset?.textPrimary ?: if (effectiveTopDark) Color.White else SlateTextPrimary,
                                        maxLines = 1
                                    )
                                }

                                // End Side: Frosted Glass Notification & Theme Toggle Squares
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val hasAvailableUpdate = appUpdateState is AppUpdateState.UpdateAvailable ||
                                        appUpdateState is AppUpdateState.Downloading ||
                                        appUpdateState is AppUpdateState.ReadyToInstall

                                    AnimatedVisibility(
                                        visible = hasAvailableUpdate,
                                        enter = fadeIn(tween(260)) + scaleIn(
                                            initialScale = 0.72f,
                                            animationSpec = spring(
                                                dampingRatio = 0.65f,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        ),
                                        exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.75f)
                                    ) {
                                        HeaderUpdateDownloadButton(
                                            updateState = appUpdateState,
                                            isDarkMode = isDarkMode,
                                            animationsEnabled = isDynamicAnimationsEnabled,
                                            contentDescription = AppStrings.get("update_top_banner_desc", currentLang),
                                            onClick = { viewModel.openUpdateDialog() }
                                        )
                                    }

                                    val isNotifOpen = selectedNavPage == AppNavPage.NOTIFICATIONS
                                    val iconTileShape = RoundedCornerShape(14.dp)
                                    val iconInk = topPreset?.textPrimary ?: if (effectiveTopDark) Color.White else SlateTextPrimary
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .liquidGlass(
                                                shape = iconTileShape,
                                                elevation = 3.dp,
                                                isDark = effectiveTopDark,
                                                cornerRadius = 14.dp
                                            )
                                            .clickable { openNotificationsPage() }
                                            .testTag("header_notifications_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BadgedBox(
                                            badge = {
                                                if (notificationsCount > 0) {
                                                    Badge(
                                                        containerColor = RedDanger,
                                                        contentColor = Color.White
                                                    ) {
                                                        Text(
                                                            text = if (notificationsCount > 99) "99+" else "$notificationsCount",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isNotifOpen) Icons.Default.NotificationsActive else Icons.Outlined.Notifications,
                                                contentDescription = AppStrings.get("header_notifications_desc", currentLang),
                                                tint = if (isNotifOpen) RedDanger else iconInk,
                                                modifier = Modifier.size(21.dp)
                                            )
                                        }
                                    }

                                    val isCustomThemeActive = topPreset != null
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .liquidGlass(
                                                shape = iconTileShape,
                                                elevation = 3.dp,
                                                isDark = effectiveTopDark,
                                                cornerRadius = 14.dp
                                            )
                                            .clickable { viewModel.cycleTopBarThemeMode() }
                                            .testTag("header_theme_toggle"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isCustomThemeActive -> Icons.Outlined.Palette
                                                isDarkMode -> Icons.Outlined.DarkMode
                                                else -> Icons.Outlined.WbSunny
                                            },
                                            contentDescription = when {
                                                isCustomThemeActive -> AppStrings.get("theme_custom_mode", currentLang)
                                                isDarkMode -> AppStrings.get("theme_dark", currentLang)
                                                else -> AppStrings.get("theme_light", currentLang)
                                            },
                                            tint = when {
                                                isCustomThemeActive -> topPreset.primary
                                                isDarkMode -> AmberWarning
                                                else -> iconInk
                                            },
                                            modifier = Modifier.size(21.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
                        floatingActionButton = {
                            if (selectedNavPage == AppNavPage.RECORDS) {
                                val fabPreset = LocalThemePreset.current
                                val effectiveFabDark = fabPreset?.isDark ?: isDarkMode
                                val fabActiveAccent = fabPreset?.primary ?: if (effectiveFabDark) CyanGlow else EmeraldSuccess
                                val fabHaptic = LocalHapticFeedback.current
                                val fabIconTint = if (fabActiveAccent.luminance() > 0.45f) Color(0xFF0F172A) else Color.White

                                Surface(
                                    onClick = {
                                        fabHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        quickLeavePersonnelId = null
                                        showAddLeaveDialog = true
                                    },
                                    shape = CircleShape,
                                    color = fabActiveAccent,
                                    shadowElevation = 5.dp,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .testTag("add_personnel_fab")
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = AppStrings.get("add_leave_dialog_title", currentLang),
                                            tint = fabIconTint,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        bottomBar = {
                            AppBottomNavigationPanel(
                                selectedPage = selectedNavPage,
                                onSelectPage = { selectedNavPage = it },
                                isDarkMode = isDarkMode,
                                animationsEnabled = isDynamicAnimationsEnabled,
                                currentLang = currentLang,
                                backdropLayer = backdropLayer,
                                backdropOffsetInRoot = backdropOffsetInRoot
                            )
                        }
                    ) { innerPadding ->
                        val topBarClearance = innerPadding.calculateTopPadding()
                        val bottomDockClearance = innerPadding.calculateBottomPadding()
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .onGloballyPositioned { backdropOffsetInRoot = it.positionInRoot() }
                                .drawWithContent {
                                    backdropLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    drawLayer(backdropLayer)
                                }
                        ) {
                            // Ambient Liquid Dynamic Aurora Mesh Background captured inside backdropLayer
                            DynamicAuroraBackground(
                                isDark = isDarkMode,
                                animationsEnabled = isDynamicAnimationsEnabled
                            )

                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                            val isTablet = maxWidth >= 600.dp
                            val contentMaxWidth = if (isTablet) 980.dp else 680.dp
                            val horizontalPadding = if (isTablet) 24.dp else 16.dp
                            val contentPadding = PaddingValues(
                                start = horizontalPadding,
                                end = horizontalPadding,
                                top = topBarClearance + 10.dp,
                                bottom = 14.dp
                            )

                            val saveableStateHolder = rememberSaveableStateHolder()
                            val dashboardListState = rememberLazyListState()
                            val recordsListState = rememberLazyListState()

                            Crossfade(
                                targetState = selectedNavPage,
                                animationSpec = tween(
                                    durationMillis = if (isDynamicAnimationsEnabled) 115 else 0,
                                    easing = FastOutSlowInEasing
                                ),
                                label = "mainNavCrossfade"
                            ) { currentPage ->
                                saveableStateHolder.SaveableStateProvider(key = currentPage) {
                                    when (currentPage) {
                                        AppNavPage.DATABASE -> {
                                            RoomDatabaseScreen(
                                                viewModel = viewModel,
                                                onBack = { selectedNavPage = AppNavPage.DASHBOARD },
                                                topBarClearance = topBarClearance,
                                                bottomDockClearance = bottomDockClearance
                                            )
                                        }
                                        AppNavPage.ANALYTICS -> {
                                            AnalyticsDashboardScreen(
                                                viewModel = viewModel,
                                                onBack = { selectedNavPage = AppNavPage.DASHBOARD },
                                                onSelectPersonnel = { personnelId ->
                                                    val p = allPersonnelList.find { it.id == personnelId }
                                                    if (p != null) {
                                                        personnelToEdit = p
                                                        showAddEditDialog = true
                                                    }
                                                },
                                                topBarClearance = topBarClearance,
                                                bottomDockClearance = bottomDockClearance
                                            )
                                        }
                                        AppNavPage.NOTIFICATIONS -> {
                                            NotificationScreen(
                                                viewModel = viewModel,
                                                onBack = { selectedNavPage = previousNavPage },
                                                onSelectPersonnel = { personnelId ->
                                                    val p = allPersonnelList.find { it.id == personnelId }
                                                    if (p != null) {
                                                        personnelToEdit = p
                                                        showAddEditDialog = true
                                                    }
                                                },
                                                topBarClearance = topBarClearance,
                                                bottomDockClearance = bottomDockClearance
                                            )
                                        }
                                        AppNavPage.SETTINGS -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .widthIn(max = contentMaxWidth)
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
                                                    },
                                                    onCheckNotifications = { openNotificationsPage() },
                                                    onExportPdf = { viewModel.exportPdf(context) },
                                                    onExportExcel = {
                                                        excelExportLauncher.launch(ExcelXlsxExporter.defaultFileName())
                                                    },
                                                    onOpenBackup = { showExportDialog = true },
                                                    onOpenRoomDatabase = { selectedNavPage = AppNavPage.DATABASE },
                                                    onToggleTheme = { viewModel.toggleTheme() },
                                                    onOpenDeveloper = { showDeveloperDialog = true },
                                                    onResetSampleData = { showResetConfirmDialog = true },
                                                    currentThemeMode = currentThemeMode,
                                                    onSelectThemeMode = { viewModel.setThemeMode(it) },
                                                    selectedThemeEngineId = selectedThemeEngineId,
                                                    onSelectThemeEngine = { viewModel.setThemeEngine(it) },
                                                    dutyThreshold = dutyThreshold,
                                                    onSaveThreshold = { viewModel.setDutyThreshold(it) },
                                                    isBiometricEnabled = isBiometricEnabled,
                                                    onToggleBiometric = { enabled -> viewModel.setBiometricLock(enabled) },
                                                    isNoScreenshotEnabled = isNoScreenshotEnabled,
                                                    onToggleNoScreenshot = { enabled -> viewModel.setNoScreenshotEnabled(enabled) },
                                                    onLockNow = { viewModel.lockApp() },
                                                    isDynamicAnimationsEnabled = isDynamicAnimationsEnabled,
                                                    onToggleDynamicAnimations = { viewModel.setDynamicAnimationsEnabled(it) },
                                                    isLivingLuxuryMode = isLivingLuxuryMode,
                                                    onToggleLivingLuxuryMode = { viewModel.setLivingLuxuryMode(it) },
                                                    isFitToScreenEnabled = isFitToScreenEnabled,
                                                    onToggleFitToScreen = { viewModel.setFitToScreenEnabled(it) },
                                                    trashedCount = trashedCount,
                                                    onOpenTrashBin = { showTrashBinDialog = true },
                                                    isDriveSyncEnabled = isDriveSyncEnabled,
                                                    onToggleDriveSync = { viewModel.setDriveSyncEnabled(it) },
                                                    lastDriveBackupTime = lastDriveBackupTime,
                                                    onBackupToDrive = {
                                                        driveBackupLauncher.launch("respite_pulse_backup_${System.currentTimeMillis()}.json")
                                                    },
                                                    onRestoreFromDrive = {
                                                        driveRestoreLauncher.launch(arrayOf("application/json", "text/*"))
                                                    },
                                                    drawerIsOpen = (selectedNavPage == AppNavPage.SETTINGS),
                                                    onCheckUpdate = { viewModel.checkForAppUpdate(manual = true) },
                                                    availableUpdateVersion = (appUpdateState as? AppUpdateState.UpdateAvailable)?.versionName,
                                                    isFullScreenPage = true,
                                                    topBarClearance = topBarClearance,
                                                    bottomDockClearance = bottomDockClearance
                                                )
                                            }
                                        }
                                        AppNavPage.DASHBOARD -> {
                                            val activeDashboardFilterCount = (if (selectedStatus != "ALL") 1 else 0) +
                                                (if (selectedRank != "ALL") 1 else 0) +
                                                (if (currentSort != SortOption.RETURN_SOON) 1 else 0)

                                            LazyColumn(
                                                state = dashboardListState,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .widthIn(max = contentMaxWidth)
                                                    .testTag("dashboard_lazy_column"),
                                                contentPadding = contentPadding,
                                                verticalArrangement = Arrangement.spacedBy(14.dp)
                                            ) {
                                        // 1. Greeting + Personnel Overview + Date Glass Pill
                                        item {
                                            val effectiveDashDark = activeThemePreset?.isDark ?: isDarkMode
                                            val dashTextPrimary = activeThemePreset?.textPrimary
                                                ?: if (effectiveDashDark) Color.White else SlateTextPrimary
                                            val dashTextSecondary = activeThemePreset?.textSecondary
                                                ?: if (effectiveDashDark) Color(0xFF94A3B8) else SlateTextSecondary
                                            val greetingKey = remember(todayMillis, resumeRefreshCount) {
                                                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                                                when {
                                                    hour < 12 -> "greeting_morning"
                                                    hour < 17 -> "greeting_afternoon"
                                                    else -> "greeting_evening"
                                                }
                                            }
                                            val formattedDatePill = remember(currentLang, todayMillis, resumeRefreshCount) {
                                                val locale = when (currentLang) {
                                                    AppLanguage.ARABIC -> Locale.forLanguageTag("ar")
                                                    AppLanguage.FRENCH -> Locale.FRENCH
                                                    AppLanguage.TAMAZIGHT -> Locale.FRENCH
                                                    AppLanguage.ENGLISH -> Locale.ENGLISH
                                                }
                                                SimpleDateFormat("EEE, MMM d", locale)
                                                    .format(Date())
                                                    .uppercase(locale)
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 2.dp, vertical = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f),
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Text(
                                                        text = AppStrings.get(greetingKey, currentLang),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = dashTextSecondary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = AppStrings.get("personnel_overview_title", currentLang),
                                                        fontSize = 24.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = dashTextPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Box(
                                                    modifier = Modifier
                                                        .liquidGlass(
                                                            shape = CircleShape,
                                                            elevation = 2.dp,
                                                            isDark = effectiveDashDark,
                                                            cornerRadius = 50.dp
                                                        )
                                                        .padding(horizontal = 13.dp, vertical = 7.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = formattedDatePill,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 0.4.sp,
                                                        color = dashTextSecondary,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }

                                        // 2. Overview Frosted Glass Card
                                        item {
                                            StatisticsSection(
                                                stats = stats,
                                                currentLang = currentLang,
                                                selectedStatus = selectedStatus,
                                                onSelectStatus = {
                                                    viewModel.selectedStatus.value = it
                                                    selectedNavPage = AppNavPage.RECORDS
                                                },
                                                onOpenAnalytics = {
                                                    selectedNavPage = AppNavPage.ANALYTICS
                                                },
                                                isDarkMode = isDarkMode,
                                                animationsEnabled = isDynamicAnimationsEnabled
                                            )
                                        }

                                        // 3. 3 Quick Action Glass Buttons (+ Add person, Urgent alerts, Room DB)
                                        item {
                                            DashboardQuickActionRow(
                                                urgentCount = stats.overdue + stats.returningToday,
                                                currentLang = currentLang,
                                                isDarkMode = isDarkMode,
                                                onAddPersonnel = {
                                                    personnelToEdit = null
                                                    showAddEditDialog = true
                                                },
                                                onOpenUrgentAlerts = {
                                                    if (selectedNavPage != AppNavPage.NOTIFICATIONS) {
                                                        previousNavPage = selectedNavPage
                                                    }
                                                    selectedNavPage = AppNavPage.NOTIFICATIONS
                                                },
                                                onOpenDatabase = {
                                                    selectedNavPage = AppNavPage.DATABASE
                                                }
                                            )
                                        }

                                        // 4. Frosted Glass Search & Filter Bar
                                        item {
                                            DashboardPillSearchBar(
                                                searchQuery = searchQuery,
                                                onSearchQueryChange = {
                                                    viewModel.searchQuery.value = it
                                                    if (it.trim().length >= 3) {
                                                        viewModel.addRecentSearch(it)
                                                    }
                                                },
                                                onSubmitSearch = {
                                                    if (searchQuery.isNotBlank()) {
                                                        viewModel.addRecentSearch(searchQuery)
                                                    }
                                                },
                                                onOpenFilter = {
                                                    showFilterSortDialog = true
                                                },
                                                recentSearches = recentSearches,
                                                onSelectRecentSearch = { queryItem ->
                                                    viewModel.searchQuery.value = queryItem
                                                    viewModel.addRecentSearch(queryItem)
                                                },
                                                onRemoveRecentSearch = { queryItem ->
                                                    viewModel.removeRecentSearch(queryItem)
                                                },
                                                onClearRecentSearches = {
                                                    viewModel.clearRecentSearches()
                                                },
                                                activeFilterCount = activeDashboardFilterCount,
                                                isDarkMode = isDarkMode,
                                                currentLang = currentLang,
                                                searchFieldTestTag = "search_field",
                                                filterButtonTestTag = "filter_sort_dialog_button"
                                            )
                                        }

                                        // 5. Section Header ("Pinned Persons (X / Y)" + "View all")
                                        val pinnedDashboardList = personnelList.filter { it.isPinned }
                                        val totalPinnedCount = allPersonnelList.count { it.isPinned }

                                        item {
                                            val effectiveDashDark = activeThemePreset?.isDark ?: isDarkMode
                                            val dashTextPrimary = activeThemePreset?.textPrimary
                                                ?: if (effectiveDashDark) Color.White else SlateTextPrimary
                                            val dashTextSecondary = activeThemePreset?.textSecondary
                                                ?: if (effectiveDashDark) Color(0xFF94A3B8) else SlateTextSecondary
                                            val dashAccent = activeThemePreset?.primary
                                                ?: if (effectiveDashDark) CyanGlow else VibrantCyan

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 2.dp, vertical = 2.dp)
                                                    .testTag("dashboard_pinned_persons_header"),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PushPin,
                                                        contentDescription = null,
                                                        tint = AmberWarning,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = AppStrings.get("pinned_persons_title", currentLang),
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = dashTextPrimary
                                                    )
                                                    Text(
                                                        text = "(${pinnedDashboardList.size} / $totalPinnedCount)",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = dashTextSecondary
                                                    )
                                                }

                                                TextButton(
                                                    onClick = { selectedNavPage = AppNavPage.RECORDS },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("dashboard_view_all_button")
                                                ) {
                                                    Text(
                                                        text = AppStrings.get("view_all", currentLang),
                                                        fontSize = 13.5.sp,
                                                        color = dashAccent,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Pinned Records List ONLY
                                        if (pinnedDashboardList.isEmpty()) {
                                            item {
                                                val effectiveDashDark = activeThemePreset?.isDark ?: isDarkMode
                                                val dashAccent = activeThemePreset?.primary
                                                    ?: if (effectiveDashDark) CyanGlow else VibrantCyan
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .liquidGlass(
                                                            shape = LiquidGlassDefaults.CardShape,
                                                            elevation = 4.dp,
                                                            isDark = effectiveDashDark
                                                        )
                                                        .padding(24.dp)
                                                        .testTag("dashboard_pinned_empty_card"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (totalPinnedCount > 0) Icons.Default.FilterList else Icons.Default.PushPin,
                                                            contentDescription = null,
                                                            tint = if (totalPinnedCount > 0) {
                                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                                                            } else {
                                                                AmberWarning.copy(alpha = 0.85f)
                                                            },
                                                            modifier = Modifier.size(40.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = AppStrings.get(
                                                                if (totalPinnedCount > 0) "no_results" else "no_pinned_persons",
                                                                currentLang
                                                            ),
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = AppStrings.get(
                                                                if (totalPinnedCount > 0) "no_results_desc" else "no_pinned_persons_desc",
                                                                currentLang
                                                            ),
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        if (totalPinnedCount == 0 && allPersonnelList.isNotEmpty()) {
                                                            TextButton(
                                                                onClick = { selectedNavPage = AppNavPage.RECORDS },
                                                                modifier = Modifier.padding(top = 2.dp)
                                                            ) {
                                                                Text(
                                                                    text = AppStrings.get("view_all", currentLang),
                                                                    color = dashAccent,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            items(
                                                items = pinnedDashboardList,
                                                key = { it.id },
                                                contentType = { "personnel_card" }
                                            ) { person ->
                                                PersonnelCard(
                                                    personnel = person,
                                                    dutyThresholdDays = dutyThreshold,
                                                    modifier = Modifier.animateItem(
                                                        placementSpec = if (isDynamicAnimationsEnabled) {
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                                stiffness = 380f,
                                                                visibilityThreshold = IntOffset.VisibilityThreshold
                                                            )
                                                        } else null
                                                    ),
                                                    isDarkMode = isDarkMode,
                                                    animationsEnabled = isDynamicAnimationsEnabled,
                                                    currentLang = currentLang,
                                                    effectiveDaysSinceDuty = dutyReferenceMap[person.id]?.daysSinceReference ?: person.daysSinceDutyReturn,
                                                    lastShortLeaveInfo = lastShortLeaveMap[person.id],
                                                    onTogglePin = {
                                                        val willPin = !person.isPinned
                                                        viewModel.togglePinPersonnel(person)
                                                        if (willPin && dashboardListState.firstVisibleItemIndex > 4) {
                                                            scope.launch { dashboardListState.animateScrollToItem(4) }
                                                        }
                                                    },
                                                    onSwipeDelete = {
                                                        viewModel.deletePersonnel(person)
                                                        lastSwipeDeletedPerson = person
                                                        swipeUndoTicket++
                                                    },
                                                    onConfirmDuty = {
                                                        viewModel.confirmReturnToDuty(person.id)
                                                        Toast.makeText(
                                                            context,
                                                            "${AppStrings.get("confirm_duty_btn", currentLang)}: ${formatRank(person.rank, currentLang)} / ${person.getLocalizedName(currentLang)}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                    onEdit = {
                                                        personnelToEdit = person
                                                        showAddEditDialog = true
                                                    },
                                                    onDelete = {
                                                        personnelToDelete = person
                                                    },
                                                    onViewHistory = {
                                                        personnelForHistory = person
                                                    },
                                                    onExtendLeave = {
                                                        personnelToExtend = person
                                                    },
                                                    onQuickAddLeave = {
                                                        quickLeavePersonnelId = person.id
                                                        showAddLeaveDialog = true
                                                    }
                                                )
                                            }
                                        }

                                        item {
                                            Spacer(modifier = Modifier.height(maxOf(bottomDockClearance + 24.dp, 112.dp)))
                                        }
                                    }
                                }
                                        AppNavPage.RECORDS -> {
                                            val activeFilterCount = (if (selectedStatus != "ALL") 1 else 0) +
                                                    (if (selectedRank != "ALL") 1 else 0) +
                                                    (if (currentSort != SortOption.RETURN_SOON) 1 else 0)
                                            val chunkedPersonnel = remember(personnelList) { personnelList.chunked(2) }

                                            LazyColumn(
                                                state = recordsListState,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .widthIn(max = contentMaxWidth)
                                                    .testTag("records_lazy_column"),
                                                contentPadding = contentPadding,
                                                verticalArrangement = Arrangement.spacedBy(14.dp)
                                            ) {
                                        // Full Pill Search Bar with Filter & Sort dialog trigger
                                        item {
                                            DashboardPillSearchBar(
                                                searchQuery = searchQuery,
                                                onSearchQueryChange = {
                                                    viewModel.searchQuery.value = it
                                                    if (it.trim().length >= 3) {
                                                        viewModel.addRecentSearch(it)
                                                    }
                                                },
                                                onSubmitSearch = {
                                                    if (searchQuery.isNotBlank()) {
                                                        viewModel.addRecentSearch(searchQuery)
                                                    }
                                                },
                                                onOpenFilter = { showFilterSortDialog = true },
                                                recentSearches = recentSearches,
                                                onSelectRecentSearch = { queryItem ->
                                                    viewModel.searchQuery.value = queryItem
                                                    viewModel.addRecentSearch(queryItem)
                                                },
                                                onRemoveRecentSearch = { queryItem ->
                                                    viewModel.removeRecentSearch(queryItem)
                                                },
                                                onClearRecentSearches = {
                                                    viewModel.clearRecentSearches()
                                                },
                                                activeFilterCount = activeFilterCount,
                                                isDarkMode = isDarkMode,
                                                currentLang = currentLang,
                                                searchFieldTestTag = "search_field_records",
                                                filterButtonTestTag = "filter_sort_dialog_button_records"
                                            )
                                        }

                                        // Inline 1-Tap Liquid Glass Quick Status Filter Strip
                                        item {
                                            val primaryAccent = activeThemePreset?.primary ?: if (isDarkMode) CyanGlow else VibrantCyan
                                            val quickFilterItems = remember(stats, currentLang, primaryAccent) {
                                                listOf(
                                                    Triple("ALL", AppStrings.get("status_all", currentLang) to Icons.Default.Person, stats.total to primaryAccent),
                                                    Triple("ON_DUTY", stripLeadingEmojiPrefix(AppStrings.get("status_on_duty", currentLang)) to Icons.Default.CheckCircle, stats.onDuty to EmeraldSuccess),
                                                    Triple("AT_HOME", stripLeadingEmojiPrefix(AppStrings.get("status_at_home", currentLang)) to Icons.Default.Home, stats.atHome to primaryAccent),
                                                    Triple("RETURNING_TODAY", stripLeadingEmojiPrefix(AppStrings.get("status_returning_today", currentLang)) to Icons.Default.NotificationsActive, stats.returningToday to AmberWarning),
                                                    Triple("OVERDUE", stripLeadingEmojiPrefix(AppStrings.get("status_overdue", currentLang)) to Icons.Default.Warning, stats.overdue to RedDanger),
                                                    Triple("ELIGIBLE", stripLeadingEmojiPrefix(AppStrings.get("status_eligible", currentLang)) to Icons.Default.Star, stats.eligible to AmberWarning)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState())
                                                    .testTag("records_quick_filter_strip"),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                quickFilterItems.forEach { (statusKey, labelAndIcon, countAndAccent) ->
                                                    val (labelText, iconVec) = labelAndIcon
                                                    val (countVal, chipAccent) = countAndAccent
                                                    val isChipSelected = selectedStatus == statusKey
                                                    Box(
                                                        modifier = Modifier
                                                            .defaultMinSize(minHeight = 42.dp)
                                                            .liquidGlassPanel(
                                                                shape = CircleShape,
                                                                cornerRadius = 21.dp,
                                                                isDark = isDarkMode,
                                                                accent = chipAccent,
                                                                secondaryAccent = activeThemePreset?.secondary ?: RoyalBlueAccent,
                                                                isSelected = isChipSelected
                                                            )
                                                            .clickable {
                                                                viewModel.selectedStatus.value = statusKey
                                                            }
                                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                                            .testTag("records_quick_filter_$statusKey"),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = iconVec,
                                                                contentDescription = null,
                                                                tint = if (isChipSelected) chipAccent else if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF475569),
                                                                modifier = Modifier.size(15.dp)
                                                            )
                                                            Text(
                                                                text = labelText,
                                                                fontSize = 11.5.sp,
                                                                fontWeight = if (isChipSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                                color = if (isChipSelected) {
                                                                    if (isDarkMode) Color.White else Color(0xFF0F172A)
                                                                } else {
                                                                    if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF334155)
                                                                },
                                                                maxLines = 1
                                                            )
                                                            Box(
                                                                modifier = Modifier
                                                                    .background(
                                                                        color = chipAccent.copy(alpha = if (isChipSelected) 0.22f else 0.12f),
                                                                        shape = CircleShape
                                                                    )
                                                                    .padding(horizontal = 6.dp, vertical = 1.dp),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = "$countVal",
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.ExtraBold,
                                                                    color = chipAccent
                                                                )
                                                            }
                                                        }
                                                    }
                                                }

                                                if (activeFilterCount > 0) {
                                                    Box(
                                                        modifier = Modifier
                                                            .defaultMinSize(minHeight = 42.dp)
                                                            .liquidGlassPanel(
                                                                shape = CircleShape,
                                                                cornerRadius = 21.dp,
                                                                isDark = isDarkMode,
                                                                accent = CoralRedAccent,
                                                                isSelected = true
                                                            )
                                                            .clickable {
                                                                viewModel.selectedStatus.value = "ALL"
                                                                viewModel.selectedRank.value = "ALL"
                                                                viewModel.sortOption.value = SortOption.RETURN_SOON
                                                            }
                                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                                            .testTag("records_quick_filter_reset"),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Refresh,
                                                                contentDescription = null,
                                                                tint = CoralRedAccent,
                                                                modifier = Modifier.size(15.dp)
                                                            )
                                                            Text(
                                                                text = AppStrings.get("reset_filters", currentLang),
                                                                fontSize = 11.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = CoralRedAccent,
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Results Count & Quick Trash Bin Indicator
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

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .defaultMinSize(minHeight = 40.dp)
                                                            .liquidGlassPanel(
                                                                shape = RoundedCornerShape(50.dp),
                                                                cornerRadius = 50.dp,
                                                                isDark = isDarkMode,
                                                                accent = CoralRedAccent,
                                                                isSelected = trashedCount > 0
                                                            )
                                                            .clickable { showTrashBinDialog = true }
                                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                                            .testTag("records_trash_bin_button"),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.DeleteSweep,
                                                                contentDescription = AppStrings.get("trash_bin_title", currentLang),
                                                                tint = CoralRedAccent,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Text(
                                                                text = if (trashedCount > 0) {
                                                                    "${AppStrings.get("trash_bin_title", currentLang)} ($trashedCount)"
                                                                } else {
                                                                    AppStrings.get("trash_bin_title", currentLang)
                                                                },
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = CoralRedAccent
                                                            )
                                                        }
                                                    }

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
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
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
                                            items(
                                                items = chunkedPersonnel,
                                                key = { pair -> pair.joinToString("-") { it.id.toString() } },
                                                contentType = { "personnel_tablet_row" }
                                            ) { rowItems ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .animateItem(
                                                            placementSpec = if (isDynamicAnimationsEnabled) {
                                                                spring(
                                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                                    stiffness = 380f,
                                                                    visibilityThreshold = IntOffset.VisibilityThreshold
                                                                )
                                                            } else null
                                                        ),
                                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                                ) {
                                                    for (person in rowItems) {
                                                        androidx.compose.runtime.key(person.id) {
                                                            Box(modifier = Modifier.weight(1f)) {
                                                                PersonnelCard(
                                                                    personnel = person,
                                                                    dutyThresholdDays = dutyThreshold,
                                                                    isDarkMode = isDarkMode,
                                                                    animationsEnabled = isDynamicAnimationsEnabled,
                                                                    currentLang = currentLang,
                                                                    effectiveDaysSinceDuty = dutyReferenceMap[person.id]?.daysSinceReference ?: person.daysSinceDutyReturn,
                                                                    lastShortLeaveInfo = lastShortLeaveMap[person.id],
                                                                    onTogglePin = {
                                                                        val willPin = !person.isPinned
                                                                        viewModel.togglePinPersonnel(person)
                                                                        if (willPin && recordsListState.firstVisibleItemIndex > 2) {
                                                                            scope.launch { recordsListState.animateScrollToItem(2) }
                                                                        }
                                                                    },
                                                                    onSwipeDelete = {
                                                                        viewModel.deletePersonnel(person)
                                                                        lastSwipeDeletedPerson = person
                                                                        swipeUndoTicket++
                                                                    },
                                                                    onConfirmDuty = {
                                                                        viewModel.confirmReturnToDuty(person.id)
                                                                        Toast.makeText(
                                                                            context,
                                                                            "${AppStrings.get("confirm_duty_btn", currentLang)}: ${formatRank(person.rank, currentLang)} / ${person.getLocalizedName(currentLang)}",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    },
                                                                    onEdit = {
                                                                        personnelToEdit = person
                                                                        showAddEditDialog = true
                                                                    },
                                                                    onDelete = {
                                                                        personnelToDelete = person
                                                                    },
                                                                    onViewHistory = {
                                                                        personnelForHistory = person
                                                                    },
                                                                    onExtendLeave = {
                                                                        personnelToExtend = person
                                                                    },
                                                                    onQuickAddLeave = {
                                                                        quickLeavePersonnelId = person.id
                                                                        showAddLeaveDialog = true
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                    if (rowItems.size == 1) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                    }
                                                }
                                            }
                                        } else {
                                            items(
                                                items = personnelList,
                                                key = { it.id },
                                                contentType = { "personnel_card" }
                                            ) { person ->
                                                PersonnelCard(
                                                    personnel = person,
                                                    dutyThresholdDays = dutyThreshold,
                                                    modifier = Modifier.animateItem(
                                                        placementSpec = if (isDynamicAnimationsEnabled) {
                                                            spring(
                                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                                stiffness = 380f,
                                                                visibilityThreshold = IntOffset.VisibilityThreshold
                                                            )
                                                        } else null
                                                    ),
                                                    isDarkMode = isDarkMode,
                                                    animationsEnabled = isDynamicAnimationsEnabled,
                                                    currentLang = currentLang,
                                                    effectiveDaysSinceDuty = dutyReferenceMap[person.id]?.daysSinceReference ?: person.daysSinceDutyReturn,
                                                    lastShortLeaveInfo = lastShortLeaveMap[person.id],
                                                    onTogglePin = {
                                                        val willPin = !person.isPinned
                                                        viewModel.togglePinPersonnel(person)
                                                        if (willPin && recordsListState.firstVisibleItemIndex > 2) {
                                                            scope.launch { recordsListState.animateScrollToItem(2) }
                                                        }
                                                    },
                                                    onSwipeDelete = {
                                                        viewModel.deletePersonnel(person)
                                                        lastSwipeDeletedPerson = person
                                                        swipeUndoTicket++
                                                    },
                                                    onConfirmDuty = {
                                                        viewModel.confirmReturnToDuty(person.id)
                                                        Toast.makeText(
                                                            context,
                                                            "${AppStrings.get("confirm_duty_btn", currentLang)}: ${formatRank(person.rank, currentLang)} / ${person.getLocalizedName(currentLang)}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                    onEdit = {
                                                        personnelToEdit = person
                                                        showAddEditDialog = true
                                                    },
                                                    onDelete = {
                                                        personnelToDelete = person
                                                    },
                                                    onViewHistory = {
                                                        personnelForHistory = person
                                                    },
                                                    onExtendLeave = {
                                                        personnelToExtend = person
                                                    },
                                                    onQuickAddLeave = {
                                                        quickLeavePersonnelId = person.id
                                                        showAddLeaveDialog = true
                                                    }
                                                )
                                            }
                                        }

                                            item {
                                                Spacer(modifier = Modifier.height(maxOf(bottomDockClearance + 24.dp, 112.dp)))
                                            }
                                        }
                                    }
                                }
                            }

                            // Floating Undo Bar when a personnel card is swipe-deleted into Trash Bin
                            AnimatedVisibility(
                                visible = lastSwipeDeletedPerson != null,
                                enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.92f),
                                exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.92f),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 14.dp,
                                        bottom = bottomDockClearance + 14.dp
                                    )
                            ) {
                                lastSwipeDeletedPerson?.let { deletedPerson ->
                                    Box(
                                        modifier = Modifier
                                            .widthIn(max = 520.dp)
                                            .fillMaxWidth()
                                            .liquidGlassDialogShell(
                                                shape = RoundedCornerShape(20.dp),
                                                cornerRadius = 20.dp,
                                                elevation = 14.dp,
                                                isDark = isDarkMode,
                                                primaryAccent = CoralRedAccent,
                                                secondaryAccent = EmeraldSuccess
                                            )
                                            .testTag("swipe_delete_undo_banner")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
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
                                                        .size(34.dp)
                                                        .liquidGlassPanel(
                                                            shape = CircleShape,
                                                            cornerRadius = 17.dp,
                                                            isDark = isDarkMode,
                                                            accent = CoralRedAccent,
                                                            isSelected = true
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeleteSweep,
                                                        contentDescription = null,
                                                        tint = CoralRedAccent,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Text(
                                                    text = "${deletedPerson.getLocalizedName(currentLang)} — ${AppStrings.get("trash_moved_toast", currentLang)}",
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .defaultMinSize(minHeight = 40.dp)
                                                        .liquidGlassPanel(
                                                            shape = CircleShape,
                                                            cornerRadius = 20.dp,
                                                            isDark = isDarkMode,
                                                            accent = EmeraldSuccess,
                                                            isSelected = true
                                                        )
                                                        .clickable {
                                                            viewModel.restorePersonnelFromTrash(deletedPerson)
                                                            lastSwipeDeletedPerson = null
                                                            Toast.makeText(context, AppStrings.get("trash_restored_toast", currentLang), Toast.LENGTH_SHORT).show()
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                        .testTag("swipe_delete_undo_button"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.AutoMirrored.Filled.Undo,
                                                            contentDescription = null,
                                                            tint = EmeraldSuccess,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = AppStrings.get("undo_short", currentLang),
                                                            color = EmeraldSuccess,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 11.5.sp
                                                        )
                                                    }
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .defaultMinSize(minHeight = 40.dp)
                                                        .liquidGlassPanel(
                                                            shape = CircleShape,
                                                            cornerRadius = 20.dp,
                                                            isDark = isDarkMode,
                                                            accent = VibrantCyan,
                                                            isSelected = false
                                                        )
                                                        .clickable {
                                                            lastSwipeDeletedPerson = null
                                                            showTrashBinDialog = true
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                        .testTag("swipe_delete_open_trash_button"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = AppStrings.get("trash_bin_title", currentLang),
                                                        color = if (isDarkMode) CyanGlow else RoyalBlueAccent,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
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

    // Add / Edit Personnel Dialog
    if (showAddEditDialog) {
        AddEditPersonnelDialog(
            personnelToEdit = personnelToEdit,
            onDismiss = {
                showAddEditDialog = false
                personnelToEdit = null
            },
            onSave = { id, fullName, fullNameLatin, rank, regNumber, startMillis, returnMillis, isDuty, leaveType, wilaya, notes ->
                viewModel.savePersonnel(
                    id = id,
                    fullName = fullName,
                    fullNameLatin = fullNameLatin,
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
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Add New Leave Dialog (Triggered by circular + FAB or PersonnelCard emerald quick-action)
    if (showAddLeaveDialog) {
        AddNewLeaveDialog(
            personnelList = allPersonnelList,
            initialPersonnelId = quickLeavePersonnelId,
            onDismiss = {
                showAddLeaveDialog = false
                quickLeavePersonnelId = null
            },
            onOpenAddPersonnel = {
                personnelToEdit = null
                showAddEditDialog = true
            },
            onExtendCurrentLeave = { target ->
                showAddLeaveDialog = false
                quickLeavePersonnelId = null
                personnelToExtend = target
            },
            onConfirm = { personnelId, leaveType, startMillis, returnMillis ->
                viewModel.grantNewLeaveWithType(
                    personnelId = personnelId,
                    leaveType = leaveType,
                    startDateMillis = startMillis,
                    returnDateMillis = returnMillis
                )
                showAddLeaveDialog = false
                quickLeavePersonnelId = null
                Toast.makeText(
                    context,
                    AppStrings.get(
                        if (com.example.data.isShortLeaveType(leaveType)) {
                            "short_leave_recorded_success"
                        } else {
                            "leave_added_success"
                        },
                        currentLang
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            },
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Personnel Leave History Dialog (Triggered by "السجل" button on personnel cards)
    personnelForHistory?.let { targetPerson ->
        val liveTargetPerson = allPersonnelList.firstOrNull { it.id == targetPerson.id } ?: targetPerson
        PersonnelLeaveHistoryDialog(
            personnel = liveTargetPerson,
            allLeaves = allLeavesList,
            onDismiss = { personnelForHistory = null },
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled,
            onExtendRecord = { record ->
                historyRecordToExtend = liveTargetPerson to record
            }
        )
    }

    // Extend Active Leave Dialog (Triggered from PersonnelCard or AddNewLeaveDialog)
    personnelToExtend?.let { targetPerson ->
        com.example.ui.components.ExtendLeaveDialog(
            personnel = targetPerson,
            onDismiss = { personnelToExtend = null },
            onConfirmExtend = { newReturnMillis, _, reasonNote ->
                viewModel.extendPersonnelLeave(
                    personnelId = targetPerson.id,
                    newReturnDateMillis = newReturnMillis,
                    reasonNote = reasonNote
                )
                personnelToExtend = null
                Toast.makeText(
                    context,
                    AppStrings.get("extend_leave_success", currentLang),
                    Toast.LENGTH_SHORT
                ).show()
            },
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Extend Specific Leave Record Dialog (Triggered from PersonnelLeaveHistoryDialog)
    historyRecordToExtend?.let { (targetPerson, targetRecord) ->
        com.example.ui.components.ExtendLeaveDialog(
            personnel = targetPerson,
            targetRecord = targetRecord,
            onDismiss = { historyRecordToExtend = null },
            onConfirmExtend = { newReturnMillis, _, reasonNote ->
                viewModel.extendLeaveRecord(
                    record = targetRecord,
                    newReturnDateMillis = newReturnMillis,
                    reasonNote = reasonNote
                )
                historyRecordToExtend = null
                Toast.makeText(
                    context,
                    AppStrings.get("extend_leave_success", currentLang),
                    Toast.LENGTH_SHORT
                ).show()
            },
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Delete Confirmation Dialog (Liquid Glass)
    personnelToDelete?.let { target ->
        val qMark = if (currentLang == AppLanguage.ARABIC) "؟" else "?"
        LiquidGlassConfirmDialog(
            title = AppStrings.get("confirm_delete_title", currentLang),
            message = "${AppStrings.get("confirm_delete_message", currentLang)} ${formatRank(target.rank, currentLang)} / ${target.getLocalizedName(currentLang)} (${target.registrationNumber})$qMark",
            confirmText = AppStrings.get("delete", currentLang),
            cancelText = AppStrings.get("cancel", currentLang),
            icon = Icons.Default.DeleteForever,
            accentColor = RedDanger,
            secondaryColor = CoralRedAccent,
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled,
            dialogTestTag = "delete_personnel_confirm_dialog",
            confirmTestTag = "delete_personnel_confirm_button",
            cancelTestTag = "delete_personnel_cancel_button",
            onConfirm = {
                viewModel.deletePersonnel(target)
                personnelToDelete = null
                Toast.makeText(context, AppStrings.get("delete_success", currentLang), Toast.LENGTH_SHORT).show()
            },
            onDismiss = { personnelToDelete = null }
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
                viewModel.sortOption.value = SortOption.RETURN_SOON
            },
            onDismiss = { showFilterSortDialog = false },
            isDarkMode = isDarkMode,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled,
            currentLang = currentLang
        )
    }

    // Developer Info Dialog (From Settings)
    if (showDeveloperDialog) {
        DeveloperInfoDialog(
            currentLang = currentLang,
            onDismiss = { showDeveloperDialog = false },
            isDarkMode = isDarkMode,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
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
            onImportJsonAsync = { jsonStr, replaceAll, onComplete ->
                viewModel.importPersonnelFromJson(jsonStr, replaceAll, onComplete)
            },
            onDismiss = { showExportDialog = false },
            currentLang = currentLang,
            isDarkMode = isDarkMode,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Reset Sample Data Dialog (Liquid Glass)
    if (showResetConfirmDialog) {
        LiquidGlassConfirmDialog(
            title = AppStrings.get("reset_sample_dialog_title", currentLang),
            message = AppStrings.get("reset_sample_dialog_desc", currentLang),
            confirmText = AppStrings.get("drawer_reset_title", currentLang),
            cancelText = AppStrings.get("cancel", currentLang),
            icon = Icons.Default.Refresh,
            accentColor = AmberWarning,
            secondaryColor = CoralRedAccent,
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled,
            dialogTestTag = "reset_sample_confirm_dialog",
            confirmTestTag = "reset_sample_confirm_button",
            cancelTestTag = "reset_sample_cancel_button",
            onConfirm = {
                viewModel.resetToSampleData()
                showResetConfirmDialog = false
                Toast.makeText(context, AppStrings.get("reset_sample_success", currentLang), Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showResetConfirmDialog = false }
        )
    }

    // Floating In-App Update Dialog (Strictly centered, in-app; no external redirects)
    if (showUpdateDialog) {
        InAppUpdateDialog(
            updateState = appUpdateState,
            isDarkMode = isDarkMode,
            animationsEnabled = isDynamicAnimationsEnabled,
            currentLang = currentLang,
            onStartDownload = { viewModel.startAppUpdateDownload() },
            onCancelDownload = { viewModel.cancelAppUpdateDownload() },
            onInstallApk = { viewModel.installDownloadedApk() },
            onRetryCheck = { viewModel.checkForAppUpdate(manual = true) },
            onDismiss = { viewModel.dismissUpdateDialog() }
        )
    }

    // Persistent Trash Bin Dialog (Restore or Permanently Delete)
    if (showTrashBinDialog) {
        TrashBinDialog(
            trashedPersonnel = trashedPersonnel,
            isDarkMode = isDarkMode,
            currentLang = currentLang,
            onRestore = { person ->
                viewModel.restorePersonnelFromTrash(person)
                Toast.makeText(context, AppStrings.get("trash_restored_toast", currentLang), Toast.LENGTH_SHORT).show()
            },
            onRestoreAll = {
                viewModel.restoreAllFromTrash()
                Toast.makeText(context, AppStrings.get("trash_restored_all_toast", currentLang), Toast.LENGTH_SHORT).show()
            },
            onDeleteForever = { person ->
                viewModel.deletePersonnelPermanently(person)
                Toast.makeText(context, AppStrings.get("delete_success", currentLang), Toast.LENGTH_SHORT).show()
            },
            onEmptyTrash = {
                viewModel.emptyTrash()
                Toast.makeText(context, AppStrings.get("trash_emptied_toast", currentLang), Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showTrashBinDialog = false },
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }
}

private fun stripLeadingEmojiPrefix(label: String): String {
    val trimmed = label.trim()
    val firstSpace = trimmed.indexOf(' ')
    if (firstSpace <= 0) return trimmed
    val prefix = trimmed.substring(0, firstSpace)
    return if (prefix.none { it.isLetterOrDigit() }) {
        trimmed.substring(firstSpace + 1).trimStart()
    } else {
        trimmed
    }
}

@Composable
private fun HeaderUpdateDownloadButton(
    updateState: AppUpdateState,
    isDarkMode: Boolean,
    animationsEnabled: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = if (animationsEnabled) {
            spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium)
        } else {
            tween(durationMillis = 0)
        },
        label = "headerDownloadScale"
    )

    val shape = RoundedCornerShape(14.dp)
    val isReady = updateState is AppUpdateState.ReadyToInstall
    val isDownloading = updateState is AppUpdateState.Downloading
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val accentColor = if (isReady) EmeraldSuccess else (preset?.primary ?: MaterialTheme.colorScheme.primary)

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .liquidGlass(
                shape = shape,
                elevation = 3.dp,
                isDark = effectiveDark,
                cornerRadius = 14.dp
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClickLabel = contentDescription
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .testTag("header_update_download_button"),
        contentAlignment = Alignment.Center
    ) {
        when {
            isDownloading -> {
                val dlState = updateState as AppUpdateState.Downloading
                if (dlState.totalBytes > 0L) {
                    CircularProgressIndicator(
                        progress = { dlState.progress.coerceIn(0f, 1f) },
                        color = accentColor,
                        trackColor = accentColor.copy(alpha = 0.22f),
                        strokeWidth = 2.4.dp,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        color = accentColor,
                        strokeWidth = 2.4.dp,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = contentDescription,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            isReady -> {
                Icon(
                    imageVector = Icons.Default.DownloadDone,
                    contentDescription = contentDescription,
                    tint = EmeraldSuccess,
                    modifier = Modifier.size(20.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = contentDescription,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

