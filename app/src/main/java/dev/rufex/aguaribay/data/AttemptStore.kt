package dev.rufex.aguaribay.data

import android.content.Context
import java.time.LocalDate

class AttemptStore(
    context: Context,
    private val today: () -> LocalDate = { LocalDate.now() },
) {
    private val prefs = context.getSharedPreferences("attempts", Context.MODE_PRIVATE)

    fun incrementAttempt(packageName: String) = increment(attemptKey(packageName))

    fun incrementOpened(packageName: String) = increment(openedKey(packageName))

    fun getAttempts(packageName: String, date: LocalDate = today()): Int =
        prefs.getInt(attemptKey(packageName, date), 0)

    fun getOpened(packageName: String, date: LocalDate = today()): Int =
        prefs.getInt(openedKey(packageName, date), 0)

    private fun increment(key: String) {
        prefs.edit().putInt(key, prefs.getInt(key, 0) + 1).apply()
    }

    private fun attemptKey(packageName: String, date: LocalDate = today()) =
        "attempt_${packageName}_$date"

    private fun openedKey(packageName: String, date: LocalDate = today()) =
        "opened_${packageName}_$date"
}
