package dev.rufex.aguaribay.ui.confirmation

import android.app.Application
import android.content.SharedPreferences
import dev.rufex.aguaribay.service.SessionGuard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ConfirmationViewModelTest {

    private lateinit var viewModel: ConfirmationViewModel
    private val backingMap = mutableMapOf<String, Any>()

    @Before
    fun setUp() {
        SessionGuard.clearAll()

        val editor = mock<SharedPreferences.Editor>()
        val prefs = mock<SharedPreferences>()
        val app = mock<Application>()

        whenever(app.getSharedPreferences(any(), any())).thenReturn(prefs)
        whenever(prefs.getInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] as? Int ?: it.arguments[1] as Int
        }
        whenever(prefs.getStringSet(any(), any())).thenAnswer {
            @Suppress("UNCHECKED_CAST")
            backingMap[it.arguments[0] as String] as? Set<String> ?: it.arguments[1] as Set<String>
        }
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] = it.arguments[1] as Int
            editor
        }
        whenever(editor.putStringSet(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] = it.arguments[1] as Set<String>
            editor
        }
        whenever(editor.apply()).then { }

        viewModel = ConfirmationViewModel(app)
    }

    @Test
    fun `confirmOpen starts a session`() {
        viewModel.confirmOpen("com.example.app")
        assertTrue(SessionGuard.isSessionActive("com.example.app"))
    }

    @Test
    fun `confirmOpen increments opened count`() {
        viewModel.confirmOpen("com.example.app")
        val key = "opened_com.example.app_${java.time.LocalDate.now()}"
        assertEquals(1, backingMap[key])
    }

    @Test
    fun `confirmOpen is independent per package`() {
        viewModel.confirmOpen("com.app.one")
        assertTrue(SessionGuard.isSessionActive("com.app.one"))
        assertTrue(!SessionGuard.isSessionActive("com.app.two"))
    }

    @Test
    fun `rescheduleRecheck does not crash when service is not running`() {
        viewModel.rescheduleRecheck("com.example.app")
    }
}
