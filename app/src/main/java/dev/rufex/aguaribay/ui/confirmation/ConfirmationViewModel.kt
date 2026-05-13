package dev.rufex.aguaribay.ui.confirmation

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.rufex.aguaribay.data.AttemptStore
import dev.rufex.aguaribay.data.SettingsStore
import dev.rufex.aguaribay.service.SessionGuard
import dev.rufex.aguaribay.usage.UsageStatsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ConfirmationUiState(
    val appName: String = "",
    val appIcon: Bitmap? = null,
    val attemptsToday: Int = 0,
    val openedToday: Int = 0,
    val timeSpent24h: Long = 0L,
    val timeSpent7d: Long = 0L,
    val secondsLeft: Int = 0,
    val canConfirm: Boolean = false,
)

class ConfirmationViewModel(application: Application) : AndroidViewModel(application) {

    private val attemptStore = AttemptStore(application)
    private val settingsStore = SettingsStore(application)

    private val _uiState = MutableStateFlow(ConfirmationUiState())
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun load(packageName: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val context = getApplication<Application>()
            val pm = context.packageManager

            val (label, icon) = withContext(Dispatchers.IO) {
                try {
                    val info = pm.getApplicationInfo(packageName, 0)
                    info.loadLabel(pm).toString() to info.loadIcon(pm).toBitmap()
                } catch (e: Exception) {
                    packageName to null
                }
            }

            val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val helper = UsageStatsHelper(usageManager)

            _uiState.update {
                it.copy(
                    appName = label,
                    appIcon = icon,
                    attemptsToday = attemptStore.getAttempts(packageName),
                    openedToday = attemptStore.getOpened(packageName),
                    timeSpent24h = helper.getTimeSpentLast24h(packageName),
                    timeSpent7d = helper.getTimeSpentLast7Days(packageName),
                )
            }

            val delaySecs = settingsStore.getConfirmationDelaySecs()
            for (remaining in delaySecs downTo 1) {
                _uiState.update { it.copy(secondsLeft = remaining) }
                delay(1_000L)
            }
            _uiState.update { it.copy(secondsLeft = 0, canConfirm = true) }
        }
    }

    fun confirmOpen(packageName: String) {
        SessionGuard.startSession(packageName)
        attemptStore.incrementOpened(packageName)
    }
}

internal fun formatDuration(millis: Long): String = when {
    millis < 60_000L -> "< 1m"
    millis < 3_600_000L -> "${millis / 60_000}m"
    else -> "${millis / 3_600_000}h ${(millis % 3_600_000) / 60_000}m"
}

private fun Drawable.toBitmap(): Bitmap {
    val size = 96
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, size, size)
    draw(canvas)
    return bitmap
}
