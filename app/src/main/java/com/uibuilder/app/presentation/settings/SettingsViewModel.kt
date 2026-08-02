package com.uibuilder.app.presentation.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserSettings(
    val darkMode: Boolean = false,
    val rtl: Boolean = false,
    val showGrid: Boolean = true,
    val snapToGrid: Boolean = false,
    val autoSave: Boolean = true
)

private val Context.dataStore by preferencesDataStore(name = "uibuilder_settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    init { loadSettings() }

    private fun loadSettings() {
        viewModelScope.launch {
            val prefs = context.dataStore.data.first()
            _settings.value = UserSettings(
                darkMode = prefs[DARK_MODE] ?: false,
                rtl = prefs[RTL] ?: false,
                showGrid = prefs[SHOW_GRID] ?: true,
                snapToGrid = prefs[SNAP_TO_GRID] ?: false,
                autoSave = prefs[AUTO_SAVE] ?: true
            )
        }
    }

    fun setDarkMode(value: Boolean) = update(DARK_MODE, value) { copy(darkMode = value) }
    fun setRtl(value: Boolean) = update(RTL, value) { copy(rtl = value) }
    fun setShowGrid(value: Boolean) = update(SHOW_GRID, value) { copy(showGrid = value) }
    fun setSnapToGrid(value: Boolean) = update(SNAP_TO_GRID, value) { copy(snapToGrid = value) }
    fun setAutoSave(value: Boolean) = update(AUTO_SAVE, value) { copy(autoSave = value) }

    private fun update(
        key: androidx.datastore.preferences.core.Preferences.Key<Boolean>,
        value: Boolean,
        block: UserSettings.() -> UserSettings
    ) {
        _settings.value = _settings.value.block()
        viewModelScope.launch {
            context.dataStore.edit { it[key] = value }
        }
    }

    companion object {
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val RTL = booleanPreferencesKey("rtl")
        private val SHOW_GRID = booleanPreferencesKey("show_grid")
        private val SNAP_TO_GRID = booleanPreferencesKey("snap_to_grid")
        private val AUTO_SAVE = booleanPreferencesKey("auto_save")
    }
}
