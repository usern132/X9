package dk.itu.moapd.x9.s25137.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.data.repositories.UserPreferences
import dk.itu.moapd.x9.s25137.domain.models.Location
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
    val route: String, val labelRes: Int, val icon: ImageVector
) {
    HOME("home", R.string.home, Icons.Filled.Home),
    CALENDAR("calendar", R.string.calendar, Icons.Filled.CalendarMonth),
    ACCOUNT("account", R.string.account, Icons.Filled.AccountCircle),
}

data class MainActions(
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
    val fetchCurrentLocation: ((Location) -> Unit, () -> Unit) -> Unit,
    val onStartLocationTracking: () -> Unit,
    val onStopLocationTracking: () -> Unit,
    val setLocationTraceEnabled: (Boolean) -> Unit
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
        fetchCurrentLocation = { _, _ -> },
        onStartLocationTracking = {},
        onStopLocationTracking = {},
        setLocationTraceEnabled = {}
    )
}

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
    val preferences by preferencesViewModel.uiState.collectAsState()
    val actions = MainActions(
        onLogout = { mainViewModel.logOut() },
        onInsertReport = { mainViewModel.insertReport(it) },
        onEditReport = { mainViewModel.updateReport(it) },
        onDeleteReport = { mainViewModel.deleteReport(it) },
        isReportEditable = { mainViewModel.isReportEditable(it) },
        isReportDeletable = { mainViewModel.isReportDeletable(it) },
        showLoginAlertDialog = { mainViewModel.showLoginAlertDialog() },
        showLocationRequiredAlertDialog = { mainViewModel.showLocationRequiredAlertDialog() },
        showLocationErrorAlertDialog = { mainViewModel.showLocationErrorAlertDialog() },
        fetchCurrentLocation = { onSuccess, onError ->
            mainViewModel.getCurrentLocation(onSuccess, onError)
        },
        onStartLocationTracking = onStartLocationTracking,
        onStopLocationTracking = onStopLocationTracking,
        setLocationTraceEnabled = { preferencesViewModel.setLocationTraceEnabled(it) },
    )
    MainScaffoldContent(
        uiState = state,
        currentUser = state.currentUser,
        locationTrace = locationTrace,
        preferences = preferences,
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
    actions: MainActions
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    var hasLocationPermission by remember { mutableStateOf(false) }

    val globalLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            actions.onStartLocationTracking()
        } else {
            actions.showLocationRequiredAlertDialog()
            actions.setLocationTraceEnabled(false)
        }
    }

    // Using the showLocationTrace shared preference as a key, we can observe
    // any changes in the toggle and recompose accordingly.
    // The lifecycle key is required to re-check the permission when the user resumes the activity
    // after returning from the app's settings page where they may have enabled the location permission
    LaunchedEffect(preferences.showLocationTrace, lifecycleState) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        hasLocationPermission = granted

        if (preferences.showLocationTrace) {
            if (granted)
                actions.onStartLocationTracking()
            else {
                // Request location permission to the user
                globalLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            actions.onStopLocationTracking()
        }
    }

    fun isLoggedIn(): Boolean = currentUser != null

    fun navigateOrShowLoginAlertDialog(route: String) =
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
            uiState = MainUiState(reports = Report.previewReports),
            currentUser = user,
            locationTrace = emptyList(),
            preferences = UserPreferences(
                showLocationTrace = false
            ),
            actions = MainActions(),
        )
    }
}
