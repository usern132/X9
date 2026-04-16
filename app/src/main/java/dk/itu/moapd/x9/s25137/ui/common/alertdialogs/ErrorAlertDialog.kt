package dk.itu.moapd.x9.s25137.ui.common.alertdialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.s25137.R

@Composable
fun ErrorAlertDialog(errorMessage: String, dismiss: () -> Unit) = BaseAlertDialog(
    icon = {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(red = 200, green = 100, blue = 100)
        )
    },
    title = stringResource(R.string.error_occurred),
    text = errorMessage,
    dismiss = dismiss
)