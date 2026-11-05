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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.util.AppLanguage
import com.example.util.AppStrings

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
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC
) {
    val isDark = isDarkMode

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isDark) Color(0xFF1E293B) else Color(0xF2FFFFFF),
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
                .widthIn(max = 560.dp)
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
                                .background(if (isDark) Color(0x3300BCD4) else Color(0xFFE0F7FA), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color(0xFF00BCD4),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = AppStrings.get("filter_sort_btn", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color.White else SlateTextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AppStrings.get("close", currentLang),
                            tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = if (isDark) Color(0x33475569) else Color(0x33CBD5E1))
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
                            text = AppStrings.get("sort", currentLang) + ":",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00BCD4)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SortOption.entries.forEach { option ->
                                val isSelected = selectedSort == option
                                val label = when (option) {
                                    SortOption.RETURN_SOON -> AppStrings.get("sort_return_soon", currentLang)
                                    SortOption.OVERDUE_FIRST -> AppStrings.get("sort_overdue_first", currentLang)
                                    SortOption.START_DESC -> AppStrings.get("sort_start_desc", currentLang)
                                    SortOption.NAME_ASC -> AppStrings.get("sort_name_asc", currentLang)
                                    SortOption.REG_ASC -> AppStrings.get("sort_reg_asc", currentLang)
                                }

                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectSort(option) },
                                    label = {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF00BCD4) else if (isDark) Color(0xFFE2E8F0) else SlateTextPrimary
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDark) Color(0x22334155) else Color(0xF2FFFFFF),
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

                    HorizontalDivider(color = if (isDark) Color(0x33475569) else Color(0x33CBD5E1))

                    // 2. Leave Status Filter Section
                    Column {
                        Text(
                            text = AppStrings.get("status_all", currentLang) + ":",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00BCD4)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val statuses = listOf(
                            "ALL" to AppStrings.get("status_all", currentLang),
                            "AT_HOME" to "${AppStrings.get("status_at_home", currentLang)} (${stats.atHome})",
                            "RETURNING_TODAY" to "${AppStrings.get("status_returning_today", currentLang)} (${stats.returningToday})",
                            "OVERDUE" to "${AppStrings.get("status_overdue", currentLang)} (${stats.overdue})",
                            "ON_DUTY" to "${AppStrings.get("status_on_duty", currentLang)} (${stats.onDuty})",
                            "ELIGIBLE" to "${AppStrings.get("status_eligible", currentLang)} (${stats.eligible})"
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            statuses.forEach { (code, label) ->
                                val isSelected = selectedStatus == code
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectStatus(code) },
                                    label = {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF00BCD4) else if (isDark) Color(0xFFE2E8F0) else SlateTextPrimary
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDark) Color(0x22334155) else Color(0xF2FFFFFF),
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

                    HorizontalDivider(color = if (isDark) Color(0x33475569) else Color(0x33CBD5E1))

                    // 3. Rank Filter Section
                    Column {
                        Text(
                            text = AppStrings.get("all_ranks", currentLang) + ":",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00BCD4)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val rankOptions = listOf("ALL" to AppStrings.get("all_ranks", currentLang)) +
                                uniqueRanks.map { it to it }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rankOptions.forEach { (code, label) ->
                                val isSelected = selectedRank == code
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onSelectRank(code) },
                                    label = {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (isSelected) Color(0xFF00BCD4) else if (isDark) Color(0xFFE2E8F0) else SlateTextPrimary
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = if (isDark) Color(0x22334155) else Color(0xF2FFFFFF),
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetFilters,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0x40CBD5E1))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFFCBD5E1) else SlateTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("reset_filters", currentLang),
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else SlateTextSecondary
                        )
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00BCD4),
                            contentColor = Color.White
                        )
                    ) {
                        Text(AppStrings.get("apply", currentLang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
