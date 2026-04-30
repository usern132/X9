package dk.itu.moapd.x9.s25137.ui.account

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.s25137.R

@Composable
fun LoggedOutAccountScreen(navigateToLoginScreen: (Context) -> Unit) {
    val context = LocalContext.current
    val actionListActions = mapOf(
        R.string.settings to { Unit }
    )
    BaseAccountScreen(
        actionListActions = actionListActions,
        logInOrLogOutButton = {
            Button(onClick = { navigateToLoginScreen(context) }) {
                Text(stringResource(R.string.log_in))
            }
        }
    ) {
        Text(
            text = stringResource(R.string.log_in_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoggedOutAccountScreenPreview() {
    LoggedOutAccountScreen(navigateToLoginScreen = {})
}
