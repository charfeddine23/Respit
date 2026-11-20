package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import com.example.data.isShortLeaveType
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    var showPersonnelPickerDialog by remember { mutableStateOf(false) }
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

    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryColor = preset?.primary ?: if (effectiveDark) CyanGlow else MaterialTheme.colorScheme.primary
    val secondaryColor = preset?.secondary ?: if (effectiveDark) Color(0xFF8B5CF6) else RoyalBlueAccent
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.65f)
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else Color(0xFF0F172A)
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedContainerColor = if (effectiveDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.56f),
        unfocusedContainerColor = if (effectiveDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.36f),
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = if (effectiveDark) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.78f),
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = textSecondary,
        cursorColor = primaryColor
    )

    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }
    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
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
                    color = Color.Transparent,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                        .heightIn(max = 680.dp)
                        .liquidGlassDialogShell(
                            shape = RoundedCornerShape(28.dp),
                            cornerRadius = 28.dp,
                            isDark = effectiveDark,
                            primaryAccent = primaryColor,
                            secondaryAccent = secondaryColor
                        )
                        .testTag("add_leave_dialog")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Liquid Glass Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            primaryColor.copy(alpha = if (effectiveDark) 0.20f else 0.12f),
                                            secondaryColor.copy(alpha = if (effectiveDark) 0.12f else 0.06f),
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
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(14.dp),
                                                cornerRadius = 14.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor,
                                                isSelected = true,
                                                softTintWhenSelected = false
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

                                Surface(
                                    onClick = onDismiss,
                                    shape = CircleShape,
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .liquidGlassPanel(
                                            shape = CircleShape,
                                            cornerRadius = 24.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        )
                                        .testTag("add_leave_close_btn")
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = AppStrings.get("close", currentLang),
                                            tint = textSecondary
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = glassDividerColor)

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
                                Surface(
                                    onClick = {
                                        onDismiss()
                                        onOpenAddPersonnel()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(14.dp),
                                            cornerRadius = 14.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor,
                                            isSelected = true,
                                            softTintWhenSelected = false
                                        )
                                        .testTag("add_leave_open_add_personnel_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.PersonAdd,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            AppStrings.get("add_personnel", currentLang),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                                    .testTag("add_leave_form_scroll"),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // 1. Personnel Selector Section (Liquid Glass Card)
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(20.dp),
                                            cornerRadius = 20.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        )
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
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                showPersonnelPickerDialog = true
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color.Transparent,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(14.dp),
                                                    cornerRadius = 14.dp,
                                                    isDark = effectiveDark,
                                                    accent = primaryColor,
                                                    secondaryAccent = secondaryColor,
                                                    isSelected = showPersonnelPickerDialog,
                                                    softTintWhenSelected = true
                                                )
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
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .liquidGlassPanel(
                                                            shape = CircleShape,
                                                            cornerRadius = 17.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor,
                                                            isSelected = true,
                                                            softTintWhenSelected = true
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = AppStrings.get("select_personnel_label", currentLang),
                                                        tint = primaryColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                    // 2. Leave Type Section (Liquid Glass Card + Glass Pills)
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(20.dp),
                                                cornerRadius = 20.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor
                                            )
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

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                algerianLeaveTypes.withIndex().toList().chunked(2).forEach { pair ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        pair.forEach { (index, type) ->
                                                            val isSelected = selectedLeaveType == type
                                                            Surface(
                                                                onClick = {
                                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                    selectedLeaveType = type
                                                                },
                                                                shape = RoundedCornerShape(14.dp),
                                                                color = Color.Transparent,
                                                                modifier = Modifier
                                                                    .weight(1f)
                                                                    .defaultMinSize(minHeight = 44.dp)
                                                                    .liquidGlassPanel(
                                                                        shape = RoundedCornerShape(14.dp),
                                                                        cornerRadius = 14.dp,
                                                                        isDark = effectiveDark,
                                                                        accent = primaryColor,
                                                                        secondaryAccent = secondaryColor,
                                                                        isSelected = isSelected,
                                                                        softTintWhenSelected = true
                                                                    )
                                                                    .testTag("add_leave_type_option_$index")
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(horizontal = 10.dp, vertical = 10.dp)
                                                                ) {
                                                                    if (isSelected) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Check,
                                                                            contentDescription = null,
                                                                            tint = primaryColor,
                                                                            modifier = Modifier.size(15.dp)
                                                                        )
                                                                        Spacer(modifier = Modifier.width(5.dp))
                                                                    }
                                                                    Text(
                                                                        text = formatLeaveType(type, currentLang),
                                                                        fontSize = 12.sp,
                                                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                                        color = if (isSelected) primaryColor else textPrimary,
                                                                        maxLines = 1,
                                                                        overflow = TextOverflow.Ellipsis
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        if (pair.size == 1) {
                                                            Spacer(modifier = Modifier.weight(1f))
                                                        }
                                                    }
                                                }
                                            }

                                            AnimatedVisibility(
                                                visible = isShortLeaveType(selectedLeaveType),
                                                enter = if (animationsEnabled) fadeIn(tween(150)) else EnterTransition.None,
                                                exit = if (animationsEnabled) fadeOut(tween(120)) else ExitTransition.None
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(14.dp),
                                                            cornerRadius = 14.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor,
                                                            isSelected = true,
                                                            softTintWhenSelected = true
                                                        )
                                                        .testTag("add_leave_short_hint")
                                                ) {
                                                    Text(
                                                        text = AppStrings.get("short_leave_history_only_hint", currentLang),
                                                        fontSize = 11.5.sp,
                                                        lineHeight = 16.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = textPrimary,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // 3. Departure Date, Return Date & Number of Days (Liquid Glass Card)
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(20.dp),
                                                cornerRadius = 20.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor
                                            )
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
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .heightIn(min = 56.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(14.dp),
                                                            cornerRadius = 14.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor
                                                        )
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
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .heightIn(min = 56.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(14.dp),
                                                            cornerRadius = 14.dp,
                                                            isDark = effectiveDark,
                                                            accent = EmeraldSuccess,
                                                            secondaryAccent = primaryColor
                                                        )
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
                                                Surface(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        val current = currentInclusiveDays.toInt()
                                                        if (current > 1) applyDaysCount(current - 1)
                                                    },
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(14.dp),
                                                            cornerRadius = 14.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor
                                                        )
                                                        .testTag("add_leave_days_minus")
                                                ) {
                                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                        Icon(
                                                            imageVector = Icons.Default.Remove,
                                                            contentDescription = null,
                                                            tint = primaryColor
                                                        )
                                                    }
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
                                                    trailingIcon = {
                                                        Text(
                                                            text = AppStrings.get("days", currentLang),
                                                            fontSize = 11.5.sp,
                                                            color = textSecondary,
                                                            modifier = Modifier.padding(horizontal = 8.dp)
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

                                                Surface(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        applyDaysCount(currentInclusiveDays.toInt() + 1)
                                                    },
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(14.dp),
                                                            cornerRadius = 14.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor
                                                        )
                                                        .testTag("add_leave_days_plus")
                                                ) {
                                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                                        Icon(
                                                            imageVector = Icons.Default.Add,
                                                            contentDescription = null,
                                                            tint = primaryColor
                                                        )
                                                    }
                                                }
                                            }

                                            // Quick duration Liquid Glass pills
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
                                                        shape = RoundedCornerShape(50.dp),
                                                        color = Color.Transparent,
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .heightIn(min = 48.dp)
                                                            .liquidGlassPanel(
                                                                shape = RoundedCornerShape(50.dp),
                                                                cornerRadius = 50.dp,
                                                                isDark = effectiveDark,
                                                                accent = primaryColor,
                                                                secondaryAccent = secondaryColor,
                                                                isSelected = isMatch,
                                                                softTintWhenSelected = false
                                                            )
                                                            .testTag("add_leave_quick_days_$quickDays")
                                                    ) {
                                                        Box(
                                                            contentAlignment = Alignment.Center,
                                                            modifier = Modifier.fillMaxSize()
                                                        ) {
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

                                HorizontalDivider(color = glassDividerColor)

                                // Action Footer (Pinned Liquid Glass Buttons)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        onClick = onDismiss,
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .weight(0.85f)
                                            .height(48.dp)
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(16.dp),
                                                cornerRadius = 16.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor
                                            )
                                            .testTag("cancel_add_leave_button")
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Text(
                                                text = AppStrings.get("cancel", currentLang),
                                                fontWeight = FontWeight.Bold,
                                                color = textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = {
                                            val target = selectedPersonnel ?: return@Surface
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
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .weight(1.35f)
                                            .height(48.dp)
                                            .shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(16.dp),
                                                ambientColor = EmeraldSuccess.copy(alpha = 0.35f),
                                                spotColor = EmeraldSuccess.copy(alpha = 0.35f)
                                            )
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(16.dp),
                                                cornerRadius = 16.dp,
                                                isDark = effectiveDark,
                                                accent = EmeraldSuccess,
                                                secondaryAccent = Color(0xFF059669),
                                                isSelected = true,
                                                softTintWhenSelected = false
                                            )
                                            .testTag("confirm_add_leave_button")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = AppStrings.get("confirm", currentLang),
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
        }
    }

    if (showPersonnelPickerDialog) {
        Dialog(
            onDismissRequest = { showPersonnelPickerDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
            SyncDialogLiquidGlassWindow()

            CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.Transparent,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp)
                        .heightIn(max = 560.dp)
                        .liquidGlassDialogShell(
                            shape = RoundedCornerShape(28.dp),
                            cornerRadius = 28.dp,
                            isDark = effectiveDark,
                            primaryAccent = primaryColor,
                            secondaryAccent = secondaryColor
                        )
                        .testTag("add_leave_personnel_picker_dialog")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Liquid Glass Picker Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            primaryColor.copy(alpha = if (effectiveDark) 0.22f else 0.12f),
                                            secondaryColor.copy(alpha = if (effectiveDark) 0.12f else 0.06f),
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(13.dp),
                                                cornerRadius = 13.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor,
                                                isSelected = true,
                                                softTintWhenSelected = false
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Text(
                                        text = AppStrings.get("select_personnel_label", currentLang),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Box(
                                        modifier = Modifier.liquidGlassPanel(
                                            shape = RoundedCornerShape(50.dp),
                                            cornerRadius = 50.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                    ) {
                                        Text(
                                            text = "${filteredPersonnel.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = primaryColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Surface(
                                    onClick = { showPersonnelPickerDialog = false },
                                    shape = CircleShape,
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .liquidGlassPanel(
                                            shape = CircleShape,
                                            cornerRadius = 24.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        )
                                        .testTag("add_leave_personnel_picker_close_btn")
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = AppStrings.get("close", currentLang),
                                            tint = textSecondary
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = glassDividerColor)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Liquid Glass Search Bar
                            OutlinedTextField(
                                value = personnelSearchQuery,
                                onValueChange = { personnelSearchQuery = it },
                                placeholder = {
                                    Text(
                                        AppStrings.get("search_personnel_hint", currentLang),
                                        fontSize = 12.5.sp,
                                        color = textSecondary
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
                                                tint = textSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = inputColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_leave_personnel_search")
                            )

                            // Scrollable Liquid Glass Personnel List
                            if (filteredPersonnel.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(18.dp),
                                            cornerRadius = 18.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = primaryColor.copy(alpha = 0.7f),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = AppStrings.get("no_results", currentLang),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 120.dp, max = 360.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filteredPersonnel, key = { it.id }) { person ->
                                        val isSelected = person.id == selectedPersonnel?.id
                                        Surface(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                selectedPersonnelId = person.id
                                                showPersonnelPickerDialog = false
                                                personnelSearchQuery = ""
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color.Transparent,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(16.dp),
                                                    cornerRadius = 16.dp,
                                                    isDark = effectiveDark,
                                                    accent = primaryColor,
                                                    secondaryAccent = secondaryColor,
                                                    isSelected = isSelected,
                                                    softTintWhenSelected = true
                                                )
                                                .testTag("add_leave_personnel_item_${person.id}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .liquidGlassPanel(
                                                            shape = CircleShape,
                                                            cornerRadius = 18.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor,
                                                            isSelected = isSelected,
                                                            softTintWhenSelected = !isSelected
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = if (isSelected) Color.White else primaryColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

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
                                                        modifier = Modifier.size(20.dp)
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
}
