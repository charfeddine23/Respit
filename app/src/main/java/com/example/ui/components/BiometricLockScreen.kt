package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.ui.theme.KufiArabicBrandFontFamily
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.BiometricAuthHelper

@Composable
fun BiometricLockScreen(
    onUnlockSuccess: () -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var authErrorFeedback by remember { mutableStateOf<String?>(null) }

    fun triggerBiometricPrompt() {
        authErrorFeedback = null
        val activity = context as? FragmentActivity
        if (activity != null) {
            BiometricAuthHelper.authenticate(
                activity = activity,
                title = AppStrings.get("biometric_prompt_title", currentLang),
                subtitle = AppStrings.get("biometric_prompt_subtitle", currentLang),
                defaultError = AppStrings.get("biometric_default_error", currentLang),
                onSuccess = {
                    onUnlockSuccess()
                },
                onError = { err ->
                    authErrorFeedback = err
                },
                onFailed = {
                    authErrorFeedback = AppStrings.get("biometric_auth_failed", currentLang)
                }
            )
        } else {
            // Fallback for non-fragment environments
            onUnlockSuccess()
        }
    }

    // Auto-launch prompt when screen opens
    LaunchedEffect(Unit) {
        triggerBiometricPrompt()
    }

    val primaryCyan = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00BCD4)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("biometric_lock_screen"),
        color = if (isDarkMode) Color(0xFF0B1120) else Color(0xFFF1F5F9)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.widthIn(max = 420.dp)
            ) {
                // Enlarged frameless pulsing app logo
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = AppStrings.get("app_icon_desc", currentLang),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        if (isDarkMode) Color.White else Color(0xFF0A192F)
                    ),
                    modifier = Modifier
                        .size(width = 112.dp, height = 88.dp)
                        .scale(pulseScale)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val isArabicTitle = currentLang == AppLanguage.ARABIC
                Text(
                    text = AppStrings.get("app_title", currentLang),
                    fontFamily = if (isArabicTitle) KufiArabicBrandFontFamily else FontFamily.Default,
                    fontSize = if (isArabicTitle) 26.sp else 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryCyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = AppStrings.get("biometric_screen_protected", currentLang),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = AppStrings.get("biometric_screen_instruction", currentLang),
                    fontSize = 13.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (authErrorFeedback != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = authErrorFeedback ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Primary Unlock Button
                Button(
                    onClick = { triggerBiometricPrompt() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("biometric_unlock_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = AppStrings.get("biometric_title", currentLang),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = AppStrings.get("biometric_unlock_device_btn", currentLang),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

/**
 * Backward compatibility alias for PinLockScreen.
 * Legacy PIN and pattern drawing systems have been completely removed
 * and routed to modern AndroidX Biometric authentication.
 */
@Composable
fun PinLockScreen(
    onVerifyPin: (String) -> Boolean = { true },
    onVerifyPattern: ((String) -> Boolean)? = null,
    onBiometricUnlock: (() -> Unit)? = null,
    onPasskeyUnlock: (() -> Unit)? = null,
    lockType: String = "BIOMETRIC",
    isBiometricAvailable: Boolean = true,
    isPasskeyAvailable: Boolean = false,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    BiometricLockScreen(
        onUnlockSuccess = {
            onBiometricUnlock?.invoke() ?: onVerifyPin("")
        },
        isDarkMode = isDarkMode,
        currentLang = currentLang,
        modifier = modifier
    )
}
