package dk.itu.moapd.x9.s25137.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.itu.moapd.x9.s25137.data.repositories.NotificationTopic
import dk.itu.moapd.x9.s25137.data.repositories.NotificationsRepository
import dk.itu.moapd.x9.s25137.data.repositories.PreferencesRepository
import dk.itu.moapd.x9.s25137.data.repositories.UserPreference
import dk.itu.moapd.x9.s25137.data.repositories.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {
    private val _preferencesBeingUpdated = MutableStateFlow<Set<UserPreference>>(emptySet())
    val preferencesBeingUpdated = _preferencesBeingUpdated.asStateFlow()
    val preferencesFlow: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserPreferences()
        )

    fun setPreference(preference: UserPreference, enabled: Boolean) {
        _preferencesBeingUpdated.update { it + preference }
        // If the preference defines whether a notification topic should be subscribed to,
        // (un)subscribe accordingly.
        if (preference.notificationTopic != null) {
            setTopicSubscription(enabled, preference.notificationTopic, preference)
        } else {
            viewModelScope.launch {
                preferencesRepository.setPreference(preference, enabled)
                _preferencesBeingUpdated.update { it - preference }
            }
        }
    }

    private fun setTopicSubscription(
        enabled: Boolean,
        notificationTopic: NotificationTopic,
        preference: UserPreference
    ) {
        val onResult: (Boolean) -> Unit = { success ->
            viewModelScope.launch {
                if (success) {
                    preferencesRepository.setPreference(preference, enabled)
                }
                _preferencesBeingUpdated.update { it - preference }
            }
        }

        if (enabled) {
            notificationsRepository.subscribeToTopic(notificationTopic, onResult)
        } else {
            notificationsRepository.unsubscribeFromTopic(notificationTopic, onResult)
        }
    }
}