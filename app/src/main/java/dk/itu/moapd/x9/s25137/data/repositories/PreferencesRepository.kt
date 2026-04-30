package dk.itu.moapd.x9.s25137.data.repositories

/* Code adapted from the DataStore codelab repository, found at https://github.com/android/codelab-android-datastore/tree/preferences_datastore.
 * Its original license is attached below.

 *
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import dk.itu.moapd.x9.s25137.data.repositories.PreferencesRepository.PreferencesKeys.SHOW_LOCATION_TRACE_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserPreferences(
    val showLocationTrace: Boolean
)

class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val SHOW_LOCATION_TRACE_KEY = booleanPreferencesKey("show_location_trace")
    }

    val preferencesFlow: Flow<UserPreferences> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }.map { preferences -> mapUserPreferences(preferences) }

    suspend fun setShowLocationTrace(showLocationTrace: Boolean) {
        dataStore.edit { preferences -> preferences[SHOW_LOCATION_TRACE_KEY] = showLocationTrace }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val showLocationTrace = preferences[SHOW_LOCATION_TRACE_KEY] ?: false
        return UserPreferences(showLocationTrace)
    }
}