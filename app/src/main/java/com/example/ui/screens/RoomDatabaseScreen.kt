package com.example.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.example.data.AppThemeStyle
import com.example.data.LeaveRecord
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.PersonnelViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.BiometricAuthHelper
import com.example.util.ExcelXlsxExporter
import com.example.util.SyncDialogSecureWindow
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDatabaseScreen(
    viewModel: PersonnelViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val themeStyle by viewModel.currentThemeStyle.collectAsState()
    val isNeumorphic = themeStyle == AppThemeStyle.NEUMORPHISM

    val personnelList by viewModel.allPersonnel.collectAsState()
    val leavesList by viewModel.allLeaves.collectAsState()
    val personnelCount by viewModel.personnelCount.collectAsState()
    val leavesCount by viewModel.leavesCount.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    val bgColor = if (isDarkMode) Color(0xFF131926) else if (isNeumorphic) Color(0xFFE6EEF8) else Color(0xFFF1F5F9)
    val cardBg = if (isDarkMode) Color(0xFF161F30) else if (isNeumorphic) Color(0xFFE6EEF8) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val isDynamicAnimationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()
    val isNoScreenshotEnabled by viewModel.isNoScreenshotEnabled.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val excelExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val result = runCatching {
                    withContext(Dispatchers.IO) {
                        ExcelXlsxExporter.writeWorkbookToUri(
                            context = context,
                            uri = uri,
                            personnelList = personnelList,
                            leaveRecords = leavesList,
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

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Box(modifier = Modifier.fillMaxSize().testTag("room_database_screen")) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(
                    color = if (isDarkMode) Color(0xCC0F172A) else Color(0xDAE2E8F0),
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.get("nav_database", currentLang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }
                            Text(
                                text = AppStrings.get("room_db_sub", currentLang),
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Room 2.7.0",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.14f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f)),
                                modifier = Modifier.testTag("room_ksp_badge")
                            ) {
                                Text(
                                    text = AppStrings.get("room_ksp_active", currentLang),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val contentModifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 720.dp)
                        .align(Alignment.TopCenter)

                    val filteredPersonnel = remember(personnelList, searchQuery, currentLang) {
                        if (searchQuery.isBlank()) {
                            personnelList
                        } else {
                            val q = searchQuery.trim().lowercase(Locale.getDefault())
                            personnelList.filter {
                                it.fullName.lowercase(Locale.getDefault()).contains(q) ||
                                        it.fullNameLatin.lowercase(Locale.getDefault()).contains(q) ||
                                        it.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                                        it.rank.lowercase(Locale.getDefault()).contains(q) ||
                                        formatRank(it.rank, currentLang).lowercase(Locale.getDefault()).contains(q)
                            }
                        }
                    }

                    val filteredLeaves = remember(leavesList, searchQuery, currentLang) {
                        if (searchQuery.isBlank()) {
                            leavesList
                        } else {
                            val q = searchQuery.trim().lowercase(Locale.getDefault())
                            leavesList.filter {
                                it.personnelName.lowercase(Locale.getDefault()).contains(q) ||
                                        it.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                                        it.leaveType.lowercase(Locale.getDefault()).contains(q) ||
                                        formatLeaveType(it.leaveType, currentLang).lowercase(Locale.getDefault()).contains(q) ||
                                        it.rank.lowercase(Locale.getDefault()).contains(q) ||
                                        formatRank(it.rank, currentLang).lowercase(Locale.getDefault()).contains(q)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = contentModifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                    ) {
                        // 1. Room Header Info Card
                        item {
                            RoomDatabaseHeaderCard(
                                isDarkMode = isDarkMode,
                                isNeumorphic = isNeumorphic,
                                currentLang = currentLang,
                                personnelCount = personnelCount,
                                leavesCount = leavesCount,
                                onClearData = {
                                    showClearDialog = true
                                },
                                onExportExcel = {
                                    excelExportLauncher.launch(ExcelXlsxExporter.defaultFileName())
                                }
                            )
                        }

                        // 2. Tab Navigation Row
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = cardBg,
                                shadowElevation = if (isNeumorphic) 2.dp else 1.dp,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TabRow(
                                    selectedTabIndex = selectedTab,
                                    containerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    indicator = { tabPositions ->
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                            color = MaterialTheme.colorScheme.primary,
                                            height = 3.dp
                                        )
                                    }
                                ) {
                                    Tab(
                                        selected = selectedTab == 0,
                                        onClick = { selectedTab = 0 },
                                        text = {
                                            Text(
                                                text = "${AppStrings.get("room_tab_personnel", currentLang)} ($personnelCount)",
                                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp,
                                                color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else textSecondary
                                            )
                                        },
                                        modifier = Modifier.testTag("room_tab_personnel")
                                    )
                                    Tab(
                                        selected = selectedTab == 1,
                                        onClick = { selectedTab = 1 },
                                        text = {
                                            Text(
                                                text = "${AppStrings.get("room_tab_leaves", currentLang)} ($leavesCount)",
                                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp,
                                                color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else textSecondary
                                            )
                                        },
                                        modifier = Modifier.testTag("room_tab_leaves")
                                    )
                                    Tab(
                                        selected = selectedTab == 2,
                                        onClick = { selectedTab = 2 },
                                        text = {
                                            Text(
                                                text = AppStrings.get("room_tab_diagnostics", currentLang),
                                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp,
                                                color = if (selectedTab == 2) MaterialTheme.colorScheme.primary else textSecondary
                                            )
                                        },
                                        modifier = Modifier.testTag("room_tab_diagnostics")
                                    )
                                }
                            }
                        }

                        // 3. Tab Contents
                        when (selectedTab) {
                            0 -> {
                                // Personnel Table Content
                                item {
                                    SearchBox(
                                        query = searchQuery,
                                        onQueryChange = { searchQuery = it },
                                        placeholder = AppStrings.get("search_placeholder", currentLang),
                                        isDarkMode = isDarkMode,
                                        clearDesc = AppStrings.get("clear_icon_desc", currentLang)
                                    )
                                }

                                if (filteredPersonnel.isEmpty()) {
                                    item {
                                        EmptyDatabaseState(
                                            isDarkMode = isDarkMode,
                                            title = AppStrings.get("no_results", currentLang),
                                            description = AppStrings.get("no_results_desc", currentLang)
                                        )
                                    }
                                } else {
                                    items(filteredPersonnel, key = { it.id }) { person ->
                                        RoomPersonnelRowCard(
                                            personnel = person,
                                            isDarkMode = isDarkMode,
                                            currentLang = currentLang,
                                            onDelete = {
                                                viewModel.deletePersonnelFromRoom(person)
                                                Toast.makeText(context, AppStrings.get("delete_success", currentLang), Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }
                            1 -> {
                                // Leave Records Table Content (Full-width sleek search, no manual add button)
                                item {
                                    SearchBox(
                                        query = searchQuery,
                                        onQueryChange = { searchQuery = it },
                                        placeholder = AppStrings.get("room_search_leaves", currentLang),
                                        isDarkMode = isDarkMode,
                                        clearDesc = AppStrings.get("clear_icon_desc", currentLang)
                                    )
                                }

                                if (filteredLeaves.isEmpty()) {
                                    item {
                                        EmptyDatabaseState(
                                            isDarkMode = isDarkMode,
                                            title = AppStrings.get("room_no_leaves", currentLang),
                                            description = AppStrings.get("room_no_leaves_desc", currentLang)
                                        )
                                    }
                                } else {
                                    items(filteredLeaves, key = { it.id }) { leave ->
                                        RoomLeaveRecordRowCard(
                                            leave = leave,
                                            isDarkMode = isDarkMode,
                                            currentLang = currentLang
                                        )
                                    }
                                }
                            }
                            2 -> {
                                // Engine Diagnostics & Specs
                                item {
                                    RoomDiagnosticsCard(
                                        isDarkMode = isDarkMode,
                                        currentLang = currentLang,
                                        personnelCount = personnelCount,
                                        leavesCount = leavesCount
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Luxurious Biometric-Protected Clear Confirmation Dialog
            if (showClearDialog) {
                ClearPersonnelConfirmationDialog(
                    isDarkMode = isDarkMode,
                    animationsEnabled = isDynamicAnimationsEnabled,
                    isNoScreenshotEnabled = isNoScreenshotEnabled,
                    currentLang = currentLang,
                    onDismiss = { showClearDialog = false },
                    onConfirmedWithBiometric = {
                        viewModel.clearAllRoomData()
                        showClearDialog = false
                        Toast.makeText(
                            context,
                            AppStrings.get("room_clear_success", currentLang),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun RoomDatabaseHeaderCard(
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    currentLang: AppLanguage,
    personnelCount: Int,
    leavesCount: Int,
    onClearData: () -> Unit,
    onExportExcel: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val clearInteraction = remember { MutableInteractionSource() }
    val isClearPressed by clearInteraction.collectIsPressedAsState()
    val clearScale by animateFloatAsState(
        targetValue = if (isClearPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "clearBtnScale"
    )
    val exportInteraction = remember { MutableInteractionSource() }
    val isExportPressed by exportInteraction.collectIsPressedAsState()
    val exportScale by animateFloatAsState(
        targetValue = if (isExportPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "exportExcelBtnScale"
    )
    val contentLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        shadowElevation = if (isNeumorphic) 4.dp else 3.dp,
        border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
        ),
        modifier = Modifier.fillMaxWidth().testTag("room_header_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Stat counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RoomStatBox(
                    label = AppStrings.get("room_total_personnel_count", currentLang),
                    value = "$personnelCount",
                    color = MaterialTheme.colorScheme.primary,
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f)
                )
                RoomStatBox(
                    label = AppStrings.get("room_total_leaves_count", currentLang),
                    value = "$leavesCount",
                    color = Color(0xFF7C3AED),
                    isDarkMode = isDarkMode,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Action Row: Physical LEFT = Clear Personnel Table | Physical RIGHT = Export Excel (.xlsx) A4 Landscape
            val themePrimary = MaterialTheme.colorScheme.primary
            val themeSecondary = MaterialTheme.colorScheme.secondary
            val buttonShape = if (isNeumorphic) RoundedCornerShape(50.dp) else RoundedCornerShape(16.dp)
            val borderGradient = Brush.linearGradient(
                colors = listOf(
                    themePrimary.copy(alpha = if (isDarkMode) 0.72f else 0.62f),
                    themeSecondary.copy(alpha = if (isDarkMode) 0.56f else 0.48f)
                )
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // LEFT: Clear Personnel Table Button (Clean theme-matched OutlinedButton, zero label highlights)
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClearData()
                        },
                        interactionSource = clearInteraction,
                        shape = buttonShape,
                        border = BorderStroke(1.25.dp, borderGradient),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = themePrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = clearScale
                                scaleY = clearScale
                            }
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("room_clear_tables_btn")
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides contentLayoutDir) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = themePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("room_clear_btn", currentLang),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themePrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = themeSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    // RIGHT: Export Database to Excel (.xlsx) A4 Landscape Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onExportExcel()
                        },
                        interactionSource = exportInteraction,
                        shape = buttonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = exportScale
                                scaleY = exportScale
                            }
                            .defaultMinSize(minHeight = 48.dp)
                            .clip(buttonShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(themePrimary, Color(0xFF0D9488))
                                )
                            )
                            .testTag("room_export_xlsx_btn")
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides contentLayoutDir) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("room_export_xlsx_btn", currentLang),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
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

@Composable
private fun RoomStatBox(
    label: String,
    value: String,
    color: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = if (isDarkMode) 0.12f else 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    isDarkMode: Boolean,
    clearDesc: String = "Clear",
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isFocused by remember { mutableStateOf(false) }

    val containerBg = if (isDarkMode) {
        Brush.horizontalGradient(
            listOf(Color(0xFF172033), Color(0xFF1E293B), Color(0xFF172033))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color(0xFFFFFFFF), Color(0xFFF8FAFC), Color(0xFFFFFFFF))
        )
    }

    val borderBrush = if (isFocused) {
        Brush.horizontalGradient(
            listOf(Color(0xFF00BCD4), Color(0xFF38BDF8), Color(0xFF7C3AED))
        )
    } else {
        Brush.horizontalGradient(
            if (isDarkMode) {
                listOf(Color(0x4438BDF8), Color(0x337C3AED), Color(0x4438BDF8))
            } else {
                listOf(Color(0x5538BDF8), Color(0x447C3AED), Color(0x5538BDF8))
            }
        )
    }

    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val hintColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isFocused) 10.dp else 4.dp,
                shape = shape,
                ambientColor = Color(0xFF00BCD4).copy(alpha = 0.25f),
                spotColor = Color(0xFF7C3AED).copy(alpha = 0.25f)
            )
            .clip(shape)
            .background(containerBg)
            .border(width = if (isFocused) 1.5.dp else 1.dp, brush = borderBrush, shape = shape)
            .height(46.dp)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF00BCD4).copy(alpha = if (isFocused) 0.26f else 0.16f),
                                Color(0xFF7C3AED).copy(alpha = if (isFocused) 0.24f else 0.14f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = if (isFocused) Color(0xFF38BDF8) else Color(0xFF0284C7),
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 13.sp,
                        color = hintColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(Color(0xFF38BDF8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                        .testTag("room_search_input")
                )
            }

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onQueryChange("")
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(hintColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = clearDesc,
                            tint = hintColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomPersonnelRowCard(
    personnel: Personnel,
    isDarkMode: Boolean,
    currentLang: AppLanguage,
    onDelete: () -> Unit
) {
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x2638BDF8)
        ),
        modifier = Modifier.fillMaxWidth().testTag("room_personnel_item_${personnel.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(personnel.status.hexColor).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(personnel.status.hexColor),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = personnel.getLocalizedName(currentLang),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(personnel.status.bgHexColor)
                    ) {
                        Text(
                            text = personnel.status.getCompactBadgeLabel(currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(personnel.status.hexColor),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${formatRank(personnel.rank, currentLang)} • ${personnel.registrationNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ID: #${personnel.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF0284C7),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatLeaveType(personnel.leaveType, currentLang)} (${personnel.totalLeaveDays} ${AppStrings.get("days", currentLang)}) • ${AppStrings.get("room_return_prefix", currentLang)} ${personnel.formattedReturnDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary,
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = AppStrings.get("delete", currentLang),
                    tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RoomLeaveRecordRowCard(
    leave: LeaveRecord,
    isDarkMode: Boolean,
    currentLang: AppLanguage
) {
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x2638BDF8)
        ),
        modifier = Modifier.fillMaxWidth().testTag("room_leave_item_${leave.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C3AED).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = leave.personnelName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF7C3AED).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = formatLeaveType(leave.leaveType, currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF7C3AED),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatRank(leave.rank, currentLang)} • ${leave.registrationNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${leave.formattedStartDate} ➔ ${leave.formattedReturnDate} (${leave.durationDays} ${AppStrings.get("days", currentLang)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondary,
                        fontSize = 11.sp
                    )
                }

                if (leave.destinationWilaya.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${AppStrings.get("room_destination_prefix", currentLang)} ${formatWilaya(leave.destinationWilaya, currentLang, includeCode = false)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF0284C7),
                        fontSize = 11.sp
                    )
                }
            }

            // Immutable leave record badge (cannot be deleted)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF10B981).copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f))
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = AppStrings.get("room_immutable_badge", currentLang),
                    tint = Color(0xFF10B981),
                    modifier = Modifier.padding(7.dp).size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun RoomDiagnosticsCard(
    isDarkMode: Boolean,
    currentLang: AppLanguage,
    personnelCount: Int,
    leavesCount: Int
) {
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val recUnit = AppStrings.get("room_diag_records_unit", currentLang)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
        ),
        modifier = Modifier.fillMaxWidth().testTag("room_diagnostics_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.get("room_diag_title", currentLang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            DiagnosticRow(AppStrings.get("room_diag_db_file", currentLang), "personnel_leaves_db", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_version", currentLang), "4 (WAL Mode)", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_orm", currentLang), "AndroidX Room 2.7.0 (KSP)", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_concurrency", currentLang), "Coroutines & Flow (IO)", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_pattern", currentLang), "Repository Pattern", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_tables", currentLang), "personnel, leave_records", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_personnel_rows", currentLang), "$personnelCount $recUnit", Color(0xFF10B981), textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_leaves_rows", currentLang), "$leavesCount $recUnit", Color(0xFF7C3AED), textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_sandbox", currentLang), AppStrings.get("room_diag_sandbox_val", currentLang), textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_cloud", currentLang), AppStrings.get("room_diag_local_only", currentLang), Color(0xFF0284C7), textSecondary)
        }
    }
}

@Composable
private fun DiagnosticRow(
    label: String,
    value: String,
    valueColor: Color,
    labelColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = labelColor)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
private fun EmptyDatabaseState(
    isDarkMode: Boolean,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                tint = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun ClearPersonnelConfirmationDialog(
    isDarkMode: Boolean,
    animationsEnabled: Boolean,
    isNoScreenshotEnabled: Boolean,
    currentLang: AppLanguage,
    onDismiss: () -> Unit,
    onConfirmedWithBiometric: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var authError by remember { mutableStateOf<String?>(null) }

    val enterState = remember { MutableTransitionState(false).apply { targetState = true } }
    val infiniteTransition = rememberInfiniteTransition(label = "clearDialogMotion")
    val pulseScale by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.07f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "clearPulse"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    val auraAlpha by if (animationsEnabled) {
        infiniteTransition.animateFloat(
            initialValue = 0.45f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1900, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "clearAuraAlpha"
        )
    } else {
        remember { mutableStateOf(0.65f) }
    }

    fun launchBiometricVerification() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        authError = null
        val activity = context.findFragmentActivity()
        if (activity == null || !BiometricAuthHelper.isBiometricOrDeviceCredentialAvailable(context)) {
            authError = AppStrings.get("room_clear_biometric_unavailable", currentLang)
            return
        }
        BiometricAuthHelper.authenticate(
            activity = activity,
            title = AppStrings.get("room_clear_biometric_title", currentLang),
            subtitle = AppStrings.get("room_clear_biometric_subtitle", currentLang),
            defaultError = AppStrings.get("biometric_default_error", currentLang),
            onSuccess = {
                onConfirmedWithBiometric()
            },
            onError = { err ->
                authError = err.ifBlank { AppStrings.get("biometric_default_error", currentLang) }
            },
            onFailed = {
                authError = AppStrings.get("biometric_auth_failed", currentLang)
            }
        )
    }

    val cardShape = RoundedCornerShape(28.dp)
    val surfaceColor = if (isDarkMode) Color(0xFF101827) else Color(0xFFFFFFFF)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val gradientColors = listOf(
        Color(0xFFEF4444),
        Color(0xFFF43F5E),
        Color(0xFFF59E0B),
        Color(0xFF7C3AED),
        Color(0xFFEF4444)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(isNoScreenshotEnabled)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = enterState,
                enter = fadeIn(tween(240)) + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                        .drawBehind {
                            val cornerRadius = 30.dp.toPx()
                            val auraBrush = Brush.linearGradient(
                                colors = gradientColors.map { it.copy(alpha = 0.24f * auraAlpha) },
                                start = Offset.Zero,
                                end = Offset(size.width, size.height)
                            )
                            drawRoundRect(
                                brush = auraBrush,
                                topLeft = Offset(-6.dp.toPx(), -6.dp.toPx()),
                                size = Size(size.width + 12.dp.toPx(), size.height + 12.dp.toPx()),
                                cornerRadius = CornerRadius(cornerRadius + 6.dp.toPx(), cornerRadius + 6.dp.toPx()),
                                style = Stroke(width = 10.dp.toPx())
                            )
                            drawRoundRect(
                                brush = auraBrush,
                                topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                                size = Size(size.width + 4.dp.toPx(), size.height + 4.dp.toPx()),
                                cornerRadius = CornerRadius(cornerRadius + 2.dp.toPx(), cornerRadius + 2.dp.toPx()),
                                style = Stroke(width = 4.dp.toPx())
                            )
                        }
                ) {
                    Surface(
                        shape = cardShape,
                        color = surfaceColor,
                        shadowElevation = 18.dp,
                        border = BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFEF4444).copy(alpha = 0.75f),
                                    Color(0xFFF59E0B).copy(alpha = 0.45f),
                                    Color(0xFF7C3AED).copy(alpha = 0.65f)
                                )
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("room_clear_confirm_dialog")
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top Glowing Biometric + Warning Shield Emblem
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .graphicsLayer {
                                        scaleX = pulseScale
                                        scaleY = pulseScale
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    Color(0xFFEF4444).copy(alpha = 0.28f),
                                                    Color(0xFF7C3AED).copy(alpha = 0.12f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFDC2626), Color(0xFFF43F5E))
                                            )
                                        )
                                        .border(2.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkMode) Color(0xFF101827) else Color.White)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF59E0B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Biometric Security Tag
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color(0xFFEF4444).copy(alpha = if (isDarkMode) 0.16f else 0.10f),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.30f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = AppStrings.get("room_clear_biometric_badge", currentLang),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = AppStrings.get("room_clear_confirm_title", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isDarkMode) Color(0xFF1E293B).copy(alpha = 0.7f) else Color(0xFFF8FAFC),
                                border = BorderStroke(
                                    1.dp,
                                    if (isDarkMode) Color(0x33475569) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = AppStrings.get("room_clear_confirm_desc", currentLang),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 19.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                )
                            }

                            AnimatedVisibility(visible = authError != null) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFDC2626).copy(alpha = 0.14f),
                                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = authError.orEmpty(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isDarkMode) Color(0xFFFCA5A5) else Color(0xFFB91C1C),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isDarkMode) Color(0x4494A3B8) else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("room_clear_cancel_btn")
                                ) {
                                    Text(
                                        text = AppStrings.get("cancel", currentLang),
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        fontSize = 13.sp
                                    )
                                }

                                Button(
                                    onClick = { launchBiometricVerification() },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .weight(1.35f)
                                        .defaultMinSize(minHeight = 48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFDC2626), Color(0xFFF43F5E))
                                            )
                                        )
                                        .testTag("room_clear_biometric_confirm_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("room_clear_verify_btn", currentLang),
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
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

private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
