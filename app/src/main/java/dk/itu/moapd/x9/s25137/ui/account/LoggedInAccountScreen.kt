package dk.itu.moapd.x9.s25137.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.ui.common.ProfilePicture
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

@Composable
fun LoggedInAccountScreen(
    name: String,
    email: String,
    profilePictureUrl: String?,
    onLogout: () -> Unit,
    onMyReportsClick: () -> Unit
) {
    val actionListActions = mapOf(
        R.string.my_reports to onMyReportsClick,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
            ActionList(modifier = Modifier.padding(vertical = 32.dp), actions = actionListActions)
            Button(onClick = onLogout) {
                Text(stringResource(R.string.log_out))
            }
        }
    }
}

@Composable
private fun ActionList(modifier: Modifier = Modifier, actions: Map<Int, () -> Unit> = emptyMap()) {
    Column(modifier = modifier) {
        HorizontalDivider()
        actions.forEach { action ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = action.value)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(action.key),
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider()
        }
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
            onMyReportsClick = {}
        )
    }
}
