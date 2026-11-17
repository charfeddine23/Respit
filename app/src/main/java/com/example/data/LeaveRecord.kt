package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.util.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "leave_records")
data class LeaveRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personnelId: Long = 0,
    val personnelName: String,
    val personnelNameLatin: String = "",
    val rank: String,
    val registrationNumber: String,
    val leaveType: String,
    val startDateMillis: Long,
    val returnDateMillis: Long,
    val durationDays: Long,
    val statusText: String = "جارية",
    val destinationWilaya: String = "",
    val notes: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    fun getDisplayName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> personnelName.trim().ifBlank { personnelNameLatin.trim() }
        AppLanguage.TAMAZIGHT, AppLanguage.FRENCH, AppLanguage.ENGLISH -> personnelNameLatin.trim().ifBlank { personnelName.trim() }
    }

    fun matchesNameQuery(normalizedQuery: String): Boolean {
        if (normalizedQuery.isEmpty()) return true
        return personnelName.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
            personnelNameLatin.lowercase(Locale.getDefault()).contains(normalizedQuery)
    }

    val formattedStartDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(startDateMillis))}\u200E"
        }

    val formattedReturnDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(returnDateMillis))}\u200E"
        }
}
