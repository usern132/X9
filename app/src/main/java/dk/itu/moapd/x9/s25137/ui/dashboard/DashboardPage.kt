package dk.itu.moapd.x9.s25137.ui.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.ui.main.MainUiState
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private enum class DashboardTags(val tag: String) {
    CREATE_REPORT("dashboard:createReport")
}

@Composable
fun DashboardPage(
    uiState: StateFlow<MainUiState>,
    onCreateReportClick: () -> Unit,
    onReportClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        ReportList(
            uiState = uiState,
            onItemClick = onReportClick
        )

        FloatingActionButton(
            onClick = onCreateReportClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag(DashboardTags.CREATE_REPORT.tag),
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Report")
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DashboardPagePreview() {
    AppTheme {
        DashboardPage(
            uiState = MutableStateFlow(MainUiState()),
            onCreateReportClick = {},
            onReportClick = {},
        )
    }
}
