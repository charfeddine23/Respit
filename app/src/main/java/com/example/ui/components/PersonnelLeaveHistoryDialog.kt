package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.LeaveRecord
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow

@Composable
fun PersonnelLeaveHistoryDialog(
    personnel: Personnel,
    allLeaves: List<LeaveRecord>,
    onDismiss: () -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val personLeaves = remember(personnel, allLeaves) {
        Personnel.findLeavesForPersonnel(personnel, allLeaves)
            .sortedWith(
                compareByDescending<LeaveRecord> { it.startDateMillis }
                    .thenByDescending { it.createdAtMillis }
            )
    }

    val totalDays = remember(personLeaves) {
        personLeaves.sumOf { record ->
            Personnel.inclusiveLeaveDays(record.startDateMillis, record.returnDateMillis)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val dialogContainerBg = when {
        isNeumorphic && isDarkMode -> Color(0xFF161F30)
        isNeumorphic -> Color(0xFFE6EEF8)
        isDarkMode -> Color(0xF50F172A)
        else -> Color(0xFAFFFFFF)
    }
    val cardBg = when {
        isNeumorphic && isDarkMode -> Color(0xFF1C273B)
        isNeumorphic -> Color(0xFFF0F5FC)
        isDarkMode -> Color(0xFF182338)
        else -> Color(0xFFF8FAFC)
    }
    val borderColor = if (isDarkMode) primaryColor.copy(alpha = 0.24f) else primaryColor.copy(alpha = 0.16f)
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)

    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }
    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Dialog(onDismissRequest = onDismiss) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            AnimatedVisibility(
                visibleState = entranceState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(200, easing = FastOutSlowInEasing)) + scaleIn(
                        initialScale = 0.93f,
                        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMedium)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(140)) + scaleOut(targetScale = 0.95f)
                } else ExitTransition.None
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = dialogContainerBg,
                    border = BorderStroke(
                        1.3.dp,
                        Brush.linearGradient(
                            listOf(
                                primaryColor.copy(alpha = if (isDarkMode) 0.50f else 0.38f),
                                secondaryColor.copy(alpha = if (isDarkMode) 0.35f else 0.25f),
                                Color.White.copy(alpha = if (isDarkMode) 0.14f else 0.90f)
                            )
                        )
                    ),
                    shadowElevation = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                        .heightIn(max = 680.dp)
                        .testTag("personnel_leave_history_dialog")
                ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                primaryColor.copy(alpha = if (isDarkMode) 0.18f else 0.10f),
                                                secondaryColor.copy(alpha = if (isDarkMode) 0.10f else 0.05f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            ) {
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
                                                    Brush.linearGradient(listOf(primaryColor, secondaryColor))
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.History,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = AppStrings.get("leave_history_title", currentLang),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black,
                                                color = textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${formatRank(personnel.rank, currentLang)} • ${personnel.getLocalizedName(currentLang)}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.5.sp,
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
                                            .testTag("leave_history_close_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = AppStrings.get("close", currentLang),
                                            tint = textSecondary
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = borderColor)

                            // Summary KPI Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = cardBg,
                                    border = BorderStroke(1.dp, borderColor),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${personLeaves.size}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = AppStrings.get("leave_history_count", currentLang),
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = cardBg,
                                    border = BorderStroke(1.dp, borderColor),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$totalDays ${AppStrings.get("days", currentLang)}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EmeraldSuccess
                                        )
                                        Text(
                                            text = AppStrings.get("leave_history_total_days", currentLang),
                                            fontSize = 11.sp,
                                            color = textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            if (personLeaves.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EventBusy,
                                        contentDescription = null,
                                        tint = textSecondary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Text(
                                        text = AppStrings.get("no_leave_history_desc", currentLang),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .fillMaxWidth()
                                        .testTag("leave_history_list"),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(personLeaves, key = { it.id }) { record ->
                                        val daysCount = Personnel.inclusiveLeaveDays(
                                            record.startDateMillis,
                                            record.returnDateMillis
                                        )
                                        val wilayaDisplay = formatWilaya(
                                            record.destinationWilaya,
                                            currentLang,
                                            includeCode = false
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = cardBg,
                                            border = BorderStroke(1.dp, borderColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("leave_history_item_${record.id}")
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isDarkMode) Color(0x337C3AED) else Color(0xFFEDE9FE)
                                                    ) {
                                                        Text(
                                                            text = formatLeaveType(record.leaveType, currentLang),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isDarkMode) Color(0xFFC4B5FD) else Color(0xFF6D28D9),
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                        )
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(50.dp),
                                                        color = primaryColor.copy(alpha = 0.12f)
                                                    ) {
                                                        Text(
                                                            text = "$daysCount ${AppStrings.get("days", currentLang)}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = primaryColor,
                                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                                                        )
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.CalendarMonth,
                                                            contentDescription = null,
                                                            tint = if (isDarkMode) CyanGlow else VibrantCyan,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Text(
                                                            text = "${AppStrings.get("departure_date_label", currentLang)}: ${record.formattedStartDate}",
                                                            fontSize = 11.5.sp,
                                                            fontFamily = FontFamily.Monospace,
                                                            color = textPrimary,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${AppStrings.get("leave_return_date_short", currentLang)}: ${record.formattedReturnDate}",
                                                        fontSize = 11.5.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = EmeraldSuccess,
                                                        fontWeight = FontWeight.Bold
                                                    )

                                                    if (record.statusText.isNotBlank()) {
                                                        Text(
                                                            text = record.statusText,
                                                            fontSize = 10.5.sp,
                                                            color = textSecondary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }

                                                if (wilayaDisplay.isNotBlank() || record.notes.isNotBlank()) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (wilayaDisplay.isNotBlank()) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.LocationOn,
                                                                    contentDescription = null,
                                                                    tint = primaryColor,
                                                                    modifier = Modifier.size(12.dp)
                                                                )
                                                                Text(
                                                                    text = wilayaDisplay,
                                                                    fontSize = 10.5.sp,
                                                                    color = textSecondary
                                                                )
                                                            }
                                                        }
                                                        if (record.notes.isNotBlank()) {
                                                            Text(
                                                                text = record.notes,
                                                                fontSize = 10.5.sp,
                                                                color = SlateTextSecondary,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis,
                                                                modifier = Modifier.weight(1f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }

                            HorizontalDivider(color = borderColor)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = onDismiss,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("leave_history_done_btn")
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
        }
    }
}
