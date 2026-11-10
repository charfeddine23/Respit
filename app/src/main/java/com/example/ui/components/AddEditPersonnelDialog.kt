package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Personnel
import com.example.data.algerianLeaveTypes
import com.example.data.algerianRanks
import com.example.data.algerianWilayas
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPersonnelDialog(
    personnelToEdit: Personnel?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        fullName: String,
        rank: String,
        registrationNumber: String,
        startDateMillis: Long,
        returnDateMillis: Long,
        isReturnedToDuty: Boolean,
        leaveType: String,
        wilaya: String,
        notes: String
    ) -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC
) {
    val context = LocalContext.current
    val isEdit = personnelToEdit != null

    val now = System.currentTimeMillis()
    fun getOffsetMillis(baseMillis: Long, days: Int): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = baseMillis
            add(Calendar.DAY_OF_YEAR, days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    var fullName by remember { mutableStateOf(personnelToEdit?.fullName ?: "") }
    var rank by remember { mutableStateOf(personnelToEdit?.rank ?: algerianRanks.first()) }
    var wilaya by remember { mutableStateOf(personnelToEdit?.wilaya ?: "16 - الجزائر") }
    var regNumber by remember { mutableStateOf(personnelToEdit?.registrationNumber ?: "") }

    var startDateMillis by remember {
        mutableLongStateOf(personnelToEdit?.startDateMillis ?: Personnel.toStartOfDay(now))
    }
    var returnDateMillis by remember {
        mutableLongStateOf(
            personnelToEdit?.returnDateMillis ?: getOffsetMillis(startDateMillis, 10)
        )
    }
    var isReturnedToDuty by remember {
        mutableStateOf(personnelToEdit?.isReturnedToDuty ?: false)
    }

    var leaveType by remember { mutableStateOf(personnelToEdit?.leaveType ?: algerianLeaveTypes.first()) }
    var notes by remember { mutableStateOf(personnelToEdit?.notes ?: "") }

    var rankDropdownExpanded by remember { mutableStateOf(false) }
    var wilayaDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    // Start Date Picker
    val startCal = Calendar.getInstance().apply { timeInMillis = startDateMillis }
    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth, 0, 0, 0)
            c.set(Calendar.MILLISECOND, 0)
            startDateMillis = c.timeInMillis
            if (returnDateMillis < startDateMillis) {
                returnDateMillis = getOffsetMillis(startDateMillis, 10)
            }
        },
        startCal.get(Calendar.YEAR),
        startCal.get(Calendar.MONTH),
        startCal.get(Calendar.DAY_OF_MONTH)
    )

    // Return Date Picker
    val returnCal = Calendar.getInstance().apply { timeInMillis = returnDateMillis }
    val returnDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth, 0, 0, 0)
            c.set(Calendar.MILLISECOND, 0)
            returnDateMillis = c.timeInMillis
        },
        returnCal.get(Calendar.YEAR),
        returnCal.get(Calendar.MONTH),
        returnCal.get(Calendar.DAY_OF_MONTH)
    )

    val totalDays = TimeUnit.MILLISECONDS.toDays(
        Personnel.toStartOfDay(returnDateMillis) - Personnel.toStartOfDay(startDateMillis)
    ).coerceAtLeast(0)

    val isDark = isDarkMode

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = if (isDark) Color.White else SlateTextPrimary,
        unfocusedTextColor = if (isDark) Color.White else SlateTextPrimary,
        focusedContainerColor = if (isDark) Color(0x331E293B) else Color(0x80FFFFFF),
        unfocusedContainerColor = if (isDark) Color(0x221E293B) else Color(0x60FFFFFF),
        focusedBorderColor = Color(0xFF00BCD4),
        unfocusedBorderColor = if (isDark) Color(0x40CBD5E1) else Color(0x4000BCD4),
        focusedLabelColor = Color(0xFF00BCD4),
        unfocusedLabelColor = if (isDark) Color(0xFF94A3B8) else SlateTextSecondary,
        cursorColor = Color(0xFF00BCD4)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = if (isDark) Color(0xFF161F30) else Color(0xFFE6EEF8),
            border = BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDark) 0.2f else 0.95f),
                        Color(0x302563EB),
                        Color.White.copy(alpha = 0.5f)
                    )
                )
            ),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(vertical = 12.dp)
                .testTag("add_edit_personnel_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.get(if (isEdit) "edit_personnel_title" else "add_personnel_title", currentLang),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else SlateTextPrimary
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.get("cancel", currentLang),
                            tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error Banner
                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 1. Full Name Input
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        errorMessage = null
                    },
                    label = { Text(AppStrings.get("full_name_label", currentLang)) },
                    singleLine = true,
                    colors = inputColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("full_name_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Military Rank Dropdown
                ExposedDropdownMenuBox(
                    expanded = rankDropdownExpanded,
                    onExpandedChange = { rankDropdownExpanded = !rankDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = rank,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.get("rank_label", currentLang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rankDropdownExpanded) },
                        colors = inputColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = rankDropdownExpanded,
                        onDismissRequest = { rankDropdownExpanded = false }
                    ) {
                        algerianRanks.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r, fontWeight = if (r == rank) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    rank = r
                                    rankDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Registration Number Input
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = {
                        regNumber = it
                        errorMessage = null
                    },
                    label = { Text(AppStrings.get("reg_number_label", currentLang)) },
                    placeholder = { Text("DZ-123456") },
                    singleLine = true,
                    colors = inputColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_number_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Wilaya Dropdown
                ExposedDropdownMenuBox(
                    expanded = wilayaDropdownExpanded,
                    onExpandedChange = { wilayaDropdownExpanded = !wilayaDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = wilaya,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.get("wilaya_label", currentLang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = wilayaDropdownExpanded) },
                        colors = inputColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = wilayaDropdownExpanded,
                        onDismissRequest = { wilayaDropdownExpanded = false }
                    ) {
                        algerianWilayas.forEach { w ->
                            DropdownMenuItem(
                                text = { Text(w, fontWeight = if (w == wilaya) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    wilaya = w
                                    wilayaDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Leave Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = leaveType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.get("leave_type_label", currentLang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        colors = inputColors,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        algerianLeaveTypes.forEach { lt ->
                            DropdownMenuItem(
                                text = { Text(lt, fontWeight = if (lt == leaveType) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    leaveType = lt
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dates Section Header
                Text(
                    text = AppStrings.get("benefit_date", currentLang).replace(":", "") + " & " + AppStrings.get("return_date", currentLang).replace(":", ""),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00BCD4)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Start Date Field Button
                OutlinedButton(
                    onClick = { startDatePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0x40CBD5E1) else Color(0x4000BCD4))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = AppStrings.get("start_date_label", currentLang),
                                fontSize = 10.sp,
                                color = if (isDark) Color(0xFF94A3B8) else SlateTextSecondary
                            )
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Text(
                                    text = sdf.format(Date(startDateMillis)),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color.White else SlateTextPrimary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF00BCD4)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Quick Duration Add Chips (+10, +15, +20, +30 days)
                Text(
                    text = AppStrings.get("quick_duration_add", currentLang),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFCBD5E1) else SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val durationPills = listOf(
                        10 to AppStrings.get("ten_days", currentLang),
                        15 to AppStrings.get("fifteen_days", currentLang),
                        20 to AppStrings.get("twenty_days", currentLang),
                        30 to AppStrings.get("thirty_days", currentLang)
                    )

                    durationPills.forEach { (days, label) ->
                        val isSelected = totalDays == days.toLong()
                        Surface(
                            onClick = {
                                returnDateMillis = getOffsetMillis(startDateMillis, days)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) {
                                if (isDark) Color(0x3300BCD4) else Color(0xFFE0F7FA)
                            } else {
                                if (isDark) Color(0x221E293B) else Color(0x80FFFFFF)
                            },
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Color(0xFF00BCD4) else (if (isDark) Color(0x33475569) else Color(0x33CBD5E1))
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF00BCD4) else (if (isDark) Color(0xFFE2E8F0) else SlateTextPrimary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Return Date Field Button
                OutlinedButton(
                    onClick = { returnDatePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0x40CBD5E1) else Color(0x4000BCD4))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = AppStrings.get("return_date_label", currentLang),
                                fontSize = 10.sp,
                                color = if (isDark) Color(0xFF94A3B8) else SlateTextSecondary
                            )
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Text(
                                    text = sdf.format(Date(returnDateMillis)),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) Color.White else SlateTextPrimary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF00BCD4)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Total Days Summary Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                    border = BorderStroke(1.dp, Color(0xFF00BCD4).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF00838F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${AppStrings.get("leave_duration", currentLang)} $totalDays ${AppStrings.get("days", currentLang)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF00838F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // On Duty / Active Duty Switch Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0x221E293B) else Color(0x80FFFFFF),
                    border = BorderStroke(1.dp, if (isDark) Color(0x33475569) else Color(0x33CBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("on_duty_switch_label", currentLang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else SlateTextPrimary
                        )
                        Switch(
                            checked = isReturnedToDuty,
                            onCheckedChange = { isReturnedToDuty = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes Field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(AppStrings.get("notes_label", currentLang).replace(":", "")) },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save & Cancel Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (fullName.isBlank() || regNumber.isBlank()) {
                                errorMessage = AppStrings.get("fill_required", currentLang)
                                return@Button
                            }

                            onSave(
                                personnelToEdit?.id ?: 0L,
                                fullName.trim(),
                                rank,
                                regNumber.trim(),
                                startDateMillis,
                                returnDateMillis,
                                isReturnedToDuty,
                                leaveType,
                                wilaya,
                                notes.trim()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        )
                    ) {
                        Text(AppStrings.get("save_changes", currentLang), fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0x3364748B))
                    ) {
                        Text(AppStrings.get("cancel", currentLang), color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
