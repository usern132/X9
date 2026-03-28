package dk.itu.moapd.x9.s25137

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.ui.dashboard.DashboardPage
import dk.itu.moapd.x9.s25137.ui.main.MainUiState
import dk.itu.moapd.x9.s25137.ui.reports.CreateReportContent
import dk.itu.moapd.x9.s25137.ui.reports.details.ReportDetailsPage
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportFlowInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun dashboardCallbacksAreTriggeredFromFabAndReportClick() {
        val first = Report(
            key = "key-1",
            title = "First report",
            location = "A",
            timestamp = 1L,
            type = Type.OTHER,
            description = "First",
            severity = Severity.MINOR
        )
        val second = Report(
            key = "key-2",
            title = "Second report",
            location = "B",
            timestamp = 2L,
            type = Type.OTHER,
            description = "Second",
            severity = Severity.MODERATE
        )
        val state = MutableStateFlow(MainUiState(reports = listOf(first, second)))
        var createClicks = 0
        var clickedIndex: Int? = null

        composeRule.setContent {
            AppTheme {
                DashboardPage(
                    uiState = state,
                    onCreateReportClick = { createClicks += 1 },
                    onReportClick = { clickedIndex = it }
                )
            }
        }

        composeRule.onNodeWithTag("dashboard:createReport").performClick()
        composeRule.onNodeWithText("Second report").performClick()

        assertEquals(1, createClicks)
        assertEquals(1, clickedIndex)
    }

    @Test
    fun createReportEmptySubmitShowsAllRequiredErrors() {
        var submittedReport: Report? = null

        composeRule.setContent {
            AppTheme {
                CreateReportContent(onSubmit = { submittedReport = it })
            }
        }

        composeRule.onNodeWithTag("createReport:submit").performClick()

        composeRule.onAllNodesWithText("This field is required.").assertCountEquals(5)
        assertNull(submittedReport)
    }

    @Test
    fun createReportValidSubmitEmitsReport() {
        val selectedDate = 1_710_000_000_000L
        val submittedReport = mutableStateOf<Report?>(null)

        composeRule.setContent {
            AppTheme {
                CreateReportContent(
                    initialSelectedDateMillis = selectedDate,
                    onSubmit = { submittedReport.value = it }
                )
            }
        }

        val textFields = composeRule.onAllNodes(hasSetTextAction())
        textFields[0].performTextInput("Road blocked")
        textFields[1].performTextInput("Copenhagen")
        textFields[2].performTextInput("Two lanes blocked by a truck")

        composeRule.onNodeWithTag("createReport:type").performClick()
        composeRule.onNodeWithText("Other").performClick()

        composeRule.onNodeWithTag("createReport:submit").performClick()

        assertNotNull(submittedReport.value)
        assertEquals("Road blocked", submittedReport.value?.title)
        assertEquals("Copenhagen", submittedReport.value?.location)
        assertEquals(selectedDate, submittedReport.value?.timestamp)
        assertEquals(Type.OTHER, submittedReport.value?.type)
        assertEquals(Severity.MINOR, submittedReport.value?.severity)
        assertEquals("Two lanes blocked by a truck", submittedReport.value?.description)
    }

    @Test
    fun reportDetailsRendersAllCoreFields() {
        val report = Report(
            key = "detail-1",
            title = "Large pothole",
            location = "Main St 42",
            timestamp = 1_710_000_000_000L,
            type = Type.ROAD_INCIDENTS,
            description = "Dangerous pothole near bike lane",
            severity = Severity.MAJOR
        )

        composeRule.setContent {
            AppTheme {
                ReportDetailsPage(report = report)
            }
        }

        composeRule.onNodeWithText("Large pothole").assertIsDisplayed()
        composeRule.onNodeWithText("Main St 42").assertIsDisplayed()
        composeRule.onNodeWithText("Dangerous pothole near bike lane").assertIsDisplayed()
        composeRule.onNodeWithText("Road incidents").assertIsDisplayed()
        composeRule.onNodeWithText("Major").assertIsDisplayed()
    }

    @Test
    fun createReportDateValidationPreventsSubmissionWithoutDate() {
        var submittedReport: Report? = null

        composeRule.setContent {
            AppTheme {
                CreateReportContent(onSubmit = { submittedReport = it })
            }
        }

        val textFields = composeRule.onAllNodes(hasSetTextAction())
        textFields[0].performTextInput("Title")
        textFields[1].performTextInput("Location")
        textFields[2].performTextInput("Description")

        composeRule.onNodeWithTag("createReport:type").performClick()
        composeRule.onNodeWithText("Other").performClick()
        composeRule.onNodeWithTag("createReport:submit").performClick()

        composeRule.onAllNodesWithText("This field is required.").assertCountEquals(1)
        assertTrue(submittedReport == null)
    }
}
