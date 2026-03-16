package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.Report.Companion.generateRandomReports
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ReportList(
    modifier: Modifier = Modifier,
    reports: StateFlow<List<Report>>,
    onItemClick: (Int) -> Unit = {}
) {
    val reportList by reports.collectAsState()

    AppTheme {
        Surface(modifier = modifier.fillMaxWidth()) {
            LazyColumn {
                itemsIndexed(reportList) { index, report ->
                    ReportListItem(
                        report = report,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(index) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListPreview() {
    ReportList(reports = MutableStateFlow(generateRandomReports(20)))
}
