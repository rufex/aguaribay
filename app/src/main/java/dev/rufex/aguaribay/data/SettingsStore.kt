package dev.rufex.aguaribay.data

import android.content.Context

class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getConfirmationDelaySecs(): Int = prefs.getInt(KEY_DELAY, DEFAULT_DELAY)

    fun setConfirmationDelaySecs(n: Int) {
        prefs.edit().putInt(KEY_DELAY, n).apply()
    }

    fun getSessionRecheckMinutes(): Int = prefs.getInt(KEY_RECHECK, DEFAULT_RECHECK)

    fun setSessionRecheckMinutes(n: Int) {
        prefs.edit().putInt(KEY_RECHECK, n).apply()
    }

    companion object {
        private const val KEY_DELAY = "confirmation_delay_secs"
        private const val KEY_RECHECK = "session_recheck_minutes"
        const val DEFAULT_DELAY = 2
        const val DEFAULT_RECHECK = 10
    }
}
