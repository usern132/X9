package dk.itu.moapd.x9.s25137.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.ui.main.MainUiState
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private enum class DashboardElements(val testTag: String) {
    CREATE_REPORT_BUTTON("dashboard:createReport")
}

@Composable
fun DashboardPage(
    uiState: StateFlow<MainUiState>,
    onCreateReportClick: () -> Unit,
    onReportClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateReportClick,
                modifier = Modifier
                    .testTag(DashboardElements.CREATE_REPORT_BUTTON.testTag),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Report")
            }
        }) { _ ->
        ReportList(
            uiState = uiState,
            onItemClick = onReportClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPagePreview() {
    AppTheme {
        DashboardPage(
            uiState = MutableStateFlow(
                MainUiState(reports = Report.generateRandomReports(20))
            ),
            onCreateReportClick = {},
            onReportClick = { _ -> },
        )
    }
}
