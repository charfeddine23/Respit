package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings

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
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                    )
                )
            ),
            tonalElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .padding(16.dp)
                .testTag("developer_info_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("app_and_dev_info", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.get("close", currentLang),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Icon in Liquid Glass Card
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .liquidGlassInteractive(
                            shape = RoundedCornerShape(20.dp),
                            glowColor = MaterialTheme.colorScheme.primary
                        )
                        .background(
                            color = Color(0xFFFAF6EE),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = AppStrings.get("app_icon_desc", currentLang),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(65.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // App Name
                Text(
                    text = "Respite Pulse",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Version Badge (Dynamic from BuildConfig)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Version: v${com.example.BuildConfig.VERSION_NAME} (${com.example.BuildConfig.VERSION_CODE})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Developer Box (Strictly Kahlessenane Charfeddine)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(
                            1.dp,
                            Color(0x40BAE6FD),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppStrings.get("certified_developer", currentLang),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Developed by Charfeddine Ka",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = AppStrings.get("app_system_desc", currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )

                        val isDarkSurface = MaterialTheme.colorScheme.surface.luminance() < 0.5f
                        // Compact, sleek WhatsApp contact pill (48dp touch target)
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .defaultMinSize(minHeight = 48.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .clickable { openDeveloperWhatsApp(context, currentLang) }
                                .padding(horizontal = 4.dp)
                                .testTag("developer_whatsapp_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = Color(0xFF25D366).copy(alpha = if (isDarkSurface) 0.16f else 0.10f),
                                border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.42f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_whatsapp),
                                            contentDescription = AppStrings.get("contact_whatsapp", currentLang),
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = AppStrings.get("contact_whatsapp", currentLang),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDarkSurface) Color(0xFF4ADE80) else Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = AppStrings.get("close", currentLang),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
