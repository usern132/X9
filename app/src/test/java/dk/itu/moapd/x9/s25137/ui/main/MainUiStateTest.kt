package dk.itu.moapd.x9.s25137.ui.main

import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MainUiStateTest {

    @Test
    fun defaultStateHasNoUserAndNoReports() {
        val state = MainUiState()

        assertNull(state.currentUser)
        assertTrue(state.reports.isEmpty())
        assertNull(state.errorMessage)
        assertFalse(state.showLoginAlertDialog)
    }

    @Test
    fun copyUpdatesUserAndReports() {
        val user = User(uid = "user-1", name = "Oriol")
        val report = Report(key = "id-1", title = "Report")
        val state = MainUiState().copy(currentUser = user, reports = listOf(report))

        assertEquals("user-1", state.currentUser?.uid)
        assertEquals(1, state.reports.size)
        assertEquals("id-1", state.reports.first().key)
    }

    @Test
    fun copyUpdatesErrorAndDialogFlags() {
        val state = MainUiState().copy(
            errorMessage = "boom",
            showLoginAlertDialog = true
        )

        assertEquals("boom", state.errorMessage)
        assertTrue(state.showLoginAlertDialog)
    }
}

