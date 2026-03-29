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
import androidx.compose.material3.DatePickerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportForm(
    report: Report? = null,
    viewModel: ReportFormViewModel = hiltViewModel(),
    onSubmit: (Report) -> Unit,
    submitButtonText: String = stringResource(R.string.submit),
    testInitialSelectedDateMillis: Long? = null
) {
    LaunchedEffect(report) {
        viewModel.initialize(report, testInitialSelectedDateMillis)
    }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.selectedDate?.time
    )

    val formattedDate = uiState.selectedDate?.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    } ?: ""

    val requiredErrorMessage = stringResource(R.string.field_is_required)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
    ) {
        TitleInput(uiState, requiredErrorMessage)
        LocationInput(uiState, requiredErrorMessage)
        DateInput(formattedDate, uiState, requiredErrorMessage, datePickerState)
        TypeInput(uiState, requiredErrorMessage)
        DescriptionInput(uiState, requiredErrorMessage)
        SeverityInput(uiState)

        // Spacer to push the submit button to the bottom of the screen
        Spacer(modifier = Modifier.weight(1f))

        SubmitButton(viewModel, onSubmit, submitButtonText)
    }
}

@Composable
private fun SubmitButton(
    viewModel: ReportFormViewModel,
    onSubmit: (Report) -> Unit,
    submitButtonText: String
) {
    Button(
        onClick = {
            val hasErrors = viewModel.validateFields()
            if (!hasErrors) {
                val report = viewModel.getFormReport()
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

@Composable
private fun SeverityInput(uiState: ReportFormUiState) {
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
                        selected = (severity == uiState.selectedSeverity),
                        onClick = { uiState.selectedSeverity = severity },
                        role = Role.RadioButton
                    )
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (severity == uiState.selectedSeverity), onClick = null
                )
                Text(text = stringResource(labelResId), modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun DescriptionInput(
    uiState: ReportFormUiState,
    requiredErrorMessage: String
) {
    OutlinedTextField(
        value = uiState.description,
        onValueChange = {
            uiState.description = it; uiState.errors[ReportField.DESCRIPTION] = false
        },
        label = { Text(stringResource(R.string.report_description)) },
        isError = uiState.errors[ReportField.DESCRIPTION] ?: false,
        supportingText = {
            if (uiState.errors[ReportField.DESCRIPTION] == true) Text(
                requiredErrorMessage
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .testTag(ReportField.DESCRIPTION.testTag),
        maxLines = 5
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TypeInput(
    uiState: ReportFormUiState,
    requiredErrorMessage: String
) {
    ExposedDropdownMenuBox(
        expanded = uiState.expandedDropdown,
        onExpandedChange = { uiState.expandedDropdown = !uiState.expandedDropdown },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = uiState.selectedType?.let { stringResource(it.nameResId) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.report_type)) },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Traffic, contentDescription = null)
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedDropdown) },
            isError = uiState.errors[ReportField.TYPE] ?: false,
            supportingText = {
                if (uiState.errors[ReportField.TYPE] == true) Text(
                    requiredErrorMessage
                )
            },
            modifier = Modifier
                .menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryNotEditable, true
                )
                .fillMaxWidth()
                .testTag(ReportField.TYPE.testTag)
        )
        ExposedDropdownMenu(
            expanded = uiState.expandedDropdown,
            onDismissRequest = { uiState.expandedDropdown = false }) {
            Type.entries.forEach { type ->
                DropdownMenuItem(text = { Text(stringResource(type.nameResId)) }, onClick = {
                    uiState.selectedType = type
                    uiState.expandedDropdown = false
                    uiState.errors[ReportField.TYPE] = false
                })
            }
        }
    }
}

@Composable
private fun DateInput(
    formattedDate: String,
    uiState: ReportFormUiState,
    requiredErrorMessage: String,
    datePickerState: DatePickerState
) {
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
        isError = uiState.errors[ReportField.DATE] ?: false,
        supportingText = {
            if (uiState.errors[ReportField.DATE] == true) Text(
                requiredErrorMessage
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uiState.showDatePicker = true }
            .testTag(ReportField.DATE.testTag),
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ))

    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { uiState.showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    uiState.selectedDate = datePickerState.selectedDateMillis?.let { Date(it) }
                    uiState.showDatePicker = false
                    uiState.errors[ReportField.DATE] = false
                }) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = { uiState.showDatePicker = false }) {
                    Text("Cancel")
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun LocationInput(
    uiState: ReportFormUiState,
    requiredErrorMessage: String
) {
    OutlinedTextField(
        value = uiState.location,
        onValueChange = { uiState.location = it; uiState.errors[ReportField.LOCATION] = false },
        label = { Text(stringResource(R.string.report_location)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.AddLocationAlt, contentDescription = null
            )
        },
        isError = uiState.errors[ReportField.LOCATION] ?: false,
        supportingText = {
            if (uiState.errors[ReportField.LOCATION] == true) Text(
                requiredErrorMessage
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ReportField.LOCATION.testTag)
    )
}

@Composable
private fun TitleInput(
    uiState: ReportFormUiState,
    requiredErrorMessage: String
) {
    OutlinedTextField(
        value = uiState.title,
        onValueChange = { uiState.title = it; uiState.errors[ReportField.TITLE] = false },
        label = { Text(stringResource(R.string.report_title)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.DirectionsCar, contentDescription = null
            )
        },
        isError = uiState.errors[ReportField.TITLE] ?: false,
        supportingText = {
            if (uiState.errors[ReportField.TITLE] == true) Text(
                requiredErrorMessage
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ReportField.TITLE.testTag)
    )
}

@Preview(showBackground = true)
@Composable
fun ReportFormPreview() {
    AppTheme {
        ReportForm(onSubmit = {})
    }
}
