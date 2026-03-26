package dk.itu.moapd.x9.s25137.ui.reports.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.domain.models.Report.Companion.generateRandomReports
import dk.itu.moapd.x9.s25137.ui.main.MainUiState
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ReportList(
    uiState: StateFlow<MainUiState>,
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit = {}
) {
    val state by uiState.collectAsState()

    AppTheme {
        Surface(modifier = modifier.fillMaxWidth()) {
            LazyColumn {
                items(items = state.reports, key = { it.key!! }) { report ->
                    ReportListItem(
                        report = report,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(state.reports.indexOf(report)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportListPreview() {
    ReportList(
        uiState = MutableStateFlow(
            MainUiState(reports = generateRandomReports(20))
        )
    )
}
