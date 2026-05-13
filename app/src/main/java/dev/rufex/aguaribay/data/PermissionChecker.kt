package dev.rufex.aguaribay.data

import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import android.provider.Settings

interface PermissionChecker {
    fun hasUsageAccess(): Boolean
    fun hasOverlayPermission(): Boolean
    fun hasAccessibilityEnabled(): Boolean
    fun allGranted(): Boolean = hasUsageAccess() && hasOverlayPermission() && hasAccessibilityEnabled()
}

class SystemPermissionChecker(private val context: Context) : PermissionChecker {

    override fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun hasOverlayPermission(): Boolean = Settings.canDrawOverlays(context)

    override fun hasAccessibilityEnabled(): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.contains(context.packageName)
    }
}
