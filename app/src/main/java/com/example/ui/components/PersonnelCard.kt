package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LeaveStatus
import com.example.data.Personnel
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
    currentLang: AppLanguage = AppLanguage.ARABIC
) {
    val status = personnel.status
    val isEligible = personnel.isEligibleForLeave(dutyThresholdDays)
    val progress = personnel.leaveProgressFraction

    val animatedProgress = if (animationsEnabled && !personnel.isReturnedToDuty) {
        animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            label = "leaveProgress"
        ).value
    } else progress

    val pulseAlpha = if (animationsEnabled && (status == LeaveStatus.OVERDUE || status == LeaveStatus.RETURNING_TODAY)) {
        val infinite = rememberInfiniteTransition(label = "pulseBadge")
        infinite.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "badgePulse"
        ).value
    } else 1f

    // Dynamic Status Theme Tokens
    data class StatusVisual(val color: Color, val bgTint: Color, val icon: ImageVector, val labelKey: String)
    val visual = when {
        status == LeaveStatus.AT_HOME -> StatusVisual(
            color = if (isDarkMode) CyanGlow else VibrantCyan,
            bgTint = if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F2FE),
            icon = Icons.Default.Home,
            labelKey = "status_at_home"
        )
        status == LeaveStatus.RETURNING_TODAY -> StatusVisual(
            color = AmberWarning,
            bgTint = if (isDarkMode) Color(0x33F59E0B) else Color(0xFFFEF3C7),
            icon = Icons.Default.NotificationsActive,
            labelKey = "status_returning_today"
        )
        status == LeaveStatus.OVERDUE -> StatusVisual(
            color = RedDanger,
            bgTint = if (isDarkMode) Color(0x33EF4444) else Color(0xFFFFE4E6),
            icon = Icons.Default.Warning,
            labelKey = "status_overdue"
        )
        status == LeaveStatus.ON_DUTY && isEligible -> StatusVisual(
            color = AmberWarning,
            bgTint = if (isDarkMode) Color(0x33F59E0B) else Color(0xFFFEF3C7),
            icon = Icons.Default.Star,
            labelKey = "status_eligible"
        )
        status == LeaveStatus.ON_DUTY -> StatusVisual(
            color = EmeraldSuccess,
            bgTint = if (isDarkMode) Color(0x3310B981) else Color(0xFFDCFCE7),
            icon = Icons.Default.Shield,
            labelKey = "status_on_duty"
        )
        else -> StatusVisual(
            color = Color(0xFF8B5CF6),
            bgTint = if (isDarkMode) Color(0x338B5CF6) else Color(0xFFEDE9FE),
            icon = Icons.Default.Schedule,
            labelKey = "status_upcoming"
        )
    }

    val avatarShape = if (isNeumorphic) RoundedCornerShape(18.dp) else CircleShape

    var isExpanded by rememberSaveable(personnel.id) { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "arrowRotation"
    )

    // Unified Card Container
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Days Count Badge + Name/Rank/Wilaya + Status Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Modern Squircle / Circular Badge displaying Number of Days
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(avatarShape)
                            .background(visual.bgTint)
                            .border(1.2.dp, visual.color.copy(alpha = if (isNeumorphic) 0.35f else 0.9f), avatarShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${personnel.totalLeaveDays}",
                                fontWeight = FontWeight.Black,
                                fontSize = if (personnel.totalLeaveDays >= 100) 13.sp else 16.sp,
                                color = visual.color,
                                lineHeight = 16.sp
                            )
                            Text(
                                text = if (currentLang == AppLanguage.FRENCH) "j" else if (currentLang == AppLanguage.ENGLISH) "d" else "يوم",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = visual.color.copy(alpha = 0.85f),
                                lineHeight = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        // Full Name
                        Text(
                            text = personnel.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isDarkMode) Color.White else SlateTextPrimary,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Tags Row: Rank Pill + Registration Code + Wilaya
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Military Rank Tag
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDarkMode) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                                border = BorderStroke(1.dp, Color(0xFF00BCD4).copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = personnel.rank,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00838F),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }

                            // Registration Number Inset
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isDarkMode) Color(0x221E293B) else Color(0x1A64748B)
                            ) {
                                Text(
                                    text = personnel.registrationNumber,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkMode) Color(0xFFCBD5E1) else SlateTextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            // Wilaya Location Chip
                            if (personnel.wilaya.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = personnel.wilaya.split("-").lastOrNull()?.trim() ?: personnel.wilaya,
                                        fontSize = 10.sp,
                                        color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Prominent Live Status Badge
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
                            text = AppStrings.get(visual.labelKey, currentLang),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = visual.color
                        )
                    }
                }
            }

            // Click to Expand / Collapse Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isDarkMode) Color(0x1F38BDF8) else Color(0x1200BCD4)
                    )
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
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
                        text = if (isExpanded) {
                            AppStrings.get("click_to_collapse", currentLang)
                        } else {
                            AppStrings.get("click_to_expand", currentLang)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) CyanGlow else VibrantCyan
                    )
                }

                // Quick return date preview when collapsed
                if (!isExpanded) {
                    Text(
                        text = "${AppStrings.get("return_date", currentLang)} ${personnel.formattedReturnDate}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (personnel.isOverdue) RedDanger else (if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary)
                    )
                }
            }

            // Expandable Details Section: From Leave Dates Downwards
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
                    // Timeline & Dates Progress Bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDarkMode) Color(0x221E293B) else Color(0x66F1F5F9))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Dates Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Start Date
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Return Date
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${AppStrings.get("return_date", currentLang)} ${personnel.formattedReturnDate}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (personnel.isOverdue) RedDanger else (if (isDarkMode) Color.White else SlateTextPrimary)
                                )
                            }
                        }

                        // Progress Bar & Countdown Pill
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${AppStrings.get("leave_duration", currentLang)} ${personnel.totalLeaveDays} ${AppStrings.get("days", currentLang)}",
                                    fontSize = 10.sp,
                                    color = if (isDarkMode) Color(0xFF94A3B8) else SlateTextSecondary
                                )

                                Text(
                                    text = personnel.countdownTextAr,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = visual.color
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${AppStrings.get("days_served", currentLang)}: ${personnel.daysSinceDutyReturn} ${AppStrings.get("days", currentLang)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                                if (isEligible) {
                                    Text(
                                        text = AppStrings.get("status_eligible", currentLang),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AmberWarning
                                    )
                                }
                            }
                        }
                    }

                    // Quick Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Confirm Return to Duty (If not yet returned)
                        if (!personnel.isReturnedToDuty) {
                            Button(
                                onClick = onConfirmDuty,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldSuccess,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("confirm_duty_btn", currentLang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Edit Button
                        OutlinedButton(
                            onClick = onEdit,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isDarkMode) Color(0x4038BDF8) else Color(0x6000BCD4)),
                            modifier = Modifier
                                .then(if (personnel.isReturnedToDuty) Modifier.weight(1f) else Modifier)
                                .height(44.dp)
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
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF00838F)
                                )
                            }
                        }

                        // Delete Button
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(44.dp)
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
