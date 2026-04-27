package dk.itu.moapd.x9.s25137.ui.reports.form

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

    val formReport: Report
        get() {
            val originalReport = _uiState.value.report
            return Report(
                key = originalReport?.key,
                title = _uiState.value.title,
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude,
                address = _uiState.value.address,
                timestamp = originalReport?.timestamp ?: Date().time,
                type = _uiState.value.selectedType ?: Type.OTHER,
                description = _uiState.value.description,
                severity = _uiState.value.selectedSeverity,
                userId = originalReport?.userId ?: "",
                userName = originalReport?.userName ?: "",
                userImageUri = originalReport?.userImageUri
            )
        }

    fun initialize(report: Report?) {
        _uiState.value = ReportFormUiState(report)
    }

    fun validateFields(): Boolean {
        _uiState.value.errors[ReportField.TITLE] = _uiState.value.title.isBlank()
        _uiState.value.errors[ReportField.TYPE] = (_uiState.value.selectedType == null)
        _uiState.value.errors[ReportField.DESCRIPTION] = _uiState.value.description.isBlank()

        val hasErrors =
            _uiState.value.errors.values.any { it } // check if any value is true (it == true)
        return hasErrors
    }
}
