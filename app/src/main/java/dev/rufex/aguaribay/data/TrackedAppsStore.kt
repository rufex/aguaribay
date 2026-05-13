package dev.rufex.aguaribay.data

import android.content.Context

class TrackedAppsStore(context: Context) {

    private val prefs = context.getSharedPreferences("tracked_apps", Context.MODE_PRIVATE)

    fun getTrackedApps(): Set<String> = prefs.getStringSet(KEY_APPS, emptySet()) ?: emptySet()

    fun setTracked(packageName: String, tracked: Boolean) {
        val current = getTrackedApps().toMutableSet()
        if (tracked) current.add(packageName) else current.remove(packageName)
        prefs.edit().putStringSet(KEY_APPS, current).apply()
    }

    fun isTracked(packageName: String): Boolean = packageName in getTrackedApps()

    companion object {
        private const val KEY_APPS = "apps"
    }
}
