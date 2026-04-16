package dk.itu.moapd.x9.s25137.ui.common.alertdialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.s25137.R

@Composable
fun LocationAlertDialog(onConfirm: () -> Unit, dismiss: () -> Unit) = BaseAlertDialog(
    icon = {
        Icon(
            imageVector = Icons.Default.LocationOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    },
    title = stringResource(R.string.location_permission_required_title),
    text = stringResource(R.string.location_permission_required_message),
    confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(text = stringResource(R.string.settings))
        }
    },
    dismiss = dismiss
)
