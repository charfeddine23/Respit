package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppThemeMode
import com.example.util.AppLanguage

@Composable
fun SettingsDialog(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    currentThemeMode: AppThemeMode = AppThemeMode.LIGHT,
    onSelectThemeMode: (AppThemeMode) -> Unit = {},
    currentThreshold: Int,
    onSaveThreshold: (Int) -> Unit,
    isPinEnabled: Boolean,
    currentPin: String,
    onSavePin: (pin: String, enabled: Boolean) -> Unit,
    onLockNow: () -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ARABIC,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isFocusMode: Boolean = false,
    onToggleFocusMode: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var thresholdValue by remember { mutableFloatStateOf(currentThreshold.toFloat()) }
    var pinEnabledState by remember { mutableStateOf(isPinEnabled) }
    var enteredPin by remember { mutableStateOf(if (isPinEnabled) currentPin else "") }
    var pinError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            ),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("settings_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الإعدادات العامة (Settings)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 0: Language Selection (French, English, Arabic)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.65f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x40BAE6FD),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "لغة التطبيق / Langue / Language",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اختر لغة الواجهة المفضلة لديك",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppLanguage.values().forEach { lang ->
                                val selected = currentLanguage == lang
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSelectLanguage(lang) },
                                    label = {
                                        Text(
                                            lang.displayName,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = if (selected) Color.White else if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDarkMode) Color(0x33334155) else Color.White.copy(alpha = 0.7f),
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (selected) MaterialTheme.colorScheme.primary else (if (isDarkMode) Color(0x4D475569) else Color(0x3338BDF8)),
                                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                                        enabled = true,
                                        selected = selected
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Theme Settings (إدارة المظهر مع خيار اتباع نظام الجهاز)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.65f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x40BAE6FD),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (currentThemeMode) {
                                    AppThemeMode.DARK -> Icons.Default.DarkMode
                                    AppThemeMode.LIGHT -> Icons.Default.LightMode
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "مظهر التطبيق (Theme Settings)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (currentThemeMode) {
                                        AppThemeMode.DARK -> "مفعل: الوضع الليلي اليدوي 🌙"
                                        AppThemeMode.LIGHT -> "مفعل: الوضع النهاري (افتراضي) ☀️"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chips for Theme Mode: Light (Default), Dark (Manual)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppThemeMode.entries.forEach { mode ->
                                val selected = currentThemeMode == mode
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSelectThemeMode(mode) },
                                    label = {
                                        Text(
                                            text = when (mode) {
                                                AppThemeMode.LIGHT -> "النهاري ☀️ (افتراضي)"
                                                AppThemeMode.DARK -> "الليلي 🌙"
                                            },
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = if (selected) Color.White else if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDarkMode) Color(0x33334155) else Color.White.copy(alpha = 0.7f),
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (selected) MaterialTheme.colorScheme.primary else (if (isDarkMode) Color(0x4D475569) else Color(0x3338BDF8)),
                                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                                        enabled = true,
                                        selected = selected
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section: Focus Mode Toggle (نمط التركيز لقراءة السجلات)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.65f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x40BAE6FD),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
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
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "نمط التركيز لقراءة السجلات",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isFocusMode) "مفعل (وضوح عالي وإبراز تواريخ الإجازة)" else "معطل (العرض القياسي)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isFocusMode,
                            onCheckedChange = { onToggleFocusMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF0284C7)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Duty Eligibility Threshold (عتبة مستحق للإجازة)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.65f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x40BAE6FD),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "عتبة الاستحقاق للإجازة (بالأيام)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "عدد الأيام المتواصلة بالخدمة التي يعتبر بعدها الفرد 'مستحقاً للإجازة'.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "القيمة المحددة:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${thresholdValue.toInt()} يوماً",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Slider(
                            value = thresholdValue,
                            onValueChange = { thresholdValue = it },
                            valueRange = 10f..180f,
                            steps = 33, // increments of ~5 days
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("10 أيام", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("45 يوماً (الافتراضي)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("180 يوماً", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: 4-Digit PIN Lock
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.65f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x40BAE6FD),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "قفل التطبيق برمز PIN",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "حماية بـ 4 أرقام سرية",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = pinEnabledState,
                                onCheckedChange = { pinEnabledState = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        if (pinEnabledState) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = enteredPin,
                                onValueChange = {
                                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                        enteredPin = it
                                        pinError = null
                                    }
                                },
                                label = { Text("رمز PIN (4 أرقام)") },
                                placeholder = { Text("مثال: 1234") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                visualTransformation = PasswordVisualTransformation(),
                                isError = pinError != null,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = if (isDarkMode) Color(0xFFF8FAFC) else Color.White,
                                    unfocusedContainerColor = if (isDarkMode) Color(0xFFF1F5F9) else Color(0xF7FFFFFF),
                                    unfocusedBorderColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0x3338BDF8),
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedLabelColor = if (isDarkMode) Color(0xFF0F172A) else MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = if (isDarkMode) Color(0xFF334155) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    cursorColor = Color.Black
                                ),
                                supportingText = {
                                    if (pinError != null) {
                                        Text(pinError!!, color = MaterialTheme.colorScheme.error)
                                    } else {
                                        Text("أدخل 4 أرقام لتأمين التطبيق")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (isPinEnabled) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = {
                                        onLockNow()
                                        onDismiss()
                                    },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("قفل التطبيق الآن", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Save & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (pinEnabledState) {
                                if (enteredPin.length != 4) {
                                    pinError = "يجب أن يتكون رمز PIN من 4 أرقام بالضبط"
                                    return@Button
                                }
                                onSavePin(enteredPin, true)
                            } else {
                                onSavePin("", false)
                            }

                            onSaveThreshold(thresholdValue.toInt())
                            Toast.makeText(context, "تم حفظ الإعدادات بنجاح", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("حفظ التغييرات", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إلغاء")
                    }
                }
            }
        }
    }
}
