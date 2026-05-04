package dk.itu.moapd.x9.s25137.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.itu.moapd.x9.s25137.data.repositories.NotificationTopic
import dk.itu.moapd.x9.s25137.data.repositories.NotificationsRepository
import dk.itu.moapd.x9.s25137.data.repositories.PreferencesRepository
import dk.itu.moapd.x9.s25137.data.repositories.UserPreference
import dk.itu.moapd.x9.s25137.data.repositories.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {
    val preferencesFlow: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserPreferences()
        )

    fun setPreference(preference: UserPreference, enabled: Boolean) {
        // If the preference defines whether a notification topic should be subscribed to,
        // (un)subscribe accordingly.
        if (preference.notificationTopic != null) {
            setTopicSubscription(enabled, preference.notificationTopic, preference)
        } else {
            viewModelScope.launch {
                preferencesRepository.setPreference(preference, enabled)
            }
        }
    }

    private fun setTopicSubscription(
        enabled: Boolean,
        notificationTopic: NotificationTopic,
        preference: UserPreference
    ) {
        if (enabled) {
            notificationsRepository.subscribeToTopic(notificationTopic) { success ->
                if (success) {
                    viewModelScope.launch {
                        preferencesRepository.setPreference(preference, true)
                    }
                }
            }
        } else {
            notificationsRepository.unsubscribeFromTopic(notificationTopic) { success ->
                if (success) {
                    viewModelScope.launch {
                        preferencesRepository.setPreference(preference, false)
                    }
                }
            }
        }
    }
}