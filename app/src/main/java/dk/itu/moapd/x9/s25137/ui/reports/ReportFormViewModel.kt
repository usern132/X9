package dk.itu.moapd.x9.s25137.ui.reports

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Type
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReportFormViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ReportFormUiState())
    val uiState: StateFlow<ReportFormUiState> = _uiState

    fun initialize(report: Report?, testInitialSelectedDateMillis: Long? = null) {
        _uiState.value = ReportFormUiState(report, testInitialSelectedDateMillis)
    }

    fun validateFields(): Boolean {
        _uiState.value.errors[ReportField.TITLE] = _uiState.value.title.isBlank()
        _uiState.value.errors[ReportField.LOCATION] = _uiState.value.location.isBlank()
        _uiState.value.errors[ReportField.DATE] = (_uiState.value.selectedDate == null)
        _uiState.value.errors[ReportField.TYPE] = (_uiState.value.selectedType == null)
        _uiState.value.errors[ReportField.DESCRIPTION] = _uiState.value.description.isBlank()

        val hasErrors =
            _uiState.value.errors.values.any { it } // check if any value is true (it == true)
        return hasErrors
    }

    fun getFormReport(): Report {
        val isNewReport = _uiState.value.report == null
        val report =
            if (isNewReport)
                Report(
                    title = _uiState.value.title,
                    location = _uiState.value.location,
                    timestamp = _uiState.value.selectedDate?.time ?: Date().time,
                    type = _uiState.value.selectedType ?: Type.OTHER,
                    description = _uiState.value.description,
                    severity = _uiState.value.selectedSeverity
                )
            else _uiState.value.report!!.copy(
                title = _uiState.value.title,
                location = _uiState.value.location,
                timestamp = _uiState.value.selectedDate?.time ?: Date().time,
                type = _uiState.value.selectedType ?: Type.OTHER,
                description = _uiState.value.description,
                severity = _uiState.value.selectedSeverity
            )
        return report
    }
}
