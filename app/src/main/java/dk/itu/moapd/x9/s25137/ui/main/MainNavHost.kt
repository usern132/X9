package dk.itu.moapd.x9.s25137.ui.main

import android.Manifest
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.x9.s25137.data.repositories.UserPreferences
import dk.itu.moapd.x9.s25137.domain.models.Location
import dk.itu.moapd.x9.s25137.domain.models.User
import dk.itu.moapd.x9.s25137.ui.account.LoggedInAccountScreen
import dk.itu.moapd.x9.s25137.ui.account.LoggedOutAccountScreen
import dk.itu.moapd.x9.s25137.ui.auth.LoginActivity
import dk.itu.moapd.x9.s25137.ui.calendar.CalendarPage
import dk.itu.moapd.x9.s25137.ui.dashboard.DashboardPage
import dk.itu.moapd.x9.s25137.ui.preferences.PreferencesPage
import dk.itu.moapd.x9.s25137.ui.reports.details.ReportDetailsPage
import dk.itu.moapd.x9.s25137.ui.reports.form.CreateReportForm
import dk.itu.moapd.x9.s25137.ui.reports.form.EditReportForm
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList

private const val ANIM_DURATION = 150

@Composable
fun MainNavHost(
    navController: NavHostController,
    uiState: MainUiState,
    currentUser: User?,
    locationTrace: List<LatLng>,
    preferences: UserPreferences,
    innerPadding: PaddingValues,
    isLoggedIn: () -> Boolean,
    hasLocationPermission: Boolean,
    actions: MainActions,
) {
    val enterTransition: EnterTransition = fadeIn(animationSpec = tween(ANIM_DURATION))
    val exitTransition = fadeOut(animationSpec = tween(ANIM_DURATION))

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
        } else actions.showLocationRequiredAlertDialog()
    }

    return NavHost(
        navController = navController,
        startDestination = TopLevelDestinations.entries.first().route,
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
                isFABEnabled = isLoggedIn() && hasLocationPermission,
                onCreateReportClick = {
                    onCreateReportClick(
                        permissionLauncher = permissionLauncher,
                        isLoggedIn = isLoggedIn,
                        actions = actions
                    )
                },
                onReportClick = onReportClick(navController),
                isReportDeletable = actions.isReportDeletable,
                onDeleteReport = { key -> actions.onDeleteReport(key) },
                locationTrace = locationTrace,
                hasLocationPermission = hasLocationPermission,
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
            "report_details/{reportKey}",
            arguments = listOf(navArgument("reportKey") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportKey = backStackEntry.arguments?.getString("reportKey")
            val report = uiState.reports.find { it.key == reportKey }
            report?.let { report ->
                ReportDetailsPage(
                    report = report,
                    isEditable = actions.isReportEditable(report),
                    onEditButtonClick = { navController.navigate("edit_report/$reportKey") },
                    onAuthorClick = { navController.navigate("reports/${report.userId}") },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable(
            "reports/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userIdArg = it.arguments?.getString("userId")
            ReportList(
                reports = uiState.reports.filter { report -> report.userId == userIdArg },
                isReportDeletable = actions.isReportDeletable,
                onDeleteReport = actions.onDeleteReport,
                onItemClick = onReportClick(navController)
            )
        }
        composable(
            "edit_report/{reportKey}",
            arguments = listOf(navArgument("reportKey") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportKey = backStackEntry.arguments?.getString("reportKey")
            val report = uiState.reports.find { it.key == reportKey }
            report?.let { report ->
                EditReportForm(
                    report = report,
                    onSubmit = { report ->
                        actions.onEditReport(report)
                        navController.popBackStack()
                    }
                )
            }
        }
        composable("calendar") {
            CalendarPage(
                reports = uiState.reports,
                onDeleteReport = actions.onDeleteReport,
                isReportDeletable = actions.isReportDeletable,
                onItemClick = onReportClick(navController)
            )
        }
        composable("account") {
            if (isLoggedIn()) LoggedInAccountScreen(
                onLogout = actions.onLogout,
                name = currentUser!!.name ?: "",
                email = currentUser.email ?: "",
                profilePictureUrl = currentUser.photoUri?.toString(),
                onMyReportsClick = { navController.navigate("reports/${currentUser.uid}") },
                onPreferencesClick = { navController.navigate("preferences") })
            else {
                LoggedOutAccountScreen(
                    navigateToLoginScreen = { context ->
                        context.startActivity(
                            Intent(context, LoginActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                    },
                    onPreferencesClick = { navController.navigate("preferences") }
                )
            }
        }
        composable("preferences") {
            PreferencesPage(
                uiState = preferences,
                onShowLocationTraceChanged = { enabled ->
                    actions.setLocationTraceEnabled(
                        enabled
                    )
                }
            )
        }
    }
}

private fun onReportClick(
    navController: NavHostController
): (String) -> Unit =
    { key -> navController.navigate("report_details/$key") }

private fun onCreateReportClick(
    permissionLauncher: ActivityResultLauncher<String>,
    isLoggedIn: () -> Boolean,
    actions: MainActions
) {
    // First, check if the user is logged in
    if (!isLoggedIn()) actions.showLoginAlertDialog()
    else {
        // Second, check if location permission is granted.
        // permissionLauncher will handle the conditional action for whether permission was granted
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}