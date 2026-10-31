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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardStats
import com.example.ui.theme.liquidGlass

@Composable
fun StatisticsSection(
    stats: DashboardStats,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("statistics_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidStatCard(
                title = "إجمالي الأفراد",
                count = stats.total.toString(),
                subtitle = "مسجلون بالنظام",
                icon = Icons.Default.Group,
                accentColor = Color(0xFF0284C7),
                bgTint = Color(0xFFD4EDFD),
                modifier = Modifier.weight(1f)
            )

            LiquidStatCard(
                title = "في إجازة (بالبيت)",
                count = stats.atHome.toString(),
                subtitle = "جاري احتساب متبقي الدخول",
                icon = Icons.Default.Home,
                accentColor = Color(0xFF0284C7),
                bgTint = Color(0xFFD4EDFD),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiquidStatCard(
                title = "الدخول اليوم أو متأخر",
                count = (stats.returningToday + stats.overdue).toString(),
                subtitle = "${stats.returningToday} اليوم / ${stats.overdue} متأخر",
                icon = Icons.Default.Warning,
                accentColor = Color(0xFFE11D48),
                bgTint = Color(0xFFFFE4E6),
                modifier = Modifier.weight(1f)
            )

            LiquidStatCard(
                title = "حاضرون بالخدمة",
                count = stats.onDuty.toString(),
                subtitle = if (stats.eligible > 0) "${stats.eligible} مستحق للإجازة 🌟" else "مستأنفون للعمل بالوحدة",
                icon = Icons.Default.CheckCircle,
                accentColor = Color(0xFF059669),
                bgTint = Color(0xFFD1FAE5),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LiquidStatCard(
    title: String,
    count: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    bgTint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .liquidGlass(
                shape = RoundedCornerShape(22.dp),
                elevation = 6.dp,
                borderAlpha = 0.9f
            )
            .padding(14.dp)
    ) {
        val isDark = androidx.compose.foundation.isSystemInDarkTheme()

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) MaterialTheme.colorScheme.onSurface else Color(0xFF0A2540)
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isDark) bgTint.copy(alpha = 0.25f) else bgTint)
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                if (isDark) listOf(Color.White.copy(alpha = 0.6f), accentColor.copy(alpha = 0.5f))
                                else listOf(Color.White, accentColor.copy(alpha = 0.35f))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = accentColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF486581),
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}
