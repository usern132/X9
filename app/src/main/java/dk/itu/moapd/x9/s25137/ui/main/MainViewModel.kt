package dk.itu.moapd.x9.s25137.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.s25137.data.repositories.AuthRepository
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "MainViewModel"
private const val ADD_FAKE_REPORTS = false

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val authRepository = AuthRepository()
    private var _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports
    val currentUser: User?
        get() = authRepository.currentUser

    init {
        if (ADD_FAKE_REPORTS) addFakeReports()
    }

    fun addFakeReports(n: Int = 100) {
        _reports.value = Report.generateRandomReports(n)
    }

    fun addReport(report: Report) {
        // Add a report at the top of the list
        _reports.update { currentReportsList ->
            listOf(report) + currentReportsList
        }
    }

    fun signOut() = authRepository.signOut()
}