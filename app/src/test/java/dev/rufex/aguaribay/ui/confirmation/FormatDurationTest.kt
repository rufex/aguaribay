package dev.rufex.aguaribay.ui.confirmation

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatDurationTest {

    @Test
    fun `zero millis formats as less than 1 minute`() {
        assertEquals("< 1m", formatDuration(0L))
    }

    @Test
    fun `30 seconds formats as less than 1 minute`() {
        assertEquals("< 1m", formatDuration(30_000L))
    }

    @Test
    fun `exactly 1 minute formats as 1m`() {
        assertEquals("1m", formatDuration(60_000L))
    }

    @Test
    fun `45 minutes formats correctly`() {
        assertEquals("45m", formatDuration(2_700_000L))
    }

    @Test
    fun `exactly 1 hour formats as 1h 0m`() {
        assertEquals("1h 0m", formatDuration(3_600_000L))
    }

    @Test
    fun `2 hours 14 minutes formats correctly`() {
        assertEquals("2h 14m", formatDuration(8_040_000L))
    }

    @Test
    fun `59 minutes 59 seconds is still minutes-only`() {
        assertEquals("59m", formatDuration(3_599_000L))
    }
}
