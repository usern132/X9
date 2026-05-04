package dk.itu.moapd.x9.s25137.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.data.repositories.UserPreference
import dk.itu.moapd.x9.s25137.data.repositories.UserPreferences
import dk.itu.moapd.x9.s25137.domain.models.Report
import dk.itu.moapd.x9.s25137.domain.models.User
import dk.itu.moapd.x9.s25137.ui.preferences.PreferencesViewModel
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme
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

enum class TopLevelDestinations(
    val route: Route,
    @StringRes
    val labelRes: Int,
    val icon: ImageVector
) {
    HOME(Route.Home, R.string.home, Icons.Filled.Home),
    CALENDAR(Route.Calendar, R.string.calendar, Icons.Filled.CalendarMonth),
    ACCOUNT(Route.Account, R.string.account, Icons.Filled.AccountCircle),
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    uiState: StateFlow<MainUiState>,
    mainViewModel: MainViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel(),
    onStartLocationTracking: () -> Unit,
    onStopLocationTracking: () -> Unit
) {
    val state by uiState.collectAsState()
    val locationTrace by mainViewModel.locationTrace.collectAsState()
    val preferences by preferencesViewModel.preferencesFlow.collectAsState()
    val preferencesBeingUpdated by preferencesViewModel.preferencesBeingUpdated.collectAsState()

    val actions = MainActions(
        onLogout = { mainViewModel.logOut() },
        onInsertReport = { mainViewModel.insertReport(it) },
        onEditReport = { mainViewModel.updateReport(it) },
        onDeleteReport = { mainViewModel.deleteReport(it) },
        isReportEditable = { mainViewModel.isReportEditable(it) },
        isReportDeletable = { mainViewModel.isReportDeletable(it) },
        showLoginAlertDialog = { mainViewModel.showLoginAlertDialog() },
        showLocationRequiredAlertDialog = { mainViewModel.showLocationRequiredAlertDialog(it) },
        showLocationErrorAlertDialog = { mainViewModel.showLocationErrorAlertDialog() },
        showNotificationRequiredAlertDialog = { mainViewModel.showNotificationRequiredAlertDialog(it) },
        fetchCurrentLocation = { onSuccess, onError ->
            mainViewModel.getCurrentLocation(onSuccess, onError)
        },
        onStartLocationTracking = onStartLocationTracking,
        onStopLocationTracking = onStopLocationTracking,
        setPreference = { preference, enabled ->
            preferencesViewModel.setPreference(
                preference = preference,
                enabled = enabled
            )
        },
        showCameraRequiredAlertDialog = { mainViewModel.showCameraRequiredAlertDialog(it) }
    )
    MainScaffoldContent(
        uiState = state,
        currentUser = state.currentUser,
        locationTrace = locationTrace,
        preferences = preferences,
        preferencesBeingUpdated = preferencesBeingUpdated,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffoldContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    currentUser: User?,
    locationTrace: List<LatLng>,
    preferences: UserPreferences,
    preferencesBeingUpdated: Set<UserPreference>,
    actions: MainActions
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionRequiredForTracingMessage =
        stringResource(R.string.location_permission_required_for_tracing_message)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            actions.onStartLocationTracking()
        } else {
            actions.showLocationRequiredAlertDialog(locationPermissionRequiredForTracingMessage)
            actions.setPreference(UserPreference.SHOW_LOCATION_TRACE, false)
        }
    }

    val locationTracingNotificationPermissionAlertDialogMessage =
        stringResource(R.string.location_tracing_notification_permission_required_message)
    val locationTracingNotificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            actions.showNotificationRequiredAlertDialog(
                locationTracingNotificationPermissionAlertDialogMessage
            )
            actions.setPreference(UserPreference.SHOW_LOCATION_TRACE, false)
        }
    }

    // Using the showLocationTrace shared preference as a key, we can observe
    // any changes in the toggle and recompose accordingly.
    // The lifecycle key is required to re-check the permission when the user resumes the activity
    // after returning from the app's settings page where they may have enabled the location permission
    LaunchedEffect(preferences.showLocationTrace, lifecycleState) {
        val locationPermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        hasLocationPermission = locationPermissionGranted

        if (preferences.showLocationTrace) {
            if (!locationPermissionGranted)
            // Request location permission to the user
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationPermissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!notificationPermissionGranted) {
                    // Request notification permission to the user
                    locationTracingNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    actions.onStartLocationTracking()
                }
            }
            // If user enables location tracing but permissions are not granted,
            // the service won't be enabled
            else {
                actions.onStartLocationTracking()
            }
        } else {
            actions.onStopLocationTracking()
        }
    }

    val newReportsNotificationPermissionAlertDialogMessage =
        stringResource(R.string.new_reports_notification_permission_required_message)
    val newReportsNotificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            actions.showNotificationRequiredAlertDialog(
                newReportsNotificationPermissionAlertDialogMessage
            )
            actions.setPreference(UserPreference.RECEIVE_NOTIFICATIONS_FOR_NEW_REPORTS, false)
        }
    }

    LaunchedEffect(preferences.receiveNotificationsForNewReports, lifecycleState) {
        val notificationsPermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            else NotificationManagerCompat.from(context).areNotificationsEnabled()

        if (preferences.receiveNotificationsForNewReports) {
            if (!notificationsPermissionGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    newReportsNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                else actions.showNotificationRequiredAlertDialog(
                    newReportsNotificationPermissionAlertDialogMessage
                )
            } else {
                actions.setPreference(UserPreference.RECEIVE_NOTIFICATIONS_FOR_NEW_REPORTS, true)
            }
        }
    }

    fun isLoggedIn(): Boolean = currentUser != null

    fun navigateOrShowLoginAlertDialog(route: Any) =
        if (isLoggedIn()) navController.navigate(route)
        else actions.showLoginAlertDialog()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
            )
        },
        bottomBar = { BottomNavigationBar(currentDestination, navController) },
    ) { innerPadding ->
        MainNavHost(
            navController = navController,
            uiState = uiState,
            currentUser = currentUser,
            locationTrace = locationTrace,
            preferences = preferences,
            innerPadding = innerPadding,
            isLoggedIn = { isLoggedIn() },
            hasLocationPermission = hasLocationPermission,
            preferencesBeingUpdated = preferencesBeingUpdated,
            actions = actions
        )
    }
}

@Composable
private fun BottomNavigationBar(
    currentDestination: NavDestination?, navController: NavHostController
) {
    NavigationBar {
        TopLevelDestinations.entries.forEach { destination ->
            val selected =
                currentDestination?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
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
            uiState = MainUiState(reports = Report.previewReports),
            currentUser = user,
            locationTrace = emptyList(),
            preferences = UserPreferences(),
            preferencesBeingUpdated = emptySet(),
            actions = MainActions(),
        )
    }
}
