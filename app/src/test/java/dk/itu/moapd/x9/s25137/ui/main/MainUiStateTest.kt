package dk.itu.moapd.x9.s25137.ui.main

import dk.itu.moapd.x9.s25137.domain.models.Report
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MainUiStateTest {

    @Test
    fun defaultStateHasNoUserAndNoReports() {
        val state = MainUiState()

        assertEquals(null, state.userId)
        assertTrue(state.reports.isEmpty())
    }

    @Test
    fun copyUpdatesUserAndReports() {
        val report = Report(key = "id-1", title = "Report")
        val state = MainUiState().copy(userId = "user-1", reports = listOf(report))

        assertEquals("user-1", state.userId)
        assertEquals(1, state.reports.size)
        assertEquals("id-1", state.reports.first().key)
    }
}

