package dev.rufex.aguaribay

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenRoutingTest {

    private fun route(
        usage: Boolean = true,
        overlay: Boolean = true,
        accessibility: Boolean = true,
        current: Screen = Screen.Home,
    ) = nextScreen(usage, overlay, accessibility, current)

    @Test
    fun `all permissions granted routes to Home`() {
        assertEquals(Screen.Home, route())
    }

    @Test
    fun `missing usage access routes to UsageAccess`() {
        assertEquals(Screen.UsageAccess, route(usage = false))
    }

    @Test
    fun `missing overlay routes to Overlay`() {
        assertEquals(Screen.Overlay, route(overlay = false))
    }

    @Test
    fun `missing accessibility routes to Accessibility`() {
        assertEquals(Screen.Accessibility, route(accessibility = false))
    }

    @Test
    fun `usage access is checked before overlay`() {
        assertEquals(Screen.UsageAccess, route(usage = false, overlay = false))
    }

    @Test
    fun `overlay is checked before accessibility`() {
        assertEquals(Screen.Overlay, route(overlay = false, accessibility = false))
    }

    @Test
    fun `Settings screen is preserved when all permissions granted`() {
        assertEquals(Screen.Settings, route(current = Screen.Settings))
    }

    @Test
    fun `Settings screen is overridden when permission is revoked`() {
        assertEquals(Screen.Accessibility, route(accessibility = false, current = Screen.Settings))
    }
}
