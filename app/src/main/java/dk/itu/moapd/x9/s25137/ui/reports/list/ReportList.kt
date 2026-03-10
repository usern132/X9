package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Report.Companion.generateRandomReports

@Composable
fun ReportList(
    modifier: Modifier = Modifier,
    reports: List<Report> = generateRandomReports()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        items(reports) { report ->
            ReportListItem(report = report)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListPreview() {
    ReportList()
}