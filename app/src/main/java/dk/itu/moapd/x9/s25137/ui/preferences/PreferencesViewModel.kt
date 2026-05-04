package dk.itu.moapd.x9.s25137.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val preferencesFlow: StateFlow<UserPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserPreferences()
        )

    fun setPreference(preference: UserPreference, enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPreference(preference, enabled)
        }
    }
}