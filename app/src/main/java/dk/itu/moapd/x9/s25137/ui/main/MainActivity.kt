package dk.itu.moapd.x9.s25137.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.ErrorAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.LocationAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.LoginAlertDialog
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

private const val TAG = "MainActivity"

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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                val uiState by viewModel.uiState.collectAsState()

                MainScaffold(
                    uiState = viewModel.uiState,
                    viewModel = viewModel
                )

                uiState.errorMessage?.let { errorMessage ->
                    ErrorAlertDialog(errorMessage, dismiss = { viewModel.errorConsumed() })
                }

                if (uiState.showLoginAlertDialog)
                    LoginAlertDialog(dismiss = { viewModel.hideLoginAlertDialog() })

                if (uiState.showLocationRequiredAlertDialog)
                    LocationAlertDialog(
                        onConfirm = {
                            // Navigate to the app's system settings, where the user can adjust the permissions
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null)
                            ).also { startActivity(it) }
                            viewModel.hideLocationRequiredAlertDialog()
                        },
                        dismiss = { viewModel.hideLocationRequiredAlertDialog() }
                    )
            }
        }
    }
}