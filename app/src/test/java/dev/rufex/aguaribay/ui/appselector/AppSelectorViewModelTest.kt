package dev.rufex.aguaribay.ui.appselector

import android.content.pm.ApplicationInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSelectorViewModelTest {

    @Test
    fun `isUserApp excludes system apps`() {
        val info = ApplicationInfo()
        info.flags = ApplicationInfo.FLAG_SYSTEM
        info.packageName = "com.android.settings"
        assertFalse(isUserApp(info, "dev.rufex.aguaribay"))
    }

    @Test
    fun `isUserApp excludes updated system apps`() {
        val info = ApplicationInfo()
        info.flags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        info.packageName = "com.android.chrome"
        assertFalse(isUserApp(info, "dev.rufex.aguaribay"))
    }

    @Test
    fun `isUserApp excludes own package`() {
        val info = ApplicationInfo()
        info.flags = 0
        info.packageName = "dev.rufex.aguaribay"
        assertFalse(isUserApp(info, "dev.rufex.aguaribay"))
    }

    @Test
    fun `isUserApp includes third-party user apps`() {
        val info = ApplicationInfo()
        info.flags = 0
        info.packageName = "com.example.someapp"
        assertTrue(isUserApp(info, "dev.rufex.aguaribay"))
    }

    @Test
    fun `isUserApp includes multiple different user apps`() {
        val packages = listOf("com.spotify.music", "com.instagram.android", "com.twitter.android")
        packages.forEach { pkg ->
            val info = ApplicationInfo()
            info.flags = 0
            info.packageName = pkg
            assertTrue("$pkg should be included", isUserApp(info, "dev.rufex.aguaribay"))
        }
    }
}
