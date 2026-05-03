package dk.itu.moapd.x9.s25137.ui.reports.form

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity

enum class ReportField(val testTag: String) {
    TITLE("createReport:title"),
    TYPE("createReport:type"),
    DESCRIPTION("createReport:description"),
    SUBMIT("createReport:submit")
}

class ReportFormUiState(
    val report: Report? = null
) {
    var title by mutableStateOf(report?.title ?: "")

    var latitude: Double = report?.latitude ?: 0.0
    var longitude: Double = report?.longitude ?: 0.0
    var address: String = report?.address ?: ""

    var description by mutableStateOf(report?.description ?: "")
    var selectedType by mutableStateOf(report?.type)
    var selectedSeverity by mutableStateOf(report?.severity ?: Severity.MINOR)
    var attachedImageUri: Uri? by mutableStateOf(report?.remoteImageUri?.toUri())


    var expandedDropdown by mutableStateOf(false)

    val errors = mutableStateMapOf<ReportField, Boolean>()

}