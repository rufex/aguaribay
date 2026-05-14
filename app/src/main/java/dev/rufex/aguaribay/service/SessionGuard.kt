package dev.rufex.aguaribay.service

object SessionGuard {
    private val activeSessions = mutableSetOf<String>()

    fun startSession(packageName: String) {
        activeSessions.add(packageName)
    }

    fun endSession(packageName: String) {
        activeSessions.remove(packageName)
    }

    fun isSessionActive(packageName: String): Boolean = packageName in activeSessions

    fun clearAll() {
        activeSessions.clear()
    }
}
