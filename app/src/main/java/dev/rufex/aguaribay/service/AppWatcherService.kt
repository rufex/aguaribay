package dev.rufex.aguaribay.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import dev.rufex.aguaribay.data.AttemptStore
import dev.rufex.aguaribay.data.TrackedAppsStore
import dev.rufex.aguaribay.ui.confirmation.ConfirmationActivity

class AppWatcherService : AccessibilityService() {

    private lateinit var trackedAppsStore: TrackedAppsStore
    private lateinit var attemptStore: AttemptStore

    private var lastFiredPackage: String? = null
    private var lastFiredTime: Long = 0L
    private var lastForegroundPackage: String? = null

    override fun onCreate() {
        super.onCreate()
        trackedAppsStore = TrackedAppsStore(this)
        attemptStore = AttemptStore(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> event.packageName?.toString()
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> foregroundAppPackage()
            else -> return
        } ?: return
        if (pkg == packageName) return

        val prev = lastForegroundPackage
        if (prev != null && prev != pkg) SessionGuard.endSession(prev)
        lastForegroundPackage = pkg

        if (SessionGuard.isSessionActive(pkg)) return

        val now = System.currentTimeMillis()
        if (pkg == lastFiredPackage && now - lastFiredTime < DEBOUNCE_MS) return

        if (!trackedAppsStore.isTracked(pkg)) return

        lastFiredPackage = pkg
        lastFiredTime = now

        attemptStore.incrementAttempt(pkg)

        startActivity(
            Intent(this, ConfirmationActivity::class.java).apply {
                putExtra(ConfirmationActivity.EXTRA_PACKAGE, pkg)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
        )
    }

    private fun foregroundAppPackage(): String? =
        windows.find { it.isActive && it.type == AccessibilityWindowInfo.TYPE_APPLICATION }
            ?.root?.packageName?.toString()

    override fun onInterrupt() = Unit

    companion object {
        private const val DEBOUNCE_MS = 500L
    }
}
