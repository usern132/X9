package dk.itu.moapd.x9.s25137.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.services.LocationService
import dk.itu.moapd.x9.s25137.services.MyFirebaseMessagingService
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.CameraPermissionAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.ErrorAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.LocationAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.LoginAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.NotificationAlertDialog
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

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

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private var locationService: LocationService? = null
    private var locationServiceBound: Boolean = false
    private var pendingStartTracking: Boolean = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            val service = binder.service
            locationService = service
            locationServiceBound = true

            if (pendingStartTracking) {
                service.subscribeToLocationUpdates()
                pendingStartTracking = false
            }
            locationService?.let { viewModel.observeLocationUpdates(it) }

        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            locationServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logFCMToken()
        MyFirebaseMessagingService.createNotificationChannels(this)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                val uiState by viewModel.uiState.collectAsState()

                MainScaffold(
                    uiState = viewModel.uiState,
                    mainViewModel = viewModel,
                    onStartLocationTracking = { startLocationService() },
                    onStopLocationTracking = { stopLocationService() },
                )

                uiState.errorMessage?.let { errorMessage ->
                    ErrorAlertDialog(errorMessage, dismiss = { viewModel.errorConsumed() })
                }

                if (uiState.showLoginAlertDialog)
                    LoginAlertDialog(dismiss = { viewModel.hideLoginAlertDialog() })

                if (uiState.locationRequiredAlertDialog != null)
                    LocationAlertDialog(
                        message = uiState.locationRequiredAlertDialog!!,
                        onConfirm = {
                            navigateToAppSystemSettings()
                            viewModel.hideLocationRequiredAlertDialog()
                        },
                        dismiss = { viewModel.hideLocationRequiredAlertDialog() }
                    )
                if (uiState.showLocationErrorAlertDialog)
                    ErrorAlertDialog(
                        errorMessage = stringResource(R.string.location_error_message),
                        dismiss = { viewModel.hideLocationErrorAlertDialog() },
                    )
                if (uiState.notificationRequiredAlertDialog != null)
                    NotificationAlertDialog(
                        message = uiState.notificationRequiredAlertDialog!!,
                        onConfirm = {
                            navigateToAppSystemSettings()
                            viewModel.hideNotificationRequiredAlertDialog()
                        },
                        dismiss = { viewModel.hideNotificationRequiredAlertDialog() },
                    )
                if (uiState.cameraRequiredAlertDialog != null) {
                    CameraPermissionAlertDialog(
                        onConfirm = {
                            navigateToAppSystemSettings()
                            viewModel.hideCameraRequiredAlertDialog()
                        },
                        dismiss = { viewModel.hideCameraRequiredAlertDialog() },
                        message = uiState.cameraRequiredAlertDialog!!
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, LocationService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        if (locationServiceBound)
            locationService?.subscribeToLocationUpdates()
        else
            pendingStartTracking = true
    }

    private fun stopLocationService() {
        locationService?.unsubscribeFromLocationUpdates()
        viewModel.clearLocationTrace()
        pendingStartTracking = false
        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)
    }

    override fun onStop() {
        if (locationServiceBound) {
            unbindService(serviceConnection)
            locationServiceBound = false
        }
        super.onStop()
    }
}

/** Navigate to the app's system settings, where the user can adjust the permissions. */
private fun MainActivity.navigateToAppSystemSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also { startActivity(it) }
}

private fun logFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result
        Log.d(TAG, "Current FCM token: $token")
    }
}