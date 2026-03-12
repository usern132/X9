package dk.itu.moapd.x9.s25137.ui.reports.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = report.title)
            Text(text = report.location)
            Text(text = report.date.toFormattedString())
            Text(text = stringResource(report.type.nameResId))
            Text(text = report.description)
            Text(text = stringResource(report.severity.nameResId))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportDetailsPagePreview() {
    AppTheme {
        val sampleReport = Report(
            title = "Pothole on Main St",
            location = "Main St, 123",
            date = Date(),
            type = Type.OTHER,
            description = "Large pothole in the middle of the road.",
            severity = Severity.MODERATE
        )
        ReportDetailsPage(report = sampleReport)
    }
}