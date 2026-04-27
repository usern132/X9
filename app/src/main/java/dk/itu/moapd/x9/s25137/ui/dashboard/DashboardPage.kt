package dk.itu.moapd.x9.s25137.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList
import dk.itu.moapd.x9.s25137.ui.reports.map.ReportMap
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

private enum class DashboardElement(val testTag: String) {
    CREATE_REPORT_BUTTON("dashboard:createReport")
}

private enum class Destinations(
    val route: String, val labelRes: Int, val icon: ImageVector
) {
    LIST("list", R.string.list, Icons.AutoMirrored.Filled.List),
    MAP("map", R.string.map, Icons.Filled.Map),
}

@Composable
fun DashboardPage(
    modifier: Modifier = Modifier,
    reports: List<Report>,
    isFABEnabled: Boolean,
    onCreateReportClick: () -> Unit,
    onDeleteReport: (key: String) -> Unit,
    onReportClick: (String) -> Unit,
    isReportDeletable: (report: Report) -> Boolean = { false }
) {
    val navController = rememberNavController()
    val startDestination = Destinations.LIST
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateReportClick,
                containerColor = if (isFABEnabled) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isFABEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.testTag(DashboardElement.CREATE_REPORT_BUTTON.testTag),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Report")
            }
        }) { innerPadding ->
        Column {
            PrimaryTabRow(
                selectedTabIndex = selectedDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                Destinations.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = index == selectedDestination,
                        onClick = {
//                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = destination.icon, contentDescription = null)
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text(
                                    text = stringResource(destination.labelRes)
                                )
                            }
                        }
                    )
                }
            }
            when (selectedDestination) {
                Destinations.LIST.ordinal -> ReportList(
                    reports = reports,
                    onItemClick = onReportClick,
                    onDeleteReport = onDeleteReport,
                    isReportDeletable = isReportDeletable,
                    modifier = Modifier.padding(innerPadding)
                )

                Destinations.MAP.ordinal -> ReportMap()
            }
        }
    }
}

@Composable
private fun DashboardPagePreviewBase(isFABEnabled: Boolean) {
    AppTheme {
        DashboardPage(
            reports = Report.previewReports,
            isFABEnabled = isFABEnabled,
            onCreateReportClick = {},
            onReportClick = {},
            onDeleteReport = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPagePreviewFABEnabled() =
    DashboardPagePreviewBase(isFABEnabled = true)

@Preview(showBackground = true)
@Composable
fun DashboardPagePreviewFABDisabled() =
    DashboardPagePreviewBase(isFABEnabled = false)
