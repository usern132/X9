package dk.itu.moapd.x9.s25137.ui.common.alertdialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.s25137.R

@Composable
fun LoginAlertDialog(dismiss: () -> Unit) = BaseAlertDialog(
    icon = {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    },
    title = stringResource(R.string.restricted_feature_title),
    text = stringResource(R.string.restricted_feature_message),
    dismiss = dismiss
)