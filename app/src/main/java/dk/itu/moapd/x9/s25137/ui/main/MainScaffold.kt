package dk.itu.moapd.x9.s25137.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.ui.reports.ReportViewModel
import dk.itu.moapd.x9.s25137.ui.reports.list.ReportList

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
    viewModel: ReportViewModel = viewModel()
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    Scaffold(
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
        NavHost(
            navController = navController,
            startDestination = destinations.first().route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(ANIM_DURATION))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(ANIM_DURATION))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(ANIM_DURATION))
            }
        ) {
            composable("home") {
                ReportList(reports = viewModel.reports)
            }
            composable("calendar") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Calendar Screen - Work in Progress")
                }
            }
            composable("account") {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Account Screen - Work in Progress")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MainScaffoldPreview() {
    MainScaffold()
}
