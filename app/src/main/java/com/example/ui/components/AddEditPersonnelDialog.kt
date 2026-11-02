package com.example.ui.components

import android.app.DatePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    val isEdit = personnelToEdit != null

    // Default dates
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

    // Calculate duration in days
    val totalDays = TimeUnit.MILLISECONDS.toDays(
        Personnel.toStartOfDay(returnDateMillis) - Personnel.toStartOfDay(startDateMillis)
    ).coerceAtLeast(0)

    val today = Personnel.toStartOfDay(System.currentTimeMillis())
    val daysRemaining = TimeUnit.MILLISECONDS.toDays(
        Personnel.toStartOfDay(returnDateMillis) - today
    )

    val isDark = isDarkMode

    // Pure black text in dark mode for maximum contrast and readability
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = if (isDark) Color(0xFFF8FAFC) else Color.White,
        unfocusedContainerColor = if (isDark) Color(0xFFF1F5F9) else Color(0xF7FFFFFF),
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = if (isDark) Color(0xFF94A3B8) else Color(0x3338BDF8),
        focusedLabelColor = if (isDark) Color(0xFF0F172A) else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = if (isDark) Color(0xFF334155) else Color(0xFF64748B),
        focusedPlaceholderColor = Color(0xFF64748B),
        unfocusedPlaceholderColor = Color(0xFF94A3B8),
        cursorColor = Color.Black
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF),
            border = androidx.compose.foundation.BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDark) 0.3f else 0.9f),
                        Color(0x8038BDF8),
                        Color(0x400284C7)
                    )
                )
            ),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("add_edit_personnel_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEdit) "تعديل بيانات الفرد والإجازة" else "إضافة فرد وإجازة جديدة",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Error message
                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        errorMessage = null
                    },
                    label = { Text("الاسم واللقب *") },
                    placeholder = { Text("مثال: عبد الرحمن بلقاسمي") },
                    singleLine = true,
                    colors = inputColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("full_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rank Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = rankDropdownExpanded,
                    onExpandedChange = { rankDropdownExpanded = !rankDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = rank,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("الرتبة") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rankDropdownExpanded) },
                        colors = inputColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("rank_input"),
                        shape = RoundedCornerShape(12.dp)
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

                // Wilaya Dropdown (All 69 Algerian Wilayas)
                ExposedDropdownMenuBox(
                    expanded = wilayaDropdownExpanded,
                    onExpandedChange = { wilayaDropdownExpanded = !wilayaDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = wilaya,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("الولاية") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = wilayaDropdownExpanded) },
                        colors = inputColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("wilaya_input"),
                        shape = RoundedCornerShape(12.dp)
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

                // Registration Number
                OutlinedTextField(
                    value = regNumber,
                    onValueChange = {
                        regNumber = it
                        errorMessage = null
                    },
                    label = { Text("رقم التسجيل *") },
                    placeholder = { Text("مثال: DZ-45892/2026") },
                    singleLine = true,
                    colors = inputColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_number_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section Title: إعدادات الإجازة وتواريخ الاستفادة والدخول
                Text(
                    text = "تواريخ الإجازة ومتابعة الدخول",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0284C7)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 1. تاريخ الإستفادة (Start Date - لون أسود #000000 في الوضع الليلي والنهاري)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
                        .border(1.dp, if (isDark) Color(0xFFCBD5E1) else Color(0x3338BDF8), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🛫 تاريخ الإستفادة (بداية الإجازة) *",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000)
                            )

                            TextButton(onClick = {
                                startDateMillis = Personnel.toStartOfDay(System.currentTimeMillis())
                                if (returnDateMillis < startDateMillis) {
                                    returnDateMillis = getOffsetMillis(startDateMillis, 10)
                                }
                            }) {
                                Text("اليوم", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = { startDatePickerDialog.show() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_date_picker_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF000000)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDark) Color(0xFF94A3B8) else Color(0x3338BDF8)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        text = sdf.format(Date(startDateMillis)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF000000)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "اختر تاريخ الاستفادة",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color(0xFF000000)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. تاريخ الدخول (Return Date - لون أسود #000000 في الوضع الليلي والنهاري)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
                        .border(1.dp, if (isDark) Color(0xFFCBD5E1) else Color(0x3338BDF8), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🛬 تاريخ الدخول (العودة للعمل) *",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF000000)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Quick duration addition chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val durationChips = listOf(
                                3 to "+3 أيام",
                                5 to "+5 أيام",
                                10 to "+10 أيام",
                                15 to "+15 يوماً",
                                30 to "+30 يوماً (شهر)"
                            )
                            durationChips.forEach { (days, label) ->
                                FilterChip(
                                    selected = totalDays == days.toLong(),
                                    onClick = {
                                        returnDateMillis = getOffsetMillis(startDateMillis, days)
                                    },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    shape = RoundedCornerShape(50.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0284C7),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = Color(0xFF0A2540)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = { returnDatePickerDialog.show() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("return_date_picker_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF000000)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDark) Color(0xFF94A3B8) else Color(0x3338BDF8)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        text = sdf.format(Date(returnDateMillis)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF000000)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "اختر تاريخ الدخول",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color(0xFF000000)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Live dynamic countdown & duration preview card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) Color(0x330284C7) else Color(0xFFF0F9FF))
                        .border(1.dp, if (isDark) Color(0x6638BDF8) else Color(0xFFBAE6FD), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "مدة الإجازة الإجمالية: $totalDays يوماً",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                            val statusNote = when {
                                isReturnedToDuty -> "الوضعية: حاضر بالخدمة (مستأنف للعمل)"
                                today in Personnel.toStartOfDay(startDateMillis) until Personnel.toStartOfDay(returnDateMillis) ->
                                    "🏠 في البيت حالياً: باقي $daysRemaining أيام على الدخول"
                                today == Personnel.toStartOfDay(returnDateMillis) ->
                                    "🔔 تاريخ الدخول هو اليوم!"
                                today > Personnel.toStartOfDay(returnDateMillis) ->
                                    "⚠️ متأخر بـ ${today - Personnel.toStartOfDay(returnDateMillis) / (1000 * 60 * 60 * 24)} يوم عن الدخول"
                                else -> "⏳ إجازة مبرمجة قادمة"
                            }
                            Text(
                                text = statusNote,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0369A1)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Is Returned to Duty Checkbox
                Surface(
                    color = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0x4D475569) else Color(0x3338BDF8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isReturnedToDuty,
                            onCheckedChange = { isReturnedToDuty = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF059669))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "حاضر بالخدمة (التحق بالوحدة)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "حدد هذا الخيار إذا كان الفرد قد استأنف عمله بالفعل بعد الإجازة",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Leave Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = leaveType,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("نوع الإجازة / العطلة") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        colors = inputColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        algerianLeaveTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t, fontWeight = if (t == leaveType) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    leaveType = t
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes / Unit
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات / الوحدة (اختياري)") },
                    placeholder = { Text("الكتيبة، السرية، ملاحظات خاصة...") },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "يرجى إدخال الاسم واللقب"
                                return@Button
                            }
                            if (regNumber.isBlank()) {
                                errorMessage = "يرجى إدخال رقم التسجيل"
                                return@Button
                            }
                            if (returnDateMillis < startDateMillis) {
                                errorMessage = "تاريخ الدخول يجب أن يكون بعد تاريخ الإستفادة"
                                return@Button
                            }
                            onSave(
                                personnelToEdit?.id ?: 0L,
                                fullName.trim(),
                                rank.trim(),
                                regNumber.trim(),
                                startDateMillis,
                                returnDateMillis,
                                isReturnedToDuty,
                                leaveType.trim(),
                                wilaya.trim(),
                                notes.trim()
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_personnel_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isEdit) "حفظ التعديل" else "إضافة الفرد")
                    }
                }
            }
        }
    }
}
