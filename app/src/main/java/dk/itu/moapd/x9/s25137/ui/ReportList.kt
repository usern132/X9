package dk.itu.moapd.x9.s25137.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.Report
import dk.itu.moapd.x9.s25137.Report.Companion.generateRandomReports

@Composable
fun TrafficReportList(
    modifier: Modifier = Modifier,
    trafficReports: List<Report> = generateRandomReports()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        items(trafficReports) { report ->
            TrafficReportListItem(report = report)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrafficReportListPreview() {
    TrafficReportList()
}