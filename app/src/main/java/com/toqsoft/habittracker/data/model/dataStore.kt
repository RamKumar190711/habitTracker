package com.toqsoft.habittracker.data.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("lock_settings")

object FingerprintDataStore {

    private val FINGERPRINT_KEY = booleanPreferencesKey("fingerprint_enabled")

    fun isEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[FINGERPRINT_KEY] ?: false }

    suspend fun save(context: Context, enabled: Boolean) {
        context.dataStore.edit {
            it[FINGERPRINT_KEY] = enabled
        }
    }
}
