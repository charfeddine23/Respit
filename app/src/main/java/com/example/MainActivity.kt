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
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlin.math.cos
import kotlin.math.sin
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
import com.example.ui.components.DeveloperInfoDialog
import com.example.ui.components.ExportDialog
import com.example.ui.components.FilterSortDialog
import com.example.ui.components.InAppUpdateDialog
import com.example.ui.components.LiquidGlassConfirmDialog
import com.example.ui.screens.EntryLandingScreen
import com.example.ui.components.LiquidGlassFloatingCapsule
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
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassInteractive
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppUpdateState
import com.example.util.ExcelXlsxExporter
import com.example.util.WindowSecurityHelper
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
                livingLuxuryMode = isLivingLuxuryMode
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
    val selectedWilaya by viewModel.selectedWilaya.collectAsState()
    val uniqueWilayas by viewModel.uniqueWilayas.collectAsState()
    val currentSort by viewModel.sortOption.collectAsState()

    val notificationsCount by viewModel.unreadNotificationsCount.collectAsState()
    val isDriveSyncEnabled by viewModel.isDriveSyncEnabled.collectAsState()
    val lastDriveBackupTime by viewModel.lastDriveBackupTime.collectAsState()
    val appUpdateState by viewModel.appUpdateState.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()

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

    val scope = rememberCoroutineScope()

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
    var initialLeavePersonnelId by remember { mutableStateOf<Long?>(null) }
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
    var showEntryLanding by rememberSaveable { mutableStateOf(openNotificationsSignal == 0) }
    var entrySearchQuery by rememberSaveable { mutableStateOf("") }
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
            showEntryLanding = false
            if (selectedNavPage != AppNavPage.NOTIFICATIONS) {
                previousNavPage = selectedNavPage
            }
            selectedNavPage = AppNavPage.NOTIFICATIONS
        }
    }

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
            isNeumorphic = false,
            animationsEnabled = isDynamicAnimationsEnabled
        )
        return
    }

    // Back navigation: Pop secondary pages (including Settings/Analytics/Notifications) back to previous or Dashboard
    BackHandler(enabled = !showEntryLanding && selectedNavPage != AppNavPage.DASHBOARD) {
        selectedNavPage = if (selectedNavPage == AppNavPage.NOTIFICATIONS) previousNavPage else AppNavPage.DASHBOARD
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Crossfade(
            targetState = showEntryLanding,
            animationSpec = tween(
                durationMillis = if (isDynamicAnimationsEnabled) 220 else 0,
                easing = FastOutSlowInEasing
            ),
            label = "entryToMainCrossfade"
        ) { isOnEntryScreen ->
            if (isOnEntryScreen) {
                EntryLandingScreen(
                    personnelList = allPersonnelList,
                    dutyReferenceMap = dutyReferenceMap,
                    stats = stats,
                    searchQuery = entrySearchQuery,
                    onSearchQueryChange = { entrySearchQuery = it },
                    onQuickAddLeave = { target ->
                        initialLeavePersonnelId = target.id
                        showAddLeaveDialog = true
                    },
                    onOpenPersonnelHistory = { target ->
                        personnelForHistory = target
                    },
                    onTogglePin = { target ->
                        viewModel.togglePinPersonnel(target)
                    },
                    onAddPersonnel = {
                        personnelToEdit = null
                        showAddEditDialog = true
                    },
                    onSeeMore = {
                        selectedNavPage = AppNavPage.DASHBOARD
                        showEntryLanding = false
                    },
                    currentLang = currentLang,
                    isDarkMode = isDarkMode,
                    animationsEnabled = isDynamicAnimationsEnabled
                )
            } else {
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
                    val topActiveAccent = topPreset?.primary ?: if (effectiveTopDark) CyanGlow else RoyalBlueAccent
                    val topSecondaryAccent = topPreset?.secondary ?: if (effectiveTopDark) Color(0xFF8B5CF6) else VibrantCyan

                    // Keep exact status bar top spacing even when system status bar is hidden (Fit to Screen)
                    val localDensity = androidx.compose.ui.platform.LocalDensity.current
                    val liveStatusBarTopPx = WindowInsets.statusBars.getTop(localDensity)
                    val ignoringVisStatusBarTopPx = WindowInsets.statusBarsIgnoringVisibility.getTop(localDensity)
                    val cutoutTopPx = WindowInsets.displayCutout.getTop(localDensity)
                    var cachedStatusBarTopPx by remember { mutableIntStateOf(0) }
                    val observedStatusTopPx = maxOf(liveStatusBarTopPx, ignoringVisStatusBarTopPx)
                    if (observedStatusTopPx > cachedStatusBarTopPx) {
                        cachedStatusBarTopPx = observedStatusTopPx
                    }
                    val preservedStatusBarTopDp = remember(observedStatusTopPx, cachedStatusBarTopPx, cutoutTopPx, isFitToScreenEnabled, localDensity) {
                        val fallbackResourcePx = if (observedStatusTopPx == 0 && cachedStatusBarTopPx == 0 && isFitToScreenEnabled) {
                            val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
                            if (resId > 0) context.resources.getDimensionPixelSize(resId) else 0
                        } else {
                            0
                        }
                        val effectiveTopPx = maxOf(observedStatusTopPx, cachedStatusBarTopPx, cutoutTopPx, fallbackResourcePx)
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
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LiquidGlassFloatingCapsule(
                            modifier = Modifier
                                .widthIn(max = 560.dp)
                                .fillMaxWidth()
                                .height(64.dp)
                                .testTag("top_bar_glass_capsule"),
                            isDarkMode = effectiveTopDark,
                            activeAccent = topActiveAccent,
                            secondaryAccent = topSecondaryAccent,
                            isLiving = isLivingLuxuryMode,
                            showBorder = false,
                            backdropLayer = backdropLayer,
                            backdropOffsetInRoot = backdropOffsetInRoot
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Start Side (Right in Arabic/RTL, Left in English/LTR):
                                // Arabic (RTL, Left->Right): [Update?] [Theme] [Notifications (Far Right)]
                                // English (LTR, Left->Right): [Notifications (Far Left)] [Theme] [Update?]
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Notifications Button (Far Right in Arabic, Far Left in English)
                                    val isNotifOpen = selectedNavPage == AppNavPage.NOTIFICATIONS
                                    val notifPillTint = if (isNotifOpen) {
                                        Color(0xFFEF4444).copy(alpha = if (effectiveTopDark) 0.22f else 0.14f)
                                    } else if (effectiveTopDark) {
                                        Color.White.copy(alpha = 0.10f)
                                    } else {
                                        Color.White.copy(alpha = 0.42f)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(notifPillTint, CircleShape)
                                    ) {
                                        IconButton(
                                            onClick = openNotificationsPage,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .testTag("header_notifications_button")
                                        ) {
                                            BadgedBox(
                                                badge = {
                                                    if (notificationsCount > 0) {
                                                        Badge(
                                                            containerColor = Color(0xFFEF4444),
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
                                                    imageVector = if (isNotifOpen) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                                    contentDescription = AppStrings.get("header_notifications_desc", currentLang),
                                                    tint = if (isNotifOpen) Color(0xFFEF4444) else topActiveAccent,
                                                    modifier = Modifier.size(21.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Light / Dark Mode Toggle Button
                                    val themePillTint = if (effectiveTopDark) {
                                        Color.White.copy(alpha = 0.10f)
                                    } else {
                                        Color.White.copy(alpha = 0.42f)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(themePillTint, CircleShape)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.toggleTheme() },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .testTag("header_theme_toggle")
                                        ) {
                                            Icon(
                                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                contentDescription = AppStrings.get(if (isDarkMode) "theme_light" else "theme_dark", currentLang),
                                                tint = if (isDarkMode) Color(0xFFFBBF24) else topActiveAccent,
                                                modifier = Modifier.size(21.dp)
                                            )
                                        }
                                    }

                                    val hasAvailableUpdate = appUpdateState is AppUpdateState.UpdateAvailable ||
                                        appUpdateState is AppUpdateState.Downloading ||
                                        appUpdateState is AppUpdateState.ReadyToInstall

                                    // Sleek Dynamic Download Arrow shown ONLY in the Top Banner when an update exists
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
                                }

                                // End Side (Far Left in Arabic/RTL -> [Logo] [ريسبيت], Far Right in English/LTR -> [Respite] [Logo])
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { showEntryLanding = true }
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                        .testTag("app_title_row")
                                ) {
                                    val isArabicTitle = currentLang == AppLanguage.ARABIC
                                    Text(
                                        text = AppStrings.get("app_title", currentLang),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = if (isArabicTitle) KufiArabicBrandFontFamily else androidx.compose.ui.text.font.FontFamily.Default,
                                            fontSize = if (isArabicTitle) 21.sp else 18.sp,
                                            lineHeight = if (isArabicTitle) 26.sp else 24.sp
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = if (effectiveTopDark) Color.White else Color(0xFF0F172A),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Image(
                                        painter = painterResource(id = R.drawable.app_logo),
                                        contentDescription = AppStrings.get("app_icon_desc", currentLang),
                                        contentScale = ContentScale.Fit,
                                        colorFilter = ColorFilter.tint(
                                            if (effectiveTopDark) Color.White else topActiveAccent
                                        ),
                                        modifier = Modifier.size(width = 48.dp, height = 38.dp)
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
                                val fabBreathScale = if (isLivingLuxuryMode && isDynamicAnimationsEnabled) {
                                    val fabInf = rememberInfiniteTransition(label = "fabLuxuryBreath")
                                    fabInf.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 1.055f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "fabBreathScale"
                                    ).value
                                } else 1f

                                val fabPreset = LocalThemePreset.current
                                val effectiveFabDark = fabPreset?.isDark ?: isDarkMode
                                val fabActiveAccent = fabPreset?.primary ?: if (effectiveFabDark) CyanGlow else RoyalBlueAccent
                                val fabSecondaryAccent = fabPreset?.secondary ?: if (effectiveFabDark) Color(0xFF8B5CF6) else VibrantCyan
                                val fabHaptic = LocalHapticFeedback.current

                                LiquidGlassFloatingCapsule(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .graphicsLayer {
                                            scaleX = fabBreathScale
                                            scaleY = fabBreathScale
                                        },
                                    isDarkMode = effectiveFabDark,
                                    activeAccent = fabActiveAccent,
                                    secondaryAccent = fabSecondaryAccent,
                                    isLiving = isLivingLuxuryMode,
                                    showBorder = false,
                                    backdropLayer = backdropLayer,
                                    backdropOffsetInRoot = backdropOffsetInRoot
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .clickable {
                                                fabHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                initialLeavePersonnelId = null
                                                showAddLeaveDialog = true
                                            }
                                            .testTag("add_personnel_fab"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = AppStrings.get("add_leave_dialog_title", currentLang),
                                            tint = if (effectiveFabDark) Color.White else fabActiveAccent,
                                            modifier = Modifier.size(30.dp)
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
                                notificationsCount = notificationsCount,
                                isDarkMode = isDarkMode,
                                isNeumorphic = false,
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
                                isNeumorphic = false,
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
                                                    availableUpdateVersion = null,
                                                    isFullScreenPage = true,
                                                    topBarClearance = topBarClearance,
                                                    bottomDockClearance = bottomDockClearance
                                                )
                                            }
                                        }
                                        AppNavPage.DASHBOARD -> {
                                            LazyColumn(
                                                state = dashboardListState,
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
                                                onOpenAnalytics = {
                                                    selectedNavPage = AppNavPage.ANALYTICS
                                                },
                                                isDarkMode = isDarkMode,
                                                isNeumorphic = false,
                                                animationsEnabled = isDynamicAnimationsEnabled
                                            )
                                        }

                                        // 3 Prominent Liquid Glass Quick Action Tiles (Add Personnel, Urgent Alerts, Room DB)
                                        item {
                                            val urgentCount = stats.overdue + stats.returningToday
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // 1. Emerald Liquid Glass Action Tile: "+ إضافة فرد"
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .defaultMinSize(minHeight = 50.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(16.dp),
                                                            cornerRadius = 16.dp,
                                                            isDark = isDarkMode,
                                                            accent = EmeraldSuccess,
                                                            secondaryAccent = Color(0xFF059669),
                                                            isSelected = true,
                                                            softTintWhenSelected = false
                                                        )
                                                        .clickable {
                                                            personnelToEdit = null
                                                            showAddEditDialog = true
                                                        }
                                                        .testTag("action_btn_add_personnel"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 8.dp, vertical = 12.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Add,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(17.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = AppStrings.get("add_personnel", currentLang),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }

                                                // 2. Coral Liquid Glass Action Tile: "تنبيهات عاجلة" + urgent pill badge
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .defaultMinSize(minHeight = 50.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(16.dp),
                                                            cornerRadius = 16.dp,
                                                            isDark = isDarkMode,
                                                            accent = CoralRedAccent,
                                                            secondaryAccent = Color(0xFFDC2626),
                                                            isSelected = true,
                                                            softTintWhenSelected = false
                                                        )
                                                        .clickable {
                                                            if (selectedNavPage != AppNavPage.NOTIFICATIONS) {
                                                                previousNavPage = selectedNavPage
                                                            }
                                                            selectedNavPage = AppNavPage.NOTIFICATIONS
                                                        }
                                                        .testTag("action_btn_urgent_alerts"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 8.dp, vertical = 12.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.NotificationsActive,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = AppStrings.get("urgent_alerts_title", currentLang).split("(").first().trim(),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        if (urgentCount > 0) {
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Box(
                                                                modifier = Modifier
                                                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = "$urgentCount",
                                                                    color = Color.White,
                                                                    fontSize = 9.5.sp,
                                                                    fontWeight = FontWeight.ExtraBold
                                                                )
                                                            }
                                                        }
                                                    }
                                                }

                                                // 3. Royal Blue Liquid Glass Action Tile: "قاعدة البيانات"
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .defaultMinSize(minHeight = 50.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(16.dp),
                                                            cornerRadius = 16.dp,
                                                            isDark = isDarkMode,
                                                            accent = RoyalBlueAccent,
                                                            secondaryAccent = VibrantCyan,
                                                            isSelected = true,
                                                            softTintWhenSelected = false
                                                        )
                                                        .clickable {
                                                            selectedNavPage = AppNavPage.DATABASE
                                                        }
                                                        .testTag("action_btn_room_db"),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 8.dp, vertical = 12.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Storage,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(17.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = AppStrings.get("nav_database", currentLang),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
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
                                                        isNeumorphic = false,
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
                                                                isNeumorphic = false
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

                                        // Filtered / Pinned Records List
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
                                            items(
                                                items = personnelList,
                                                key = { it.id },
                                                contentType = { "personnel_card" }
                                            ) { person ->
                                                PersonnelCard(
                                                    personnel = person,
                                                    dutyThresholdDays = dutyThreshold,
                                                    modifier = Modifier.animateItem(
                                                        placementSpec = spring(
                                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                                            stiffness = 380f,
                                                            visibilityThreshold = IntOffset.VisibilityThreshold
                                                        )
                                                    ),
                                                    isDarkMode = isDarkMode,
                                                    isNeumorphic = false,
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
                                                    }
                                                )
                                            }
                                        }

                                        item {
                                            Spacer(modifier = Modifier.height(maxOf(bottomDockClearance + 24.dp, 108.dp)))
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
                                                        isNeumorphic = false,
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
                                                            isNeumorphic = false
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

                                        // Inline 1-Tap Liquid Glass Quick Status Filter Strip
                                        item {
                                            val primaryAccent = activeThemePreset?.primary ?: if (isDarkMode) CyanGlow else RoyalBlueAccent
                                            val quickFilterItems = remember(stats, currentLang, primaryAccent) {
                                                listOf(
                                                    Triple("ALL", AppStrings.get("status_all", currentLang) to Icons.Default.Person, stats.total to primaryAccent),
                                                    Triple("ON_DUTY", stripLeadingEmojiPrefix(AppStrings.get("status_on_duty", currentLang)) to Icons.Default.CheckCircle, stats.onDuty to EmeraldSuccess),
                                                    Triple("AT_HOME", stripLeadingEmojiPrefix(AppStrings.get("status_at_home", currentLang)) to Icons.Default.Home, stats.atHome to VibrantCyan),
                                                    Triple("RETURNING_TODAY", stripLeadingEmojiPrefix(AppStrings.get("status_returning_today", currentLang)) to Icons.Default.NotificationsActive, stats.returningToday to RoyalBlueAccent),
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

                                                if (activeFilterCount > 0 || selectedWilaya != "ALL") {
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
                                                                viewModel.selectedWilaya.value = "ALL"
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
                                            items(
                                                items = chunkedPersonnel,
                                                key = { pair -> pair.joinToString("-") { it.id.toString() } },
                                                contentType = { "personnel_tablet_row" }
                                            ) { rowItems ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .animateItem(
                                                            placementSpec = spring(
                                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                                stiffness = 380f,
                                                                visibilityThreshold = IntOffset.VisibilityThreshold
                                                            )
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
                                                                    isNeumorphic = false,
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
                                                        placementSpec = spring(
                                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                                            stiffness = 380f,
                                                            visibilityThreshold = IntOffset.VisibilityThreshold
                                                        )
                                                    ),
                                                    isDarkMode = isDarkMode,
                                                    isNeumorphic = false,
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
            isNeumorphic = false,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Add New Leave Dialog (Triggered by circular + FAB or Entry screen + button)
    if (showAddLeaveDialog) {
        AddNewLeaveDialog(
            personnelList = allPersonnelList,
            initialPersonnelId = initialLeavePersonnelId,
            onDismiss = {
                showAddLeaveDialog = false
                initialLeavePersonnelId = null
            },
            onOpenAddPersonnel = {
                personnelToEdit = null
                showAddEditDialog = true
            },
            onExtendCurrentLeave = { target ->
                showAddLeaveDialog = false
                initialLeavePersonnelId = null
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
                initialLeavePersonnelId = null
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
            isNeumorphic = false,
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
            isNeumorphic = false,
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
            isNeumorphic = false,
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
            isNeumorphic = false,
            animationsEnabled = isDynamicAnimationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Delete Confirmation Dialog (Liquid Glass)
    if (personnelToDelete != null) {
        val target = personnelToDelete!!
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
                viewModel.selectedWilaya.value = "ALL"
                viewModel.sortOption.value = SortOption.RETURN_SOON
            },
            onDismiss = { showFilterSortDialog = false },
            isDarkMode = isDarkMode,
            isNeumorphic = false,
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
            onImportJson = { jsonStr, replaceAll ->
                viewModel.importPersonnelFromJson(jsonStr, replaceAll)
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
            isNeumorphic = false,
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
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "headerDownloadScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "headerDownloadMotion")
    val arrowFloatY by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = -2.2f,
            targetValue = 2.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1050, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "arrowFloatY"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val ringRotation by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "headerRingRotation"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val haloPulse by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.55f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "headerHaloPulse"
        )
    } else {
        remember { mutableStateOf(0.8f) }
    }

    // Match adjacent header_theme_toggle & header_notifications_button exact shape and 48.dp size
    val shape = CircleShape
    val isReady = updateState is AppUpdateState.ReadyToInstall
    val isDownloading = updateState is AppUpdateState.Downloading
    val accentGlow = if (isReady) Color(0xFF10B981) else Color(0xFF00E5FF)

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .liquidGlassInteractive(
                shape = shape,
                glowColor = accentGlow,
                isDark = isDarkMode,
                isNeumorphic = false
            )
            .drawWithContent {
                drawContent()
                if (size.width <= 0f || size.height <= 0f) return@drawWithContent
                val strokePx = 1.6.dp.toPx()
                val insetPx = 1.4.dp.toPx()
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = (size.minDimension / 2f).coerceAtLeast(1f)
                val rad = Math.toRadians(ringRotation.toDouble())
                val dx = (cos(rad) * radius).toFloat()
                val dy = (sin(rad) * radius).toFloat()

                val ringColors = if (isReady) {
                    listOf(
                        Color(0xFF10B981).copy(alpha = 0.92f * haloPulse),
                        Color(0xFF34D399).copy(alpha = 0.55f * haloPulse),
                        Color(0xFF00E5FF).copy(alpha = 0.85f * haloPulse)
                    )
                } else {
                    listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.92f * haloPulse),
                        Color(0xFF3B82F6).copy(alpha = 0.55f * haloPulse),
                        Color(0xFF10B981).copy(alpha = 0.85f * haloPulse)
                    )
                }
                val ringBrush = Brush.linearGradient(
                    colors = ringColors,
                    start = Offset(cx + dx, cy + dy),
                    end = Offset(cx - dx, cy - dy)
                )

                drawCircle(
                    brush = ringBrush,
                    radius = radius - insetPx,
                    center = Offset(cx, cy),
                    style = Stroke(width = strokePx)
                )
            }
            .clip(shape)
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
                        color = Color(0xFF00E5FF),
                        trackColor = Color(0xFF00E5FF).copy(alpha = 0.22f),
                        strokeWidth = 2.4.dp,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        color = Color(0xFF00E5FF),
                        strokeWidth = 2.4.dp,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = contentDescription,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer { translationY = arrowFloatY.dp.toPx() * 0.5f }
                )
            }
            isReady -> {
                Icon(
                    imageVector = Icons.Default.DownloadDone,
                    contentDescription = contentDescription,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(21.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = contentDescription,
                    tint = if (isDarkMode) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(21.dp)
                        .graphicsLayer { translationY = arrowFloatY.dp.toPx() }
                )
            }
        }
    }
}

