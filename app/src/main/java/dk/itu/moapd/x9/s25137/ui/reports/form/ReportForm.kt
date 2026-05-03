package dk.itu.moapd.x9.s25137.ui.reports.form

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Traffic
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import dk.itu.moapd.x9.s25137.domain.models.Location
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme


@Composable
fun EditReportForm(
    report: Report,
    viewModel: ReportFormViewModel = hiltViewModel(),
    onSubmit: (Report) -> Unit,
    submitButtonText: String = stringResource(R.string.submit)
) = ReportForm(
    location = Location(latitude = report.latitude, longitude = report.longitude),
    report = report,
    viewModel = viewModel,
    onSubmit = onSubmit,
    submitButtonText = submitButtonText,
)

@Composable
fun CreateReportForm(
    location: Location,
    viewModel: ReportFormViewModel = hiltViewModel(),
    onSubmit: (Report) -> Unit,
    submitButtonText: String = stringResource(R.string.submit),
    onCameraPermissionDenied: () -> Unit
) = ReportForm(
    location = location,
    viewModel = viewModel,
    onSubmit = onSubmit,
    submitButtonText = submitButtonText,
    showImageAttachmentButtonsRow = true,
    onCameraPermissionDenied = onCameraPermissionDenied
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportForm(
    location: Location,
    report: Report? = null,
    viewModel: ReportFormViewModel = hiltViewModel(),
    onSubmit: (Report) -> Unit,
    submitButtonText: String = stringResource(R.string.submit),
    showImageAttachmentButtonsRow: Boolean = false,
    onCameraPermissionDenied: () -> Unit = { }
) {
    LaunchedEffect(report) {
        viewModel.initialize(report)
    }

    val uiState by viewModel.uiState.collectAsState()
    uiState.latitude = location.latitude
    uiState.longitude = location.longitude

    val scrollState = rememberScrollState()

    val requiredErrorMessage = stringResource(R.string.field_is_required)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
    ) {
        TitleInput(uiState, requiredErrorMessage)
        TypeInput(uiState, requiredErrorMessage)
        DescriptionInput(uiState, requiredErrorMessage)
        SeverityInput(uiState)
        Spacer(modifier = Modifier.height(16.dp))
        if (showImageAttachmentButtonsRow) ImageAttachmentButtonsRow(
            uiState = uiState,
            createTempUri = { viewModel.createTempImageUri() },
            onCameraPermissionDenied = onCameraPermissionDenied
        )

        // Spacer to push the submit button to the bottom of the screen
        Spacer(modifier = Modifier.weight(1f))

        LocationDisclaimer()
        SubmitButton(
            modifier = Modifier.padding(top = 4.dp),
            viewModel = viewModel,
            onSubmit = onSubmit,
            submitButtonText = submitButtonText
        )
    }
}

@Composable
private fun ImageAttachmentButtonsRow(
    uiState: ReportFormUiState,
    createTempUri: () -> Uri,
    onCameraPermissionDenied: () -> Unit
) {

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uiState.attachedImageUri = uri
        }

    val tempUri = createTempUri()
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { wasSaved ->
            if (wasSaved)
                uiState.attachedImageUri = tempUri
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                cameraLauncher.launch(tempUri)
            } else {
                onCameraPermissionDenied()
            }
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Icon(imageVector = Icons.Outlined.AttachFile, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.attach_image))
        }
        Button(
            onClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        ) {
            Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.capture_image))
        }
    }
}

@Composable
private fun LocationDisclaimer() = Text(
    stringResource(R.string.report_location_disclaimer),
    style = MaterialTheme.typography.bodySmall
)

@Composable
private fun SubmitButton(
    modifier: Modifier = Modifier,
    viewModel: ReportFormViewModel,
    onSubmit: (Report) -> Unit,
    submitButtonText: String
) {
    Button(
        onClick = {
            val hasErrors = viewModel.validateFields()
            if (!hasErrors) {
                val report = viewModel.formReport
                onSubmit(report)
            }
        },
        modifier = modifier
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
fun CreateReportFormPreview() {
    AppTheme {
        CreateReportForm(
            location = Location(latitude = 0.0, longitude = 0.0),
            onSubmit = {},
            onCameraPermissionDenied = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditReportFormPreview() {
    AppTheme {
        EditReportForm(
            report = Report(
                title = "Important speed camera on highway",
                latitude = 0.0,
                longitude = 0.0,
                address = "Address",
                timestamp = 0L,
                type = Type.SPEED_CAMERA,
                description = "There is a speed camera on the highway.",
                severity = Severity.MODERATE
            ),
            onSubmit = {}
        )
    }
}
