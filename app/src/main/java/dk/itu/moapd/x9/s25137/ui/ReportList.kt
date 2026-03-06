package dk.itu.moapd.x9.s25137.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.Report

@Composable
fun TrafficReportList(
    trafficReports: List<Report>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(trafficReports) { report ->
            TrafficReportListItem(report = report)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrafficReportListPreview() {
    TrafficReportList(trafficReports = Report.generateRandomReports(10))
}