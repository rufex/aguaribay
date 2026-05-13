package dev.rufex.aguaribay.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionGuardTest {

    @Before
    fun setUp() {
        SessionGuard.endSession("com.app.one")
        SessionGuard.endSession("com.app.two")
    }

    @Test
    fun `session is not active by default`() {
        assertFalse(SessionGuard.isSessionActive("com.example.app"))
    }

    @Test
    fun `startSession makes session active`() {
        SessionGuard.startSession("com.example.app")
        assertTrue(SessionGuard.isSessionActive("com.example.app"))
    }

    @Test
    fun `endSession deactivates a session`() {
        SessionGuard.startSession("com.example.app")
        SessionGuard.endSession("com.example.app")
        assertFalse(SessionGuard.isSessionActive("com.example.app"))
    }

    @Test
    fun `endSession on inactive package is a no-op`() {
        SessionGuard.endSession("com.never.started")
        assertFalse(SessionGuard.isSessionActive("com.never.started"))
    }

    @Test
    fun `sessions are independent per package`() {
        SessionGuard.startSession("com.app.one")
        assertFalse(SessionGuard.isSessionActive("com.app.two"))
    }

    @Test
    fun `ending one session does not affect another`() {
        SessionGuard.startSession("com.app.one")
        SessionGuard.startSession("com.app.two")
        SessionGuard.endSession("com.app.one")
        assertTrue(SessionGuard.isSessionActive("com.app.two"))
    }
}
