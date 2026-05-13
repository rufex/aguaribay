package dev.rufex.aguaribay.usage

import android.app.usage.UsageStatsManager
import java.util.concurrent.TimeUnit

class UsageStatsHelper(
    private val manager: UsageStatsManager,
    private val now: () -> Long = System::currentTimeMillis,
) {
    fun getTimeSpentLast24h(packageName: String): Long {
        val end = now()
        return queryTotalTime(packageName, end - TimeUnit.HOURS.toMillis(24), end)
    }

    fun getTimeSpentLast7Days(packageName: String): Long {
        val end = now()
        return queryTotalTime(packageName, end - TimeUnit.DAYS.toMillis(7), end)
    }

    private fun queryTotalTime(packageName: String, start: Long, end: Long): Long =
        manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            ?.filter { it.packageName == packageName }
            ?.sumOf { it.totalTimeInForeground }
            ?: 0L
}
