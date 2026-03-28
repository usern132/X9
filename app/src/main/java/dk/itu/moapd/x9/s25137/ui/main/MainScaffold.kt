package dk.itu.moapd.x9.s25137.ui.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.User
import dk.itu.moapd.x9.s25137.ui.account.AccountScreen
import dk.itu.moapd.x9.s25137.ui.dashboard.DashboardPage
import dk.itu.moapd.x9.s25137.ui.reports.ReportForm
import dk.itu.moapd.x9.s25137.ui.reports.details.ReportDetailsPage
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import dk.itu.moapd.x9.s25137.ui.utils.PlaceholderScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/* Code adapted from the MOAPD 2026 subject repository, found at https://github.com/fabricionarcizo/moapd2026/.
 * Its original license is attached below.

 * MIT License
 *
 * Copyright (c) 2026 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

private data class TopLevelDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
)

private val destinations = listOf(
    TopLevelDestination("home", R.string.home, Icons.Filled.Home),
    TopLevelDestination("calendar", R.string.calendar, Icons.Filled.CalendarMonth),
    TopLevelDestination("account", R.string.account, Icons.Filled.AccountCircle),
)


private const val ANIM_DURATION = 150

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    uiState: StateFlow<MainUiState>,
    viewModel: MainViewModel = viewModel(),
    onLogout: () -> Unit
) {
    MainScaffoldContent(
        uiState = uiState,
        currentUser = viewModel.currentUser,
        onLogout = onLogout,
        onInsertReport = { viewModel.insertReport(it) },
        onEditReport = { viewModel.updateReport(it) },
        onDeleteReport = { viewModel.deleteReport(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffoldContent(
    uiState: StateFlow<MainUiState>,
    currentUser: User?,
    onLogout: () -> Unit,
    onInsertReport: (Report) -> Unit,
    onEditReport: (Report) -> Unit,
    onDeleteReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(text = stringResource(destination.labelRes)) }
                    )
                }
            }
        },
    ) { innerPadding ->
        val enterTransition: EnterTransition = fadeIn(animationSpec = tween(ANIM_DURATION))
        val exitTransition = fadeOut(animationSpec = tween(ANIM_DURATION))
        NavHost(
            navController = navController,
            startDestination = destinations.first().route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { enterTransition },
            popExitTransition = { exitTransition }
        ) {
            composable("home") {
                DashboardPage(
                    uiState = uiState,
                    onCreateReportClick = { navController.navigate("create_report") },
                    onReportClick = { index -> navController.navigate("report_details/$index") },
                    onDeleteReport = { key -> onDeleteReport(key) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("create_report") {
                ReportForm(
                    onSubmit = { report ->
                        onInsertReport(report)
                        navController.popBackStack()
                    }
                )
            }
            composable(
                "report_details/{reportIndex}",
                arguments = listOf(navArgument("reportIndex") { type = NavType.IntType })
            ) { backStackEntry ->
                val reportIndex = backStackEntry.arguments?.getInt("reportIndex") ?: 0
                val state by uiState.collectAsState()
                if (reportIndex in state.reports.indices) {
                    val report = state.reports[reportIndex]
                    // A logged in user can only edit their own reports
                    val isEditable = report.userId == currentUser?.uid
                    ReportDetailsPage(
                        report = report,
                        isEditable = isEditable,
                        onEditButtonClick = { navController.navigate("edit_report/$reportIndex") },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            composable(
                "edit_report/{reportIndex}",
                arguments = listOf(navArgument("reportIndex") { type = NavType.IntType })
            ) { backStackEntry ->
                val reportIndex = backStackEntry.arguments?.getInt("reportIndex") ?: 0
                val state by uiState.collectAsState()
                if (reportIndex in state.reports.indices) {
                    val report = state.reports[reportIndex]
                    ReportForm(
                        report = report,
                        onSubmit = { report ->
                            onEditReport(report)
                            navController.popBackStack()
                        }
                    )
                }
            }
            composable("calendar") {
                PlaceholderScreen(name = "calendar")
            }
            composable("account") {
                AccountScreen(
                    onLogout = onLogout,
                    name = currentUser?.name ?: "",
                    email = currentUser?.email ?: "",
                    profilePictureUrl = currentUser?.photoUri?.toString()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScaffoldPreview() {
    val user = User(
        uid = "user123",
        name = "John Doe",
        email = "john@example.com"
    )
    val reports = Report.generateRandomReports(20).mapIndexed { index, report ->
        if (index == 0) report.copy(userId = "user123") else report
    }
    val uiState = MutableStateFlow(MainUiState(userId = "user123", reports = reports))

    AppTheme {
        MainScaffoldContent(
            uiState = uiState,
            currentUser = user,
            onLogout = {},
            onInsertReport = {},
            onEditReport = {},
            onDeleteReport = {}
        )
    }
}
