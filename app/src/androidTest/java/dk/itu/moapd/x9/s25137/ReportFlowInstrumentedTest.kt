package dk.itu.moapd.x9.s25137

import android.view.View
import android.widget.TextView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.x9.s25137.ui.main.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportFlowInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun clickingCreateReportButtonNavigatesToCreateForm() {
        openCreateReportForm()
        onView(withId(R.id.submit_button)).check(matches(isDisplayed()))
    }

    @Test
    fun savingValidReportReturnsToListAndShowsNewReportAtTheTop() {
        val reportTitle = "UI test report ${System.currentTimeMillis()}"
        val reportLocation = "Copenhagen"
        val reportDate = "10/03/2026"
        val expectedSubtitle = "$reportDate · $reportLocation"

        openCreateReportForm()
        fillRequiredFields(reportTitle, reportLocation, reportDate)

        onView(withId(R.id.submit_button)).perform(click())

        onView(withId(R.id.create_report_button)).check(matches(isDisplayed()))
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(reportTitle).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithText(reportTitle)[0].assertIsDisplayed()
        composeRule.onAllNodesWithText(expectedSubtitle)[0].assertIsDisplayed()
    }

    @Test
    fun savingInvalidReportStaysOnFormAndDoesNotCreateReport() {
        val reportTitle = "Invalid report ${System.currentTimeMillis()}"

        openCreateReportForm()
        onView(withId(R.id.report_title_input)).perform(
            replaceText(reportTitle),
            closeSoftKeyboard()
        )

        onView(withId(R.id.submit_button)).perform(click())

        onView(withId(R.id.create_report_form)).check(matches(isDisplayed()))
        onView(withId(R.id.report_location_input_layout)).check(
            matches(hasTextInputLayoutErrorText(R.string.field_is_required))
        )

        onView(withId(R.id.create_report_button)).check(doesNotExist())
        composeRule.onNodeWithText(reportTitle).assertDoesNotExist()
    }

    private fun openCreateReportForm() {
        onView(withId(R.id.create_report_button)).perform(click())
        onView(withId(R.id.create_report_form)).check(matches(isDisplayed()))
    }

    private fun fillRequiredFields(reportTitle: String, location: String, date: String) {
        onView(withId(R.id.report_title_input)).perform(
            replaceText(reportTitle),
            closeSoftKeyboard()
        )
        onView(withId(R.id.report_location_input)).perform(
            replaceText(location),
            closeSoftKeyboard()
        )
        onView(withId(R.id.report_date_input)).perform(setText(date))
        onView(withId(R.id.report_type_input)).perform(setText("Other"), closeSoftKeyboard())
        onView(withId(R.id.report_description_input)).perform(
            replaceText("This is a test report!"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.moderate_button)).perform(click())
    }

    private fun setText(text: String): ViewAction = object : ViewAction {
        override fun getDescription(): String = "Set text on a TextView without requiring focus"

        override fun getConstraints(): Matcher<View> = isAssignableFrom(TextView::class.java)

        override fun perform(uiController: androidx.test.espresso.UiController, view: View) {
            (view as TextView).text = text
        }
    }

    private fun hasTextInputLayoutErrorText(errorTextResId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("TextInputLayout error should match expected string resource")
            }

            override fun matchesSafely(view: View): Boolean {
                if (view !is TextInputLayout) return false
                val expectedError = view.context.getString(errorTextResId)
                return view.error?.toString() == expectedError
            }
        }
    }
}