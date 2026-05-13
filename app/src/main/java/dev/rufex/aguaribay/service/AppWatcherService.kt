package dev.rufex.aguaribay.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AppWatcherService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // implemented in Step 6
    }

    override fun onInterrupt() {
        // implemented in Step 6
    }
}
