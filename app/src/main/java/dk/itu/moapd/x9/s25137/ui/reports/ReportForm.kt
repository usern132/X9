package dk.itu.moapd.x9.s25137.ui.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Traffic
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ReportField(val testTag: String) {
    TITLE("createReport:title"), LOCATION("createReport:location"), DATE("createReport:date"), TYPE(
        "createReport:type"
    ),
    DESCRIPTION("createReport:description"), SUBMIT("createReport:submit")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportForm(
    report: Report? = null,
    onSubmit: (Report) -> Unit,
    submitButtonText: String = stringResource(R.string.submit),
    testInitialSelectedDateMillis: Long? = null
) {
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf(report?.title ?: "") }
    var location by remember { mutableStateOf(report?.location ?: "") }
    var description by remember { mutableStateOf(report?.description ?: "") }
    var selectedType by remember { mutableStateOf(report?.type) }
    var selectedSeverity by remember { mutableStateOf(report?.severity ?: Severity.MINOR) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis =
                report?.timestamp ?: testInitialSelectedDateMillis
        )
    val selectedDate = datePickerState.selectedDateMillis
    val formattedDate = selectedDate?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    } ?: ""

    var expandedDropdown by remember { mutableStateOf(false) }

    val errors = remember { mutableStateMapOf<ReportField, Boolean>() }

    val requiredErrorMessage = stringResource(R.string.field_is_required)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
    ) {

        // Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; errors[ReportField.TITLE] = false },
            label = { Text(stringResource(R.string.report_title)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DirectionsCar, contentDescription = null
                )
            },
            isError = errors[ReportField.TITLE] ?: false,
            supportingText = { if (errors[ReportField.TITLE] == true) Text(requiredErrorMessage) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(ReportField.TITLE.testTag)
        )

        // Location Input
        OutlinedTextField(
            value = location,
            onValueChange = { location = it; errors[ReportField.LOCATION] = false },
            label = { Text(stringResource(R.string.report_location)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AddLocationAlt, contentDescription = null
                )
            },
            isError = errors[ReportField.LOCATION] ?: false,
            supportingText = { if (errors[ReportField.LOCATION] == true) Text(requiredErrorMessage) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(ReportField.LOCATION.testTag)
        )

        // Date Input
        OutlinedTextField(
            value = formattedDate,
            onValueChange = { },
            label = { Text(stringResource(R.string.report_date)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday, contentDescription = null
                )
            },
            readOnly = true,
            isError = errors[ReportField.DATE] ?: false,
            supportingText = { if (errors[ReportField.DATE] == true) Text(requiredErrorMessage) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .testTag(ReportField.DATE.testTag),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ))

        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    errors[ReportField.DATE] = false
                }) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }

        // Type Input (Exposed Dropdown)
        ExposedDropdownMenuBox(
            expanded = expandedDropdown,
            onExpandedChange = { expandedDropdown = !expandedDropdown },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedType?.let { stringResource(it.nameResId) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.report_type)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Traffic, contentDescription = null)
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                isError = errors[ReportField.TYPE] ?: false,
                supportingText = { if (errors[ReportField.TYPE] == true) Text(requiredErrorMessage) },
                modifier = Modifier
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable, true
                    )
                    .fillMaxWidth()
                    .testTag(ReportField.TYPE.testTag)
            )
            ExposedDropdownMenu(
                expanded = expandedDropdown, onDismissRequest = { expandedDropdown = false }) {
                Type.entries.forEach { type ->
                    DropdownMenuItem(text = { Text(stringResource(type.nameResId)) }, onClick = {
                        selectedType = type
                        expandedDropdown = false
                        errors[ReportField.TYPE] = false
                    })
                }
            }
        }

        // Description Input
        OutlinedTextField(
            value = description,
            onValueChange = { description = it; errors[ReportField.DESCRIPTION] = false },
            label = { Text(stringResource(R.string.report_description)) },
            isError = errors[ReportField.DESCRIPTION] ?: false,
            supportingText = {
                if (errors[ReportField.DESCRIPTION] == true) Text(
                    requiredErrorMessage
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .testTag(ReportField.DESCRIPTION.testTag),
            maxLines = 5
        )

        // Severity Radio Group
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val severities = listOf(
                Severity.MINOR to R.string.minor,
                Severity.MODERATE to R.string.moderate,
                Severity.MAJOR to R.string.major
            )

            severities.forEach { (severity, labelResId) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .selectable(
                            selected = (severity == selectedSeverity),
                            onClick = { selectedSeverity = severity },
                            role = Role.RadioButton
                        )
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (severity == selectedSeverity), onClick = null
                    )
                    Text(text = stringResource(labelResId), modifier = Modifier.padding(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom if screen is large

        // Submit Button
        Button(
            onClick = {
                errors[ReportField.TITLE] = title.isBlank()
                errors[ReportField.LOCATION] = location.isBlank()
                errors[ReportField.DATE] = (selectedDate == null)
                errors[ReportField.TYPE] = (selectedType == null)
                errors[ReportField.DESCRIPTION] = description.isBlank()

                val hasErrors = errors.values.any { it } // check if any value is true (it == true)

                if (!hasErrors) {
                    val isNewReport = report == null
                    val report =
                        if (isNewReport)
                            Report(
                                title = title,
                                location = location,
                                timestamp = selectedDate ?: Date().time,
                                type = selectedType ?: Type.OTHER,
                                description = description,
                                severity = selectedSeverity
                            )
                        else report.copy(
                            title = title,
                            location = location,
                            timestamp = selectedDate ?: Date().time,
                            type = selectedType ?: Type.OTHER,
                            description = description,
                            severity = selectedSeverity
                        )
                    onSubmit(report)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag(ReportField.SUBMIT.testTag)
        ) {
            Text(text = submitButtonText, modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportFormPreview() {
    AppTheme {
        ReportForm(
            onSubmit = {})
    }
}
