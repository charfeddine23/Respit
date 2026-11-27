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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.data.LeaveRecord
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private fun addCalendarDaysNormalized(baseMillis: Long, daysToAdd: Int): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = Personnel.toStartOfDay(baseMillis)
    cal.add(Calendar.DAY_OF_YEAR, daysToAdd)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtendLeaveDialog(
    personnel: Personnel,
    targetRecord: LeaveRecord? = null,
    onDismiss: () -> Unit,
    onConfirmExtend: (newReturnDateMillis: Long, addedDays: Int, reasonNote: String) -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryColor = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryColor = preset?.secondary ?: if (effectiveDark) PurpleAccent else RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.62f)

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    val baseStartMillis = remember(personnel, targetRecord) {
        val raw = targetRecord?.startDateMillis ?: personnel.startDateMillis
        if (raw > 0L) Personnel.toStartOfDay(raw) else Personnel.toStartOfDay(System.currentTimeMillis())
    }
    val currentReturnMillis = remember(personnel, targetRecord, baseStartMillis) {
        val raw = targetRecord?.returnDateMillis ?: personnel.returnDateMillis
        if (raw > 0L) maxOf(baseStartMillis, Personnel.toStartOfDay(raw)) else baseStartMillis
    }
    val activeLeaveType = targetRecord?.leaveType ?: personnel.leaveType
    val currentTotalDays = remember(baseStartMillis, currentReturnMillis) {
        Personnel.inclusiveLeaveDays(baseStartMillis, currentReturnMillis)
    }

    var addedDays by remember { mutableIntStateOf(3) }
    var daysInputText by remember { mutableStateOf("3") }
    var reasonNote by remember { mutableStateOf("") }

    val newReturnMillis = remember(currentReturnMillis, addedDays) {
        addCalendarDaysNormalized(currentReturnMillis, addedDays.coerceAtLeast(1))
    }
    val newTotalDays = remember(baseStartMillis, newReturnMillis) {
        Personnel.inclusiveLeaveDays(baseStartMillis, newReturnMillis)
    }

    val currentReturnFormatted = remember(currentReturnMillis) { sdf.format(Date(currentReturnMillis)) }
    val newReturnFormatted = remember(newReturnMillis) { sdf.format(Date(newReturnMillis)) }

    val quickDayOptions = remember { listOf(1, 2, 3, 5, 7, 10, 15) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedContainerColor = if (effectiveDark) Color(0x281E293B) else Color(0xCCFFFFFF),
        unfocusedContainerColor = if (effectiveDark) Color(0x1C1E293B) else Color(0xA6FFFFFF),
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = if (effectiveDark) Color.White.copy(alpha = 0.22f) else primaryColor.copy(alpha = 0.32f),
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
                        fadeIn(tween(190, easing = FastOutSlowInEasing)) + scaleIn(
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
                            .heightIn(max = 700.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor
                            )
                            .testTag("extend_leave_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LiquidGlassDialogHeader(
                                title = AppStrings.get("extend_leave_title", currentLang),
                                subtitle = AppStrings.get("extend_leave_sub", currentLang),
                                icon = Icons.Default.MoreTime,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor,
                                isDark = effectiveDark,
                                closeContentDescription = AppStrings.get("close", currentLang),
                                closeTestTag = "extend_close_btn",
                                badgeText = "+$addedDays",
                                onDismiss = onDismiss
                            )

                            HorizontalDivider(color = glassDividerColor)

                            Column(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                                    .testTag("extend_leave_scroll"),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // 1. Personnel & Current -> New Schedule Comparison Card
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
                                        .testTag("extend_preview_card")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = personnel.getLocalizedName(currentLang),
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = textPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${formatRank(personnel.rank, currentLang)} • ${formatLeaveType(activeLeaveType, currentLang)}",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = textSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Box(
                                                modifier = Modifier.liquidGlassPanel(
                                                    shape = RoundedCornerShape(50.dp),
                                                    cornerRadius = 50.dp,
                                                    isDark = effectiveDark,
                                                    accent = EmeraldSuccess,
                                                    secondaryAccent = primaryColor,
                                                    isSelected = true,
                                                    softTintWhenSelected = true
                                                )
                                            ) {
                                                Text(
                                                    text = "+$addedDays ${AppStrings.get("days", currentLang)}",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = EmeraldSuccess,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        HorizontalDivider(color = glassDividerColor)

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Current Return Box
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(14.dp),
                                                        cornerRadius = 14.dp,
                                                        isDark = effectiveDark,
                                                        accent = secondaryColor
                                                    )
                                                    .padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Text(
                                                    text = AppStrings.get("extend_current_return_label", currentLang),
                                                    fontSize = 10.5.sp,
                                                    color = textSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = currentReturnFormatted,
                                                    fontSize = 13.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textPrimary
                                                )
                                                Text(
                                                    text = "${AppStrings.get("extend_current_duration", currentLang)}: $currentTotalDays ${AppStrings.get("days", currentLang)}",
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = textSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(18.dp)
                                            )

                                            // New Extended Return Box
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(14.dp),
                                                        cornerRadius = 14.dp,
                                                        isDark = effectiveDark,
                                                        accent = EmeraldSuccess,
                                                        secondaryAccent = primaryColor,
                                                        isSelected = true,
                                                        softTintWhenSelected = true
                                                    )
                                                    .padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Text(
                                                    text = AppStrings.get("extend_new_return_label", currentLang),
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldSuccess,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = newReturnFormatted,
                                                    fontSize = 13.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = EmeraldSuccess
                                                )
                                                Text(
                                                    text = "${AppStrings.get("extend_new_duration", currentLang)}: $newTotalDays ${AppStrings.get("days", currentLang)}",
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryColor,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }

                                // 2. Additional Extension Days Section
                                LiquidGlassSectionHighlight(
                                    title = AppStrings.get("extend_duration_section", currentLang),
                                    icon = Icons.Default.EditCalendar,
                                    accent = primaryColor,
                                    isDark = effectiveDark,
                                    badgeText = "+$addedDays ${AppStrings.get("days", currentLang)}"
                                )

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
                                        // Quick 1-Tap Extension Chips (+1, +2, +3, +5, +7, +10, +15)
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            quickDayOptions.forEach { daysOption ->
                                                val isSelected = addedDays == daysOption
                                                Surface(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        addedDays = daysOption
                                                        daysInputText = daysOption.toString()
                                                    },
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = Color.Transparent,
                                                    modifier = Modifier
                                                        .defaultMinSize(minWidth = 64.dp, minHeight = 48.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(14.dp),
                                                            cornerRadius = 14.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor,
                                                            isSelected = isSelected,
                                                            softTintWhenSelected = true
                                                        )
                                                        .testTag("extend_quick_days_$daysOption")
                                                ) {
                                                    Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                                    ) {
                                                        Text(
                                                            text = "+$daysOption ${AppStrings.get("days", currentLang)}",
                                                            fontSize = 12.5.sp,
                                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                            color = if (isSelected) primaryColor else textPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Stepper (- / Input / +)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Surface(
                                                onClick = {
                                                    if (addedDays > 1) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        addedDays -= 1
                                                        daysInputText = addedDays.toString()
                                                    }
                                                },
                                                shape = RoundedCornerShape(14.dp),
                                                color = Color.Transparent,
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(14.dp),
                                                        cornerRadius = 14.dp,
                                                        isDark = effectiveDark,
                                                        accent = secondaryColor
                                                    )
                                                    .testTag("extend_dec_days_btn")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Remove,
                                                        contentDescription = AppStrings.get("extend_duration_section", currentLang),
                                                        tint = textPrimary
                                                    )
                                                }
                                            }

                                            OutlinedTextField(
                                                value = daysInputText,
                                                onValueChange = { raw ->
                                                    val digits = raw.filter { it.isDigit() }.take(3)
                                                    daysInputText = digits
                                                    val parsed = digits.toIntOrNull()
                                                    if (parsed != null && parsed in 1..365) {
                                                        addedDays = parsed
                                                    }
                                                },
                                                label = { Text(AppStrings.get("extend_days_added_label", currentLang)) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                colors = textFieldColors,
                                                shape = RoundedCornerShape(14.dp),
                                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    textAlign = TextAlign.Center
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("extend_days_input")
                                            )

                                            Surface(
                                                onClick = {
                                                    if (addedDays < 365) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        addedDays += 1
                                                        daysInputText = addedDays.toString()
                                                    }
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
                                                        secondaryAccent = secondaryColor,
                                                        isSelected = true,
                                                        softTintWhenSelected = true
                                                    )
                                                    .testTag("extend_inc_days_btn")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = AppStrings.get("extend_duration_section", currentLang),
                                                        tint = primaryColor
                                                    )
                                                }
                                            }
                                        }

                                        // Direct Return Date Picker Button
                                        Surface(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                val cal = Calendar.getInstance()
                                                cal.timeInMillis = newReturnMillis
                                                val picker = DatePickerDialog(
                                                    context,
                                                    { _, y, m, d ->
                                                        val pickedCal = Calendar.getInstance()
                                                        pickedCal.set(y, m, d, 0, 0, 0)
                                                        pickedCal.set(Calendar.MILLISECOND, 0)
                                                        val diff = Personnel.signedDaysBetween(
                                                            currentReturnMillis,
                                                            pickedCal.timeInMillis
                                                        ).toInt().coerceIn(1, 365)
                                                        addedDays = diff
                                                        daysInputText = diff.toString()
                                                    },
                                                    cal.get(Calendar.YEAR),
                                                    cal.get(Calendar.MONTH),
                                                    cal.get(Calendar.DAY_OF_MONTH)
                                                )
                                                picker.datePicker.minDate = addCalendarDaysNormalized(currentReturnMillis, 1)
                                                picker.show()
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color.Transparent,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .defaultMinSize(minHeight = 48.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(14.dp),
                                                    cornerRadius = 14.dp,
                                                    isDark = effectiveDark,
                                                    accent = EmeraldSuccess,
                                                    secondaryAccent = primaryColor,
                                                    isSelected = true,
                                                    softTintWhenSelected = true
                                                )
                                                .testTag("extend_new_return_date_btn")
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
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CalendarMonth,
                                                        contentDescription = AppStrings.get("extend_new_return_label", currentLang),
                                                        tint = EmeraldSuccess,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            text = AppStrings.get("extend_new_return_label", currentLang),
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = textSecondary
                                                        )
                                                        Text(
                                                            text = AppStrings.get("extend_pick_date_hint", currentLang),
                                                            fontSize = 10.sp,
                                                            color = textSecondary.copy(alpha = 0.85f),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = newReturnFormatted,
                                                    fontSize = 14.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = EmeraldSuccess
                                                )
                                            }
                                        }
                                    }
                                }

                                // 3. Optional Extension Reason / Note
                                OutlinedTextField(
                                    value = reasonNote,
                                    onValueChange = { reasonNote = it },
                                    label = { Text(AppStrings.get("extend_reason_label", currentLang)) },
                                    placeholder = { Text(AppStrings.get("extend_reason_placeholder", currentLang)) },
                                    colors = textFieldColors,
                                    shape = RoundedCornerShape(16.dp),
                                    maxLines = 2,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("extend_reason_input")
                                )
                            }

                            HorizontalDivider(color = glassDividerColor)

                            // Footer Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                LiquidGlassActionButton(
                                    text = AppStrings.get("cancel", currentLang),
                                    onClick = onDismiss,
                                    isDark = effectiveDark,
                                    primaryAccent = secondaryColor,
                                    secondaryAccent = primaryColor,
                                    isPrimary = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("extend_cancel_btn")
                                )
                                LiquidGlassActionButton(
                                    text = AppStrings.get("extend_confirm_btn", currentLang),
                                    icon = Icons.Default.Check,
                                    onClick = {
                                        val finalDays = addedDays.coerceIn(1, 365)
                                        val finalReturnMillis = addCalendarDaysNormalized(currentReturnMillis, finalDays)
                                        onConfirmExtend(finalReturnMillis, finalDays, reasonNote)
                                    },
                                    isDark = effectiveDark,
                                    primaryAccent = EmeraldSuccess,
                                    secondaryAccent = primaryColor,
                                    isPrimary = true,
                                    modifier = Modifier
                                        .weight(1.35f)
                                        .testTag("extend_confirm_btn")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
