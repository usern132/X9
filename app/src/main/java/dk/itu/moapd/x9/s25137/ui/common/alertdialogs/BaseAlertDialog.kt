package dk.itu.moapd.x9.s25137.ui.common.alertdialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.s25137.R

@Composable
fun BaseAlertDialog(
    icon: @Composable () -> Unit, title: String, text: String, dismiss: () -> Unit
) = AlertDialog(
    icon = icon,
    title = { Text(text = title) },
    text = { Text(text = text) },
    onDismissRequest = dismiss,
    confirmButton = {},
    dismissButton = {
        TextButton(onClick = dismiss) {
            Text(
                text = stringResource(R.string.dismiss)
            )
        }
    })