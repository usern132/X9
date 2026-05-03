package dk.itu.moapd.x9.s25137.ui.reports.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.domain.models.toFormattedString
import dk.itu.moapd.x9.s25137.ui.common.ProfilePicture
import dk.itu.moapd.x9.s25137.ui.reports.components.ReportFormImage
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import java.util.Date

private const val VERTICAL_SPACING = 12

@Composable
fun ReportDetailsPage(
    report: Report,
    isEditable: Boolean,
    onEditButtonClick: () -> Unit,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            if (isEditable) {
                FloatingActionButton(onClick = { onEditButtonClick() }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit report")
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(VERTICAL_SPACING.dp)
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                AuthorDetails(report, modifier = Modifier.clickable(onClick = onAuthorClick))

                // This empty box adds VERTICAL_SPACING dp of spacing
                Box {}

                ReportDetailsItem(
                    label = stringResource(R.string.report_location),
                    value = "(${report.latitude}, ${report.longitude})\n${report.address}"
                )
                ReportDetailsItem(
                    label = stringResource(R.string.report_date),
                    value = Date(report.timestamp).toFormattedString(includeTime = true)
                )
                ReportDetailsItem(
                    label = stringResource(R.string.report_type),
                    value = stringResource(report.type.nameResId)
                )
                ReportDetailsItem(
                    label = stringResource(R.string.report_severity),
                    value = stringResource(report.severity.nameResId)
                )
                ReportDetailsItem(
                    label = stringResource(R.string.report_description),
                    value = report.description
                )

                Box {}

                if (report.remoteImageUri != null)
                    ReportFormImage(attachedImageUri = report.remoteImageUri.toUri())
            }
        }
    }
}

@Composable
private fun AuthorDetails(report: Report, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ProfilePicture(
            profilePictureUri = report.userImageUri,
            size = 30
        )
        Text(text = report.userName, style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
private fun ReportDetailsPagePreview(isEditable: Boolean) {
    AppTheme {
        val sampleReport = Report(
            title = "Large pothole on Main St affecting cyclists",
            latitude = 53.3498,
            longitude = -6.2603,
            address = "Main St, 123",
            timestamp = Date().time,
            type = Type.OTHER,
            description = "Large pothole in the middle of the road." +
                    " It has been there for several weeks and is causing issues for cyclists.",
            severity = Severity.MODERATE,
            userId = "123",
            userName = "John Doe",
            userImageUri = null
        )
        ReportDetailsPage(
            report = sampleReport,
            isEditable = isEditable,
            onAuthorClick = {},
            onEditButtonClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportDetailsPageEditablePreview() = ReportDetailsPagePreview(isEditable = true)

@Preview(showBackground = true)
@Composable
private fun ReportDetailsPageNotEditablePreview() = ReportDetailsPagePreview(isEditable = false)
