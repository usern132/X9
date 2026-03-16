package dk.itu.moapd.x9.s25137.ui.reports

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import io.bloco.faker.Faker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "ReportViewModel"
private const val addFakeReports = false

class ReportViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private var _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    init {
        if (addFakeReports) addFakeReports()
    }

    fun addFakeReports(n: Int = 100) {
        val faker = Faker()
        val fakeReports = List(n) {
            Report(
                title = faker.lorem.sentence(wordCount = 3),
                location = faker.address.streetAddress(),
                date = faker.date.backward(),
                type = Type.entries.random(),
                description = faker.lorem.paragraphs(1).toString(),
                severity = Severity.entries.random()
            )
        }
        _reports.value = fakeReports
    }

    fun addReport(report: Report) {
        // Add a report at the top of the list
        _reports.update { currentReportsList ->
            listOf(report) + currentReportsList
        }
    }
}
