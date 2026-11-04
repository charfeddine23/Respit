package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.DashboardStats
import com.example.ui.SortOption
import com.example.ui.theme.LiquidGlassDefaults
import com.example.ui.theme.liquidGlass

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSortDialog(
    selectedSort: SortOption,
    onSelectSort: (SortOption) -> Unit,
    selectedStatus: String,
    onSelectStatus: (String) -> Unit,
    selectedRank: String,
    onSelectRank: (String) -> Unit,
    uniqueRanks: List<String>,
    stats: DashboardStats,
    onResetFilters: () -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean = false
) {
    val isDark = isDarkMode

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isDark) Color(0xFF1E293B) else Color(0xD9FFFFFF),
            border = BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    if (isDark) listOf(Color.White.copy(alpha = 0.5f), Color(0xFF00E5FF).copy(alpha = 0.4f))
                    else listOf(Color.White.copy(alpha = 0.95f), Color(0x8000BCD4), Color.White.copy(alpha = 0.6f))
                )
            ),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("filter_sort_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "الفلترة والترتيب المتقدم",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "تخصيص عرض وفرز سجلات الأفراد",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Sort Section
                    Column {
                        Text(
                            text = "ترتيب السجلات حسب:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SortOption.values().forEach { option ->
                                val isSelected = selectedSort == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectSort(option) },
                                    label = {
                                        Text(
                                            text = option.titleAr,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF00BCD4) else if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDark) Color(0x33334155) else Color(0xF2FFFFFF),
                                        selectedContainerColor = if (isDark) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                                        selectedLabelColor = Color(0xFF00BCD4)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) Color(0xFF00BCD4) else (if (isDark) Color(0x4D475569) else Color(0x40CBD5E1)),
                                        selectedBorderColor = Color(0xFF00BCD4),
                                        enabled = true,
                                        selected = isSelected
                                    ),
                                    shape = RoundedCornerShape(50.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // 2. Leave Status Filter Section
                    Column {
                        Text(
                            text = "تصفية حسب حالة الإجازة:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val statusOptions = listOf(
                            "ALL" to "جميع الحالات",
                            "ELIGIBLE" to "🌟 مستحق للإجازة (${stats.eligible})",
                            "AT_HOME" to "🏠 في إجازة (بالبيت) (${stats.atHome})",
                            "RETURNING_TODAY" to "🔔 الدخول اليوم (${stats.returningToday})",
                            "OVERDUE" to "⚠️ متأخر عن الدخول (${stats.overdue})",
                            "ON_DUTY" to "🎖️ حاضر بالخدمة (${stats.onDuty})",
                            "UPCOMING" to "⏳ إجازة مبرمجة (${stats.upcoming})"
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            statusOptions.forEach { (code, label) ->
                                val isSelected = selectedStatus == code
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectStatus(code) },
                                    label = {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF00BCD4) else if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDark) Color(0x33334155) else Color(0xF2FFFFFF),
                                        selectedContainerColor = if (isDark) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                                        selectedLabelColor = Color(0xFF00BCD4)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) Color(0xFF00BCD4) else (if (isDark) Color(0x4D475569) else Color(0x40CBD5E1)),
                                        selectedBorderColor = Color(0xFF00BCD4),
                                        enabled = true,
                                        selected = isSelected
                                    ),
                                    shape = RoundedCornerShape(50.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // 3. Military Rank Filter Section
                    Column {
                        Text(
                            text = "تصفية حسب الرتبة العسكرية:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val isAllRankSelected = selectedRank == "ALL"
                            FilterChip(
                                selected = isAllRankSelected,
                                onClick = { onSelectRank("ALL") },
                                label = {
                                    Text(
                                        text = "كل الرتب",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isAllRankSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isAllRankSelected) Color(0xFF00BCD4) else if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (isDark) Color(0x33334155) else Color(0xF2FFFFFF),
                                    selectedContainerColor = if (isDark) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                                    selectedLabelColor = Color(0xFF00BCD4)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isAllRankSelected) Color(0xFF00BCD4) else (if (isDark) Color(0x4D475569) else Color(0x40CBD5E1)),
                                    selectedBorderColor = Color(0xFF00BCD4),
                                    enabled = true,
                                    selected = isAllRankSelected
                                ),
                                shape = RoundedCornerShape(50.dp)
                            )

                            uniqueRanks.forEach { rank ->
                                val isRankSelected = selectedRank == rank
                                FilterChip(
                                    selected = isRankSelected,
                                    onClick = { onSelectRank(if (isRankSelected) "ALL" else rank) },
                                    label = {
                                        Text(
                                            text = rank,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isRankSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isRankSelected) Color(0xFF00BCD4) else if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDark) Color(0x33334155) else Color(0xF2FFFFFF),
                                        selectedContainerColor = if (isDark) Color(0x3300BCD4) else Color(0xFFE0F7FA),
                                        selectedLabelColor = Color(0xFF00BCD4)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isRankSelected) Color(0xFF00BCD4) else (if (isDark) Color(0x4D475569) else Color(0x40CBD5E1)),
                                        selectedBorderColor = Color(0xFF00BCD4),
                                        enabled = true,
                                        selected = isRankSelected
                                    ),
                                    shape = RoundedCornerShape(50.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onResetFilters()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "إعادة ضبط",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تطبيق",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
