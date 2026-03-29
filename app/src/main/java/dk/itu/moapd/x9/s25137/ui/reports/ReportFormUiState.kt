package dk.itu.moapd.x9.s25137.ui.reports

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import java.util.Date

enum class ReportField(val testTag: String) {
    TITLE("createReport:title"),
    LOCATION("createReport:location"),
    DATE("createReport:date"),
    TYPE("createReport:type"),
    DESCRIPTION("createReport:description"),
    SUBMIT("createReport:submit")
}

class ReportFormUiState(
    val report: Report? = null,
    private val testInitialSelectedDateMillis: Long? = null
) {
    var title by mutableStateOf(report?.title ?: "")
    var location by mutableStateOf(report?.location ?: "")
    var description by mutableStateOf(report?.description ?: "")
    var selectedType by mutableStateOf(report?.type)
    var selectedSeverity by mutableStateOf(report?.severity ?: Severity.MINOR)

    var showDatePicker by mutableStateOf(false)

    var expandedDropdown by mutableStateOf(false)

    val errors = mutableStateMapOf<ReportField, Boolean>()

    var selectedDate by mutableStateOf(
        report?.timestamp?.let { Date(it) } ?: testInitialSelectedDateMillis?.let { Date(it) })
}