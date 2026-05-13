package dev.rufex.aguaribay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.rufex.aguaribay.data.PermissionChecker
import dev.rufex.aguaribay.data.SystemPermissionChecker
import dev.rufex.aguaribay.ui.onboarding.PermissionGateScreen

class MainActivity : ComponentActivity() {

    private lateinit var permissionChecker: PermissionChecker
    private var currentScreen by mutableStateOf<Screen>(Screen.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionChecker = SystemPermissionChecker(this)
        enableEdgeToEdge()
        setContent {
            when (currentScreen) {
                Screen.UsageAccess -> PermissionGateScreen(
                    title = "Usage Access Required",
                    description = "Aguaribay needs to see how much time you spend in apps. Tap below and enable 'Usage access' for Aguaribay.",
                    onOpenSettings = { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
                )
                Screen.Overlay -> PermissionGateScreen(
                    title = "Overlay Permission Required",
                    description = "Aguaribay needs to show a confirmation screen when you open a tracked app. Tap below and enable 'Display over other apps' for Aguaribay.",
                    onOpenSettings = {
                        startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                    },
                )
                Screen.Accessibility -> PermissionGateScreen(
                    title = "Accessibility Service Required",
                    description = "Aguaribay needs to detect when you open a tracked app. Tap below, find 'Aguaribay' under Installed Apps and enable it.",
                    onOpenSettings = { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                )
                Screen.Home -> Text("Home — coming in Step 3")
                Screen.Loading -> Unit
            }
        }
    }

    override fun onResume() {
        super.onResume()
        currentScreen = when {
            !permissionChecker.hasUsageAccess() -> Screen.UsageAccess
            !permissionChecker.hasOverlayPermission() -> Screen.Overlay
            !permissionChecker.hasAccessibilityEnabled() -> Screen.Accessibility
            else -> Screen.Home
        }
    }
}

private enum class Screen {
    Loading, UsageAccess, Overlay, Accessibility, Home
}
