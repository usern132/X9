package dk.itu.moapd.x9.s25137.ui.reports

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun CreateReportScreen(
    reportViewModel: ReportViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    CreateReportContent(
        onSubmit = { report ->
            reportViewModel.addReport(report)
            Toast.makeText(context, R.string.report_saved, Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportContent(
    onSubmit: (Report) -> Unit
) {
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<Type?>(null) }
    var selectedSeverity by remember { mutableStateOf(Severity.MINOR) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis
    val formattedDate = selectedDate?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    } ?: ""

    var expandedDropdown by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

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
            onValueChange = { title = it; titleError = false },
            label = { Text(stringResource(R.string.report_title)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DirectionsCar,
                    contentDescription = null
                )
            },
            isError = titleError,
            supportingText = { if (titleError) Text(requiredErrorMessage) },
            modifier = Modifier.fillMaxWidth()
        )

        // Location Input
        OutlinedTextField(
            value = location,
            onValueChange = { location = it; locationError = false },
            label = { Text(stringResource(R.string.report_location)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AddLocationAlt,
                    contentDescription = null
                )
            },
            isError = locationError,
            supportingText = { if (locationError) Text(requiredErrorMessage) },
            modifier = Modifier.fillMaxWidth()
        )

        // Date Input
        OutlinedTextField(
            value = formattedDate,
            onValueChange = { },
            label = { Text(stringResource(R.string.report_date)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null
                )
            },
            readOnly = true,
            isError = dateError,
            supportingText = { if (dateError) Text(requiredErrorMessage) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        dateError = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
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
                isError = typeError,
                supportingText = { if (typeError) Text(requiredErrorMessage) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false }
            ) {
                Type.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(stringResource(type.nameResId)) },
                        onClick = {
                            selectedType = type
                            expandedDropdown = false
                            typeError = false
                        }
                    )
                }
            }
        }

        // Description Input
        OutlinedTextField(
            value = description,
            onValueChange = { description = it; descriptionError = false },
            label = { Text(stringResource(R.string.report_description)) },
            isError = descriptionError,
            supportingText = { if (descriptionError) Text(requiredErrorMessage) },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = (severity == selectedSeverity),
                            onClick = { selectedSeverity = severity },
                            role = Role.RadioButton
                        )
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (severity == selectedSeverity),
                        onClick = null
                    )
                    Text(text = stringResource(labelResId), modifier = Modifier.padding(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom if screen is large

        // Submit Button
        Button(
            onClick = {
                titleError = title.isBlank()
                locationError = location.isBlank()
                dateError = (selectedDate == null)
                typeError = (selectedType == null)
                descriptionError = description.isBlank()

                val hasErrors = listOf(
                    titleError,
                    locationError,
                    dateError,
                    typeError,
                    descriptionError
                ).any { it } // check if any value is true (it == true)

                if (!hasErrors) {
                    val report = Report(
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
        ) {
            Text(stringResource(R.string.submit), modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateReportContentPreview() {
    AppTheme {
        CreateReportContent(
            onSubmit = {}
        )
    }
}
