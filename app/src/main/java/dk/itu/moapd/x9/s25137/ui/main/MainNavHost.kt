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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.x9.s25137.R
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
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object Home : Route

    @Serializable
    object Calendar : Route

    @Serializable
    object Account : Route

    @Serializable
    data class CreateReport(val latitude: Double, val longitude: Double) : Route

    @Serializable
    data class ReportDetails(val reportKey: String) : Route

    @Serializable
    data class UserReports(val userId: String) : Route

    @Serializable
    data class EditReport(val reportKey: String) : Route

    @Serializable
    object Preferences : Route
}

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

    val locationRequiredAlertDialogMessage =
        stringResource(R.string.location_permission_required_message)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            actions.fetchCurrentLocation({ location ->
                navController.navigate(Route.CreateReport(location.latitude, location.longitude))
            }, { actions.showLocationErrorAlertDialog() })
        } else {
            actions.showLocationRequiredAlertDialog(locationRequiredAlertDialogMessage)
        }
    }

    return NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { enterTransition },
        popExitTransition = { exitTransition }) {
        composable<Route.Home> {
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
        composable<Route.CreateReport> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.CreateReport>()
            val reportLocation = Location(route.latitude, route.longitude)
            val cameraPermissionRequiredAlertDialogMessage =
                stringResource(R.string.camera_permission_required_message)
            CreateReportForm(
                location = reportLocation,
                onSubmit = { report ->
                    actions.onInsertReport(report)
                    navController.popBackStack()
                },
                onCameraPermissionDenied = {
                    actions.showCameraRequiredAlertDialog(
                        cameraPermissionRequiredAlertDialogMessage
                    )
                },
            )
        }
        composable<Route.ReportDetails> { backStackEntry ->
            val reportKey = backStackEntry.toRoute<Route.ReportDetails>().reportKey
            val report = uiState.reports.find { it.key == reportKey }
            report?.let { report ->
                ReportDetailsPage(
                    report = report,
                    isEditable = actions.isReportEditable(report),
                    onEditButtonClick = { navController.navigate(Route.EditReport(reportKey)) },
                    onAuthorClick = { navController.navigate(Route.UserReports(report.userId)) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable<Route.UserReports> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.UserReports>()
            ReportList(
                reports = uiState.reports.filter { report -> report.userId == route.userId },
                isReportDeletable = actions.isReportDeletable,
                onDeleteReport = actions.onDeleteReport,
                onItemClick = onReportClick(navController)
            )
        }
        composable<Route.EditReport> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.EditReport>()
            val report = uiState.reports.find { it.key == route.reportKey }
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
        composable<Route.Calendar> {
            CalendarPage(
                reports = uiState.reports,
                onDeleteReport = actions.onDeleteReport,
                isReportDeletable = actions.isReportDeletable,
                onItemClick = onReportClick(navController)
            )
        }
        composable<Route.Account> {
            if (isLoggedIn()) LoggedInAccountScreen(
                onLogout = actions.onLogout,
                name = currentUser!!.name ?: "",
                email = currentUser.email ?: "",
                profilePictureUrl = currentUser.photoUri?.toString(),
                onMyReportsClick = { navController.navigate(Route.UserReports(currentUser.uid)) },
                onPreferencesClick = { navController.navigate(Route.Preferences) })
            else {
                LoggedOutAccountScreen(
                    navigateToLoginScreen = { context ->
                        context.startActivity(
                            Intent(context, LoginActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                    },
                    onPreferencesClick = { navController.navigate(Route.Preferences) }
                )
            }
        }
        composable<Route.Preferences> {
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
    { key -> navController.navigate(Route.ReportDetails(key)) }

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
