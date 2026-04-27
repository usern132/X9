package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.domain.models.Report

@Composable
fun ReportList(
    modifier: Modifier = Modifier,
    reports: List<Report>,
    onDeleteReport: (key: String) -> Unit,
    isReportDeletable: (report: Report) -> Boolean,
    onItemClick: (Int) -> Unit
) {
    Surface(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = reports, key = { it.key!! }) { report ->
                ReportListItem(
                    report = report,
                    onDelete = onDeleteReport,
                    isDeletable = isReportDeletable(report),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(reports.indexOf(report)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListPreview() {
    ReportList(
        reports = Report.previewReports,
        onDeleteReport = {},
        isReportDeletable = { false },
        onItemClick = {}
    )
}
