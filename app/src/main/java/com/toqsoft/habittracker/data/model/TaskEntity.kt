package com.toqsoft.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskName: String,
    val category: String,
    val date: Long,         // Store as timestamp in millis
    val time: String,       // e.g., "12:00 PM"
    val reminderType: String,  // "none", "notification", "alarm"
    val scheduleType: String,  // "always", "specific", "before"
    val selectedDays: String?, // Comma-separated "Mon,Tue" if specific
    val daysBefore: Int?,      // Only if scheduleType is "before"
    val priority: Int,
    val note: String?,
    val checklist: String?="",    // Comma-separated checklist items
    val isPending: Boolean = true
)
