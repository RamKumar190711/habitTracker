package com.toqsoft.habittracker.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toqsoft.habittracker.data.dataStore.ReminderDataStore
import com.toqsoft.habittracker.domain.model.ReminderData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val _reminders = MutableStateFlow<List<ReminderData>>(emptyList())
    val reminders: StateFlow<List<ReminderData>> = _reminders

    init {
        viewModelScope.launch {
            ReminderDataStore.loadReminders(getApplication()).collectLatest {
                _reminders.value = it
            }
        }
    }

    fun addOrUpdate(reminder: ReminderData) {
        val list = _reminders.value.toMutableList()
        val index = list.indexOfFirst { it.id == reminder.id }
        if (index != -1) list[index] = reminder else list.add(reminder)
        updateStore(list)
    }

    fun delete(reminder: ReminderData) {
        val list = _reminders.value.toMutableList()
        list.removeAll { it.id == reminder.id }
        updateStore(list)
    }

    private fun updateStore(list: List<ReminderData>) {
        _reminders.value = list
        viewModelScope.launch {
            ReminderDataStore.saveReminders(getApplication(), list)
        }
    }
}
