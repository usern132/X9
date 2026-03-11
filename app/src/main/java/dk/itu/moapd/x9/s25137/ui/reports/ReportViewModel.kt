package dk.itu.moapd.x9.s25137.ui.reports

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import io.bloco.faker.Faker

private const val TAG = "ReportViewModel"
private const val addFakeReports = true

class MainActivityViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val reports = mutableStateListOf<Report>()

    init {
        if (addFakeReports) addFakeReports()
    }

    fun addFakeReports() {
        val faker = Faker()

        for (i in 1..100) {
            val report = Report(
                title = faker.lorem.sentence(wordCount = 3),
                location = faker.address.streetAddress(),
                date = faker.date.backward(),
                type = Type.entries.random(),
                description = faker.lorem.paragraphs(1).toString(),
                severity = Severity.entries.random()
            )
            reports.add(report)
        }
        Log.d(TAG, reports.toString())
    }

    fun addReport(report: Report) {
        // Add a report at the top of the list
        reports.add(0, report)
    }
}