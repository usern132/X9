package dk.itu.moapd.x9.s25137.ui.main

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.domain.models.Location
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.User
import dk.itu.moapd.x9.s25137.ui.account.LoggedInAccountScreen
import dk.itu.moapd.x9.s25137.ui.account.LoggedOutAccountScreen
import dk.itu.moapd.x9.s25137.ui.auth.LoginActivity
import dk.itu.moapd.x9.s25137.ui.dashboard.DashboardPage
import dk.itu.moapd.x9.s25137.ui.reports.details.ReportDetailsPage
import dk.itu.moapd.x9.s25137.ui.reports.form.CreateReportForm
import dk.itu.moapd.x9.s25137.ui.reports.form.EditReportForm
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
import dk.itu.moapd.x9.s25137.ui.utils.PlaceholderScreen
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
    val route: String, val labelRes: Int, val icon: ImageVector
)

private val destinations = listOf(
    TopLevelDestination("home", R.string.home, Icons.Filled.Home),
    TopLevelDestination("calendar", R.string.calendar, Icons.Filled.CalendarMonth),
    TopLevelDestination("account", R.string.account, Icons.Filled.AccountCircle),
)

private data class Actions(
    val onLogout: () -> Unit,
    val onInsertReport: (Report) -> Unit,
    val onEditReport: (Report) -> Unit,
    val onDeleteReport: (String) -> Unit,
    val isReportEditable: (Report) -> Boolean,
    val isReportDeletable: (Report) -> Boolean,
    val modifier: Modifier = Modifier,
    val showLoginAlertDialog: () -> Unit,
    val showLocationRequiredAlertDialog: () -> Unit,
    val showLocationErrorAlertDialog: () -> Unit,
    val fetchCurrentLocation: ((Location) -> Unit, () -> Unit) -> Unit
) {
    // Dummy constructor used for the Compose preview
    constructor() : this(
        onLogout = {},
        onInsertReport = {},
        onEditReport = {},
        onDeleteReport = {},
        isReportEditable = { false },
        isReportDeletable = { false },
        showLoginAlertDialog = {},
        showLocationRequiredAlertDialog = {},
        showLocationErrorAlertDialog = {},
        fetchCurrentLocation = { _, _ -> }
    )
}

private const val ANIM_DURATION = 150

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    uiState: StateFlow<MainUiState>,
    viewModel: MainViewModel = viewModel(),
) {
    val state by uiState.collectAsState()
    val actions = Actions(
        onLogout = { viewModel.logOut() },
        onInsertReport = { viewModel.insertReport(it) },
        onEditReport = { viewModel.updateReport(it) },
        onDeleteReport = { viewModel.deleteReport(it) },
        isReportEditable = { viewModel.isReportEditable(it) },
        isReportDeletable = { viewModel.isReportDeletable(it) },
        showLoginAlertDialog = { viewModel.showLoginAlertDialog() },
        showLocationRequiredAlertDialog = { viewModel.showLocationRequiredAlertDialog() },
        showLocationErrorAlertDialog = { viewModel.showLocationErrorAlertDialog() },
        fetchCurrentLocation = { onSuccess, onError ->
            viewModel.getCurrentLocation(onSuccess, onError)
        }
    )
    MainScaffoldContent(
        uiState = state,
        currentUser = state.currentUser,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffoldContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    currentUser: User?,
    actions: Actions
) {
    val navController = rememberNavController()

    fun isLoggedIn(): Boolean = currentUser != null

    fun navigateOrShowLoginAlertDialog(route: String) =
        if (isLoggedIn()) navController.navigate(route)
        else actions.showLoginAlertDialog()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            actions.fetchCurrentLocation({ location ->
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "report_latitude", location.latitude
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "report_longitude", location.longitude
                )
                navController.navigate("create_report")
            }, { actions.showLocationErrorAlertDialog() })
        } else {
            actions.showLocationRequiredAlertDialog()
        }
    }

    fun onCreateReportClick() {
        // First, check if the user is logged in
        if (!isLoggedIn()) actions.showLoginAlertDialog()
        else {
            // Second, check if location permission is granted.
            // permissionLauncher will handle the conditional action for whether permission was granted
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    fun onReportClick(): (Int) -> Unit =
        { index -> navController.navigate("report_details/$index") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
        bottomBar = { BottomNavigationBar(currentDestination, navController) },
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
            popExitTransition = { exitTransition }) {
            composable("home") {
                DashboardPage(
                    reports = uiState.reports,
                    isFABEnabled = isLoggedIn(),
                    onCreateReportClick = { onCreateReportClick() },
                    onReportClick = onReportClick(),
                    isReportDeletable = actions.isReportDeletable,
                    onDeleteReport = { key -> actions.onDeleteReport(key) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("create_report") {
                val reportLatitude =
                    navController.previousBackStackEntry?.savedStateHandle?.get<Double>("report_latitude")
                val reportLongitude =
                    navController.previousBackStackEntry?.savedStateHandle?.get<Double>("report_longitude")

                if (reportLatitude != null && reportLongitude != null) {
                    val reportLocation = Location(reportLatitude, reportLongitude)
                    CreateReportForm(
                        location = reportLocation, onSubmit = { report ->
                            actions.onInsertReport(report)
                            navController.popBackStack()
                        })
                }
            }
            composable(
                "report_details/{reportIndex}",
                arguments = listOf(navArgument("reportIndex") { type = NavType.IntType })
            ) { backStackEntry ->
                val reportIndex = backStackEntry.arguments?.getInt("reportIndex") ?: 0
                if (reportIndex in uiState.reports.indices) {
                    val report = uiState.reports[reportIndex]
                    ReportDetailsPage(
                        report = report,
                        isEditable = actions.isReportEditable(report),
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
                if (reportIndex in uiState.reports.indices) {
                    val report = uiState.reports[reportIndex]
                    EditReportForm(
                        report = report, onSubmit = { report ->
                            actions.onEditReport(report)
                            navController.popBackStack()
                        })
                }
            }
            composable("my_reports") {
                ReportList(
                    reports = uiState.reports.filter { it.userId == currentUser?.uid!! },
                    isReportDeletable = actions.isReportDeletable,
                    onDeleteReport = actions.onDeleteReport,
                    onItemClick = onReportClick()
                )
            }
            composable("calendar") {
                PlaceholderScreen(name = "calendar")
            }
            composable("account") {
                if (isLoggedIn()) LoggedInAccountScreen(
                    onLogout = actions.onLogout,
                    name = currentUser!!.name ?: "",
                    email = currentUser.email ?: "",
                    profilePictureUrl = currentUser.photoUri?.toString(),
                    onMyReportsClick = { navController.navigate("my_reports") })
                else {
                    LoggedOutAccountScreen(
                        navigateToLoginScreen = { context ->
                            context.startActivity(
                                Intent(context, LoginActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                        })
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentDestination: NavDestination?, navController: NavHostController
) {
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
                label = { Text(text = stringResource(destination.labelRes)) })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScaffoldPreview() {
    val user = User(
        uid = "user123", name = "John Doe", email = "john@example.com"
    )
    AppTheme {
        MainScaffoldContent(
            uiState = MainUiState(reports = Report.generateRandomReports()),
            currentUser = user,
            actions = Actions()
        )
    }
}
