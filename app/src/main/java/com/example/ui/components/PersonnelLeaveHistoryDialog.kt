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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.data.isShortLeaveType
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
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

@Composable
fun PersonnelLeaveHistoryDialog(
    personnel: Personnel,
    allLeaves: List<LeaveRecord>,
    onDismiss: () -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false,
    onExtendRecord: ((LeaveRecord) -> Unit)? = null
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

    val lastShortLeaveInfo = remember(personnel, allLeaves) {
        personnel.computeLastShortLeaveInfo(allLeaves)
    }

    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryColor = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryColor = preset?.secondary ?: if (effectiveDark) PurpleAccent else RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.62f)

    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }
    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            AnimatedVisibility(
                visibleState = entranceState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(190, easing = FastOutSlowInEasing)) + scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(130)) + scaleOut(targetScale = 0.96f)
                } else ExitTransition.None
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 560.dp)
                            .heightIn(max = 760.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor
                            )
                            .testTag("personnel_leave_history_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LiquidGlassDialogHeader(
                                title = AppStrings.get("leave_history_title", currentLang),
                                subtitle = "${formatRank(personnel.rank, currentLang)} • ${personnel.getLocalizedName(currentLang)}",
                                icon = Icons.Default.History,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor,
                                isDark = effectiveDark,
                                closeContentDescription = AppStrings.get("close", currentLang),
                                onDismiss = onDismiss,
                                badgeText = if (personLeaves.isNotEmpty()) "${personLeaves.size}" else null,
                                closeTestTag = "leave_history_close_btn"
                            )

                            HorizontalDivider(color = glassDividerColor)

                            // Summary KPI Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(16.dp),
                                            cornerRadius = 16.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "${personLeaves.size}",
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = AppStrings.get("leave_history_count", currentLang),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(16.dp),
                                            cornerRadius = 16.dp,
                                            isDark = effectiveDark,
                                            accent = EmeraldSuccess,
                                            secondaryAccent = primaryColor,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$totalDays ${AppStrings.get("days", currentLang)}",
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EmeraldSuccess,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = AppStrings.get("leave_history_total_days", currentLang),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            if (lastShortLeaveInfo != null) {
                                val shortAccent = when {
                                    lastShortLeaveInfo.isCurrentlyOnShortLeave -> if (effectiveDark) CyanGlow else VibrantCyan
                                    lastShortLeaveInfo.isReturningToday -> Color(0xFFF59E0B)
                                    else -> primaryColor
                                }
                                val formattedCounter = if (lastShortLeaveInfo.daysCounter < 0L) {
                                    "\u200E${lastShortLeaveInfo.daysCounter}\u200E"
                                } else {
                                    "${lastShortLeaveInfo.daysCounter}"
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(18.dp),
                                            cornerRadius = 18.dp,
                                            isDark = effectiveDark,
                                            accent = shortAccent,
                                            secondaryAccent = secondaryColor,
                                            isSelected = true,
                                            softTintWhenSelected = true
                                        )
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .testTag("history_last_short_leave_counter")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(14.dp),
                                                    cornerRadius = 14.dp,
                                                    isDark = effectiveDark,
                                                    accent = shortAccent,
                                                    secondaryAccent = secondaryColor,
                                                    isSelected = true,
                                                    softTintWhenSelected = false
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = formattedCounter,
                                                fontSize = if (kotlin.math.abs(lastShortLeaveInfo.daysCounter) >= 100L) 13.sp else 17.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = AppStrings.get("last_short_leave_counter", currentLang),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = shortAccent,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = lastShortLeaveInfo.getCounterStatusText(currentLang),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${lastShortLeaveInfo.record.formattedStartDate} → ${lastShortLeaveInfo.record.formattedReturnDate}",
                                                fontSize = 10.5.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = textSecondary,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }

                            if (personLeaves.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(20.dp),
                                            cornerRadius = 20.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        )
                                        .padding(horizontal = 24.dp, vertical = 28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(16.dp),
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
                                                imageVector = Icons.Default.EventBusy,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        Text(
                                            text = AppStrings.get("no_leave_history_desc", currentLang),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                                    LiquidGlassSectionHighlight(
                                        title = AppStrings.get("leave_history_title", currentLang),
                                        icon = Icons.Default.Schedule,
                                        accent = primaryColor,
                                        isDark = effectiveDark,
                                        badgeText = "${personLeaves.size}"
                                    )
                                }

                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .fillMaxWidth()
                                        .testTag("leave_history_list"),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                                        val isShort = isShortLeaveType(record.leaveType)
                                        val recordAccent = if (isShort) PurpleAccent else primaryColor
                                        val isLastShortRecord = lastShortLeaveInfo != null &&
                                            record.id == lastShortLeaveInfo.record.id &&
                                            record.startDateMillis == lastShortLeaveInfo.record.startDateMillis &&
                                            record.returnDateMillis == lastShortLeaveInfo.record.returnDateMillis

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(18.dp),
                                                    cornerRadius = 18.dp,
                                                    isDark = effectiveDark,
                                                    accent = recordAccent,
                                                    secondaryAccent = secondaryColor,
                                                    isSelected = isLastShortRecord,
                                                    softTintWhenSelected = true
                                                )
                                                .testTag("leave_history_item_${record.id}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(IntrinsicSize.Min)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(4.dp)
                                                        .fillMaxHeight()
                                                        .background(
                                                            Brush.verticalGradient(
                                                                listOf(recordAccent, secondaryColor)
                                                            )
                                                        )
                                                )

                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                            modifier = Modifier.weight(1f, fill = false)
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(recordAccent.copy(alpha = if (effectiveDark) 0.24f else 0.14f))
                                                            ) {
                                                                Text(
                                                                    text = formatLeaveType(record.leaveType, currentLang),
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (effectiveDark) Color.White else recordAccent,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                                )
                                                            }

                                                            if (isLastShortRecord) {
                                                                val badgeCounter = if (lastShortLeaveInfo.daysCounter < 0L) {
                                                                    "\u200E${lastShortLeaveInfo.daysCounter}\u200E"
                                                                } else {
                                                                    "${lastShortLeaveInfo.daysCounter}"
                                                                }
                                                                Box(
                                                                    modifier = Modifier
                                                                        .clip(RoundedCornerShape(8.dp))
                                                                        .background(EmeraldSuccess.copy(alpha = if (effectiveDark) 0.24f else 0.14f))
                                                                        .testTag("leave_history_short_counter_badge")
                                                                ) {
                                                                    Text(
                                                                        text = "${AppStrings.get("short_leave_compact_label", currentLang)}: $badgeCounter",
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.ExtraBold,
                                                                        color = EmeraldSuccess,
                                                                        maxLines = 1,
                                                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        Box(
                                                            modifier = Modifier.liquidGlassPanel(
                                                                shape = RoundedCornerShape(50.dp),
                                                                cornerRadius = 50.dp,
                                                                isDark = effectiveDark,
                                                                accent = recordAccent,
                                                                secondaryAccent = secondaryColor,
                                                                isSelected = true,
                                                                softTintWhenSelected = true
                                                            )
                                                        ) {
                                                            Text(
                                                                text = "$daysCount ${AppStrings.get("days", currentLang)}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = recordAccent,
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
                                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.CalendarMonth,
                                                                contentDescription = null,
                                                                tint = if (effectiveDark) CyanGlow else VibrantCyan,
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
                                                                    color = textSecondary,
                                                                    maxLines = 1,
                                                                    overflow = TextOverflow.Ellipsis,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (onExtendRecord != null) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.End
                                                        ) {
                                                            Surface(
                                                                onClick = { onExtendRecord(record) },
                                                                shape = RoundedCornerShape(12.dp),
                                                                color = Color.Transparent,
                                                                modifier = Modifier
                                                                    .defaultMinSize(minHeight = 48.dp)
                                                                    .liquidGlassPanel(
                                                                        shape = RoundedCornerShape(12.dp),
                                                                        cornerRadius = 12.dp,
                                                                        isDark = effectiveDark,
                                                                        accent = EmeraldSuccess,
                                                                        secondaryAccent = recordAccent,
                                                                        isSelected = true,
                                                                        softTintWhenSelected = true
                                                                    )
                                                                    .testTag("extend_history_leave_btn_${record.id}")
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.MoreTime,
                                                                        contentDescription = AppStrings.get("extend_leave_btn", currentLang),
                                                                        tint = EmeraldSuccess,
                                                                        modifier = Modifier.size(16.dp)
                                                                    )
                                                                    Text(
                                                                        text = AppStrings.get("extend_short_chip", currentLang),
                                                                        fontSize = 12.sp,
                                                                        fontWeight = FontWeight.ExtraBold,
                                                                        color = EmeraldSuccess
                                                                    )
                                                                }
                                                            }
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

                            HorizontalDivider(color = glassDividerColor)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                LiquidGlassActionButton(
                                    text = AppStrings.get("close", currentLang),
                                    onClick = onDismiss,
                                    isDark = effectiveDark,
                                    primaryAccent = primaryColor,
                                    secondaryAccent = secondaryColor,
                                    isPrimary = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("leave_history_done_btn")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
