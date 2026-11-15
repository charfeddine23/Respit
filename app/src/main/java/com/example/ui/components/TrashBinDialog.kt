package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Personnel
import com.example.data.formatRank
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrashBinDialog(
    trashedPersonnel: List<Personnel>,
    isDarkMode: Boolean,
    currentLang: AppLanguage,
    onRestore: (Personnel) -> Unit,
    onRestoreAll: () -> Unit,
    onDeleteForever: (Personnel) -> Unit,
    onEmptyTrash: () -> Unit,
    onDismiss: () -> Unit
) {
    val preset = LocalThemePreset.current
    val surfaceBg = preset?.surfacePrimary ?: if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val cardBg = preset?.surfaceSecondary ?: if (isDarkMode) Color(0xFF1E293B) else Color.White
    val textPrimary = preset?.textPrimary ?: if (isDarkMode) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
    val borderAccent = preset?.borderAccent ?: if (isDarkMode) Color(0x44EF4444) else Color(0x33EF4444)
    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US) }

    var showEmptyConfirm by remember { mutableStateOf(false) }

    if (showEmptyConfirm) {
        AlertDialog(
            onDismissRequest = { showEmptyConfirm = false },
            title = {
                Text(
                    text = AppStrings.get("trash_empty_confirm_title", currentLang),
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Text(text = AppStrings.get("trash_empty_confirm_desc", currentLang))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEmptyConfirm = false
                        onEmptyTrash()
                    },
                    modifier = Modifier.testTag("trash_confirm_empty_button")
                ) {
                    Text(
                        text = AppStrings.get("trash_empty_all_btn", currentLang),
                        color = RedDanger,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyConfirm = false }) {
                    Text(AppStrings.get("cancel", currentLang))
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(min = 260.dp, max = 640.dp)
                .testTag("trash_bin_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = surfaceBg,
            border = BorderStroke(1.2.dp, borderAccent),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
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
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(CoralRedAccent.copy(alpha = 0.24f), RedDanger.copy(alpha = 0.14f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = CoralRedAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = AppStrings.get("trash_bin_title", currentLang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textPrimary
                                )
                                if (trashedPersonnel.isNotEmpty()) {
                                    Surface(
                                        shape = CircleShape,
                                        color = CoralRedAccent.copy(alpha = 0.18f)
                                    ) {
                                        Text(
                                            text = "${trashedPersonnel.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = CoralRedAccent,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = AppStrings.get("trash_bin_sub", currentLang),
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("trash_bin_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.get("close", currentLang),
                            tint = textSecondary
                        )
                    }
                }

                // Action Bar when non-empty
                AnimatedVisibility(
                    visible = trashedPersonnel.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onRestoreAll,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldSuccess,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("trash_restore_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestoreFromTrash,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("trash_restore_all_btn", currentLang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { showEmptyConfirm = true },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, RedDanger.copy(alpha = 0.65f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = RedDanger.copy(alpha = 0.08f),
                                contentColor = RedDanger
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("trash_empty_all_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("trash_empty_all_btn", currentLang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Content List or Empty State
                if (trashedPersonnel.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        (preset?.primary ?: EmeraldSuccess).copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = preset?.primary ?: EmeraldSuccess,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Text(
                                text = AppStrings.get("trash_bin_empty_title", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = AppStrings.get("trash_bin_empty_desc", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = trashedPersonnel,
                            key = { it.id },
                            contentType = { "trashed_personnel" }
                        ) { person ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = cardBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("trash_item_${person.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            text = person.fullName,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = formatRank(person.rank, currentLang),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = preset?.primary ?: Color(0xFF0284C7),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "• ${person.registrationNumber}",
                                                fontSize = 10.5.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = textSecondary
                                            )
                                        }
                                        if (person.deletedAtMillis > 0L) {
                                            Text(
                                                text = "${AppStrings.get("trash_deleted_at", currentLang)} \u200E${dateFmt.format(Date(person.deletedAtMillis))}\u200E",
                                                fontSize = 10.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            onClick = { onRestore(person) },
                                            shape = RoundedCornerShape(12.dp),
                                            color = EmeraldSuccess.copy(alpha = 0.16f),
                                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.55f)),
                                            modifier = Modifier
                                                .height(48.dp)
                                                .testTag("trash_restore_${person.id}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 10.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.RestoreFromTrash,
                                                    contentDescription = AppStrings.get("trash_restore_btn", currentLang),
                                                    tint = EmeraldSuccess,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = AppStrings.get("trash_restore_btn", currentLang),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldSuccess
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteForever(person) },
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(RedDanger.copy(alpha = 0.12f))
                                                .testTag("trash_delete_forever_${person.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteForever,
                                                contentDescription = AppStrings.get("trash_delete_forever", currentLang),
                                                tint = RedDanger,
                                                modifier = Modifier.size(18.dp)
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
}
