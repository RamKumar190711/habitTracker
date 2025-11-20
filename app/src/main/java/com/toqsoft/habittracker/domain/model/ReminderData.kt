package com.toqsoft.habittracker.domain.model

import java.util.UUID


data class ReminderData(
    val id: String = UUID.randomUUID().toString(),
    val reminderType: String,
    val time: String,
    val scheduleType: String,
    val selectedDays: List<String> = emptyList(),
    val daysBefore: Int? = null
)

