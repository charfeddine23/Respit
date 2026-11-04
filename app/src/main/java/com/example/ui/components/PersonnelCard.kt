package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassInteractive

@Composable
fun PersonnelCard(
    personnel: Personnel,
    dutyThresholdDays: Int,
    onConfirmDuty: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isFocusMode: Boolean = false,
    isDarkMode: Boolean = false
) {
    val status = personnel.status
    val isEligible = personnel.isEligibleForLeave(dutyThresholdDays)
    val progress = personnel.leaveProgressFraction

    // Dynamic colors based on status & theme
    val (statusColor, statusBgColor, statusIcon, statusTitle) = when {
        status == LeaveStatus.AT_HOME -> {
            Tuple4(
                VibrantCyan,
                CyanContainer,
                Icons.Default.Home,
                "في إجازة بالبيت 🏠"
            )
        }
        status == LeaveStatus.RETURNING_TODAY -> {
            Tuple4(
                Color(0xFFD97706),
                Color(0xFFFEF3C7),
                Icons.Default.NotificationsActive,
                "الدخول اليوم 🔔"
            )
        }
        status == LeaveStatus.OVERDUE -> {
            Tuple4(
                Color(0xFFDC2626),
                Color(0xFFFEE2E2),
                Icons.Default.Warning,
                "متأخر عن الدخول ⚠️"
            )
        }
        status == LeaveStatus.ON_DUTY && isEligible -> {
            Tuple4(
                Color(0xFFD97706),
                Color(0xFFFEF3C7),
                Icons.Default.Star,
                "مستحق للإجازة 🌟"
            )
        }
        status == LeaveStatus.ON_DUTY -> {
            Tuple4(
                Color(0xFF059669),
                Color(0xFFD1FAE5),
                Icons.Default.Shield,
                "حاضر بالخدمة 🎖️"
            )
        }
        else -> {
            Tuple4(
                Color(0xFF7C3AED),
                Color(0xFFEDE9FE),
                Icons.Default.Schedule,
                "إجازة مبرمجة ⏳"
            )
        }
    }

    val isDark = isDarkMode

    // Liquid Glass Container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = LiquidGlassDefaults.CardShape,
                elevation = 6.dp,
                borderAlpha = 0.85f,
                isDark = isDark
            )
            .testTag("personnel_item_${personnel.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Rank & Name & Registration Number & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Rank Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0x3338BDF8) else Color(0x1F0284C7))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x4038BDF8) else Color(0x2638BDF8),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = personnel.rank,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary
                            )
                        }

                        // Registration Number Inset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Color(0x221E293B) else Color(0x1A0284C7))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x3338BDF8) else Color(0x2638BDF8),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = personnel.registrationNumber,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isDark) Color(0xFF94A3B8) else SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Full Name / Surname
                    Text(
                        text = personnel.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Wilaya badge (Soft Liquid Glass)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDark) Color(0x2238BDF8) else Color(0x1F0284C7))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x4038BDF8) else Color(0x2638BDF8),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "📍 ${personnel.wilaya}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp
                            )
                        }

                        // Leave type badge (Soft Liquid Glass)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDark) Color(0x33334155) else Color.White.copy(alpha = 0.65f))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x3338BDF8) else Color(0x2638BDF8),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = personnel.leaveType,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp
                            )
                        }

                        if (personnel.notes.isNotBlank()) {
                            Text(
                                text = "• ${personnel.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Luminous Expressive Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.8f),
                                    statusColor.copy(alpha = 0.5f)
                                )
                            ),
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = statusTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // HERO SECTION: "شحال بقالو فدار ويدخل" or "مستحق للإجازة"
            when {
                status == LeaveStatus.AT_HOME -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isDark) {
                                    Brush.horizontalGradient(
                                        listOf(
                                            VibrantCyan.copy(alpha = 0.22f),
                                            CyanGlow.copy(alpha = 0.12f)
                                        )
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(
                                            CyanContainer,
                                            Color.White
                                        )
                                    )
                                }
                            )
                            .border(
                                1.dp,
                                if (isDark) {
                                    Brush.linearGradient(
                                        listOf(Color.White.copy(alpha = 0.6f), VibrantCyan.copy(alpha = 0.5f))
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(Color.White, Color(0x66CBD5E1))
                                    )
                                },
                                RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) VibrantCyan else CyanContainer)
                                            .border(
                                                1.dp,
                                                Brush.linearGradient(
                                                    listOf(Color.White, VibrantCyan.copy(alpha = 0.35f))
                                                ),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "بالبيت",
                                            tint = if (isDark) Color.White else VibrantCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "حالياً في إجازة بالبيت",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else SlateTextPrimary
                                        )
                                        Text(
                                            text = "المدة: ${personnel.totalLeaveDays} يوماً (انقضى ${personnel.daysElapsedInLeave} يوماً)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isDark) Color(0xFFCBD5E1) else SlateTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = personnel.countdownTextAr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (isDark) CyanGlow else VibrantCyan
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "تاريخ الدخول: ",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF94A3B8) else SlateTextSecondary,
                                            fontSize = 10.sp
                                        )
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            Text(
                                                text = personnel.formattedReturnDate,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color(0xFF94A3B8) else SlateTextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = VibrantCyan,
                                trackColor = VibrantCyan.copy(alpha = 0.2f)
                            )
                        }
                    }
                }

                status == LeaveStatus.RETURNING_TODAY -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isDark) {
                                        listOf(
                                            Color(0xFFD97706).copy(alpha = 0.22f),
                                            Color(0xFFFBBF24).copy(alpha = 0.12f)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFFFFBEB).copy(alpha = 0.85f),
                                            Color(0xFFFEF3C7).copy(alpha = 0.65f)
                                        )
                                    }
                                )
                            )
                            .border(
                                1.dp,
                                if (isDark) {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color(0xFFF59E0B)))
                                } else {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.95f), Color(0x4DF59E0B)))
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD97706)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "🔔 تنبيه: موعد الدخول هو اليوم!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Color(0xFFFDE68A) else Color(0xFFB45309)
                                )
                                Text(
                                    text = "تنتهي الإجازة اليوم ويجب استئناف العمل بالوحدة (${personnel.formattedReturnDate})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }

                status == LeaveStatus.OVERDUE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isDark) {
                                        listOf(
                                            Color(0xFFDC2626).copy(alpha = 0.22f),
                                            Color(0xFFEF4444).copy(alpha = 0.12f)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFFFF1F2).copy(alpha = 0.85f),
                                            Color(0xFFFEE2E2).copy(alpha = 0.65f)
                                        )
                                    }
                                )
                            )
                            .border(
                                1.dp,
                                if (isDark) {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color(0xFFEF4444)))
                                } else {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.95f), Color(0x4DEF4444)))
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDC2626)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "⚠️ ${personnel.countdownTextAr}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Color(0xFFFECACA) else Color(0xFFB91C1C)
                                )
                                Text(
                                    text = "كان من المفترض الدخول بتاريخ ${personnel.formattedReturnDate} ولم يؤكد الالتحاق",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF7F1D1D)
                                )
                            }
                        }
                    }
                }

                status == LeaveStatus.ON_DUTY && isEligible -> {
                    // Golden Prominent Banner for Leave Eligibility
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isDark) {
                                        listOf(
                                            Color(0xFFD97706).copy(alpha = 0.22f),
                                            Color(0xFFFBBF24).copy(alpha = 0.12f)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFFFFBEB).copy(alpha = 0.85f),
                                            Color(0xFFFEF3C7).copy(alpha = 0.65f)
                                        )
                                    }
                                )
                            )
                            .border(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = if (isDark) 0.8f else 0.95f),
                                        Color(0x4DF59E0B)
                                    )
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFD97706)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🌟 مستحق للإجازة (تجاوز العتبة)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFFB45309)
                                    )
                                    Text(
                                        text = "أمضى ${personnel.daysSinceDutyReturn} يوماً بالخدمة (العتبة المحددة: $dutyThresholdDays يوماً)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF78350F),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                status == LeaveStatus.ON_DUTY -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isDark) {
                                        listOf(
                                            Color(0xFF059669).copy(alpha = 0.22f),
                                            Color(0xFF10B981).copy(alpha = 0.12f)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFECFDF5).copy(alpha = 0.85f),
                                            Color(0xFFD1FAE5).copy(alpha = 0.65f)
                                        )
                                    }
                                )
                            )
                            .border(
                                1.dp,
                                if (isDark) {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color(0xFF34D399).copy(alpha = 0.5f)))
                                } else {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.95f), Color(0x4D10B981)))
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF059669)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "حاضر بالخدمة بالوحدة 🎖️",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF047857)
                                )
                                Text(
                                    text = "منقضي ${personnel.daysSinceDutyReturn} يوماً منذ آخر إجازة (${personnel.formattedReturnDate})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF065F46)
                                )
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (isDark) {
                                        listOf(
                                            Color(0xFF7C3AED).copy(alpha = 0.22f),
                                            Color(0xFF8B5CF6).copy(alpha = 0.12f)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFF5F3FF).copy(alpha = 0.85f),
                                            Color(0xFFEDE9FE).copy(alpha = 0.65f)
                                        )
                                    }
                                )
                            )
                            .border(
                                1.dp,
                                if (isDark) {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.5f), Color(0xFFA78BFA).copy(alpha = 0.5f)))
                                } else {
                                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.95f), Color(0x4D8B5CF6)))
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF7C3AED)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlightTakeoff,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "إجازة مبرمجة: ${personnel.countdownTextAr}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFDDD6FE) else Color(0xFF6D28D9)
                                )
                                Text(
                                    text = "الفترة: من ${personnel.formattedStartDate} إلى ${personnel.formattedReturnDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF4C1D95)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two-date metadata pill row (enhanced when isFocusMode is enabled)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(if (isFocusMode) 12.dp else 10.dp))
                    .background(
                        if (isFocusMode) {
                            if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        } else {
                            if (isDark) Color(0x33334155) else Color.White.copy(alpha = 0.65f)
                        }
                    )
                    .border(
                        if (isFocusMode) 1.5.dp else 1.dp,
                        if (isFocusMode) {
                            if (isDark) {
                                Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF38BDF8)))
                            }
                        } else {
                            if (isDark) {
                                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color(0x3338BDF8)))
                            } else {
                                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.95f), Color(0x2638BDF8)))
                            }
                        },
                        RoundedCornerShape(if (isFocusMode) 12.dp else 10.dp)
                    )
                    .padding(horizontal = if (isFocusMode) 12.dp else 10.dp, vertical = if (isFocusMode) 8.dp else 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(if (isFocusMode) 16.dp else 14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الإستفادة: ",
                        style = if (isFocusMode) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                        color = if (isDark) Color.White else SlateTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = personnel.formattedStartDate,
                            style = if (isFocusMode) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                            color = if (isDark) Color.White else SlateTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "⬅️",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(if (isFocusMode) 16.dp else 14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الدخول: ",
                        style = if (isFocusMode) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                        color = if (isDark) Color.White else SlateTextPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = personnel.formattedReturnDate,
                            style = if (isFocusMode) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                            color = if (isDark) Color.White else SlateTextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Text(
                    text = "(${personnel.totalLeaveDays} يوم)",
                    style = if (isFocusMode) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = VibrantCyan
                )
            }

            // Bottom Actions Row
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Action: Confirm Return / Enter Duty OR Grant New Leave
                if (status == LeaveStatus.AT_HOME || status == LeaveStatus.RETURNING_TODAY || status == LeaveStatus.OVERDUE) {
                    Surface(
                        onClick = onConfirmDuty,
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF059669).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.8f), Color(0xFF34D399))
                            )
                        ),
                        modifier = Modifier.testTag("confirm_duty_button_${personnel.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "تأكيد الدخول",
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تأكيد الدخول واستئناف العمل",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                } else {
                    Surface(
                        onClick = onEdit,
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.8f), MaterialTheme.colorScheme.primary)
                            )
                        ),
                        modifier = Modifier.testTag("new_leave_button_${personnel.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = "منح إجازة جديدة",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "منح إجازة جديدة",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Edit and Delete Liquid Icons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .liquidGlassInteractive(
                                shape = RoundedCornerShape(10.dp),
                                glowColor = MaterialTheme.colorScheme.primary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("edit_button_${personnel.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .liquidGlassInteractive(
                                shape = RoundedCornerShape(10.dp),
                                glowColor = MaterialTheme.colorScheme.error
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("delete_button_${personnel.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
