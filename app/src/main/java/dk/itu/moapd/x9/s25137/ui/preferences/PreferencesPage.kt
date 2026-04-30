package dk.itu.moapd.x9.s25137.ui.preferences

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.data.repositories.UserPreferences
import dk.itu.moapd.x9.s25137.ui.common.Action
import dk.itu.moapd.x9.s25137.ui.common.ActionList

@Composable
fun PreferencesPage(
    uiState: UserPreferences,
    onShowLocationTraceChanged: (Boolean) -> Unit,
    actions: Set<Action> = preferencesActions(uiState, onShowLocationTraceChanged)
) {
    ActionList(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 32.dp),
        actions = actions
    )
}

@Composable
private fun preferencesActions(
    uiState: UserPreferences,
    onShowLocationTraceChanged: (Boolean) -> Unit
): Set<Action> =
    setOf(
        Action(
            label = stringResource(R.string.preferences_show_location_trace),
            onClick = { onShowLocationTraceChanged(!uiState.showLocationTrace) },
            trailingComposable = {
                Switch(
                    checked = uiState.showLocationTrace,
                    onCheckedChange = onShowLocationTraceChanged
                )
            }
        )
    )

@Preview(showBackground = true)
@Composable
fun PreferencesPagePreview() {
    PreferencesPage(
        uiState = UserPreferences(showLocationTrace = true),
        onShowLocationTraceChanged = {}
    )
}
