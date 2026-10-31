package com.example.data

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val personnelId: Long? = null,
    val type: String = "INFO", // "OVERDUE", "RETURNING_TODAY", "APPROACHING_RETURN", "APPROACHING_START", "ELIGIBLE", "INFO"
    val isPinned: Boolean = false,
    val formattedDate: String = ""
)
