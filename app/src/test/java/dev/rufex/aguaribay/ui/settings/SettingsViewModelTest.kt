package dev.rufex.aguaribay.ui.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dev.rufex.aguaribay.data.SettingsStore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private val backingMap = mutableMapOf<String, Int>()

    @Before
    fun setUp() {
        val editor = mock<SharedPreferences.Editor>()
        val prefs = mock<SharedPreferences>()
        val app = mock<Application>()

        whenever(app.getSharedPreferences(any(), any())).thenReturn(prefs)
        whenever(prefs.getInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] ?: it.arguments[1] as Int
        }
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] = it.arguments[1] as Int
            editor
        }
        whenever(editor.apply()).then { }

        viewModel = SettingsViewModel(app)
    }

    @Test
    fun `initial state uses default delay`() {
        assertEquals(SettingsStore.DEFAULT_DELAY, viewModel.uiState.value.confirmationDelaySecs)
    }

    @Test
    fun `initial state uses default recheck`() {
        assertEquals(SettingsStore.DEFAULT_RECHECK, viewModel.uiState.value.sessionRecheckMinutes)
    }

    @Test
    fun `setConfirmationDelay updates state`() {
        viewModel.setConfirmationDelay(5)
        assertEquals(5, viewModel.uiState.value.confirmationDelaySecs)
    }

    @Test
    fun `setSessionRecheck updates state`() {
        viewModel.setSessionRecheck(20)
        assertEquals(20, viewModel.uiState.value.sessionRecheckMinutes)
    }

    @Test
    fun `setConfirmationDelay persists across viewmodel recreation`() {
        viewModel.setConfirmationDelay(8)

        val editor = mock<SharedPreferences.Editor>()
        val prefs = mock<SharedPreferences>()
        val app2 = mock<Application>()
        whenever(app2.getSharedPreferences(any(), any())).thenReturn(prefs)
        whenever(prefs.getInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] ?: it.arguments[1] as Int
        }
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenAnswer {
            backingMap[it.arguments[0] as String] = it.arguments[1] as Int
            editor
        }
        whenever(editor.apply()).then { }

        val vm2 = SettingsViewModel(app2)
        assertEquals(8, vm2.uiState.value.confirmationDelaySecs)
    }

    @Test
    fun `delay and recheck are independent`() {
        viewModel.setConfirmationDelay(10)
        viewModel.setSessionRecheck(30)
        assertEquals(10, viewModel.uiState.value.confirmationDelaySecs)
        assertEquals(30, viewModel.uiState.value.sessionRecheckMinutes)
    }
}
