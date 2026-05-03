package dk.itu.moapd.x9.s25137.ui.common.alertdialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.s25137.R

@Composable
fun CameraPermissionAlertDialog(
    onConfirm: () -> Unit,
    dismiss: () -> Unit,
    message: String
) {
    BaseAlertDialog(
        icon = { Icon(Icons.Default.PhotoCamera, contentDescription = null) },
        title = stringResource(R.string.camera_permission_required_title),
        text = message,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.settings))
            }
        },
        dismiss = dismiss
    )
}