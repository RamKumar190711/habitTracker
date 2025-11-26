package com.toqsoft.habittracker.data.model

import android.R
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.toqsoft.habittracker.domain.model.Category

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskName: String,
    val category: String,
    val categoryColor: Int,
    val categoryIcon: Int,     // ← NEW COLUMN
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
