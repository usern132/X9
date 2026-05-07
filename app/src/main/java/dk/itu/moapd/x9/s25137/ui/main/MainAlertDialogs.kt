package dk.itu.moapd.x9.s25137.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.CameraPermissionAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.ErrorAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.LocationAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.LoginAlertDialog
import dk.itu.moapd.x9.s25137.ui.common.alertdialogs.NotificationAlertDialog

@Composable
fun MainAlertDialogs(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    uiState.errorMessage?.let { errorMessage ->
        ErrorAlertDialog(errorMessage, dismiss = { viewModel.errorConsumed() })
    }

    if (uiState.showLoginAlertDialog)
        LoginAlertDialog(dismiss = { viewModel.hideLoginAlertDialog() })

    if (uiState.locationRequiredAlertDialog != null)
        LocationAlertDialog(
            message = uiState.locationRequiredAlertDialog,
            onConfirm = {
                onNavigateToSettings()
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
            message = uiState.notificationRequiredAlertDialog,
            onConfirm = {
                onNavigateToSettings()
                viewModel.hideNotificationRequiredAlertDialog()
            },
            dismiss = { viewModel.hideNotificationRequiredAlertDialog() },
        )

    if (uiState.cameraRequiredAlertDialog != null) {
        CameraPermissionAlertDialog(
            onConfirm = {
                onNavigateToSettings()
                viewModel.hideCameraRequiredAlertDialog()
            },
            dismiss = { viewModel.hideCameraRequiredAlertDialog() },
            message = uiState.cameraRequiredAlertDialog
        )
    }
}