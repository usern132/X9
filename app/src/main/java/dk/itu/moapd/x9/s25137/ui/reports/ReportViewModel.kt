package dk.itu.moapd.x9.s25137.ui.reports

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Report.Companion.generateRandomReports
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
        _reports.value = generateRandomReports(n)
    }

    fun addReport(report: Report) {
        // Add a report at the top of the list
        _reports.update { currentReportsList ->
            listOf(report) + currentReportsList
        }
    }
}
