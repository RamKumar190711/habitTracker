package com.toqsoft.habittracker.data.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.pinStore by preferencesDataStore("pin_prefs")

object PinDataStore {
    private val PIN_KEY = stringPreferencesKey("app_pin")

    fun getPin(context: Context): Flow<String?> =
        context.pinStore.data.map { it[PIN_KEY] }

    suspend fun savePin(context: Context, pin: String) {
        context.pinStore.edit { it[PIN_KEY] = pin }
    }

    suspend fun clearPin(context: Context) {
        context.pinStore.edit { it.remove(PIN_KEY) }
    }
}
