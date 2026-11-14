package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonnelCard(
    personnel: Personnel,
    dutyThresholdDays: Int,
    onConfirmDuty: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isFocusMode: Boolean = false,
    isDarkMode: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    effectiveDaysSinceDuty: Long = personnel.daysSinceDutyReturn
) {
    val status = personnel.status
    val isEligible = personnel.isReturnedToDuty && effectiveDaysSinceDuty >= dutyThresholdDays
    val progress = personnel.leaveProgressFraction

    val animatedProgress = if (animationsEnabled && !personnel.isReturnedToDuty) {
        animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            label = "leaveProgress"
        ).value
    } else progress

    data class StatusVisual(
        val color: Color,
        val bgTint: Color,
        val icon: ImageVector,
        val badgeKey: String
    )

    val visual = when {
        status == LeaveStatus.AT_HOME -> StatusVisual(
            color = if (isDarkMode) CyanGlow else VibrantCyan,
            bgTint = if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F2FE),
            icon = Icons.Default.Home,
            badgeKey = "badge_at_home"
        )
        status == LeaveStatus.RETURNING_TODAY -> StatusVisual(
            color = AmberWarning,
            bgTint = if (isDarkMode) Color(0x33F59E0B) else Color(0xFFFEF3C7),
            icon = Icons.Default.NotificationsActive,
            badgeKey = "badge_returning_today"
        )
        status == LeaveStatus.OVERDUE -> StatusVisual(
            color = RedDanger,
            bgTint = if (isDarkMode) Color(0x33EF4444) else Color(0xFFFFE4E6),
            icon = Icons.Default.Warning,
            badgeKey = "badge_overdue"
        )
        status == LeaveStatus.ON_DUTY && isEligible -> StatusVisual(
            color = AmberWarning,
            bgTint = if (isDarkMode) Color(0x33F59E0B) else Color(0xFFFEF3C7),
            icon = Icons.Default.Star,
            badgeKey = "badge_eligible"
        )
        status == LeaveStatus.ON_DUTY -> StatusVisual(
            color = EmeraldSuccess,
            bgTint = if (isDarkMode) Color(0x3310B981) else Color(0xFFDCFCE7),
            icon = Icons.Default.Shield,
            badgeKey = "badge_on_duty"
        )
        else -> StatusVisual(
            color = Color(0xFF8B5CF6),
            bgTint = if (isDarkMode) Color(0x338B5CF6) else Color(0xFFEDE9FE),
            icon = Icons.Default.Schedule,
            badgeKey = "badge_upcoming"
        )
    }

    val localizedRank = formatRank(personnel.rank, currentLang)
    val localizedWilaya = formatWilaya(personnel.wilaya, currentLang, includeCode = false)
    val localizedLeaveType = formatLeaveType(personnel.leaveType, currentLang)

    val avatarShape = if (isNeumorphic) RoundedCornerShape(18.dp) else CircleShape
    var isExpanded by rememberSaveable(personnel.id) { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "arrowRotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = RoundedCornerShape(24.dp),
                elevation = if (isFocusMode) 7.dp else 4.dp,
                borderAlpha = if (isFocusMode) 0.95f else 0.85f,
                isDark = isDarkMode,
                isNeumorphic = isNeumorphic,
                cornerRadius = 24.dp
            )
            .testTag("personnel_item_${personnel.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Coordinated Card Header:
            // Leading side: Days count badge + Full Name + FlowRow(Rank + Reg Number)
            // Trailing side: Status Badge + Wilaya (without code) directly underneath
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    // Days Number-Only Badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(avatarShape)
                            .background(visual.bgTint)
                            .border(
                                1.4.dp,
                                visual.color.copy(alpha = if (isNeumorphic) 0.45f else 0.9f),
                                avatarShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$effectiveDaysSinceDuty",
                            fontWeight = FontWeight.Black,
                            fontSize = if (effectiveDaysSinceDuty >= 100) 14.sp else 17.sp,
                            color = visual.color,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = personnel.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color.White else SlateTextPrimary,
                            fontSize = 15.5.sp,
                            lineHeight = 20.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Localized Military Rank Pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                                border = BorderStroke(1.dp, Color(0xFF00BCD4).copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = localizedRank,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            // Registration Number Pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDarkMode) Color(0x221E293B) else Color(0x1A64748B)
                            ) {
                                Text(
                                    text = personnel.registrationNumber,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkMode) Color(0xFFCBD5E1) else SlateTextSecondary,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Trailing Column: Status Badge + Wilaya right beneath it
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.widthIn(max = 148.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = visual.bgTint,
                        border = BorderStroke(1.2.dp, visual.color.copy(alpha = 0.8f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = visual.icon,
                                contentDescription = null,
                                tint = visual.color,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.get(visual.badgeKey, currentLang),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = visual.color,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Wilaya Chip (without numeric code) directly below Status Badge
                    if (localizedWilaya.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDarkMode) Color(0x28334155) else Color(0x66F1F5F9),
                            border = BorderStroke(
                                0.8.dp,
                                if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
                            ),
                            modifier = Modifier.testTag("personnel_wilaya_${personnel.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = AppStrings.get("wilaya_label", currentLang),
                                    tint = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = localizedWilaya,
                                    fontSize = 10.5.sp,
                                    color = if (isDarkMode) Color(0xFFE2E8F0) else SlateTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Expand / Collapse Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isDarkMode) Color(0x1F38BDF8) else Color(0x1200BCD4)
                    )
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isDarkMode) CyanGlow else VibrantCyan,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (animationsEnabled) arrowRotation else (if (isExpanded) 180f else 0f))
                    )
                    Text(
                        text = AppStrings.get(
                            if (isExpanded) "click_to_collapse" else "click_to_expand",
                            currentLang
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) CyanGlow else VibrantCyan
                    )
                }

                if (!isExpanded) {
                    Text(
                        text = "${AppStrings.get("return_date", currentLang)} ${personnel.formattedReturnDate}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (personnel.isOverdue) RedDanger else (if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Expandable Details Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = if (animationsEnabled) {
                    expandVertically(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                        fadeIn(animationSpec = tween(250))
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    shrinkVertically(animationSpec = tween(250, easing = FastOutSlowInEasing)) +
                        fadeOut(animationSpec = tween(200))
                } else ExitTransition.None
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDarkMode) Color(0x221E293B) else Color(0x66F1F5F9))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Leave Type & Duration Summary
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
                                    text = localizedLeaveType,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFFC4B5FD) else Color(0xFF6D28D9),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = "${AppStrings.get("leave_duration", currentLang)} ${personnel.totalLeaveDays} ${AppStrings.get("days", currentLang)}",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
                            )
                        }

                        // Dates Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = if (isDarkMode) CyanGlow else VibrantCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${AppStrings.get("benefit_date", currentLang)} ${personnel.formattedStartDate}",
                                    fontSize = 11.sp,
                                    color = if (isDarkMode) Color(0xFFCBD5E1) else SlateTextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "${AppStrings.get("return_date", currentLang)} ${personnel.formattedReturnDate}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (personnel.isOverdue) RedDanger else (if (isDarkMode) Color.White else SlateTextPrimary),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Progress Bar & Localized Countdown Text
                        if (!personnel.isReturnedToDuty) {
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50.dp)),
                                color = visual.color,
                                trackColor = if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1)
                            )

                            Text(
                                text = personnel.getCountdownText(currentLang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = visual.color
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${AppStrings.get("days_served", currentLang)}: $effectiveDaysSinceDuty ${AppStrings.get("days", currentLang)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                                if (isEligible) {
                                    Text(
                                        text = AppStrings.get("status_eligible", currentLang),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AmberWarning
                                    )
                                }
                            }
                        }

                        if (personnel.notes.isNotBlank()) {
                            Text(
                                text = "${AppStrings.get("notes_label", currentLang)} ${personnel.notes}",
                                fontSize = 10.5.sp,
                                color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Action Buttons Row (48dp minimum touch targets)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!personnel.isReturnedToDuty) {
                            Button(
                                onClick = onConfirmDuty,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldSuccess,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("confirm_duty_btn", currentLang),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isDarkMode) Color(0x4038BDF8) else Color(0x6000BCD4)),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier
                                .then(if (personnel.isReturnedToDuty) Modifier.weight(1f) else Modifier)
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = AppStrings.get("edit", currentLang),
                                tint = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F),
                                modifier = Modifier.size(16.dp)
                            )
                            if (personnel.isReturnedToDuty) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("edit", currentLang),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDarkMode) Color(0x22EF4444) else Color(0x1AEF4444))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = AppStrings.get("delete", currentLang),
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
