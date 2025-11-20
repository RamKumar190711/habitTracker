package com.toqsoft.habittracker.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toqsoft.habittracker.domain.model.ReminderData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "reminders")

object ReminderDataStore {
    private val REMINDERS_KEY = stringPreferencesKey("reminder_list")
    private val gson = Gson()

    suspend fun saveReminders(context: Context, list: List<ReminderData>) {
        context.dataStore.edit { prefs ->
            prefs[REMINDERS_KEY] = gson.toJson(list)
        }
    }

    fun loadReminders(context: Context): Flow<List<ReminderData>> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[REMINDERS_KEY] ?: "[]"
            gson.fromJson(json, object : TypeToken<List<ReminderData>>() {}.type)
        }
    }
}
