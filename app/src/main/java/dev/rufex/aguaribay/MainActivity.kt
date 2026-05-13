package dev.rufex.aguaribay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.rufex.aguaribay.data.PermissionChecker
import dev.rufex.aguaribay.data.SystemPermissionChecker
import dev.rufex.aguaribay.ui.appselector.AppSelectorScreen
import dev.rufex.aguaribay.ui.onboarding.PermissionGateScreen

class MainActivity : ComponentActivity() {

    private lateinit var permissionChecker: PermissionChecker
    private var currentScreen by mutableStateOf<Screen>(Screen.Accessibility)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionChecker = SystemPermissionChecker(this)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
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
                        Screen.Home -> AppSelectorScreen()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val usage = permissionChecker.hasUsageAccess()
        val overlay = permissionChecker.hasOverlayPermission()
        val accessibility = permissionChecker.hasAccessibilityEnabled()
        Log.d("Aguaribay", "permissions — usage=$usage overlay=$overlay accessibility=$accessibility")
        currentScreen = when {
            !usage -> Screen.UsageAccess
            !overlay -> Screen.Overlay
            !accessibility -> Screen.Accessibility
            else -> Screen.Home
        }
        Log.d("Aguaribay", "currentScreen=$currentScreen")
    }
}

private enum class Screen {
    UsageAccess, Overlay, Accessibility, Home
}
