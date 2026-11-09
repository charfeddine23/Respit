package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PinLockScreen(
    onVerifyPin: (String) -> Boolean = { false },
    onVerifyPattern: ((String) -> Boolean)? = null,
    onBiometricUnlock: (() -> Unit)? = null,
    onPasskeyUnlock: (() -> Unit)? = null,
    lockType: String = "PATTERN", // "PATTERN" or "PIN"
    isBiometricAvailable: Boolean = false,
    isPasskeyAvailable: Boolean = false,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(if (lockType.uppercase() == "PIN") "PIN" else "PATTERN") }
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val primaryCyan = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    fun triggerShake(msg: String) {
        errorMessage = msg
        isError = true
        scope.launch {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -20f at 80
                    20f at 160
                    -15f at 240
                    15f at 320
                    0f at 400
                }
            )
            delay(1200)
            isError = false
            errorMessage = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFE8EEF5))
            .testTag("security_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon with card glow
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .liquidGlassInteractive(
                        shape = RoundedCornerShape(20.dp),
                        glowColor = MaterialTheme.colorScheme.primary,
                        isDark = isDarkMode
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_respite_pulse_logo),
                    contentDescription = AppStrings.get("app_icon_desc", currentLang),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Name
            Text(
                text = AppStrings.get("app_title", currentLang),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Mode Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (activeMode == "PIN") Icons.Default.Pin else Icons.Default.Gesture,
                    contentDescription = null,
                    tint = primaryCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (activeMode == "PIN") AppStrings.get("enter_4digit_pin", currentLang) else AppStrings.get("unlock_with_pattern", currentLang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Error Message Banner with shake animation
            AnimatedVisibility(visible = errorMessage != null) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (activeMode == "PATTERN") {
                // Pattern Lock Grid
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isDarkMode) Color(0xFF161F2E) else Color(0xFFEDF2F8))
                        .padding(8.dp)
                ) {
                    PatternLockView(
                        onPatternComplete = { patternString ->
                            val ok = onVerifyPattern?.invoke(patternString) ?: false
                            if (!ok) {
                                triggerShake(AppStrings.get("wrong_pattern", currentLang))
                            }
                        },
                        isDark = isDarkMode,
                        isError = isError,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // PIN Lock View (4 Dots + Keypad)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 4 Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        for (i in 0 until 4) {
                            val isFilled = i < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(if (isFilled) primaryCyan else (if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)))
                                    .border(1.2.dp, if (isFilled) primaryCyan else Color(0x6094A3B8), CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                                                "C" -> enteredPin = ""
                                                "DEL" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                                else -> {
                                                    if (enteredPin.length < 4) {
                                                        enteredPin += key
                                                        if (enteredPin.length == 4) {
                                                            val ok = onVerifyPin(enteredPin)
                                                            if (!ok) {
                                                                triggerShake(AppStrings.get("wrong_pin", currentLang))
                                                                enteredPin = ""
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
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
                                                    fontSize = 17.sp,
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

            Spacer(modifier = Modifier.height(16.dp))

            // Alternative Unlock Actions (Biometric & Passkey & Switch Mode)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Biometric Unlock Button
                if (isBiometricAvailable && onBiometricUnlock != null) {
                    IconButton(
                        onClick = onBiometricUnlock,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(primaryCyan.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "بصمة الإصبع",
                            tint = primaryCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Passkey Unlock Button
                if (isPasskeyAvailable && onPasskeyUnlock != null) {
                    IconButton(
                        onClick = onPasskeyUnlock,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "مفتاح المرور Passkey",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Switch Between PIN and Pattern
                OutlinedButton(
                    onClick = {
                        activeMode = if (activeMode == "PIN") "PATTERN" else "PIN"
                        errorMessage = null
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, primaryCyan.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (activeMode == "PIN") AppStrings.get("use_pattern_btn", currentLang) else AppStrings.get("use_pin_btn", currentLang),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryCyan
                    )
                }
            }
        }
    }
}
