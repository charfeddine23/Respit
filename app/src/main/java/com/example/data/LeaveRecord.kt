package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
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

    val formattedStartDate: String
        get() {
            if (startDateMillis <= 0L) return "-"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(startDateMillis))}\u200E"
        }

    val formattedReturnDate: String
        get() {
            if (returnDateMillis <= 0L) return "-"
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return "\u200E${sdf.format(Date(returnDateMillis))}\u200E"
        }
}
