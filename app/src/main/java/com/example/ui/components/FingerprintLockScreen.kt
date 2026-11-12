package com.example.ui.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoyalBlueAccent
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun FingerprintLockScreen(
    onAuthenticate: () -> Unit,
    onFallbackUnlock: () -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    modifier: Modifier = Modifier
) {
    var isAuthenticating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Automatically trigger biometric authentication prompt once screen opens
    LaunchedEffect(Unit) {
        onAuthenticate()
    }

    // Neumorphic Background & Colors
    val backgroundColor = if (isDarkMode) Color(0xFF0F172A) else Color(0xFFE6EEF8)
    val cardBackground = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFEDF2F7)
    val lightShadow = if (isDarkMode) Color(0x1AFFFFFF) else Color(0xFFFFFFFF)
    val darkShadow = if (isDarkMode) Color(0x60000000) else Color(0x3094A3B8)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    // Pulsing animation for the tactile fingerprint sensor
    val infiniteTransition = rememberInfiniteTransition(label = "fingerprint_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .testTag("fingerprint_lock_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo with Tactile Neumorphic container
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = if (isDarkMode) 8.dp else 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = darkShadow,
                        ambientColor = lightShadow
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBackground)
                    .border(1.dp, lightShadow.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_respite_pulse_logo),
                    contentDescription = AppStrings.get("app_icon_desc", currentLang),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Name & Subtitle
            Text(
                text = AppStrings.get("app_title", currentLang),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = textPrimary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Respite Pulse • Duty & Leave System",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = textSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Neumorphic Main Security Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (isDarkMode) 10.dp else 14.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = darkShadow,
                        ambientColor = lightShadow
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(cardBackground)
                    .border(1.5.dp, lightShadow.copy(alpha = 0.9f), RoundedCornerShape(28.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Security badge pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(RoyalBlueAccent.copy(alpha = 0.12f))
                            .border(1.dp, RoyalBlueAccent.copy(alpha = 0.25f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = RoyalBlueAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("fingerprint_lock_title", currentLang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlueAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tactile Fingerprint Sensor Target (Double Neumorphic Ring + Pulse)
                    Box(
                        modifier = Modifier
                            .size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer Pulsing Glow
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            RoyalBlueAccent.copy(alpha = pulseAlpha),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Outer Neumorphic Extruded Circle
                        Box(
                            modifier = Modifier
                                .size(112.dp)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = CircleShape,
                                    spotColor = darkShadow,
                                    ambientColor = lightShadow
                                )
                                .clip(CircleShape)
                                .background(cardBackground)
                                .border(2.dp, lightShadow.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Inner Sensor Button (Interactive Click)
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = if (isDarkMode) {
                                                listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                                            } else {
                                                listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                                            }
                                        )
                                    )
                                    .clickable {
                                        errorMessage = null
                                        onAuthenticate()
                                    }
                                    .testTag("fingerprint_sensor_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = AppStrings.get("biometric_unlock_btn", currentLang),
                                    tint = Color.White,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Instruction prompt
                    Text(
                        text = AppStrings.get("fingerprint_touch_sensor", currentLang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = AppStrings.get("fingerprint_lock_desc", currentLang),
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    // Error Message
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.12f))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Scan Trigger Button
                    Surface(
                        onClick = {
                            errorMessage = null
                            onAuthenticate()
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = RoyalBlueAccent,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("scan_fingerprint_action_button")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("fingerprint_scan_now_btn", currentLang),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Accessible Fallback Unlock Button (For headless tests and emulators without enrolled hardware)
            Surface(
                onClick = onFallbackUnlock,
                shape = RoundedCornerShape(14.dp),
                color = cardBackground,
                border = BorderStroke(1.dp, lightShadow.copy(alpha = 0.9f)),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("fallback_unlock_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("fingerprint_fallback_btn", currentLang),
                        color = textSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Developer Credit
            Text(
                text = "Developed by Charfeddine Kahlessenane",
                style = MaterialTheme.typography.labelSmall,
                color = textSecondary.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
