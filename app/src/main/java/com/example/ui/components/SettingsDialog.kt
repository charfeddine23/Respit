package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppThemeMode
import com.example.data.AppThemeStyle
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsDialog(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    currentThemeMode: AppThemeMode = AppThemeMode.LIGHT,
    onSelectThemeMode: (AppThemeMode) -> Unit = {},
    currentThemeStyle: AppThemeStyle = AppThemeStyle.GLASSMORPHISM,
    onSelectThemeStyle: (AppThemeStyle) -> Unit = {},
    currentThreshold: Int,
    onSaveThreshold: (Int) -> Unit,
    isPinEnabled: Boolean,
    currentPin: String,
    onSavePin: (pin: String, enabled: Boolean) -> Unit,
    securityLockType: String = "PIN",
    currentPattern: String = "",
    isBiometricEnabled: Boolean = false,
    isPasskeyEnabled: Boolean = false,
    onSaveSecurity: (type: String, pin: String, pattern: String, biometric: Boolean, passkey: Boolean, enabled: Boolean) -> Unit = { _, _, _, _, _, _ -> },
    onLockNow: () -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ARABIC,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isFocusMode: Boolean = false,
    onToggleFocusMode: () -> Unit = {},
    isDriveSyncEnabled: Boolean = false,
    onToggleDriveSync: (Boolean) -> Unit = {},
    lastDriveBackupTime: Long = 0L,
    onBackupToDrive: () -> Unit = {},
    onRestoreFromDrive: () -> Unit = {},
    isDynamicAnimationsEnabled: Boolean = true,
    onToggleDynamicAnimations: (Boolean) -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var thresholdValue by remember { mutableFloatStateOf(currentThreshold.toFloat()) }
    var pinEnabledState by remember { mutableStateOf(isPinEnabled) }
    var enteredPin by remember { mutableStateOf(if (isPinEnabled) currentPin else "") }
    var selectedThemeStyle by remember { mutableStateOf(currentThemeStyle) }
    var selectedLockType by remember { mutableStateOf(securityLockType) }
    var patternState by remember { mutableStateOf(currentPattern) }
    var tempDrawnPattern by remember { mutableStateOf("") }
    var biometricState by remember { mutableStateOf(isBiometricEnabled) }
    var passkeyState by remember { mutableStateOf(isPasskeyEnabled) }
    var showSecurityPrivacyDialog by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf<String?>(null) }
    var patternFeedback by remember { mutableStateOf<String?>(null) }
    var driveSyncState by remember { mutableStateOf(isDriveSyncEnabled) }
    var dynamicAnimationsState by remember { mutableStateOf(isDynamicAnimationsEnabled) }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = if (isDarkMode) Color(0xFF161F30) else Color(0xFFE6EEF8),
            border = BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDarkMode) 0.2f else 0.95f),
                        Color(0x302563EB),
                        Color.White.copy(alpha = 0.5f)
                    )
                )
            ),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp) // Responsive tablet/large-screen cap
                .padding(vertical = 12.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header (App Title Respite Pulse preserved)
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
                                text = AppStrings.get("settings_title", currentLanguage),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDarkMode) Color.White else SlateTextPrimary
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
                            contentDescription = AppStrings.get("close", currentLanguage),
                            tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Language Selection (AR, FR, EN)
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
                                text = AppStrings.get("language", currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else SlateTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val langItems = listOf(
                                AppLanguage.ARABIC to "العربية",
                                AppLanguage.FRENCH to "Français",
                                AppLanguage.ENGLISH to "English"
                            )
                            langItems.forEach { (lang, label) ->
                                val selected = currentLanguage == lang
                                Surface(
                                    onClick = { onSelectLanguage(lang) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
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
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else SlateTextPrimary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Dedicated Google Drive Backup & Restore Card
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
                        .testTag("google_drive_backup_card")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkMode) Color(0x334285F4) else Color(0xFFE8F0FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = null,
                                        tint = Color(0xFF4285F4),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = AppStrings.get("drive_backup_title", currentLanguage),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color.White else SlateTextPrimary
                                    )
                                    Text(
                                        text = AppStrings.get("drive_backup_sub", currentLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = driveSyncState,
                                onCheckedChange = {
                                    driveSyncState = it
                                    onToggleDriveSync(it)
                                    Toast.makeText(
                                        context,
                                        if (it) AppStrings.get("drive_sync_toggle", currentLanguage) + " ✓"
                                        else AppStrings.get("focus_mode_inactive", currentLanguage),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF4285F4)
                                )
                            )
                        }

                        // Last Backup Info Inset
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkMode) Color(0x221E293B) else Color(0x66F1F5F9))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppStrings.get("drive_last_backup", currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
                            )

                            Text(
                                text = if (lastDriveBackupTime > 0L) sdf.format(Date(lastDriveBackupTime))
                                else AppStrings.get("drive_never", currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (lastDriveBackupTime > 0L) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF94A3B8))
                            )
                        }

                        // Action Buttons: Backup to Drive & Restore from Drive (Well balanced)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Backup Button
                            Button(
                                onClick = onBackupToDrive,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("backup_to_drive_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4285F4),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("drive_backup_btn", currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            // Restore Button
                            OutlinedButton(
                                onClick = onRestoreFromDrive,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("restore_from_drive_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.2.dp, Color(0xFF4285F4).copy(alpha = 0.7f)),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = Color(0xFF4285F4),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("drive_restore_btn", currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF8AB4F8) else Color(0xFF1967D2),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2.5: Dynamic Animations & Performance Mode (Optimization for weak devices)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkMode) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.85f))
                        .border(
                            1.dp,
                            if (isDarkMode) Color(0x33A855F7) else Color(0x40A855F7),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                        .testTag("dynamic_animations_toggle_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFA855F7).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = Color(0xFFA855F7),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = AppStrings.get("dynamic_animations_title", currentLanguage),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color.White else SlateTextPrimary
                                    )
                                    Text(
                                        text = AppStrings.get("dynamic_animations_desc", currentLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Switch(
                                checked = dynamicAnimationsState,
                                onCheckedChange = {
                                    dynamicAnimationsState = it
                                    onToggleDynamicAnimations(it)
                                    Toast.makeText(
                                        context,
                                        if (it) AppStrings.get("dynamic_animations_active", currentLanguage)
                                        else AppStrings.get("dynamic_animations_disabled", currentLanguage),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFA855F7)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status info pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (dynamicAnimationsState) Color(0xFF10B981).copy(alpha = 0.12f) else Color(0xFFF59E0B).copy(alpha = 0.12f),
                            border = BorderStroke(
                                1.dp,
                                if (dynamicAnimationsState) Color(0xFF10B981).copy(alpha = 0.3f) else Color(0xFFF59E0B).copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (dynamicAnimationsState) Color(0xFF10B981) else Color(0xFFF59E0B))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (dynamicAnimationsState) AppStrings.get("dynamic_animations_active", currentLanguage)
                                    else AppStrings.get("dynamic_animations_disabled", currentLanguage),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (dynamicAnimationsState) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Theme Settings (Light / Dark)
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
                                    text = AppStrings.get("theme_title", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else SlateTextPrimary
                                )
                                Text(
                                    text = AppStrings.get("theme_desc", currentLanguage),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                                        .height(44.dp),
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
                                                AppThemeMode.LIGHT -> AppStrings.get("theme_light_btn", currentLanguage)
                                                AppThemeMode.DARK -> AppStrings.get("theme_dark_btn", currentLanguage)
                                            },
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else SlateTextPrimary)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Theme Style (Glassmorphism vs Neumorphism)
                        Text(
                            text = AppStrings.get("theme_style_title", currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else SlateTextPrimary
                        )
                        Text(
                            text = AppStrings.get("theme_style_desc", currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val styles = listOf(
                                AppThemeStyle.GLASSMORPHISM to AppStrings.get("theme_style_glass", currentLanguage),
                                AppThemeStyle.NEUMORPHISM to AppStrings.get("theme_style_neumorph", currentLanguage)
                            )
                            styles.forEach { (style, label) ->
                                val selected = selectedThemeStyle == style
                                Surface(
                                    onClick = {
                                        selectedThemeStyle = style
                                        onSelectThemeStyle(style)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
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
                                            text = label,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = if (selected) Color(0xFF00BCD4) else (if (isDarkMode) Color(0xFFCBD5E1) else SlateTextPrimary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Focus Mode Toggle
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
                                    text = AppStrings.get("focus_mode_title", currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else SlateTextPrimary
                                )
                                Text(
                                    text = if (isFocusMode) AppStrings.get("focus_mode_active", currentLanguage)
                                    else AppStrings.get("focus_mode_inactive", currentLanguage),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
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

                // Section 5: Duty Eligibility Threshold
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
                                text = AppStrings.get("duty_threshold_title", currentLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else SlateTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = AppStrings.get("duty_threshold_desc", currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppStrings.get("selected_value", currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkMode) Color.White else SlateTextPrimary
                            )
                            Text(
                                text = "${thresholdValue.toInt()} ${AppStrings.get("days", currentLanguage)}",
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
                            Text("10 ${AppStrings.get("days", currentLanguage)}", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                            Text("45 ${AppStrings.get("days", currentLanguage)}", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                            Text("180 ${AppStrings.get("days", currentLanguage)}", fontSize = 10.sp, color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 6: Biometric App Lock (Modern Fingerprint / Device Credential)
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
                        .testTag("biometric_lock_card")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF00BCD4).copy(alpha = 0.18f))
                                        .border(1.dp, Color(0xFF00BCD4).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = Color(0xFF00BCD4),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "قفل التطبيق بالبصمة",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color.White else SlateTextPrimary
                                    )
                                    Text(
                                        text = if (biometricState) "المصادقة البيومترية مفعلة عند فتح التطبيق" else "قفل التطبيق معطل حالياً",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (biometricState) Color(0xFF10B981) else if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = biometricState,
                                onCheckedChange = { isChecked ->
                                    biometricState = isChecked
                                    pinEnabledState = isChecked
                                    onSavePin("", isChecked)
                                    onSaveSecurity("BIOMETRIC", "", "", isChecked, false, isChecked)
                                    val msg = if (isChecked) "تم تفعيل قفل التطبيق بالبصمة بنجاح" else "تم إلغاء قفل التطبيق"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF00BCD4)
                                ),
                                modifier = Modifier.testTag("biometric_lock_toggle")
                            )
                        }

                        if (biometricState) {
                            OutlinedButton(
                                onClick = {
                                    onLockNow()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF00BCD4)),
                                modifier = Modifier.fillMaxWidth().testTag("lock_now_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF00BCD4),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.get("lock_now_btn", currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00BCD4)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 7: Version Information & Developer Card (Developed by Charfeddine Kahlessenane STRICTLY PRESERVED)
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
                                    text = AppStrings.get("version_info_title", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color.White else SlateTextPrimary
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
                                text = AppStrings.get("app_version_label", currentLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
                            )
                            Text(
                                text = "v${com.example.BuildConfig.VERSION_NAME}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else SlateTextPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppStrings.get("build_code_label", currentLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
                            )
                            Text(
                                text = "#${com.example.BuildConfig.VERSION_CODE}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00BCD4)
                            )
                        }

                        // Designer Banner Pill (Strictly unchanged across all languages)
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

                // Actions: Save & Cancel (Well-proportioned 48dp height buttons with 14sp text)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onSavePin("", biometricState)
                            onSaveSecurity(
                                "BIOMETRIC",
                                "",
                                "",
                                biometricState,
                                false,
                                biometricState
                            )
                            onSelectThemeStyle(selectedThemeStyle)
                            onSaveThreshold(thresholdValue.toInt())
                            Toast.makeText(context, AppStrings.get("save_changes", currentLanguage), Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = AppStrings.get("save_changes", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0x3364748B)),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = AppStrings.get("cancel", currentLanguage),
                            color = if (isDarkMode) Color(0xFFCBD5E1) else Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
