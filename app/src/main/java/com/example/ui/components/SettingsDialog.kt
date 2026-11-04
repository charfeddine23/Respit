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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.font.FontStyle
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
            shape = RoundedCornerShape(24.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color(0xD9FFFFFF), // rgba(255, 255, 255, 0.85)
            border = BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDarkMode) 0.4f else 0.95f),
                        Color(0x8000BCD4),
                        Color.White.copy(alpha = 0.6f)
                    )
                )
            ),
            shadowElevation = 10.dp,
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
                // Header (Exact StrikePulse style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA))
                                .border(1.2.dp, Color(0xFF00BCD4).copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "Respite Pulse",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00BCD4)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 0: Language Selection (AR, FR, EN buttons matching StrikePulse)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x60CBD5E1),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val langItems = listOf(
                                AppLanguage.ARABIC to "AR",
                                AppLanguage.FRENCH to "FR",
                                AppLanguage.ENGLISH to "EN"
                            )
                            langItems.forEach { (lang, label) ->
                                val selected = currentLanguage == lang
                                Surface(
                                    onClick = { onSelectLanguage(lang) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (selected) {
                                        if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA)
                                    } else {
                                        if (isDarkMode) Color(0x221E293B) else Color.White
                                    },
                                    border = BorderStroke(
                                        width = if (selected) 1.8.dp else 1.dp,
                                        color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0x3394A3B8) else Color(0x40CBD5E1))
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF0F172A))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Theme Settings (Auto Theme / Light / Dark)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x60CBD5E1),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (currentThemeMode) {
                                    AppThemeMode.DARK -> Icons.Default.DarkMode
                                    AppThemeMode.LIGHT -> Icons.Default.LightMode
                                },
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Auto Theme",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Theme follows system automatically (Light / Dark)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Chips for Theme Mode: Light (Default), Dark (Manual)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppThemeMode.entries.forEach { mode ->
                                val selected = currentThemeMode == mode
                                Surface(
                                    onClick = { onSelectThemeMode(mode) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) {
                                        if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA)
                                    } else {
                                        if (isDarkMode) Color(0x221E293B) else Color.White
                                    },
                                    border = BorderStroke(
                                        width = if (selected) 1.5.dp else 1.dp,
                                        color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0x3394A3B8) else Color(0x40CBD5E1))
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = when (mode) {
                                                AppThemeMode.LIGHT -> "Light (النهاري) ☀️"
                                                AppThemeMode.DARK -> "Dark (الليلي) 🌙"
                                            },
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF0F172A))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section: Focus Mode Toggle (نمط التركيز لقراءة السجلات)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x60CBD5E1),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
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
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "نمط التركيز لقراءة السجلات",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isFocusMode) "مفعل (وضوح عالي وإبراز تواريخ الإجازة)" else "معطل (العرض القياسي)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = isFocusMode,
                            onCheckedChange = { onToggleFocusMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00BCD4)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Duty Eligibility Threshold (عتبة مستحق للإجازة)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x60CBD5E1),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "عتبة الاستحقاق للإجازة (بالأيام)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "عدد الأيام المتواصلة بالخدمة التي يعتبر بعدها الفرد 'مستحقاً للإجازة'.",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
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
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "${thresholdValue.toInt()} يوماً",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00BCD4)
                            )
                        }

                        Slider(
                            value = thresholdValue,
                            onValueChange = { thresholdValue = it },
                            valueRange = 10f..180f,
                            steps = 33,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF00BCD4),
                                activeTrackColor = Color(0xFF00BCD4)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("10 أيام", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                            Text("45 يوماً (الافتراضي)", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                            Text("180 يوماً", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: PIN Lock Card (Matching StrikePulse Pattern Lock card)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x60CBD5E1),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
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
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF00BCD4),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PIN Lock (رمز الحماية)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "تأمين التطبيق برمز سرّي عند الفتح",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = pinEnabledState,
                                onCheckedChange = { pinEnabledState = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF00BCD4)
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
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedContainerColor = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xF7FFFFFF),
                                    unfocusedContainerColor = if (isDarkMode) Color(0xFFF1F5F9) else Color(0xD9FFFFFF),
                                    unfocusedBorderColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0x3300BCD4),
                                    focusedBorderColor = Color(0xFF00BCD4),
                                    focusedLabelColor = Color(0xFF00BCD4),
                                    unfocusedLabelColor = if (isDarkMode) Color(0xFF334155) else Color(0xFF64748B),
                                    cursorColor = Color(0xFF00BCD4)
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

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (enteredPin.length == 4) {
                                            onSavePin(enteredPin, true)
                                            Toast.makeText(context, "تم تحديث رمز PIN", Toast.LENGTH_SHORT).show()
                                        } else {
                                            pinError = "يجب أن يتكون رمز PIN من 4 أرقام"
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.2.dp, Color(0x60CBD5E1))
                                ) {
                                    Text(
                                        text = "Change PIN",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        onLockNow()
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Lock App Now",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Version Information (Exact StrikePulse replica)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isDarkMode) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color(0x60CBD5E1),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00BCD4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Version Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                )
                            }

                            // Green Pill Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (isDarkMode) Color(0x3310B981) else Color(0xFFD1FAE5))
                                    .border(1.2.dp, Color(0xFF10B981), RoundedCornerShape(50.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "v${com.example.BuildConfig.VERSION_NAME}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "App Version",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                            Text(
                                text = "v${com.example.BuildConfig.VERSION_NAME}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Build Code",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                            Text(
                                text = "#${com.example.BuildConfig.VERSION_CODE}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00BCD4)
                            )
                        }

                        // Designer Banner Pill (Exact StrikePulse replica)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA))
                                .border(1.dp, Color(0xFF00BCD4).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFB2EBF2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF00838F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Developed by Charfeddine Kahlessenane",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00838F),
                                    fontSize = 13.sp
                                )
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
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.White
                        )
                    ) {
                        Text("حفظ التغييرات", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0x3364748B))
                    ) {
                        Text("إلغاء", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
