package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun SecurityPrivacyDialog(
    isLockEnabled: Boolean,
    currentLockType: String, // "PATTERN" or "PIN"
    currentPin: String,
    currentPattern: String,
    isBiometricEnabled: Boolean,
    isPasskeyEnabled: Boolean,
    onSaveSecuritySettings: (
        enabled: Boolean,
        lockType: String,
        pin: String,
        pattern: String,
        biometric: Boolean,
        passkey: Boolean
    ) -> Unit,
    onLockNow: () -> Unit,
    isDarkMode: Boolean,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var enabledState by remember { mutableStateOf(isLockEnabled) }
    var selectedType by remember { mutableStateOf(if (currentLockType.uppercase() == "PATTERN") "PATTERN" else "PIN") }
    var pinState by remember { mutableStateOf(currentPin) }
    var patternState by remember { mutableStateOf(currentPattern) }
    var tempDrawnPattern by remember { mutableStateOf("") }
    var patternFeedback by remember { mutableStateOf<String?>(null) }
    var biometricState by remember { mutableStateOf(isBiometricEnabled) }
    var passkeyState by remember { mutableStateOf(isPasskeyEnabled) }
    var pinInput by remember { mutableStateOf("") }

    val bgColor = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFFFFFFF)
    val cardBg = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val primaryCyan = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = bgColor,
            shadowElevation = 16.dp,
            border = BorderStroke(1.2.dp, primaryCyan.copy(alpha = if (isDarkMode) 0.5f else 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .testTag("security_privacy_dialog")
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(primaryCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = primaryCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppStrings.get("security_privacy_title", currentLang),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = AppStrings.get("security_privacy_desc", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.get("close", currentLang),
                            tint = textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card 1: Master Lock Switch
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, if (enabledState) primaryCyan.copy(alpha = 0.6f) else Color(0x3094A3B8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (enabledState) primaryCyan else textSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("enable_app_lock", currentLang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = if (enabledState) AppStrings.get("app_lock_active", currentLang) else AppStrings.get("app_lock_inactive", currentLang),
                                    fontSize = 11.sp,
                                    color = if (enabledState) primaryCyan else textSecondary
                                )
                            }
                        }

                        Switch(
                            checked = enabledState,
                            onCheckedChange = { enabledState = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = primaryCyan
                            )
                        )
                    }
                }

                AnimatedVisibility(visible = enabledState, enter = fadeIn(), exit = fadeOut()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Card 2: Lock Method Selector Tabs (PIN vs Pattern)
                        Text(
                            text = AppStrings.get("choose_lock_method", currentLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = textPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Option 1: 4-digit PIN Code
                            val isPinSelected = selectedType == "PIN"
                            Surface(
                                onClick = { selectedType = "PIN" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isPinSelected) primaryCyan.copy(alpha = 0.18f) else cardBg,
                                border = BorderStroke(
                                    if (isPinSelected) 1.5.dp else 1.dp,
                                    if (isPinSelected) primaryCyan else Color(0x3094A3B8)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pin,
                                        contentDescription = null,
                                        tint = if (isPinSelected) primaryCyan else textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = AppStrings.get("pin_code_title", currentLang),
                                        fontWeight = if (isPinSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = if (isPinSelected) primaryCyan else textPrimary
                                    )
                                }
                            }

                            // Option 2: Pattern Lock
                            val isPatternSelected = selectedType == "PATTERN"
                            Surface(
                                onClick = { selectedType = "PATTERN" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isPatternSelected) primaryCyan.copy(alpha = 0.18f) else cardBg,
                                border = BorderStroke(
                                    if (isPatternSelected) 1.5.dp else 1.dp,
                                    if (isPatternSelected) primaryCyan else Color(0x3094A3B8)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Gesture,
                                        contentDescription = null,
                                        tint = if (isPatternSelected) primaryCyan else textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = AppStrings.get("draw_pattern_title", currentLang),
                                        fontWeight = if (isPatternSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = if (isPatternSelected) primaryCyan else textPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // PIN Input View
                        if (selectedType == "PIN") {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = cardBg,
                                border = BorderStroke(1.dp, Color(0x3094A3B8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = AppStrings.get("enter_4digit_pin", currentLang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = textPrimary
                                    )

                                    if (pinState.length == 4) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "✓ ${AppStrings.get("pin_configured", currentLang)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF10B981)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 4 Circles indicator
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for (i in 0 until 4) {
                                            val isFilled = i < pinInput.length
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isFilled) primaryCyan else (if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1))
                                                    )
                                                    .border(
                                                        1.2.dp,
                                                        if (isFilled) primaryCyan else Color(0x6094A3B8),
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Numeric Keypad
                                    val keys = listOf(
                                        listOf("1", "2", "3"),
                                        listOf("4", "5", "6"),
                                        listOf("7", "8", "9"),
                                        listOf("C", "0", "DEL")
                                    )

                                    Column(
                                        modifier = Modifier.widthIn(max = 280.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        for (row in keys) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                for (key in row) {
                                                    Surface(
                                                        onClick = {
                                                            when (key) {
                                                                "C" -> pinInput = ""
                                                                "DEL" -> if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                                                                else -> {
                                                                    if (pinInput.length < 4) {
                                                                        pinInput += key
                                                                        if (pinInput.length == 4) {
                                                                            pinState = pinInput
                                                                            Toast.makeText(context, AppStrings.get("pin_saved_toast", currentLang), Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .height(44.dp),
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = if (isDarkMode) Color(0xFF0F172A) else Color.White,
                                                        border = BorderStroke(1.dp, Color(0x3094A3B8))
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            if (key == "DEL") {
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                                    contentDescription = "Delete",
                                                                    tint = textSecondary,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                            } else {
                                                                Text(
                                                                    text = key,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 16.sp,
                                                                    color = if (key == "C") Color(0xFFEF4444) else textPrimary
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

                        // Pattern View
                        if (selectedType == "PATTERN") {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = cardBg,
                                border = BorderStroke(1.dp, Color(0x3094A3B8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = AppStrings.get("draw_pattern_instruction", currentLang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = textPrimary
                                    )

                                    if (patternState.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "✓ ${AppStrings.get("pattern_saved_toast", currentLang)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF10B981)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(240.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                                            .border(1.2.dp, primaryCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                                    ) {
                                        PatternLockView(
                                            onPatternComplete = { drawn ->
                                                tempDrawnPattern = drawn
                                                val count = drawn.split(",").filter { it.isNotBlank() }.size
                                                if (count >= 4) {
                                                    patternFeedback = "${AppStrings.get("draw_pattern_title", currentLang)}: $count"
                                                    patternState = drawn
                                                    Toast.makeText(context, AppStrings.get("pattern_saved_toast", currentLang), Toast.LENGTH_SHORT).show()
                                                } else {
                                                    patternFeedback = AppStrings.get("pattern_too_short", currentLang)
                                                }
                                            },
                                            isDark = isDarkMode,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    if (patternFeedback != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = patternFeedback!!,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (tempDrawnPattern.split(",").filter { it.isNotBlank() }.size >= 4) primaryCyan else Color(0xFFEF4444)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Card 3: Biometric Authentication Toggle
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, Color(0x3094A3B8)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = if (biometricState) primaryCyan else textSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = AppStrings.get("biometric_title", currentLang),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = AppStrings.get("biometric_desc", currentLang),
                                            fontSize = 11.sp,
                                            color = textSecondary
                                        )
                                    }
                                }

                                Switch(
                                    checked = biometricState,
                                    onCheckedChange = { biometricState = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = primaryCyan
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Card 4: Passkey Authentication Toggle
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, Color(0x3094A3B8)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        tint = if (passkeyState) primaryCyan else textSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = AppStrings.get("passkey_title", currentLang),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = AppStrings.get("passkey_desc", currentLang),
                                            fontSize = 11.sp,
                                            color = textSecondary
                                        )
                                    }
                                }

                                Switch(
                                    checked = passkeyState,
                                    onCheckedChange = { passkeyState = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = primaryCyan
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Lock Now Button
                        OutlinedButton(
                            onClick = {
                                onSaveSecuritySettings(
                                    enabledState,
                                    selectedType,
                                    pinState,
                                    patternState,
                                    biometricState,
                                    passkeyState
                                )
                                onLockNow()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.2.dp, primaryCyan)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = primaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("lock_now_btn", currentLang),
                                fontWeight = FontWeight.Bold,
                                color = primaryCyan,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (enabledState) {
                                if (selectedType == "PIN" && pinState.length != 4) {
                                    Toast.makeText(context, AppStrings.get("enter_4digit_pin", currentLang), Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (selectedType == "PATTERN" && patternState.isEmpty()) {
                                    Toast.makeText(context, AppStrings.get("pattern_too_short", currentLang), Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                            }
                            onSaveSecuritySettings(
                                enabledState,
                                selectedType,
                                pinState,
                                patternState,
                                biometricState,
                                passkeyState
                            )
                            Toast.makeText(context, AppStrings.get("security_saved_toast", currentLang), Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryCyan,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = AppStrings.get("save", currentLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0x4094A3B8))
                    ) {
                        Text(
                            text = AppStrings.get("cancel", currentLang),
                            color = textSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
