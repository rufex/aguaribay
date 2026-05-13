package dev.rufex.aguaribay.data

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

class AttemptStoreTest {

    private lateinit var store: AttemptStore
    private val backingMap = mutableMapOf<String, Int>()
    private val fixedDate = LocalDate.of(2026, 5, 13)

    @Before
    fun setUp() {
        val editor = mock<SharedPreferences.Editor>()
        val prefs = mock<SharedPreferences>()
        val context = mock<Context>()

        whenever(context.getSharedPreferences(any(), any())).thenReturn(prefs)
        whenever(prefs.getInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] ?: it.arguments[1] as Int
        }
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] = it.arguments[1] as Int
            editor
        }
        whenever(editor.apply()).then { }

        store = AttemptStore(context, today = { fixedDate })
    }

    @Test
    fun `getAttempts returns 0 initially`() {
        assertEquals(0, store.getAttempts("com.example.app", fixedDate))
    }

    @Test
    fun `getOpened returns 0 initially`() {
        assertEquals(0, store.getOpened("com.example.app", fixedDate))
    }

    @Test
    fun `incrementAttempt increments count`() {
        store.incrementAttempt("com.example.app")
        assertEquals(1, store.getAttempts("com.example.app", fixedDate))
    }

    @Test
    fun `incrementAttempt accumulates on repeated calls`() {
        repeat(3) { store.incrementAttempt("com.example.app") }
        assertEquals(3, store.getAttempts("com.example.app", fixedDate))
    }

    @Test
    fun `incrementOpened increments independently from attempts`() {
        store.incrementAttempt("com.example.app")
        store.incrementAttempt("com.example.app")
        store.incrementOpened("com.example.app")

        assertEquals(2, store.getAttempts("com.example.app", fixedDate))
        assertEquals(1, store.getOpened("com.example.app", fixedDate))
    }

    @Test
    fun `counts are isolated per package`() {
        store.incrementAttempt("com.app.one")
        store.incrementAttempt("com.app.one")
        store.incrementAttempt("com.app.two")

        assertEquals(2, store.getAttempts("com.app.one", fixedDate))
        assertEquals(1, store.getAttempts("com.app.two", fixedDate))
    }

    @Test
    fun `counts are isolated per date`() {
        val yesterday = fixedDate.minusDays(1)
        store.incrementAttempt("com.example.app")

        assertEquals(1, store.getAttempts("com.example.app", fixedDate))
        assertEquals(0, store.getAttempts("com.example.app", yesterday))
    }

    @Test
    fun `getAttempts uses today by default`() {
        store.incrementAttempt("com.example.app")
        assertEquals(1, store.getAttempts("com.example.app"))
    }
}
