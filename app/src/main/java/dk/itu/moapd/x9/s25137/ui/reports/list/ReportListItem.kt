package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Severity
import dk.itu.moapd.x9.s25137.domain.models.Type
import dk.itu.moapd.x9.s25137.domain.models.toFormattedString
import java.util.Date

@Composable
fun ReportListItem(
    report: Report,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(12.dp)) {
        Text(
            text = report.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${report.date.toFormattedString()} · ${report.location}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListItemPreview() {
    val report = Report(
        title = "Broken car in road",
        location = "Barcelona",
        date = Date(),
        type = Type.BROKEN_VEHICLES,
        description = "A broken car is parked in the road",
        severity = Severity.MODERATE
    )
    ReportListItem(report = report)
}