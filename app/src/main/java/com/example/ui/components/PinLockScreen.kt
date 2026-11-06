package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun PinLockScreen(
    onVerifyPin: (String) -> Boolean,
    onVerifyPattern: ((String) -> Boolean)? = null,
    onBiometricUnlock: (() -> Unit)? = null,
    lockType: String = "PIN",
    isBiometricAvailable: Boolean = false,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    var activeMode by remember(lockType) { mutableStateOf(lockType) }
    var enteredPin by remember { mutableStateOf("") }
    var isPatternError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun triggerShake() {
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
        }
    }

    fun handleDigit(digit: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            errorMessage = null

            if (newPin.length == 4) {
                val success = onVerifyPin(newPin)
                if (!success) {
                    errorMessage = AppStrings.get("wrong_pin", currentLang)
                    triggerShake()
                    enteredPin = ""
                }
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC))
            .testTag("security_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon with glass surface
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .liquidGlassInteractive(
                        shape = RoundedCornerShape(18.dp),
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
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (activeMode == "PATTERN") {
                        AppStrings.get("unlock_with_pattern", currentLang)
                    } else {
                        AppStrings.get("unlock_with_pin", currentLang)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = AppStrings.get("security_lock_desc", currentLang),
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Mode Selector Pill (if pattern verification is supported)
            if (onVerifyPattern != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isDarkMode) Color(0x331E293B) else Color(0x22CBD5E1))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // PIN Pill
                    Surface(
                        onClick = {
                            activeMode = "PIN"
                            errorMessage = null
                            isPatternError = false
                        },
                        shape = RoundedCornerShape(50.dp),
                        color = if (activeMode == "PIN") MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pin,
                                contentDescription = null,
                                tint = if (activeMode == "PIN") Color.White else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("lock_type_pin", currentLang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeMode == "PIN") Color.White else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                            )
                        }
                    }

                    // Pattern Pill
                    Surface(
                        onClick = {
                            activeMode = "PATTERN"
                            errorMessage = null
                        },
                        shape = RoundedCornerShape(50.dp),
                        color = if (activeMode == "PATTERN") MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gesture,
                                contentDescription = null,
                                tint = if (activeMode == "PATTERN") Color.White else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("lock_type_pattern", currentLang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeMode == "PATTERN") Color.White else (if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B))
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // PATTERN MODE OR PIN MODE
            if (activeMode == "PATTERN" && onVerifyPattern != null) {
                // Pattern Lock Component
                PatternLockView(
                    onPatternComplete = { patternString ->
                        val ok = onVerifyPattern(patternString)
                        if (ok) {
                            errorMessage = null
                            isPatternError = false
                        } else {
                            isPatternError = true
                            errorMessage = AppStrings.get("wrong_pattern", currentLang)
                            triggerShake()
                            scope.launch {
                                delay(900)
                                isPatternError = false
                            }
                        }
                    },
                    isDark = isDarkMode,
                    isError = isPatternError,
                    modifier = Modifier.size(280.dp)
                )
            } else {
                // PIN MODE: 4 Dots
                Row(
                    modifier = Modifier
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) MaterialTheme.colorScheme.primary
                                    else if (isDarkMode) Color(0x331E293B) else Color(0x40CBD5E1)
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (isFilled) MaterialTheme.colorScheme.primary
                                    else if (isDarkMode) Color(0x6638BDF8) else Color(0x6600BCD4),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            // Error Message
            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFEF4444),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Numeric Keypad for PIN mode
            if (activeMode != "PATTERN") {
                Spacer(modifier = Modifier.height(16.dp))

                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BIOMETRIC", "0", "BACKSPACE")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    keypadRows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.forEach { key ->
                                when (key) {
                                    "BIOMETRIC" -> {
                                        if (isBiometricAvailable && onBiometricUnlock != null) {
                                            IconButton(
                                                onClick = onBiometricUnlock,
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isDarkMode) Color(0x2200BCD4) else Color(0x1A00BCD4)
                                                    )
                                                    .testTag("biometric_unlock_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Fingerprint,
                                                    contentDescription = AppStrings.get("biometric_unlock_btn", currentLang),
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.size(64.dp))
                                        }
                                    }
                                    "BACKSPACE" -> {
                                        IconButton(
                                            onClick = { handleBackspace() },
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isDarkMode) Color(0x1AFFFFFF) else Color(0x0D000000)
                                                )
                                                .testTag("key_backspace")
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                                contentDescription = AppStrings.get("clear_btn", currentLang),
                                                tint = if (isDarkMode) Color.White else Color(0xFF0F172A),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    else -> {
                                        Surface(
                                            onClick = { handleDigit(key) },
                                            shape = CircleShape,
                                            color = if (isDarkMode) Color(0x221E293B) else Color.White,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
                                            ),
                                            shadowElevation = 2.dp,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .testTag("key_$key")
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                Text(
                                                    text = key,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (isBiometricAvailable && onBiometricUnlock != null) {
                // Biometric shortcut in Pattern mode
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onBiometricUnlock,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkMode) Color(0x3300BCD4) else Color(0x2200BCD4),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("pattern_biometric_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("biometric_unlock_btn", currentLang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
