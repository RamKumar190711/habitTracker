package com.toqsoft.habittracker.data.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_settings")

class ThemeDataStore(private val context: Context) {

    companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
    }

    val darkThemeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[DARK_THEME_KEY] ?: false }

    suspend fun saveDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_THEME_KEY] = enabled
        }
    }
}
