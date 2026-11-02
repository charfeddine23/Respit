package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.DashboardStats
import com.example.ui.UndoAction
import com.example.ui.theme.liquidGlassInteractive
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun SideNavigationDrawerContent(
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    lastUndo: UndoAction?,
    stats: DashboardStats,
    onSelectLanguage: (AppLanguage) -> Unit,
    onUndo: () -> Unit,
    onCheckNotifications: () -> Unit,
    onExportPdf: () -> Unit,
    onOpenBackup: () -> Unit,
    onToggleTheme: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDeveloper: () -> Unit,
    onResetSampleData: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("side_navigation_drawer")
    ) {
        // 1. App Header with Glass Branding & 60 FPS Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.linearGradient(
                    if (isDarkMode) listOf(Color(0x40FFFFFF), Color(0x3338BDF8))
                    else listOf(Color.White, Color(0x6638BDF8))
                )
            ),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .liquidGlassInteractive(
                                shape = RoundedCornerShape(12.dp),
                                glowColor = MaterialTheme.colorScheme.primary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_respite_logo),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = AppStrings.get("app_title", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                        Text(
                            text = AppStrings.get("app_subtitle", currentLang),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Version 1.42.2 Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "v${com.example.BuildConfig.VERSION_NAME} (${com.example.BuildConfig.VERSION_CODE})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Language Selection Card (Français / English / العربية)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0x33475569) else Color(0x40BAE6FD)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.get("language", currentLang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = currentLang == lang
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectLanguage(lang) },
                            label = {
                                Text(
                                    text = lang.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (isDarkMode) Color(0x33334155) else Color.White.copy(alpha = 0.7f),
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSelected) MaterialTheme.colorScheme.primary else (if (isDarkMode) Color(0x4D475569) else Color(0x3338BDF8)),
                                selectedBorderColor = MaterialTheme.colorScheme.primary,
                                enabled = true,
                                selected = isSelected
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Navigation Actions
        Text(
            text = AppStrings.get("drawer_nav_title", currentLang),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Undo Action (if available)
        AnimatedVisibility(
            visible = lastUndo != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            DrawerNavigationItem(
                icon = Icons.AutoMirrored.Filled.Undo,
                iconTint = Color(0xFFD97706),
                title = AppStrings.get("undo_btn", currentLang),
                subtitle = lastUndo?.description,
                isDarkMode = isDarkMode,
                onClick = onUndo,
                testTag = "drawer_undo_item"
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Notification Check Item
        DrawerNavigationItem(
            icon = Icons.Default.NotificationsActive,
            iconTint = Color(0xFF0284C7),
            title = AppStrings.get("check_notifications", currentLang),
            subtitle = "تنبيهات الإجازات واقتراب العودة",
            isDarkMode = isDarkMode,
            onClick = onCheckNotifications,
            testTag = "drawer_notifications_item"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Export PDF Report Item
        DrawerNavigationItem(
            icon = Icons.Default.PictureAsPdf,
            iconTint = Color(0xFFDC2626),
            title = AppStrings.get("export_pdf", currentLang),
            subtitle = "مستند رسمي مفصل وجاهز للطباعة",
            isDarkMode = isDarkMode,
            onClick = onExportPdf,
            testTag = "drawer_pdf_item"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Backup & Restore Item
        DrawerNavigationItem(
            icon = Icons.Default.Share,
            iconTint = Color(0xFF7C3AED),
            title = AppStrings.get("export_data", currentLang),
            subtitle = "تصدير واستيراد بصيغة JSON",
            isDarkMode = isDarkMode,
            onClick = onOpenBackup,
            testTag = "drawer_backup_item"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Theme Switch Item
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleTheme() }
                .testTag("drawer_theme_toggle")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDarkMode) Color(0xFFFBBF24).copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = if (isDarkMode) Color(0xFFFBBF24) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isDarkMode) AppStrings.get("theme_dark", currentLang) else AppStrings.get("theme_light", currentLang),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                        )
                        Text(
                            text = if (isDarkMode) "المظهر الداكن مفعل" else "المظهر الفاتح مفعل",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onToggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Settings Item
        DrawerNavigationItem(
            icon = Icons.Default.Settings,
            iconTint = Color(0xFF059669),
            title = AppStrings.get("settings", currentLang),
            subtitle = "عتبة الاستحقاق وتأمين رمز PIN",
            isDarkMode = isDarkMode,
            onClick = onOpenSettings,
            testTag = "drawer_settings_item"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Developer Info Item
        DrawerNavigationItem(
            icon = Icons.Default.Info,
            iconTint = Color(0xFF0284C7),
            title = AppStrings.get("developer_info", currentLang),
            subtitle = "حول التطبيق والمطور",
            isDarkMode = isDarkMode,
            onClick = onOpenDeveloper,
            testTag = "drawer_developer_item"
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Reset Sample Data Item
        DrawerNavigationItem(
            icon = Icons.Default.Refresh,
            iconTint = Color(0xFF94A3B8),
            title = "استعادة البيانات النموذجية",
            subtitle = "تهيئة بيانات أولية تجريبية",
            isDarkMode = isDarkMode,
            onClick = onResetSampleData,
            testTag = "drawer_reset_item"
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(color = if (isDarkMode) Color(0x33475569) else Color(0x2638BDF8))

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Stats Overview in Sidebar
        Text(
            text = "إحصائيات سريعة للوحدة",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SidebarStatBadge("العدد الكلي", "${stats.total}", MaterialTheme.colorScheme.primary, isDarkMode, Modifier.weight(1f))
            SidebarStatBadge("في إجازة", "${stats.atHome}", Color(0xFF0284C7), isDarkMode, Modifier.weight(1f))
            SidebarStatBadge("الدخول اليوم", "${stats.returningToday}", Color(0xFFD97706), isDarkMode, Modifier.weight(1f))
            SidebarStatBadge("متأخر", "${stats.overdue}", Color(0xFFDC2626), isDarkMode, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Respite Pelse - v${com.example.BuildConfig.VERSION_NAME} (${com.example.BuildConfig.VERSION_CODE})",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun DrawerNavigationItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String?,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x33475569) else Color(0x3338BDF8)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF334155),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarStatBadge(
    label: String,
    value: String,
    color: Color,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = if (isDarkMode) 0.18f else 0.12f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = color)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) color else Color(0xFF0F172A)
            )
        }
    }
}
