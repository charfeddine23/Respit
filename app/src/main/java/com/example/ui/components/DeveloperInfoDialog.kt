package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.KufiArabicBrandFontFamily
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow

const val DEVELOPER_WHATSAPP_URL = "https://wa.me/charfeddineka?s=t"

fun openDeveloperWhatsApp(context: Context, currentLang: AppLanguage) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DEVELOPER_WHATSAPP_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(
            context,
            AppStrings.get("whatsapp_open_error", currentLang),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun DeveloperInfoDialog(
    currentLang: AppLanguage = AppLanguage.ARABIC,
    onDismiss: () -> Unit,
    isDarkMode: Boolean? = null,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val context = LocalContext.current
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode ?: (MaterialTheme.colorScheme.surface.luminance() < 0.5f)
    val primaryColor = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryColor = preset?.secondary ?: if (effectiveDark) PurpleAccent else RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.62f)
    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(
            LocalLayoutDirection provides if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            AnimatedVisibility(
                visibleState = entranceState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(180)) + scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
                } else ExitTransition.None
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 500.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor
                            )
                            .testTag("developer_info_dialog")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LiquidGlassDialogHeader(
                                title = AppStrings.get("app_and_dev_info", currentLang),
                                subtitle = "v${com.example.BuildConfig.VERSION_NAME} (${com.example.BuildConfig.VERSION_CODE})",
                                icon = Icons.Default.Info,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor,
                                isDark = effectiveDark,
                                closeContentDescription = AppStrings.get("close", currentLang),
                                onDismiss = onDismiss
                            )

                            HorizontalDivider(color = glassDividerColor)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Enlarged App Logo inside glowing glass capsule
                                Box(
                                    modifier = Modifier
                                        .size(width = 128.dp, height = 96.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(22.dp),
                                            cornerRadius = 22.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor,
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
                                            if (effectiveDark) Color.White else primaryColor
                                        ),
                                        modifier = Modifier.size(width = 92.dp, height = 72.dp)
                                    )
                                }

                                val isArabicTitle = currentLang == AppLanguage.ARABIC
                                Text(
                                    text = AppStrings.get("app_title", currentLang),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontFamily = if (isArabicTitle) KufiArabicBrandFontFamily else FontFamily.Default,
                                        fontSize = if (isArabicTitle) 26.sp else 24.sp
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )

                                // Version Badge
                                Box(
                                    modifier = Modifier
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(50.dp),
                                            cornerRadius = 50.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .padding(horizontal = 14.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "Version: v${com.example.BuildConfig.VERSION_NAME} (${com.example.BuildConfig.VERSION_CODE})",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = primaryColor
                                    )
                                }

                                // Developer Showcase Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(20.dp),
                                            cornerRadius = 20.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .liquidGlassPanel(
                                                        shape = CircleShape,
                                                        cornerRadius = 16.dp,
                                                        isDark = effectiveDark,
                                                        accent = primaryColor,
                                                        secondaryAccent = secondaryColor,
                                                        isSelected = true,
                                                        softTintWhenSelected = true
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = primaryColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = AppStrings.get("certified_developer", currentLang),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = textSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Developed by Charfeddine Ka",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textPrimary,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = AppStrings.get("app_system_desc", currentLang),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textSecondary,
                                            textAlign = TextAlign.Center,
                                            fontSize = 11.sp
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Sleek WhatsApp contact pill (48dp touch target)
                                        Box(
                                            modifier = Modifier
                                                .defaultMinSize(minHeight = 48.dp)
                                                .clip(RoundedCornerShape(50.dp))
                                                .clickable { openDeveloperWhatsApp(context, currentLang) }
                                                .padding(horizontal = 4.dp)
                                                .testTag("developer_whatsapp_button"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(50.dp),
                                                        cornerRadius = 50.dp,
                                                        isDark = effectiveDark,
                                                        accent = EmeraldSuccess,
                                                        secondaryAccent = Color(0xFF25D366),
                                                        isSelected = true,
                                                        softTintWhenSelected = true
                                                    )
                                                    .padding(horizontal = 16.dp, vertical = 9.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFF25D366)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.ic_whatsapp),
                                                            contentDescription = AppStrings.get("contact_whatsapp", currentLang),
                                                            tint = Color.White,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = AppStrings.get("contact_whatsapp", currentLang),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = if (effectiveDark) Color(0xFF4ADE80) else Color(0xFF15803D)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                LiquidGlassActionButton(
                                    text = AppStrings.get("close", currentLang),
                                    onClick = onDismiss,
                                    isDark = effectiveDark,
                                    primaryAccent = primaryColor,
                                    secondaryAccent = secondaryColor,
                                    isPrimary = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
