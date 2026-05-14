package dev.rufex.aguaribay.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import dev.rufex.aguaribay.data.AttemptStore
import dev.rufex.aguaribay.data.TrackedAppsStore
import dev.rufex.aguaribay.ui.confirmation.ConfirmationActivity

class AppWatcherService : AccessibilityService() {

    private lateinit var trackedAppsStore: TrackedAppsStore
    private lateinit var attemptStore: AttemptStore

    private val handler = Handler(Looper.getMainLooper())
    private val recheckRunnables = mutableMapOf<String, Runnable>()

    private var lastFiredPackage: String? = null
    private var lastFiredTime: Long = 0L
    private var lastForegroundPackage: String? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        trackedAppsStore = TrackedAppsStore(this)
        attemptStore = AttemptStore(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        recheckRunnables.keys.toList().forEach { cancelRecheckFor(it) }
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> event.packageName?.toString()
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> foregroundAppPackage()
            else -> return
        } ?: return
        if (pkg == packageName) return

        val prev = lastForegroundPackage
        if (prev != null && prev != pkg) {
            SessionGuard.endSession(prev)
            cancelRecheckFor(prev)
        }
        lastForegroundPackage = pkg

        if (SessionGuard.isSessionActive(pkg)) return

        val now = System.currentTimeMillis()
        if (pkg == lastFiredPackage && now - lastFiredTime < DEBOUNCE_MS) return

        if (!trackedAppsStore.isTracked(pkg)) return

        lastFiredPackage = pkg
        lastFiredTime = now

        attemptStore.incrementAttempt(pkg)

        startActivity(confirmationIntent(pkg, isRecheck = false))
    }

    private fun foregroundAppPackage(): String? =
        windows.find { it.isActive && it.type == AccessibilityWindowInfo.TYPE_APPLICATION }
            ?.root?.packageName?.toString()

    private fun scheduleRecheckFor(packageName: String, delayMs: Long) {
        cancelRecheckFor(packageName)
        val runnable = Runnable {
            recheckRunnables.remove(packageName)
            if (!SessionGuard.isSessionActive(packageName)) return@Runnable
            startActivity(confirmationIntent(packageName, isRecheck = true))
        }
        recheckRunnables[packageName] = runnable
        handler.postDelayed(runnable, delayMs)
    }

    private fun cancelRecheckFor(packageName: String) {
        recheckRunnables.remove(packageName)?.let { handler.removeCallbacks(it) }
    }

    private fun confirmationIntent(packageName: String, isRecheck: Boolean) =
        Intent(this, ConfirmationActivity::class.java).apply {
            putExtra(ConfirmationActivity.EXTRA_PACKAGE, packageName)
            putExtra(ConfirmationActivity.EXTRA_IS_RECHECK, isRecheck)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

    override fun onInterrupt() = Unit

    companion object {
        private const val DEBOUNCE_MS = 500L

        @Volatile
        private var instance: AppWatcherService? = null

        fun scheduleRecheck(packageName: String, delayMs: Long) {
            instance?.scheduleRecheckFor(packageName, delayMs)
        }

        fun cancelRecheck(packageName: String) {
            instance?.cancelRecheckFor(packageName)
        }
    }
}
