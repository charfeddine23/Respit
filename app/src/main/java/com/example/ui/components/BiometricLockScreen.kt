package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DynamicAuroraBackground
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.KufiArabicBrandFontFamily
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.BiometricAuthHelper

@Composable
fun BiometricLockScreen(
    onUnlockSuccess: () -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier,
    animationsEnabled: Boolean = true
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
                onSuccess = { onUnlockSuccess() },
                onError = { err -> authErrorFeedback = err },
                onFailed = { authErrorFeedback = AppStrings.get("biometric_auth_failed", currentLang) }
            )
        } else {
            onUnlockSuccess()
        }
    }

    LaunchedEffect(Unit) {
        triggerBiometricPrompt()
    }

    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryAccent = preset?.secondary ?: RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else Color(0xFF475569)

    val pulseScale = 1f

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .testTag("biometric_lock_screen"),
            color = Color.Transparent
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                DynamicAuroraBackground(
                    isDark = effectiveDark,
                    animationsEnabled = animationsEnabled
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 420.dp)
                            .liquidGlass(
                                shape = RoundedCornerShape(30.dp),
                                elevation = 8.dp,
                                borderAlpha = 0.92f,
                                isDark = effectiveDark,
                                cornerRadius = 30.dp
                            )
                            .padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Top Protection Shield Pill
                            Box(
                                modifier = Modifier.liquidGlassPanel(
                                    shape = RoundedCornerShape(50.dp),
                                    cornerRadius = 50.dp,
                                    isDark = effectiveDark,
                                    accent = EmeraldSuccess,
                                    secondaryAccent = primaryAccent,
                                    isSelected = true,
                                    softTintWhenSelected = true
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = AppStrings.get("biometric_title", currentLang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldSuccess
                                    )
                                }
                            }

                            // Clean Circular Logo Medallion
                            Box(
                                modifier = Modifier
                                    .size(104.dp)
                                    .scale(pulseScale)
                                    .liquidGlassPanel(
                                        shape = CircleShape,
                                        cornerRadius = 52.dp,
                                        isDark = effectiveDark,
                                        accent = primaryAccent,
                                        secondaryAccent = secondaryAccent,
                                        isSelected = true,
                                        softTintWhenSelected = true
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = AppStrings.get("app_icon_desc", currentLang),
                                    contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(
                                        if (effectiveDark) Color.White else primaryAccent
                                    ),
                                    modifier = Modifier.size(width = 76.dp, height = 60.dp)
                                )
                            }

                            val isArabicTitle = currentLang == AppLanguage.ARABIC
                            Text(
                                text = AppStrings.get("app_title", currentLang),
                                fontFamily = if (isArabicTitle) KufiArabicBrandFontFamily else FontFamily.Default,
                                fontSize = if (isArabicTitle) 28.sp else 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryAccent
                            )

                            Text(
                                text = AppStrings.get("biometric_screen_protected", currentLang),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = textPrimary,
                                textAlign = TextAlign.Center
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .liquidGlassPanel(
                                        shape = RoundedCornerShape(16.dp),
                                        cornerRadius = 16.dp,
                                        isDark = effectiveDark,
                                        accent = primaryAccent,
                                        secondaryAccent = secondaryAccent
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = primaryAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = AppStrings.get("biometric_screen_instruction", currentLang),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = textSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            if (authErrorFeedback != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(14.dp),
                                            cornerRadius = 14.dp,
                                            isDark = effectiveDark,
                                            accent = RedDanger,
                                            secondaryAccent = RedDanger,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = authErrorFeedback.orEmpty(),
                                        fontSize = 12.sp,
                                        color = RedDanger,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LiquidGlassActionButton(
                                text = AppStrings.get("biometric_unlock_device_btn", currentLang),
                                onClick = { triggerBiometricPrompt() },
                                isDark = effectiveDark,
                                primaryAccent = primaryAccent,
                                secondaryAccent = secondaryAccent,
                                icon = Icons.Default.Fingerprint,
                                isPrimary = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("biometric_unlock_button")
                            )
                        }
                    }
                }
            }
        }
    }
}
