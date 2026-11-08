package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.VibrantCyan
import com.example.util.AppLanguage
import com.example.util.AppStrings

enum class AppNavPage {
    DASHBOARD,
    RECORDS,
    DATABASE,
    NOTIFICATIONS
}

@Composable
fun AppBottomNavigationPanel(
    selectedPage: AppNavPage,
    onSelectPage: (AppNavPage) -> Unit,
    notificationsCount: Int,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean,
    currentLang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val containerBg = when {
        isNeumorphic -> if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE8EEF5)
        else -> if (isDarkMode) Color(0xF00F172A) else Color(0xF5FFFFFF)
    }

    val borderColor = when {
        isNeumorphic -> if (isDarkMode) Color(0x33475569) else Color(0x66CBD5E1)
        else -> if (isDarkMode) Color(0x3300E5FF) else Color(0x3300BCD4)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = if (isNeumorphic) 10.dp else 8.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = if (isDarkMode) Color(0x40000000) else Color(0x20000000),
                spotColor = if (isDarkMode) Color(0x6000E5FF) else Color(0x2000BCD4)
            )
            .testTag("bottom_nav_panel"),
        shape = RoundedCornerShape(26.dp),
        color = containerBg,
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Dashboard
            BottomNavItem(
                icon = Icons.Default.GridView,
                label = AppStrings.get("nav_dashboard", currentLang),
                isSelected = selectedPage == AppNavPage.DASHBOARD,
                badgeCount = 0,
                isDarkMode = isDarkMode,
                isNeumorphic = isNeumorphic,
                animationsEnabled = animationsEnabled,
                onClick = { onSelectPage(AppNavPage.DASHBOARD) },
                testTag = "bottom_nav_dashboard"
            )

            // Tab 2: Records
            BottomNavItem(
                icon = Icons.Default.People,
                label = AppStrings.get("nav_records", currentLang),
                isSelected = selectedPage == AppNavPage.RECORDS,
                badgeCount = 0,
                isDarkMode = isDarkMode,
                isNeumorphic = isNeumorphic,
                animationsEnabled = animationsEnabled,
                onClick = { onSelectPage(AppNavPage.RECORDS) },
                testTag = "bottom_nav_records"
            )

            // Tab 3: Room Database
            BottomNavItem(
                icon = Icons.Default.Storage,
                label = AppStrings.get("nav_database", currentLang),
                isSelected = selectedPage == AppNavPage.DATABASE,
                badgeCount = 0,
                isDarkMode = isDarkMode,
                isNeumorphic = isNeumorphic,
                animationsEnabled = animationsEnabled,
                onClick = { onSelectPage(AppNavPage.DATABASE) },
                testTag = "bottom_nav_database"
            )

            // Tab 4: Notifications
            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = AppStrings.get("nav_notifications", currentLang),
                isSelected = selectedPage == AppNavPage.NOTIFICATIONS,
                badgeCount = notificationsCount,
                isDarkMode = isDarkMode,
                isNeumorphic = isNeumorphic,
                animationsEnabled = animationsEnabled,
                onClick = { onSelectPage(AppNavPage.NOTIFICATIONS) },
                testTag = "bottom_nav_notifications"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int,
    isDarkMode: Boolean,
    isNeumorphic: Boolean,
    animationsEnabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0284C7)
    val inactiveColor = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)

    val itemColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        animationSpec = if (animationsEnabled) tween(250, easing = FastOutSlowInEasing) else tween(0),
        label = "navColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = if (animationsEnabled) tween(200, easing = FastOutSlowInEasing) else tween(0),
        label = "navScale"
    )

    val activePillBg = if (isDarkMode) {
        Color(0xFF00E5FF).copy(alpha = 0.12f)
    } else {
        Color(0xFF0284C7).copy(alpha = 0.10f)
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) activePillBg else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (badgeCount > 99) "99+" else "$badgeCount",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = itemColor,
                        modifier = Modifier.size(23.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = itemColor,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = itemColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
