package dk.itu.moapd.x9.s25137.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.Report
import dk.itu.moapd.x9.s25137.Severity
import dk.itu.moapd.x9.s25137.Type
import java.util.Date

@Composable
fun TrafficReportListItem(
    report: Report,
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        Column(modifier = modifier) {
            Text(
                text = report.title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = report.location,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrafficReportListItemPreview() {
    val report = Report(
        title = "Broken car in road",
        location = "Barcelona",
        date = Date(),
        type = Type.BROKEN_VEHICLES,
        description = "A broken car is parked in the road",
        severity = Severity.MODERATE
    )
    TrafficReportListItem(report = report)
}