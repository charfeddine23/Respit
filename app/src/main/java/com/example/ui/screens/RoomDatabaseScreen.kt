package com.example.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.example.data.LeaveRecord
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.PersonnelViewModel
import com.example.ui.components.LiquidGlassActionButton
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
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
    onBack: () -> Unit,
    topBarClearance: Dp = 0.dp,
    bottomDockClearance: Dp = 0.dp
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val activePreset = LocalThemePreset.current
    val effectiveDark = activePreset?.isDark ?: isDarkMode
    val primaryAccent = activePreset?.primary ?: if (effectiveDark) CyanGlow else RoyalBlueAccent
    val secondaryAccent = activePreset?.secondary ?: VibrantCyan

    val personnelList by viewModel.allPersonnel.collectAsState()
    val leavesList by viewModel.allLeaves.collectAsState()
    val personnelCount by viewModel.personnelCount.collectAsState()
    val leavesCount by viewModel.leavesCount.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    val textPrimary = activePreset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = activePreset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
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
            Column(modifier = Modifier.fillMaxSize().padding(top = topBarClearance)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 720.dp)
                            .liquidGlass(
                                shape = RoundedCornerShape(24.dp),
                                elevation = 5.dp,
                                borderAlpha = 0.9f,
                                isDark = effectiveDark,
                                cornerRadius = 24.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(14.dp),
                                            cornerRadius = 14.dp,
                                            isDark = effectiveDark,
                                            accent = primaryAccent,
                                            secondaryAccent = secondaryAccent,
                                            isSelected = true,
                                            softTintWhenSelected = false
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = AppStrings.get("nav_database", currentLang),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = AppStrings.get("room_db_sub", currentLang),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.liquidGlassPanel(
                                        shape = RoundedCornerShape(50.dp),
                                        cornerRadius = 50.dp,
                                        isDark = effectiveDark,
                                        accent = EmeraldSuccess,
                                        secondaryAccent = EmeraldSuccess,
                                        isSelected = true,
                                        softTintWhenSelected = true
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldSuccess)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Room 2.7.0",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp,
                                            color = EmeraldSuccess,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(50.dp),
                                            cornerRadius = 50.dp,
                                            isDark = effectiveDark,
                                            accent = EmeraldSuccess,
                                            secondaryAccent = VibrantCyan,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .testTag("room_ksp_badge")
                                ) {
                                    Text(
                                        text = AppStrings.get("room_ksp_active", currentLang),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                    )
                                }
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
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = maxOf(bottomDockClearance + 24.dp, 116.dp)
                        )
                    ) {
                        // 1. Room Header Info Card
                        item {
                            RoomDatabaseHeaderCard(
                                isDarkMode = effectiveDark,
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

                        // 2. Liquid Glass 3-Pill Segmented Navigation Selector
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlassPanel(
                                        shape = RoundedCornerShape(20.dp),
                                        cornerRadius = 20.dp,
                                        isDark = effectiveDark,
                                        accent = primaryAccent,
                                        secondaryAccent = secondaryAccent
                                    )
                                    .padding(5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val tabs = listOf(
                                        Triple(
                                            0,
                                            "${AppStrings.get("room_tab_personnel", currentLang)} ($personnelCount)",
                                            "room_tab_personnel"
                                        ),
                                        Triple(
                                            1,
                                            "${AppStrings.get("room_tab_leaves", currentLang)} ($leavesCount)",
                                            "room_tab_leaves"
                                        ),
                                        Triple(
                                            2,
                                            AppStrings.get("room_tab_diagnostics", currentLang),
                                            "room_tab_diagnostics"
                                        )
                                    )
                                    tabs.forEach { (index, label, tag) ->
                                        val selected = selectedTab == index
                                        Surface(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                selectedTab = index
                                            },
                                            shape = RoundedCornerShape(15.dp),
                                            color = Color.Transparent,
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 48.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(15.dp),
                                                    cornerRadius = 15.dp,
                                                    isDark = effectiveDark,
                                                    accent = primaryAccent,
                                                    secondaryAccent = secondaryAccent,
                                                    isSelected = selected,
                                                    softTintWhenSelected = false
                                                )
                                                .testTag(tag)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = if (selected) Color.White else textSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Tab Contents
                        when (selectedTab) {
                            0 -> {
                                item {
                                    SearchBox(
                                        query = searchQuery,
                                        onQueryChange = { searchQuery = it },
                                        placeholder = AppStrings.get("search_placeholder", currentLang),
                                        isDarkMode = effectiveDark,
                                        clearDesc = AppStrings.get("clear_icon_desc", currentLang)
                                    )
                                }

                                if (filteredPersonnel.isEmpty()) {
                                    item {
                                        EmptyDatabaseState(
                                            isDarkMode = effectiveDark,
                                            title = AppStrings.get("no_results", currentLang),
                                            description = AppStrings.get("no_results_desc", currentLang)
                                        )
                                    }
                                } else {
                                    items(filteredPersonnel, key = { it.id }) { person ->
                                        RoomPersonnelRowCard(
                                            personnel = person,
                                            isDarkMode = effectiveDark,
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
                                item {
                                    SearchBox(
                                        query = searchQuery,
                                        onQueryChange = { searchQuery = it },
                                        placeholder = AppStrings.get("room_search_leaves", currentLang),
                                        isDarkMode = effectiveDark,
                                        clearDesc = AppStrings.get("clear_icon_desc", currentLang)
                                    )
                                }

                                if (filteredLeaves.isEmpty()) {
                                    item {
                                        EmptyDatabaseState(
                                            isDarkMode = effectiveDark,
                                            title = AppStrings.get("room_no_leaves", currentLang),
                                            description = AppStrings.get("room_no_leaves_desc", currentLang)
                                        )
                                    }
                                } else {
                                    items(filteredLeaves, key = { it.id }) { leave ->
                                        RoomLeaveRecordRowCard(
                                            leave = leave,
                                            isDarkMode = effectiveDark,
                                            currentLang = currentLang
                                        )
                                    }
                                }
                            }
                            2 -> {
                                item {
                                    RoomDiagnosticsCard(
                                        isDarkMode = effectiveDark,
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
    currentLang: AppLanguage,
    personnelCount: Int,
    leavesCount: Int,
    onClearData: () -> Unit,
    onExportExcel: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                borderAlpha = 0.88f,
                isDark = effectiveDark,
                cornerRadius = 24.dp
            )
            .testTag("room_header_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RoomStatBox(
                    label = AppStrings.get("room_total_personnel_count", currentLang),
                    value = "$personnelCount",
                    color = preset?.primary ?: MaterialTheme.colorScheme.primary,
                    isDarkMode = effectiveDark,
                    modifier = Modifier.weight(1f)
                )
                RoomStatBox(
                    label = AppStrings.get("room_total_leaves_count", currentLang),
                    value = "$leavesCount",
                    color = EmeraldSuccess,
                    isDarkMode = effectiveDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val themePrimary = preset?.primary ?: MaterialTheme.colorScheme.primary
            val themeSecondary = preset?.secondary ?: MaterialTheme.colorScheme.secondary
            val buttonShape = RoundedCornerShape(16.dp)
            val buttonRadius = 16.dp

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClearData()
                        },
                        interactionSource = clearInteraction,
                        shape = buttonShape,
                        color = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .graphicsLayer {
                                scaleX = clearScale
                                scaleY = clearScale
                            }
                            .liquidGlassPanel(
                                shape = buttonShape,
                                cornerRadius = buttonRadius,
                                isDark = effectiveDark,
                                accent = themePrimary,
                                secondaryAccent = themeSecondary,
                                isSelected = true,
                                softTintWhenSelected = true
                            )
                            .testTag("room_clear_tables_btn")
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides contentLayoutDir) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
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

                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onExportExcel()
                        },
                        interactionSource = exportInteraction,
                        shape = buttonShape,
                        color = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .graphicsLayer {
                                scaleX = exportScale
                                scaleY = exportScale
                            }
                            .liquidGlassPanel(
                                shape = buttonShape,
                                cornerRadius = buttonRadius,
                                isDark = effectiveDark,
                                accent = themePrimary,
                                secondaryAccent = Color(0xFF0D9488),
                                isSelected = true,
                                softTintWhenSelected = false
                            )
                            .testTag("room_export_xlsx_btn")
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides contentLayoutDir) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
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
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

    Box(
        modifier = modifier
            .liquidGlassPanel(
                shape = RoundedCornerShape(16.dp),
                cornerRadius = 16.dp,
                isDark = effectiveDark,
                accent = color,
                secondaryAccent = color,
                isSelected = true,
                softTintWhenSelected = true
            )
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textSecondary,
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
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val haptic = LocalHapticFeedback.current
    var isFocused by remember { mutableStateOf(false) }

    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val hintColor = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val shape = RoundedCornerShape(18.dp)

    val searchAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlassPanel(
                shape = shape,
                cornerRadius = 18.dp,
                isDark = effectiveDark,
                accent = searchAccent,
                secondaryAccent = RoyalBlueAccent,
                isSelected = isFocused,
                softTintWhenSelected = true
            )
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .liquidGlassPanel(
                        shape = CircleShape,
                        cornerRadius = 15.dp,
                        isDark = effectiveDark,
                        accent = searchAccent,
                        secondaryAccent = RoyalBlueAccent,
                        isSelected = true,
                        softTintWhenSelected = true
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = searchAccent,
                    modifier = Modifier.size(16.dp)
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
                    cursorBrush = SolidColor(searchAccent),
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
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(hintColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = clearDesc,
                            tint = hintColor,
                            modifier = Modifier.size(14.dp)
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
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val statusColor = Color(personnel.status.hexColor)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(18.dp),
                elevation = 3.dp,
                borderAlpha = 0.85f,
                isDark = effectiveDark,
                cornerRadius = 18.dp
            )
            .testTag("room_personnel_item_${personnel.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .liquidGlassPanel(
                        shape = CircleShape,
                        cornerRadius = 22.dp,
                        isDark = effectiveDark,
                        accent = statusColor,
                        secondaryAccent = statusColor,
                        isSelected = true,
                        softTintWhenSelected = true
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = statusColor,
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
                    Box(
                        modifier = Modifier.liquidGlassPanel(
                            shape = RoundedCornerShape(8.dp),
                            cornerRadius = 8.dp,
                            isDark = effectiveDark,
                            accent = statusColor,
                            secondaryAccent = statusColor,
                            isSelected = true,
                            softTintWhenSelected = true
                        )
                    ) {
                        Text(
                            text = personnel.status.getCompactBadgeLabel(currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
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
                        color = VibrantCyan,
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

            Surface(
                onClick = onDelete,
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier
                    .size(48.dp)
                    .liquidGlassPanel(
                        shape = CircleShape,
                        cornerRadius = 24.dp,
                        isDark = effectiveDark,
                        accent = RedDanger,
                        secondaryAccent = RedDanger,
                        isSelected = true,
                        softTintWhenSelected = true
                    )
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = AppStrings.get("delete", currentLang),
                        tint = RedDanger,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val leaveAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(18.dp),
                elevation = 3.dp,
                borderAlpha = 0.85f,
                isDark = effectiveDark,
                cornerRadius = 18.dp
            )
            .testTag("room_leave_item_${leave.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .liquidGlassPanel(
                        shape = CircleShape,
                        cornerRadius = 22.dp,
                        isDark = effectiveDark,
                        accent = leaveAccent,
                        secondaryAccent = RoyalBlueAccent,
                        isSelected = true,
                        softTintWhenSelected = true
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = leaveAccent,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = Personnel.resolveLocalizedName(leave.personnelName, leave.personnelNameLatin, currentLang),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.liquidGlassPanel(
                            shape = RoundedCornerShape(8.dp),
                            cornerRadius = 8.dp,
                            isDark = effectiveDark,
                            accent = leaveAccent,
                            secondaryAccent = leaveAccent,
                            isSelected = true,
                            softTintWhenSelected = true
                        )
                    ) {
                        Text(
                            text = formatLeaveType(leave.leaveType, currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = leaveAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
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
                        color = VibrantCyan,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier.liquidGlassPanel(
                    shape = RoundedCornerShape(10.dp),
                    cornerRadius = 10.dp,
                    isDark = effectiveDark,
                    accent = EmeraldSuccess,
                    secondaryAccent = EmeraldSuccess,
                    isSelected = true,
                    softTintWhenSelected = true
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = AppStrings.get("room_immutable_badge", currentLang),
                    tint = EmeraldSuccess,
                    modifier = Modifier.padding(8.dp).size(16.dp)
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
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val accent = preset?.primary ?: MaterialTheme.colorScheme.primary
    val recUnit = AppStrings.get("room_diag_records_unit", currentLang)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(22.dp),
                elevation = 4.dp,
                borderAlpha = 0.88f,
                isDark = effectiveDark,
                cornerRadius = 22.dp
            )
            .testTag("room_diagnostics_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(10.dp),
                            cornerRadius = 10.dp,
                            isDark = effectiveDark,
                            accent = accent,
                            secondaryAccent = VibrantCyan,
                            isSelected = true,
                            softTintWhenSelected = true
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = AppStrings.get("room_diag_title", currentLang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            DiagnosticRow(AppStrings.get("room_diag_db_file", currentLang), "personnel_leaves_db", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_version", currentLang), "9 (WAL Mode)", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_orm", currentLang), "AndroidX Room 2.7.0 (KSP)", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_concurrency", currentLang), "Coroutines & Flow (IO)", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_pattern", currentLang), "Repository Pattern", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_tables", currentLang), "personnel, leave_records", textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_personnel_rows", currentLang), "$personnelCount $recUnit", EmeraldSuccess, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_leaves_rows", currentLang), "$leavesCount $recUnit", accent, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_sandbox", currentLang), AppStrings.get("room_diag_sandbox_val", currentLang), textPrimary, textSecondary)
            DiagnosticRow(AppStrings.get("room_diag_cloud", currentLang), AppStrings.get("room_diag_local_only", currentLang), accent, textSecondary)
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
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(22.dp),
                elevation = 4.dp,
                isDark = effectiveDark,
                cornerRadius = 22.dp
            )
            .padding(vertical = 32.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .liquidGlassPanel(
                        shape = CircleShape,
                        cornerRadius = 32.dp,
                        isDark = effectiveDark,
                        accent = VibrantCyan,
                        secondaryAccent = RoyalBlueAccent,
                        isSelected = true,
                        softTintWhenSelected = true
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = VibrantCyan,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
        }
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

    val enterState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }

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

    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val cardShape = RoundedCornerShape(28.dp)
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visibleState = enterState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(240)) + scaleIn(
                        initialScale = 0.88f,
                        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)
                    )
                } else androidx.compose.animation.EnterTransition.None
            ) {
                Surface(
                    shape = cardShape,
                    color = Color.Transparent,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                        .liquidGlassDialogShell(
                            shape = cardShape,
                            cornerRadius = 28.dp,
                            isDark = effectiveDark,
                            primaryAccent = RedDanger,
                            secondaryAccent = RedDanger
                        )
                        .testTag("room_clear_confirm_dialog")
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(68.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .liquidGlassPanel(
                                        shape = CircleShape,
                                        cornerRadius = 28.dp,
                                        isDark = effectiveDark,
                                        accent = RedDanger,
                                        secondaryAccent = Color(0xFFF43F5E),
                                        isSelected = true,
                                        softTintWhenSelected = false
                                    ),
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
                                    .background(if (effectiveDark) Color(0xFF101827) else Color.White)
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

                        Box(
                            modifier = Modifier.liquidGlassPanel(
                                shape = RoundedCornerShape(999.dp),
                                cornerRadius = 50.dp,
                                isDark = effectiveDark,
                                accent = RedDanger,
                                secondaryAccent = RedDanger,
                                isSelected = true,
                                softTintWhenSelected = true
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = RedDanger,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("room_clear_biometric_badge", currentLang),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (effectiveDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
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

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .liquidGlassPanel(
                                    shape = RoundedCornerShape(16.dp),
                                    cornerRadius = 16.dp,
                                    isDark = effectiveDark,
                                    accent = RedDanger,
                                    secondaryAccent = RedDanger
                                )
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = AppStrings.get("room_clear_confirm_desc", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        AnimatedVisibility(visible = authError != null) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(12.dp),
                                            cornerRadius = 12.dp,
                                            isDark = effectiveDark,
                                            accent = RedDanger,
                                            secondaryAccent = RedDanger,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = authError.orEmpty(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (effectiveDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LiquidGlassActionButton(
                                text = AppStrings.get("cancel", currentLang),
                                onClick = onDismiss,
                                isDark = effectiveDark,
                                primaryAccent = if (effectiveDark) CyanGlow else VibrantCyan,
                                secondaryAccent = RoyalBlueAccent,
                                isPrimary = false,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .testTag("room_clear_cancel_btn")
                            )

                            LiquidGlassActionButton(
                                text = AppStrings.get("room_clear_verify_btn", currentLang),
                                onClick = { launchBiometricVerification() },
                                isDark = effectiveDark,
                                primaryAccent = Color(0xFFDC2626),
                                secondaryAccent = Color(0xFFF43F5E),
                                icon = Icons.Default.Fingerprint,
                                isPrimary = true,
                                modifier = Modifier
                                    .weight(1.35f)
                                    .testTag("room_clear_biometric_confirm_btn")
                            )
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
