package dev.rufex.aguaribay.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.rufex.aguaribay.data.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val confirmationDelaySecs: Int = SettingsStore.DEFAULT_DELAY,
    val sessionRecheckMinutes: Int = SettingsStore.DEFAULT_RECHECK,
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val store = SettingsStore(application)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            confirmationDelaySecs = store.getConfirmationDelaySecs(),
            sessionRecheckMinutes = store.getSessionRecheckMinutes(),
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setConfirmationDelay(secs: Int) {
        store.setConfirmationDelaySecs(secs)
        _uiState.update { it.copy(confirmationDelaySecs = secs) }
    }

    fun setSessionRecheck(minutes: Int) {
        store.setSessionRecheckMinutes(minutes)
        _uiState.update { it.copy(sessionRecheckMinutes = minutes) }
    }

    companion object {
        const val DELAY_MIN = 0
        const val DELAY_MAX = 30
        const val RECHECK_MIN = 1
        const val RECHECK_MAX = 60
    }
}
