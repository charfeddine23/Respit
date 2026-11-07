package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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

/**
 * Dedicated Pattern Lock Screen:
 * Unlocks strictly with Pattern (PIN and Biometric removed as requested).
 */
@Composable
fun PinLockScreen(
    onVerifyPin: (String) -> Boolean = { false },
    onVerifyPattern: ((String) -> Boolean)? = null,
    onBiometricUnlock: (() -> Unit)? = null,
    lockType: String = "PATTERN",
    isBiometricAvailable: Boolean = false,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    PatternLockScreen(
        onVerifyPattern = onVerifyPattern ?: { false },
        isDarkMode = isDarkMode,
        currentLang = currentLang,
        modifier = modifier
    )
}

@Composable
fun PatternLockScreen(
    onVerifyPattern: (String) -> Boolean,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    var isPatternError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

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
                .widthIn(max = 440.dp)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon with Neumorphic/Glass card
            Box(
                modifier = Modifier
                    .size(72.dp)
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
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Name
            Text(
                text = AppStrings.get("app_title", currentLang),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (isDarkMode) Color.White else Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Header Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Gesture,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppStrings.get("unlock_with_pattern", currentLang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = AppStrings.get("draw_pattern_instruction", currentLang),
                style = MaterialTheme.typography.bodySmall,
                color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message Banner with shake animation
            AnimatedVisibility(visible = errorMessage != null) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                        .padding(bottom = 12.dp)
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

            // Interactive Pattern Lock View
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isDarkMode) Color(0xFF161F2E) else Color(0xFFEDF2F8))
                    .padding(8.dp)
            ) {
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
