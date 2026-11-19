package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Personnel
import com.example.data.algerianLeaveTypes
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.ui.theme.EmeraldSuccess
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddNewLeaveDialog(
    personnelList: List<Personnel>,
    initialPersonnelId: Long? = null,
    onDismiss: () -> Unit,
    onOpenAddPersonnel: () -> Unit,
    onConfirm: (
        personnelId: Long,
        leaveType: String,
        startDateMillis: Long,
        returnDateMillis: Long
    ) -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    fun getOffsetMillis(baseMillis: Long, daysOffset: Int): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = baseMillis
            add(Calendar.DAY_OF_YEAR, daysOffset)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    val initialSelected = remember(personnelList, initialPersonnelId) {
        personnelList.firstOrNull { it.id == initialPersonnelId } ?: personnelList.firstOrNull()
    }
    var selectedPersonnelId by remember(personnelList) {
        mutableStateOf(initialSelected?.id)
    }
    val selectedPersonnel = remember(personnelList, selectedPersonnelId) {
        personnelList.firstOrNull { it.id == selectedPersonnelId }
    }

    var isPersonnelPickerExpanded by remember { mutableStateOf(false) }
    var personnelSearchQuery by remember { mutableStateOf("") }

    var selectedLeaveType by remember {
        mutableStateOf(initialSelected?.leaveType?.takeIf { it.isNotBlank() } ?: algerianLeaveTypes.first())
    }

    val todayStart = remember { Personnel.toStartOfDay(System.currentTimeMillis()) }
    var startDateMillis by remember { mutableLongStateOf(todayStart) }
    // Default 8 days inclusive: day 0 to day +7
    var returnDateMillis by remember { mutableLongStateOf(getOffsetMillis(todayStart, 7)) }
    var daysInputText by remember {
        mutableStateOf(Personnel.inclusiveLeaveDays(startDateMillis, returnDateMillis).toString())
    }

    fun applyDaysCount(daysCount: Int) {
        val clamped = daysCount.coerceIn(1, 365)
        daysInputText = clamped.toString()
        returnDateMillis = getOffsetMillis(startDateMillis, clamped - 1)
    }

    val currentInclusiveDays = remember(startDateMillis, returnDateMillis) {
        Personnel.inclusiveLeaveDays(startDateMillis, returnDateMillis)
    }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    val startCal = Calendar.getInstance().apply { timeInMillis = startDateMillis }
    val startDatePickerDialog = remember(startDateMillis) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val newStart = c.timeInMillis
                startDateMillis = newStart
                if (returnDateMillis < newStart) {
                    val preservedDays = (daysInputText.toIntOrNull() ?: currentInclusiveDays.toInt()).coerceAtLeast(1)
                    returnDateMillis = getOffsetMillis(newStart, preservedDays - 1)
                }
                daysInputText = Personnel.inclusiveLeaveDays(startDateMillis, returnDateMillis).toString()
            },
            startCal.get(Calendar.YEAR),
            startCal.get(Calendar.MONTH),
            startCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val returnCal = Calendar.getInstance().apply { timeInMillis = returnDateMillis }
    val returnDatePickerDialog = remember(returnDateMillis, startDateMillis) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                returnDateMillis = if (c.timeInMillis < startDateMillis) startDateMillis else c.timeInMillis
                daysInputText = Personnel.inclusiveLeaveDays(startDateMillis, returnDateMillis).toString()
            },
            returnCal.get(Calendar.YEAR),
            returnCal.get(Calendar.MONTH),
            returnCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val filteredPersonnel = remember(personnelList, personnelSearchQuery, currentLang) {
        val q = personnelSearchQuery.trim().lowercase(Locale.getDefault())
        if (q.isEmpty()) personnelList
        else personnelList.filter { p ->
            p.fullName.lowercase(Locale.getDefault()).contains(q) ||
                p.fullNameLatin.lowercase(Locale.getDefault()).contains(q) ||
                p.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                formatRank(p.rank, currentLang).lowercase(Locale.getDefault()).contains(q)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val dialogContainerBg = when {
        isNeumorphic && isDarkMode -> Color(0xFF161F30)
        isNeumorphic -> Color(0xFFE6EEF8)
        isDarkMode -> Color(0xF50F172A)
        else -> Color(0xFAFFFFFF)
    }
    val sectionCardBg = when {
        isNeumorphic && isDarkMode -> Color(0xFF1C273B)
        isNeumorphic -> Color(0xFFF0F5FC)
        isDarkMode -> Color(0xFF182338)
        else -> Color(0xFFF8FAFC)
    }
    val sectionBorderColor = if (isDarkMode) primaryColor.copy(alpha = 0.24f) else primaryColor.copy(alpha = 0.16f)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedContainerColor = if (isDarkMode) Color(0x400F172A) else Color.White,
        unfocusedContainerColor = if (isDarkMode) Color(0x280F172A) else Color.White.copy(alpha = 0.85f),
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = if (isDarkMode) Color(0x45475569) else Color(0x55CBD5E1),
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = textSecondary,
        cursorColor = primaryColor
    )

    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }
    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Dialog(onDismissRequest = onDismiss) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            AnimatedVisibility(
                visibleState = entranceState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(200, easing = FastOutSlowInEasing)) + scaleIn(
                        initialScale = 0.93f,
                        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMedium)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(140)) + scaleOut(targetScale = 0.95f)
                } else ExitTransition.None
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = dialogContainerBg,
                    border = BorderStroke(
                        1.3.dp,
                        Brush.linearGradient(
                            listOf(
                                primaryColor.copy(alpha = if (isDarkMode) 0.50f else 0.38f),
                                secondaryColor.copy(alpha = if (isDarkMode) 0.35f else 0.25f),
                                Color.White.copy(alpha = if (isDarkMode) 0.14f else 0.90f)
                            )
                        )
                    ),
                    shadowElevation = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                        .heightIn(max = 680.dp)
                        .imePadding()
                        .testTag("add_leave_dialog")
                ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                primaryColor.copy(alpha = if (isDarkMode) 0.18f else 0.10f),
                                                secondaryColor.copy(alpha = if (isDarkMode) 0.10f else 0.05f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            ) {
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
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    Brush.linearGradient(listOf(primaryColor, secondaryColor))
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EventAvailable,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = AppStrings.get("add_leave_dialog_title", currentLang),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black,
                                                color = textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = AppStrings.get("add_leave_dialog_sub", currentLang),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                color = textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = onDismiss,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .testTag("add_leave_close_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = AppStrings.get("close", currentLang),
                                            tint = textSecondary
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = sectionBorderColor)

                            if (personnelList.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = AppStrings.get("no_personnel_for_leave", currentLang),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onOpenAddPersonnel()
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("add_leave_open_add_personnel_btn")
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(AppStrings.get("add_personnel", currentLang), fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                        .testTag("add_leave_form_scroll"),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // 1. Personnel Selector Section
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = sectionCardBg,
                                        border = BorderStroke(1.dp, sectionBorderColor),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = AppStrings.get("select_personnel_label", currentLang),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = primaryColor
                                            )

                                            Surface(
                                                onClick = { isPersonnelPickerExpanded = !isPersonnelPickerExpanded },
                                                shape = RoundedCornerShape(14.dp),
                                                color = if (isDarkMode) Color(0x380F172A) else Color.White,
                                                border = BorderStroke(
                                                    1.2.dp,
                                                    if (isPersonnelPickerExpanded) primaryColor else sectionBorderColor
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("add_leave_personnel_selector")
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Person,
                                                            contentDescription = null,
                                                            tint = primaryColor,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = selectedPersonnel?.getLocalizedName(currentLang)
                                                                    ?: AppStrings.get("select_personnel_label", currentLang),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 14.sp,
                                                                color = textPrimary,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            if (selectedPersonnel != null) {
                                                                Text(
                                                                    text = "${formatRank(selectedPersonnel.rank, currentLang)} • ${selectedPersonnel.registrationNumber}",
                                                                    fontSize = 11.5.sp,
                                                                    color = textSecondary,
                                                                    maxLines = 1,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Icon(
                                                        imageVector = if (isPersonnelPickerExpanded) {
                                                            Icons.Default.KeyboardArrowUp
                                                        } else {
                                                            Icons.Default.KeyboardArrowDown
                                                        },
                                                        contentDescription = null,
                                                        tint = primaryColor
                                                    )
                                                }
                                            }

                                            AnimatedVisibility(visible = isPersonnelPickerExpanded) {
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    OutlinedTextField(
                                                        value = personnelSearchQuery,
                                                        onValueChange = { personnelSearchQuery = it },
                                                        placeholder = {
                                                            Text(
                                                                AppStrings.get("search_personnel_hint", currentLang),
                                                                fontSize = 12.5.sp
                                                            )
                                                        },
                                                        leadingIcon = {
                                                            Icon(
                                                                imageVector = Icons.Default.Search,
                                                                contentDescription = null,
                                                                tint = primaryColor
                                                            )
                                                        },
                                                        trailingIcon = {
                                                            if (personnelSearchQuery.isNotEmpty()) {
                                                                IconButton(onClick = { personnelSearchQuery = "" }) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Close,
                                                                        contentDescription = AppStrings.get("clear_search", currentLang),
                                                                        modifier = Modifier.size(18.dp)
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        singleLine = true,
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors = inputColors,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .testTag("add_leave_personnel_search")
                                                    )

                                                    LazyColumn(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .heightIn(max = 190.dp),
                                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        items(filteredPersonnel, key = { it.id }) { person ->
                                                            val isSelected = person.id == selectedPersonnel?.id
                                                            Surface(
                                                                onClick = {
                                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                    selectedPersonnelId = person.id
                                                                    isPersonnelPickerExpanded = false
                                                                    personnelSearchQuery = ""
                                                                },
                                                                shape = RoundedCornerShape(12.dp),
                                                                color = when {
                                                                    isSelected -> primaryColor.copy(alpha = if (isDarkMode) 0.25f else 0.14f)
                                                                    isDarkMode -> Color(0x280F172A)
                                                                    else -> Color.White
                                                                },
                                                                border = BorderStroke(
                                                                    1.dp,
                                                                    if (isSelected) primaryColor else sectionBorderColor
                                                                ),
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .testTag("add_leave_personnel_item_${person.id}")
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                                ) {
                                                                    Column(modifier = Modifier.weight(1f)) {
                                                                        Text(
                                                                            text = person.getLocalizedName(currentLang),
                                                                            fontWeight = FontWeight.Bold,
                                                                            fontSize = 13.5.sp,
                                                                            color = textPrimary,
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis
                                                                        )
                                                                        Text(
                                                                            text = "${formatRank(person.rank, currentLang)} • ${person.registrationNumber}",
                                                                            fontSize = 11.sp,
                                                                            color = textSecondary,
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis
                                                                        )
                                                                    }
                                                                    if (isSelected) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Check,
                                                                            contentDescription = null,
                                                                            tint = primaryColor,
                                                                            modifier = Modifier.size(18.dp)
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

                                    // 2. Leave Type Section
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = sectionCardBg,
                                        border = BorderStroke(1.dp, sectionBorderColor),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Text(
                                                text = AppStrings.get("leave_type_label", currentLang),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = primaryColor
                                            )

                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                algerianLeaveTypes.forEachIndexed { index, type ->
                                                    val isSelected = selectedLeaveType == type
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            selectedLeaveType = type
                                                        },
                                                        label = {
                                                            Text(
                                                                text = formatLeaveType(type, currentLang),
                                                                fontSize = 12.sp,
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                            )
                                                        },
                                                        leadingIcon = if (isSelected) {
                                                            {
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }
                                                        } else null,
                                                        colors = FilterChipDefaults.filterChipColors(
                                                            selectedContainerColor = primaryColor.copy(alpha = if (isDarkMode) 0.28f else 0.16f),
                                                            selectedLabelColor = primaryColor,
                                                            selectedLeadingIconColor = primaryColor
                                                        ),
                                                        modifier = Modifier.testTag("add_leave_type_option_$index")
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // 3. Departure Date, Return Date & Number of Days
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = sectionCardBg,
                                        border = BorderStroke(1.dp, sectionBorderColor),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = AppStrings.get("dialog_sec_schedule", currentLang),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = primaryColor
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Surface(
                                                    onClick = { startDatePickerDialog.show() },
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = if (isDarkMode) Color(0x380F172A) else Color.White,
                                                    border = BorderStroke(1.dp, sectionBorderColor),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .heightIn(min = 56.dp)
                                                        .testTag("add_leave_start_date_btn")
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.CalendarMonth,
                                                                contentDescription = null,
                                                                tint = primaryColor,
                                                                modifier = Modifier.size(15.dp)
                                                            )
                                                            Text(
                                                                text = AppStrings.get("departure_date_label", currentLang),
                                                                fontSize = 11.sp,
                                                                color = textSecondary,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                        Text(
                                                            text = "\u200E${sdf.format(Date(startDateMillis))}\u200E",
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.5.sp,
                                                            color = textPrimary
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    onClick = { returnDatePickerDialog.show() },
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = if (isDarkMode) Color(0x380F172A) else Color.White,
                                                    border = BorderStroke(1.dp, sectionBorderColor),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .heightIn(min = 56.dp)
                                                        .testTag("add_leave_return_date_btn")
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.CalendarMonth,
                                                                contentDescription = null,
                                                                tint = EmeraldSuccess,
                                                                modifier = Modifier.size(15.dp)
                                                            )
                                                            Text(
                                                                text = AppStrings.get("leave_return_date_short", currentLang),
                                                                fontSize = 11.sp,
                                                                color = textSecondary,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                        Text(
                                                            text = "\u200E${sdf.format(Date(returnDateMillis))}\u200E",
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.5.sp,
                                                            color = textPrimary
                                                        )
                                                    }
                                                }
                                            }

                                            // Number of Days Stepper + Editable TextField
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        val current = currentInclusiveDays.toInt()
                                                        if (current > 1) applyDaysCount(current - 1)
                                                    },
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(primaryColor.copy(alpha = 0.12f))
                                                        .testTag("add_leave_days_minus")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Remove,
                                                        contentDescription = null,
                                                        tint = primaryColor
                                                    )
                                                }

                                                OutlinedTextField(
                                                    value = daysInputText,
                                                    onValueChange = { raw ->
                                                        val digitsOnly = raw.filter { it.isDigit() }.take(3)
                                                        daysInputText = digitsOnly
                                                        val parsed = digitsOnly.toIntOrNull()
                                                        if (parsed != null && parsed >= 1) {
                                                            returnDateMillis = getOffsetMillis(startDateMillis, parsed.coerceAtMost(365) - 1)
                                                        }
                                                    },
                                                    label = {
                                                        Text(
                                                            AppStrings.get("leave_days_count_label", currentLang),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    },
                                                    suffix = {
                                                        Text(
                                                            text = AppStrings.get("days", currentLang),
                                                            fontSize = 12.sp,
                                                            color = textSecondary
                                                        )
                                                    },
                                                    singleLine = true,
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    shape = RoundedCornerShape(14.dp),
                                                    colors = inputColors,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .testTag("add_leave_days_input")
                                                )

                                                IconButton(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        applyDaysCount(currentInclusiveDays.toInt() + 1)
                                                    },
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(primaryColor.copy(alpha = 0.12f))
                                                        .testTag("add_leave_days_plus")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = null,
                                                        tint = primaryColor
                                                    )
                                                }
                                            }

                                            // Quick duration chips
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf(2, 4, 8, 10, 15).forEach { quickDays ->
                                                    val isMatch = currentInclusiveDays == quickDays.toLong()
                                                    Surface(
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            applyDaysCount(quickDays)
                                                        },
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = if (isMatch) primaryColor else primaryColor.copy(alpha = 0.09f),
                                                        border = BorderStroke(
                                                            1.dp,
                                                            if (isMatch) primaryColor else primaryColor.copy(alpha = 0.22f)
                                                        ),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .heightIn(min = 40.dp)
                                                            .testTag("add_leave_quick_days_$quickDays")
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "$quickDays",
                                                                fontSize = 12.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isMatch) Color.White else primaryColor
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = sectionBorderColor)

                                // Action Footer
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = onDismiss,
                                        shape = RoundedCornerShape(14.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp),
                                        modifier = Modifier
                                            .weight(0.85f)
                                            .height(48.dp)
                                            .testTag("cancel_add_leave_button")
                                    ) {
                                        Text(
                                            text = AppStrings.get("cancel", currentLang),
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val target = selectedPersonnel ?: return@Button
                                            val effectiveDays = daysInputText.toIntOrNull()?.coerceIn(1, 365)
                                                ?: currentInclusiveDays.toInt().coerceAtLeast(1)
                                            val finalReturn = getOffsetMillis(startDateMillis, effectiveDays - 1)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onConfirm(
                                                target.id,
                                                selectedLeaveType,
                                                startDateMillis,
                                                finalReturn
                                            )
                                        },
                                        enabled = selectedPersonnel != null,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EmeraldSuccess,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        modifier = Modifier
                                            .weight(1.35f)
                                            .height(48.dp)
                                            .testTag("confirm_add_leave_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("confirm", currentLang),
                                            fontWeight = FontWeight.ExtraBold,
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
