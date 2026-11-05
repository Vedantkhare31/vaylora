package com.vaylora.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vaylora.app.data.local.prefs.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkTheme: Boolean = true,
    val notificationsEnabled: Boolean = true
)

class SettingsViewModel(context: Context) : ViewModel() {
    private val prefs = AppPreferences(context)
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.isDarkTheme.collect { dark ->
                _uiState.value = _uiState.value.copy(isDarkTheme = dark)
            }
        }
        viewModelScope.launch {
            prefs.notificationsEnabled.collect { notifs ->
                _uiState.value = _uiState.value.copy(notificationsEnabled = notifs)
            }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkTheme(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }

    // ── Factory for context-aware instantiation ────────────────────────────
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(context.applicationContext) as T
    }
}
