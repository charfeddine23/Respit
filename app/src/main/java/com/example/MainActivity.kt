package com.example

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.example.ui.PersonnelViewModel
import com.example.ui.SortOption
import com.example.ui.components.AddEditPersonnelDialog
import com.example.ui.components.DeveloperInfoDialog
import com.example.ui.components.ExportDialog
import com.example.ui.components.FilterSortDialog
import com.example.ui.components.PersonnelCard
import com.example.ui.components.PinLockScreen
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SideNavigationDrawerContent
import com.example.ui.components.StatisticsSection
import com.example.ui.screens.NotificationScreen
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
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
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isFocusMode by viewModel.isFocusMode.collectAsState()
    val dutyThreshold by viewModel.dutyThresholdDays.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val currentPin by viewModel.pinCode.collectAsState()
    val lastUndo by viewModel.lastUndoAction.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()

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

    var showAddEditDialog by remember { mutableStateOf(false) }
    var personnelToEdit by remember { mutableStateOf<Personnel?>(null) }
    var personnelToDelete by remember { mutableStateOf<Personnel?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showFilterSortDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showNotificationScreen by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.checkUpcomingNotifications()
        }
    }

    // If locked with PIN, present full screen PIN Lock Screen
    if (isLocked) {
        PinLockScreen(
            onVerifyPin = { enteredPin ->
                val ok = viewModel.verifyPin(enteredPin)
                if (ok) {
                    Toast.makeText(context, "تم إلغاء القفل بنجاح", Toast.LENGTH_SHORT).show()
                }
                ok
            }
        )
        return
    }

    // Direct in-app Notification Screen
    if (showNotificationScreen) {
        NotificationScreen(
            viewModel = viewModel,
            onBack = { showNotificationScreen = false },
            onSelectPersonnel = { personnelId ->
                val p = allPersonnelList.find { it.id == personnelId }
                if (p != null) {
                    personnelToEdit = p
                    showAddEditDialog = true
                }
            }
        )
        return
    }

    // ModalNavigationDrawer anchored on the left edge for smooth left-edge swipe gesture
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true, // left-edge swipe gesture enabled
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.widthIn(max = 330.dp),
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
                                showNotificationScreen = true
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
                            onToggleTheme = {
                                viewModel.toggleTheme()
                            },
                            onOpenSettings = {
                                showSettingsDialog = true
                                scope.launch { drawerState.close() }
                            },
                            onOpenDeveloper = {
                                showDeveloperDialog = true
                                scope.launch { drawerState.close() }
                            },
                            onResetSampleData = {
                                showResetConfirmDialog = true
                                scope.launch { drawerState.close() }
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
                    // Ambient Liquid Gradient Background Canvas (Theme Adaptive)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (isDarkMode) {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF0B1120),
                                        Color(0xFF0F172A),
                                        Color(0xFF1E293B)
                                    )
                                )
                            )
                            // Luminous Deep Neon orbs in dark mode
                            drawCircle(
                                color = Color(0x330284C7),
                                radius = size.width * 0.45f,
                                center = Offset(size.width * 0.85f, size.height * 0.15f)
                            )
                            drawCircle(
                                color = Color(0x266366F1),
                                radius = size.width * 0.5f,
                                center = Offset(size.width * 0.15f, size.height * 0.55f)
                            )
                            drawCircle(
                                color = Color(0x2610B981),
                                radius = size.width * 0.4f,
                                center = Offset(size.width * 0.9f, size.height * 0.85f)
                            )
                        } else {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFE0F7FA), // Soft cyan/blue at top
                                        Color(0xFFEBFBFC),
                                        Color(0xFFFFFFFF), // Clean white in the middle
                                        Color(0xFFF9F2FB),
                                        Color(0xFFF3E5F5)  // Gentle muted purple/blue at bottom
                                    )
                                )
                            )
                            drawCircle(
                                color = Color(0x3300E5FF),
                                radius = size.width * 0.55f,
                                center = Offset(size.width * 0.05f, size.height * 0.12f)
                            )
                            drawCircle(
                                color = Color(0x2400BCD4),
                                radius = size.width * 0.5f,
                                center = Offset(size.width * 0.88f, size.height * 0.38f)
                            )
                            drawCircle(
                                color = Color(0x248B5CF6),
                                radius = size.width * 0.6f,
                                center = Offset(size.width * 0.95f, size.height * 0.82f)
                            )
                        }
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_scaffold"),
                        containerColor = Color.Transparent,
                        topBar = {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Surface(
                                    color = if (isDarkMode) Color(0xFC0F172A) else Color.Transparent,
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
                                        // Left Side (الجهة اليسرى تماماً): App Title Row with Logo & Version
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    viewModel.toggleTheme()
                                                    val modeName = if (!isDarkMode) "الليلي" else "النهاري"
                                                    Toast.makeText(context, "تم التبديل إلى الوضع ", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(vertical = 4.dp, horizontal = 2.dp)
                                                .testTag("app_title_row")
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .liquidGlassInteractive(
                                                        shape = RoundedCornerShape(10.dp),
                                                        glowColor = MaterialTheme.colorScheme.primary
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.app_logo),
                                                    contentDescription = "أيقونة التطبيق",
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

                                        // Right Side (الجهة اليمنى): Quick Notifications & Drawer Menu Button
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Quick Notification Button with soft rounded corners & live badge
                                            Box(
                                                modifier = Modifier
                                                    .liquidGlassInteractive(
                                                        shape = RoundedCornerShape(12.dp),
                                                        glowColor = MaterialTheme.colorScheme.primary
                                                    )
                                            ) {
                                                IconButton(
                                                    onClick = { showNotificationScreen = true },
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(
                                                            color = if (isDarkMode) Color(0x3300BCD4) else Color(0x2200BCD4),
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                        .border(
                                                            1.2.dp,
                                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                            RoundedCornerShape(12.dp)
                                                        )
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
                                                                        text = "",
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Notifications,
                                                            contentDescription = AppStrings.get("check_notifications", currentLang),
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        floatingActionButton = {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Bottom Left: Settings Button (Aurora Glass)
                                    Surface(
                                        onClick = { showSettingsDialog = true },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(18.dp),
                                                ambientColor = if (isDarkMode) Color(0x3300BCD4) else Color(0x2200BCD4),
                                                spotColor = if (isDarkMode) Color(0x4000E5FF) else Color(0x3300BCD4)
                                            )
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                if (isDarkMode) {
                                                    Brush.verticalGradient(listOf(Color(0xEE1E293B), Color(0xDD0F172A)))
                                                } else {
                                                    Brush.verticalGradient(listOf(Color(0xF0FFFFFF), Color(0xE0F0FDFA)))
                                                }
                                            )
                                            .border(
                                                BorderStroke(
                                                    1.5.dp,
                                                    Brush.linearGradient(
                                                        listOf(
                                                            Color.White.copy(alpha = if (isDarkMode) 0.6f else 0.95f),
                                                            Color(0xFF00E5FF).copy(alpha = 0.7f),
                                                            Color.White.copy(alpha = 0.5f)
                                                        )
                                                    )
                                                ),
                                                RoundedCornerShape(18.dp)
                                            )
                                            .testTag("bottom_settings_button"),
                                        shape = RoundedCornerShape(18.dp),
                                        color = Color.Transparent
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = AppStrings.get("settings", currentLang),
                                                tint = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4),
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }

                                    // Bottom Right: Add Personnel Button (Vibrant Cyan Gradient)
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
                                                    listOf(
                                                        Color(0xFF00BCD4),
                                                        Color(0xFF00E5FF)
                                                    )
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
                                                contentDescription = "إضافة فرد جديد",
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Native Compose Liquid Glass Dashboard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("personnel_lazy_column"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Statistics KPI Cards (Liquid Glass)
                    item {
                        StatisticsSection(stats = stats, isDarkMode = isDarkMode)
                    }

                    // Liquid Glass Search Container with Integrated Filter & Sort Button on the Left
                    item {
                        val activeFilterCount = (if (selectedStatus != "ALL") 1 else 0) +
                                (if (selectedRank != "ALL") 1 else 0) +
                                (if (currentSort != SortOption.RETURN_SOON) 1 else 0)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .liquidGlass(
                                    shape = LiquidGlassDefaults.CardShape,
                                    elevation = 5.dp,
                                    borderAlpha = 0.85f
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
                                    // Search Input with high contrast slate text (#0F172A)
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
                                                "بحث بالاسم، رقم التسجيل، الرتبة...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF64748B)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "بحث",
                                                tint = Color(0xFF00BCD4)
                                            )
                                        },
                                        trailingIcon = {
                                            if (searchQuery.isNotEmpty()) {
                                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Clear,
                                                        contentDescription = "مسح",
                                                        tint = Color(0xFF64748B)
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
                                            focusedTextColor = Color(0xFF0F172A),
                                            unfocusedTextColor = Color(0xFF0F172A),
                                            focusedContainerColor = if (isDarkMode) Color(0xFFF1F5F9) else Color(0xF5FFFFFF),
                                            unfocusedContainerColor = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xD9FFFFFF),
                                            unfocusedBorderColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0x4000BCD4),
                                            focusedBorderColor = Color(0xFF00BCD4),
                                            focusedPlaceholderColor = Color(0xFF64748B),
                                            unfocusedPlaceholderColor = Color(0xFF64748B),
                                            cursorColor = Color(0xFF00BCD4)
                                        ),
                                        singleLine = true,
                                        shape = RoundedCornerShape(14.dp)
                                    )

                                    // Consolidated Filter & Sort Button (Positioned on the Left)
                                    Box(
                                        modifier = Modifier.liquidGlassInteractive(
                                            shape = RoundedCornerShape(14.dp),
                                            glowColor = if (activeFilterCount > 0) Color(0xFF00BCD4) else MaterialTheme.colorScheme.primary
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
                                                    .testTag("filter_sort_dialog_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Tune,
                                                    contentDescription = "الفلترة والترتيب",
                                                    tint = if (activeFilterCount > 0) Color(0xFF00BCD4) else MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Recent Searches row (last 5 employee names queried)
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
                                            contentDescription = "عمليات البحث الأخيرة",
                                            tint = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "عمليات البحث الأخيرة:",
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
                                                border = androidx.compose.foundation.BorderStroke(
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
                                                            contentDescription = "حذف",
                                                            tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Clear all button
                                        TextButton(
                                            onClick = { viewModel.clearRecentSearches() },
                                            modifier = Modifier.height(28.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp)
                                        ) {
                                            Text(
                                                text = "مسح",
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
                                text = "سجلات الأفراد (${personnelList.size} من ${allPersonnelList.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "عتبة الاستحقاق: $dutyThreshold يوم  |  ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currentSort.titleAr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Personnel List Cards (Liquid Glass)
                    if (personnelList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlass(
                                        shape = LiquidGlassDefaults.CardShape,
                                        elevation = 4.dp
                                    )
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "لا توجد نتائج مطابقة",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "جرب تغيير معايير البحث والفلترة أو إضافة فرد جديد.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                                onConfirmDuty = {
                                    viewModel.confirmReturnToDuty(person.id)
                                    Toast.makeText(
                                        context,
                                        "تم تأكيد استئناف عمل ${person.rank} / ${person.fullName} بنجاح",
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
                    if (id == 0L) "تمت إضافة الفرد بنجاح" else "تم تحديث البيانات بنجاح",
                    Toast.LENGTH_SHORT
                ).show()
            },
            isDarkMode = isDarkMode
        )
    }

    // Delete Confirmation Dialog
    if (personnelToDelete != null) {
        val target = personnelToDelete!!
        AlertDialog(
            onDismissRequest = { personnelToDelete = null },
            title = {
                Text("تأكيد حذف الفرد", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("هل أنت متأكد من رغبتك في حذف السجل الخاص بـ ${target.rank} / ${target.fullName} (رقم: ${target.registrationNumber})؟")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePersonnel(target)
                        personnelToDelete = null
                        Toast.makeText(context, "تم حذف السجل بنجاح", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("حذف", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { personnelToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Unified Filter & Sort Dialog (Consolidating Status, Rank, and Sort options)
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
            isDarkMode = isDarkMode
        )
    }

    // Developer Info Dialog (From Drawer)
    if (showDeveloperDialog) {
        DeveloperInfoDialog(
            onDismiss = { showDeveloperDialog = false }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            isDarkMode = isDarkMode,
            currentThemeMode = currentThemeMode,
            onSelectThemeMode = { viewModel.setThemeMode(it) },
            onToggleTheme = { viewModel.toggleTheme() },
            isFocusMode = isFocusMode,
            onToggleFocusMode = { viewModel.toggleFocusMode() },
            currentThreshold = dutyThreshold,
            onSaveThreshold = { viewModel.setDutyThreshold(it) },
            isPinEnabled = isPinEnabled,
            currentPin = currentPin,
            onSavePin = { pin, enabled -> viewModel.setPinCode(pin, enabled) },
            onLockNow = { viewModel.lockApp() },
            currentLanguage = currentLang,
            onSelectLanguage = { viewModel.setLanguage(it) },
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
            onDismiss = { showExportDialog = false }
        )
    }

    // Reset Sample Data Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text("استعادة بيانات افتراضية", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("هل تريد استعادة بيانات النماذج الأولية للإجازات؟ سيتم مسح التعديلات واستعادة أمثلة الأفراد.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToSampleData()
                        showResetConfirmDialog = false
                        Toast.makeText(context, "تمت استعادة البيانات الافتراضية بنجاح", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("استعادة", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
