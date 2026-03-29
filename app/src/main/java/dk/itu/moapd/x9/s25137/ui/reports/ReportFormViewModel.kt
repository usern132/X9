package dk.itu.moapd.x9.s25137.ui.reports

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.itu.moapd.x9.s25137.domain.models.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReportFormViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ReportFormUiState())
    val uiState: StateFlow<ReportFormUiState> = _uiState

    fun setReport(report: Report?, testInitialSelectedDateMillis: Long? = null) {
        _uiState.value = ReportFormUiState(report, testInitialSelectedDateMillis)
    }
}
