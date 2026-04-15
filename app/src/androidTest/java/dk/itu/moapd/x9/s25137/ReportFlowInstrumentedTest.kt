package dk.itu.moapd.x9.s25137

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.ui.account.LoggedInAccountScreen
import dk.itu.moapd.x9.s25137.ui.account.LoggedOutAccountScreen
import dk.itu.moapd.x9.s25137.ui.dashboard.DashboardPage
import dk.itu.moapd.x9.s25137.ui.main.MainUiState
import dk.itu.moapd.x9.s25137.ui.reports.details.ReportDetailsPage
import dk.itu.moapd.x9.s25137.ui.reports.form.ReportForm
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportFlowInstrumentedTest {

    companion object {
        private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }

    @get:Rule
    val composeRule = createAndroidComposeRule<ComposeHiltTestActivity>()

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
                    onReportClick = { clickedIndex = it },
                    onDeleteReport = {}
                )
            }
        }
        composeRule.waitForIdle()

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
                ReportForm(onSubmit = { submittedReport = it })
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("createReport:submit").performClick()

        composeRule.onAllNodesWithText("This field is required.").assertCountEquals(5)
        assertNull(submittedReport)
    }

    @Test
    fun createReportValidSubmitEmitsReport() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selectedDate = 1_710_000_000_000L
        val submittedReport = mutableStateOf<Report?>(null)

        composeRule.setContent {
            AppTheme {
                ReportForm(
                    testInitialSelectedDateMillis = selectedDate,
                    onSubmit = { submittedReport.value = it }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("createReport:title").performTextInput("Road blocked")
        composeRule.onNodeWithTag("createReport:location").performTextInput("Copenhagen")
        composeRule.onNodeWithTag("createReport:description")
            .performTextInput("Two lanes blocked by a truck")

        composeRule.onNodeWithTag("createReport:type").performClick()
        composeRule.onNodeWithText(context.getString(Type.OTHER.nameResId)).performClick()

        composeRule.onNodeWithTag("createReport:submit").performClick()

        assertNotNull(submittedReport.value)
        assertEquals("Road blocked", submittedReport.value?.title)
        assertEquals("Copenhagen", submittedReport.value?.location)
        assertEquals(
            selectedDate / MILLIS_PER_DAY,
            (submittedReport.value?.timestamp ?: 0L) / MILLIS_PER_DAY
        )
        assertEquals(Type.OTHER, submittedReport.value?.type)
        assertEquals(Severity.MINOR, submittedReport.value?.severity)
        assertEquals("Two lanes blocked by a truck", submittedReport.value?.description)
    }

    @Test
    fun reportDetailsRendersAllCoreFields() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
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
                ReportDetailsPage(
                    report = report,
                    isEditable = false,
                    onEditButtonClick = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Large pothole").assertIsDisplayed()
        composeRule.onNodeWithText("Main St 42").assertIsDisplayed()
        composeRule.onNodeWithText("Dangerous pothole near bike lane").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(Type.ROAD_INCIDENTS.nameResId))
            .assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(Severity.MAJOR.nameResId)).assertIsDisplayed()
        composeRule.onAllNodesWithContentDescription("Edit report").assertCountEquals(0)
    }

    @Test
    fun reportDetailsEditableShowsEditFabAndCallbackIsTriggered() {
        val report = Report(
            key = "editable-1",
            title = "Editable",
            location = "Main St",
            timestamp = 1_710_000_000_000L,
            type = Type.OTHER,
            description = "desc",
            severity = Severity.MINOR
        )
        var editClicks = 0

        composeRule.setContent {
            AppTheme {
                ReportDetailsPage(
                    report = report,
                    isEditable = true,
                    onEditButtonClick = { editClicks += 1 }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Edit report").assertIsDisplayed().performClick()

        assertEquals(1, editClicks)
    }

    @Test
    fun createReportWhitespaceInputShowsRequiredErrors() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selectedDate = 1_710_000_000_000L
        var submittedReport: Report? = null

        composeRule.setContent {
            AppTheme {
                ReportForm(
                    testInitialSelectedDateMillis = selectedDate,
                    onSubmit = { submittedReport = it }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("createReport:title").performTextInput("   ")
        composeRule.onNodeWithTag("createReport:location").performTextInput("   ")
        composeRule.onNodeWithTag("createReport:description").performTextInput("   ")
        composeRule.onNodeWithTag("createReport:type").performClick()
        composeRule.onNodeWithText(context.getString(Type.OTHER.nameResId)).performClick()
        composeRule.onNodeWithTag("createReport:submit").performClick()

        composeRule.onAllNodesWithText("This field is required.").assertCountEquals(3)
        assertNull(submittedReport)
    }

    @Test
    fun createReportTypeValidationPreventsSubmissionWithoutType() {
        val selectedDate = 1_710_000_000_000L
        var submittedReport: Report? = null

        composeRule.setContent {
            AppTheme {
                ReportForm(
                    testInitialSelectedDateMillis = selectedDate,
                    onSubmit = { submittedReport = it }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("createReport:title").performTextInput("Title")
        composeRule.onNodeWithTag("createReport:location").performTextInput("Location")
        composeRule.onNodeWithTag("createReport:description").performTextInput("Description")
        composeRule.onNodeWithTag("createReport:submit").performClick()

        composeRule.onAllNodesWithText("This field is required.").assertCountEquals(1)
        assertNull(submittedReport)
    }

    @Test
    fun createReportSelectedSeverityIsUsedOnSubmit() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selectedDate = 1_710_000_000_000L
        val submittedReport = mutableStateOf<Report?>(null)

        composeRule.setContent {
            AppTheme {
                ReportForm(
                    testInitialSelectedDateMillis = selectedDate,
                    onSubmit = { submittedReport.value = it }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("createReport:title").performTextInput("Broken light")
        composeRule.onNodeWithTag("createReport:location").performTextInput("Crossing")
        composeRule.onNodeWithTag("createReport:description")
            .performTextInput("Traffic light is out")
        composeRule.onNodeWithTag("createReport:type").performClick()
        composeRule.onNodeWithText(context.getString(Type.OTHER.nameResId)).performClick()
        composeRule.onNodeWithText(context.getString(Severity.MAJOR.nameResId)).performClick()
        composeRule.onNodeWithTag("createReport:submit").performClick()

        assertNotNull(submittedReport.value)
        assertEquals(Severity.MAJOR, submittedReport.value?.severity)
    }

    @Test
    fun accountScreenShowsProfileDataAndHandlesLogout() {
        var logoutClicks = 0

        composeRule.setContent {
            AppTheme {
                LoggedInAccountScreen(
                    name = "Jane Doe",
                    email = "jane@example.com",
                    profilePictureUrl = null,
                    onLogout = { logoutClicks += 1 }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Jane Doe").assertIsDisplayed()
        composeRule.onNodeWithText("jane@example.com").assertIsDisplayed()
        composeRule.onNodeWithText("Log out").assertIsDisplayed().performClick()

        assertEquals(1, logoutClicks)
    }

    @Test
    fun loggedOutAccountScreenShowsMessageAndNavigatesToLogin() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var passedContext: Context? = null

        composeRule.setContent {
            AppTheme {
                LoggedOutAccountScreen(navigateToLoginScreen = { passedContext = it })
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText(context.getString(R.string.log_in_message)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.log_in)).assertIsDisplayed()
            .performClick()

        assertNotNull(passedContext)
        assertEquals(context.packageName, passedContext?.packageName)
    }
}
