package dev.rufex.aguaribay.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionCheckerTest {

    private fun fake(usage: Boolean, overlay: Boolean, accessibility: Boolean) = object : PermissionChecker {
        override fun hasUsageAccess() = usage
        override fun hasOverlayPermission() = overlay
        override fun hasAccessibilityEnabled() = accessibility
    }

    @Test
    fun `allGranted returns true when all permissions are granted`() {
        assertTrue(fake(usage = true, overlay = true, accessibility = true).allGranted())
    }

    @Test
    fun `allGranted returns false when usage access is missing`() {
        assertFalse(fake(usage = false, overlay = true, accessibility = true).allGranted())
    }

    @Test
    fun `allGranted returns false when overlay permission is missing`() {
        assertFalse(fake(usage = true, overlay = false, accessibility = true).allGranted())
    }

    @Test
    fun `allGranted returns false when accessibility is missing`() {
        assertFalse(fake(usage = true, overlay = true, accessibility = false).allGranted())
    }

    @Test
    fun `allGranted returns false when all permissions are missing`() {
        assertFalse(fake(usage = false, overlay = false, accessibility = false).allGranted())
    }
}
