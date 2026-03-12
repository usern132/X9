package dk.itu.moapd.x9.s25137.ui.reports.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.domain.models.toFormattedString
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import java.util.Date

@Composable
fun ReportDetailsPage(
    report: Report,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = report.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            ReportDetailsItem(
                label = stringResource(R.string.report_location),
                value = report.location
            )
            ReportDetailsItem(
                label = stringResource(R.string.report_date),
                value = report.date.toFormattedString()
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
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ReportDetailsPagePreview() {
    AppTheme {
        val sampleReport = Report(
            title = "Large pothole on Main St affecting cyclists",
            location = "Main St, 123",
            date = Date(),
            type = Type.OTHER,
            description = "Large pothole in the middle of the road. It has been there for several weeks and is causing issues for cyclists.",
            severity = Severity.MODERATE
        )
        ReportDetailsPage(report = sampleReport)
    }
}
