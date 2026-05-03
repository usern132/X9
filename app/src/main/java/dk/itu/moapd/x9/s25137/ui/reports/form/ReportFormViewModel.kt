package dk.itu.moapd.x9.s25137.ui.reports.form

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Type
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReportFormViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
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
                timestamp = originalReport?.timestamp ?: Date().time,
                type = _uiState.value.selectedType ?: Type.OTHER,
                description = _uiState.value.description,
                severity = _uiState.value.selectedSeverity,
                localImageUri = _uiState.value.attachedImageUri?.toString(),
                remoteImageUri = originalReport?.remoteImageUri,
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

    fun createTempImageUri(): Uri {
        val tempFile = File.createTempFile(
            "report_image_",
            "${System.currentTimeMillis()}.jpg",
            context.cacheDir
        )
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
        return uri
    }
}
