package dk.itu.moapd.x9.s25137.ui.dashboard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

private enum class DashboardElements(val testTag: String) {
    CREATE_REPORT_BUTTON("dashboard:createReport")
}

@Composable
fun DashboardPage(
    modifier: Modifier = Modifier,
    reports: List<Report>,
    onCreateReportClick: () -> Unit,
    onDeleteReport: (key: String) -> Unit,
    onReportClick: (Int) -> Unit,
    isReportDeletable: (report: Report) -> Boolean = { false }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateReportClick,
                modifier = Modifier
                    .testTag(DashboardElements.CREATE_REPORT_BUTTON.testTag),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Report")
            }
        }) { innerPadding ->
        ReportList(
            reports = reports,
            onItemClick = onReportClick,
            onDeleteReport = onDeleteReport,
            isReportDeletable = isReportDeletable,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPagePreview() {
    AppTheme {
        DashboardPage(
            reports = Report.generateRandomReports(20),
            onCreateReportClick = {},
            onReportClick = {},
            onDeleteReport = {}
        )
    }
}
