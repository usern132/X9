package dk.itu.moapd.x9.s25137.ui.account

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.ui.common.Action
import dk.itu.moapd.x9.s25137.ui.common.ProfilePicture
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

@Composable
fun LoggedInAccountScreen(
    name: String,
    email: String,
    profilePictureUrl: String?,
    onLogout: () -> Unit,
    onMyReportsClick: () -> Unit,
    onPreferencesClick: () -> Unit,
) {
    val actionListActions = setOf(
        Action(
            label = stringResource(R.string.my_reports),
            onClick = onMyReportsClick
        )
    )

    BaseAccountScreen(
        actionListActions = actionListActions,
        logInOrLogOutButton = {
            Button(onClick = onLogout) {
                Text(stringResource(R.string.log_out))
            }
        },
        onPreferencesClick = onPreferencesClick
    ) {
        ProfilePicture(profilePictureUrl, size = 120)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoggedInAccountScreenPreview() {
    AppTheme {
        LoggedInAccountScreen(
            onLogout = {},
            name = "John Doe",
            email = "john.doe@example.com",
            profilePictureUrl = null,
            onMyReportsClick = {},
            onPreferencesClick = {}
        )
    }
}
