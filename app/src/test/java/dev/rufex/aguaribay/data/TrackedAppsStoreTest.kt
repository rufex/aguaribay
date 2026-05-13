package dev.rufex.aguaribay.data

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TrackedAppsStoreTest {

    private lateinit var store: TrackedAppsStore
    private val backingMap = mutableMapOf<String, Set<String>>()

    @Before
    fun setUp() {
        val editor = mock<SharedPreferences.Editor>()
        val prefs = mock<SharedPreferences>()
        val context = mock<Context>()

        whenever(context.getSharedPreferences(any(), any())).thenReturn(prefs)
        whenever(prefs.getStringSet(any(), any())).thenAnswer { backingMap[it.arguments[0]] ?: it.arguments[1] }
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putStringSet(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] = it.arguments[1] as Set<String>
            editor
        }
        whenever(editor.apply()).then { }

        store = TrackedAppsStore(context)
    }

    @Test
    fun `getTrackedApps returns empty set initially`() {
        assertTrue(store.getTrackedApps().isEmpty())
    }

    @Test
    fun `setTracked adds package when tracked is true`() {
        store.setTracked("com.example.app", true)
        assertTrue(store.isTracked("com.example.app"))
    }

    @Test
    fun `setTracked removes package when tracked is false`() {
        store.setTracked("com.example.app", true)
        store.setTracked("com.example.app", false)
        assertFalse(store.isTracked("com.example.app"))
    }

    @Test
    fun `multiple apps can be tracked independently`() {
        store.setTracked("com.app.one", true)
        store.setTracked("com.app.two", true)
        store.setTracked("com.app.one", false)

        assertFalse(store.isTracked("com.app.one"))
        assertTrue(store.isTracked("com.app.two"))
        assertEquals(setOf("com.app.two"), store.getTrackedApps())
    }
}
