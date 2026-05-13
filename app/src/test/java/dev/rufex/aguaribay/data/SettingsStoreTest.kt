package dev.rufex.aguaribay.data

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SettingsStoreTest {

    private lateinit var store: SettingsStore
    private val backingMap = mutableMapOf<String, Int>()

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

        store = SettingsStore(context)
    }

    @Test
    fun `getConfirmationDelaySecs returns default 2`() {
        assertEquals(SettingsStore.DEFAULT_DELAY, store.getConfirmationDelaySecs())
    }

    @Test
    fun `setConfirmationDelaySecs persists value`() {
        store.setConfirmationDelaySecs(5)
        assertEquals(5, store.getConfirmationDelaySecs())
    }

    @Test
    fun `getSessionRecheckMinutes returns default 10`() {
        assertEquals(SettingsStore.DEFAULT_RECHECK, store.getSessionRecheckMinutes())
    }

    @Test
    fun `setSessionRecheckMinutes persists value`() {
        store.setSessionRecheckMinutes(20)
        assertEquals(20, store.getSessionRecheckMinutes())
    }

    @Test
    fun `delay and recheck settings are independent`() {
        store.setConfirmationDelaySecs(7)
        store.setSessionRecheckMinutes(30)
        assertEquals(7, store.getConfirmationDelaySecs())
        assertEquals(30, store.getSessionRecheckMinutes())
    }
}
