package dev.rufex.aguaribay.usage

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.TimeUnit

class UsageStatsHelperTest {

    private lateinit var manager: UsageStatsManager
    private val fixedNow = 1_000_000_000L

    @Before
    fun setUp() {
        manager = mock()
        whenever(manager.queryUsageStats(any(), any(), any())).thenReturn(emptyList())
    }

    private fun helper() = UsageStatsHelper(manager, now = { fixedNow })

    private fun fakeStats(packageName: String, totalTime: Long): UsageStats =
        mock<UsageStats>().also {
            whenever(it.packageName).thenReturn(packageName)
            whenever(it.totalTimeInForeground).thenReturn(totalTime)
        }

    @Test
    fun `getTimeSpentLast24h queries a 24-hour window ending at now`() {
        val beginCaptor = argumentCaptor<Long>()
        val endCaptor = argumentCaptor<Long>()

        helper().getTimeSpentLast24h("com.example")

        verify(manager).queryUsageStats(any(), beginCaptor.capture(), endCaptor.capture())
        assertEquals(fixedNow, endCaptor.firstValue)
        assertEquals(fixedNow - TimeUnit.HOURS.toMillis(24), beginCaptor.firstValue)
    }

    @Test
    fun `getTimeSpentLast7Days queries a 7-day window ending at now`() {
        val beginCaptor = argumentCaptor<Long>()
        val endCaptor = argumentCaptor<Long>()

        helper().getTimeSpentLast7Days("com.example")

        verify(manager).queryUsageStats(any(), beginCaptor.capture(), endCaptor.capture())
        assertEquals(fixedNow, endCaptor.firstValue)
        assertEquals(fixedNow - TimeUnit.DAYS.toMillis(7), beginCaptor.firstValue)
    }

    @Test
    fun `returns 0 when no stats available`() {
        assertEquals(0L, helper().getTimeSpentLast24h("com.example"))
    }

    @Test
    fun `returns 0 when package is not in results`() {
        val other = fakeStats("com.other.app", 5000L)
        whenever(manager.queryUsageStats(any(), any(), any())).thenReturn(listOf(other))

        assertEquals(0L, helper().getTimeSpentLast24h("com.example"))
    }

    @Test
    fun `sums totalTimeInForeground across multiple entries for same package`() {
        val s1 = fakeStats("com.example", 1_000L)
        val s2 = fakeStats("com.example", 2_000L)
        val other = fakeStats("com.other", 9_999L)
        whenever(manager.queryUsageStats(any(), any(), any())).thenReturn(listOf(s1, s2, other))

        assertEquals(3_000L, helper().getTimeSpentLast24h("com.example"))
    }

    @Test
    fun `returns null-safe 0 when manager returns null`() {
        whenever(manager.queryUsageStats(any(), any(), any())).thenReturn(null)

        assertEquals(0L, helper().getTimeSpentLast24h("com.example"))
    }
}
