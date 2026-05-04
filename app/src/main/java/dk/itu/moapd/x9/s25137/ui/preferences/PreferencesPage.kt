package dk.itu.moapd.x9.s25137.ui.preferences

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.data.repositories.UserPreference
import dk.itu.moapd.x9.s25137.data.repositories.UserPreferences
import dk.itu.moapd.x9.s25137.ui.common.Action
import dk.itu.moapd.x9.s25137.ui.common.ActionList

@Composable
fun PreferencesPage(
    uiState: UserPreferences,
    onPreferenceChanged: (UserPreference, Boolean) -> Unit,
    actions: Set<Action> = preferencesActions(
        uiState,
        onPreferenceChanged
    )
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
    onPreferenceChanged: (UserPreference, Boolean) -> Unit
): Set<Action> =
    setOf(
        ToggleAction(
            labelResId = R.string.preferences_show_location_trace,
            preference = UserPreference.SHOW_LOCATION_TRACE,
            onPreferenceChanged = onPreferenceChanged,
            isChecked = uiState.showLocationTrace
        ),
        ToggleAction(
            labelResId = R.string.preferences_new_report_notifications,
            preference = UserPreference.RECEIVE_NOTIFICATIONS_FOR_NEW_REPORTS,
            onPreferenceChanged = onPreferenceChanged,
            isChecked = uiState.receiveNotificationsForNewReports
        )
    )

@Composable
private fun ToggleAction(
    @StringRes
    labelResId: Int,
    preference: UserPreference,
    onPreferenceChanged: (UserPreference, Boolean) -> Unit,
    isChecked: Boolean
): Action = Action(
    label = stringResource(labelResId),
    onClick = { onPreferenceChanged(preference, !isChecked) },
    trailingComposable = {
        Switch(
            checked = isChecked,
            onCheckedChange = { onPreferenceChanged(preference, it) }
        )
    }
)

@Preview(showBackground = true)
@Composable
fun PreferencesPagePreview() {
    PreferencesPage(
        uiState = UserPreferences(),
        onPreferenceChanged = { _, _ -> }
    )
}
