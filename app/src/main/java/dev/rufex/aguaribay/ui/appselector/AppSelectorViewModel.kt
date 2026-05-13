package dev.rufex.aguaribay.ui.appselector

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.rufex.aguaribay.data.TrackedAppsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppSelectorUiState(
    val apps: List<AppInfo> = emptyList(),
    val trackedApps: Set<String> = emptySet(),
    val isLoading: Boolean = true,
)

class AppSelectorViewModel(application: Application) : AndroidViewModel(application) {

    private val trackedAppsStore = TrackedAppsStore(application)

    private val _uiState = MutableStateFlow(AppSelectorUiState())
    val uiState: StateFlow<AppSelectorUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun refresh() = loadApps()

    private fun loadApps() {
        viewModelScope.launch {
            val pm = getApplication<Application>().packageManager
            val ownPackage = getApplication<Application>().packageName
            val apps = withContext(Dispatchers.IO) {
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { isUserApp(it, ownPackage) }
                    .mapNotNull { info ->
                        try {
                            AppInfo(
                                packageName = info.packageName,
                                label = info.loadLabel(pm).toString(),
                                icon = info.loadIcon(pm).toBitmap(),
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    .sortedBy { it.label.lowercase() }
            }
            _uiState.update {
                it.copy(
                    apps = apps,
                    trackedApps = trackedAppsStore.getTrackedApps(),
                    isLoading = false,
                )
            }
        }
    }

    fun toggleTracked(packageName: String) {
        val isTracked = trackedAppsStore.isTracked(packageName)
        trackedAppsStore.setTracked(packageName, !isTracked)
        _uiState.update { it.copy(trackedApps = trackedAppsStore.getTrackedApps()) }
    }
}

internal fun isUserApp(info: ApplicationInfo, ownPackage: String): Boolean =
    info.flags and ApplicationInfo.FLAG_SYSTEM == 0 && info.packageName != ownPackage

private fun Drawable.toBitmap(): Bitmap {
    val size = 96 // fixed size — AdaptiveIconDrawable returns intrinsicWidth = -1
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, size, size)
    draw(canvas)
    return bitmap
}
