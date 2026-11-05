package com.vaylora.app.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.vaylora.app.core.config.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "vaylora_prefs")

class AppPreferences(private val context: Context) {

    private val THEME_DARK = booleanPreferencesKey(AppConfig.PREFS_THEME)
    private val NOTIFICATIONS_ENABLED = booleanPreferencesKey(AppConfig.PREFS_NOTIFICATIONS)
    private val ONBOARDING_DONE = booleanPreferencesKey(AppConfig.PREFS_ONBOARDING_DONE)

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[THEME_DARK] ?: true
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_DONE] ?: false
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[THEME_DARK] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[ONBOARDING_DONE] = done }
    }
}
