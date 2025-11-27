package com.toqsoft.habittracker.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toqsoft.habittracker.data.model.ThemeDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = ThemeDataStore(application)

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> get() = _isDarkTheme

    init {
        viewModelScope.launch {
            dataStore.darkThemeFlow.collect {
                _isDarkTheme.value = it
            }
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.saveDarkTheme(enabled)
        }
    }
}
