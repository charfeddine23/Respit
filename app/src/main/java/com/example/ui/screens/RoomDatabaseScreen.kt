package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppThemeStyle
import com.example.data.LeaveRecord
import com.example.data.Personnel
import com.example.ui.PersonnelViewModel
import com.example.ui.theme.DynamicAuroraBackground
import com.example.ui.theme.liquidGlass
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    var showAddLeaveDialog by remember { mutableStateOf(false) }

    val bgColor = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val isDynamicAnimationsEnabled by viewModel.isDynamicAnimationsEnabled.collectAsState()

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(bgColor).testTag("room_database_screen")) {
            DynamicAuroraBackground(isDark = isDarkMode, isNeumorphic = isNeumorphic, animationsEnabled = isDynamicAnimationsEnabled)

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = AppStrings.get("room_db_title", currentLang),
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
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.testTag("room_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = textPrimary
                                )
                            }
                        },
                        actions = {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Room 2.7.0",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (isDarkMode) Color(0xCC0F172A) else Color(0xCCE2E8F0)
                        )
                    )
                }
            ) { innerPadding ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val contentModifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 720.dp)
                        .align(Alignment.TopCenter)

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
                                onSyncLeaves = {
                                    viewModel.syncLeavesFromPersonnel()
                                    Toast.makeText(context, AppStrings.get("room_sync_success", currentLang), Toast.LENGTH_SHORT).show()
                                },
                                onSeedData = {
                                    viewModel.seedRoomWithDefaults()
                                    Toast.makeText(context, AppStrings.get("reset_sample_success", currentLang), Toast.LENGTH_SHORT).show()
                                },
                                onClearData = {
                                    showClearDialog = true
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
                                        isDarkMode = isDarkMode
                                    )
                                }

                                val filteredPersonnel = if (searchQuery.isBlank()) {
                                    personnelList
                                } else {
                                    val q = searchQuery.trim().lowercase(Locale.getDefault())
                                    personnelList.filter {
                                        it.fullName.lowercase(Locale.getDefault()).contains(q) ||
                                                it.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                                                it.rank.lowercase(Locale.getDefault()).contains(q)
                                    }
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
                                // Leave Records Table Content
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SearchBox(
                                            query = searchQuery,
                                            onQueryChange = { searchQuery = it },
                                            placeholder = AppStrings.get("room_search_leaves", currentLang),
                                            isDarkMode = isDarkMode,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { showAddLeaveDialog = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.testTag("room_add_leave_btn")
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(AppStrings.get("room_add_leave_btn", currentLang), fontSize = 12.sp)
                                        }
                                    }
                                }

                                val filteredLeaves = if (searchQuery.isBlank()) {
                                    leavesList
                                } else {
                                    val q = searchQuery.trim().lowercase(Locale.getDefault())
                                    leavesList.filter {
                                        it.personnelName.lowercase(Locale.getDefault()).contains(q) ||
                                                it.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                                                it.leaveType.lowercase(Locale.getDefault()).contains(q) ||
                                                it.rank.lowercase(Locale.getDefault()).contains(q)
                                    }
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
                                            currentLang = currentLang,
                                            onDelete = {
                                                viewModel.deleteLeaveRecord(leave)
                                                Toast.makeText(context, AppStrings.get("delete_success", currentLang), Toast.LENGTH_SHORT).show()
                                            }
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

            // Clear Confirmation Dialog
            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    icon = {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                    },
                    title = {
                        Text(
                            text = AppStrings.get("room_clear_confirm_title", currentLang),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(text = AppStrings.get("room_clear_confirm_desc", currentLang))
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearAllRoomData()
                                showClearDialog = false
                                Toast.makeText(context, AppStrings.get("room_clear_success", currentLang), Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text(AppStrings.get("clear_btn", currentLang), color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) {
                            Text(AppStrings.get("cancel", currentLang))
                        }
                    }
                )
            }

            // Quick Add Leave Dialog
            if (showAddLeaveDialog) {
                AddRoomLeaveDialog(
                    personnelList = personnelList,
                    currentLang = currentLang,
                    isDarkMode = isDarkMode,
                    onDismiss = { showAddLeaveDialog = false },
                    onConfirm = { newLeave ->
                        viewModel.insertLeaveRecord(newLeave)
                        showAddLeaveDialog = false
                        Toast.makeText(context, "تم حفظ الإجازة في Room بنجاح", Toast.LENGTH_SHORT).show()
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
    onSyncLeaves: () -> Unit,
    onSeedData: () -> Unit,
    onClearData: () -> Unit
) {
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        shadowElevation = if (isNeumorphic) 4.dp else 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
        ),
        modifier = Modifier.fillMaxWidth().testTag("room_header_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SQLite Room Database",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                        Text(
                            text = "personnel_leaves_db • v4",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF0284C7),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "KSP Active ✅",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = AppStrings.get("room_db_desc", currentLang),
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

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

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSyncLeaves,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0284C7)
                    ),
                    modifier = Modifier.testTag("room_sync_leaves_btn")
                ) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.get("room_sync_leaves", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onSeedData,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF10B981)
                    ),
                    modifier = Modifier.testTag("room_seed_defaults_btn")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.get("room_seed_btn", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onClearData,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC2626)
                    ),
                    modifier = Modifier.testTag("room_clear_tables_btn")
                ) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(AppStrings.get("room_clear_btn", currentLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f)),
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
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, fontSize = 13.sp, color = Color(0xFF94A3B8)) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1),
            focusedContainerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White,
            unfocusedContainerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White,
            focusedTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A),
            unfocusedTextColor = if (isDarkMode) Color.White else Color(0xFF0F172A)
        ),
        modifier = modifier.fillMaxWidth()
    )
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
                        text = personnel.fullName,
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
                            text = personnel.status.labelAr,
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
                        text = "${personnel.rank} • ${personnel.registrationNumber}",
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
                    text = "${personnel.leaveType} (${personnel.totalLeaveDays} يوم) • العودة: ${personnel.formattedReturnDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary,
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
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
                            text = leave.leaveType,
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
                    text = "${leave.rank} • ${leave.registrationNumber}",
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
                        text = "${leave.formattedStartDate} ➔ ${leave.formattedReturnDate} (${leave.durationDays} يوم)",
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondary,
                        fontSize = 11.sp
                    )
                }

                if (leave.destinationWilaya.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "الوجهة: ${leave.destinationWilaya}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF0284C7),
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
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
                    text = "Room SQLite Engine Diagnostics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            DiagnosticRow("Database File", "personnel_leaves_db", textPrimary, textSecondary)
            DiagnosticRow("Database Version", "4 (WAL Mode Enabled)", textPrimary, textSecondary)
            DiagnosticRow("ORM Engine", "AndroidX Room 2.7.0 (KSP 2.3.5)", textPrimary, textSecondary)
            DiagnosticRow("Concurrency", "Kotlin Coroutines & Flow (Dispatchers.IO)", textPrimary, textSecondary)
            DiagnosticRow("Design Pattern", "Clean Repository Pattern (PersonnelRepository)", textPrimary, textSecondary)
            DiagnosticRow("Tables Configured", "personnel, leave_records", textPrimary, textSecondary)
            DiagnosticRow("Total Personnel Rows", "$personnelCount records", Color(0xFF10B981), textSecondary)
            DiagnosticRow("Total Leaves Rows", "$leavesCount records", Color(0xFF7C3AED), textSecondary)
            DiagnosticRow("Encryption & Sandbox", "Device-Private Sandbox (Secure Isolated Storage)", textPrimary, textSecondary)
            DiagnosticRow("Cloud Dependency", "None (Zero Cloud Required - Pure Device Local)", Color(0xFF0284C7), textSecondary)
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
private fun AddRoomLeaveDialog(
    personnelList: List<Personnel>,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LeaveRecord) -> Unit
) {
    var selectedPerson by remember { mutableStateOf(personnelList.firstOrNull()) }
    var leaveType by remember { mutableStateOf("عطلة سنوية") }
    var durationDaysStr by remember { mutableStateOf("10") }
    var destinationWilaya by remember { mutableStateOf(selectedPerson?.wilaya ?: "16 - الجزائر") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = AppStrings.get("room_new_leave_title", currentLang),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "اختر الفرد لتسجيل الإجازة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )

                if (personnelList.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        personnelList.take(6).forEach { p ->
                            val isSelected = selectedPerson?.id == p.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedPerson = p
                                    destinationWilaya = p.wilaya
                                },
                                label = { Text(p.fullName, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = leaveType,
                    onValueChange = { leaveType = it },
                    label = { Text("نوع الإجازة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = durationDaysStr,
                    onValueChange = { durationDaysStr = it },
                    label = { Text("مدة الإجازة بالأيام") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destinationWilaya,
                    onValueChange = { destinationWilaya = it },
                    label = { Text("الولاية / الوجهة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = durationDaysStr.toLongOrNull() ?: 10L
                    val now = System.currentTimeMillis()
                    val end = now + days * 86400000L
                    val pName = selectedPerson?.fullName ?: "فرد مسجل"
                    val pRank = selectedPerson?.rank ?: "رتبة"
                    val pReg = selectedPerson?.registrationNumber ?: "DZ-000000"

                    val record = LeaveRecord(
                        personnelId = selectedPerson?.id ?: 0L,
                        personnelName = pName,
                        rank = pRank,
                        registrationNumber = pReg,
                        leaveType = leaveType,
                        startDateMillis = now,
                        returnDateMillis = end,
                        durationDays = days,
                        statusText = "جارية (بالبيت)",
                        destinationWilaya = destinationWilaya,
                        notes = notes
                    )
                    onConfirm(record)
                }
            ) {
                Text(AppStrings.get("confirm", currentLang))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.get("cancel", currentLang))
            }
        }
    )
}
