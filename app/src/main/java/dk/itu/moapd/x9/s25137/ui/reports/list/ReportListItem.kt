package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    onDelete: (key: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled && report.key != null)
            onDelete(report.key)
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {},
        content = { ReportListItemContent(modifier, report) }
    )
}

@Composable
private fun ReportListItemContent(
    modifier: Modifier,
    report: Report
) {
    Column(modifier = modifier.padding(12.dp)) {
        Text(
            text = report.title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${Date(report.timestamp).toFormattedString()} · ${report.location}",
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
        timestamp = Date().time,
        type = Type.BROKEN_VEHICLES,
        description = "A broken car is parked in the road",
        severity = Severity.MODERATE
    )
    ReportListItem(report = report, onDelete = {})
}