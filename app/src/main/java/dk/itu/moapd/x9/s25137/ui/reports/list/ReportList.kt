package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Report.Companion.generateRandomReports
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

@Composable
fun ReportList(
    modifier: Modifier = Modifier,
    reports: List<Report> = generateRandomReports()
) {
    AppTheme {
        Surface(modifier = modifier.fillMaxWidth()) {
            LazyColumn {
                items(reports) { report ->
                    ReportListItem(report = report)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListPreview() {
    ReportList()
}