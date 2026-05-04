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
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserPreferences(
    val fcmToken: String? = null,
    val showLocationTrace: Boolean = UserPreference.SHOW_LOCATION_TRACE.defaultValue,
    val receiveNotificationsForNewReports: Boolean = UserPreference.RECEIVE_NOTIFICATIONS_FOR_NEW_REPORTS.defaultValue
)

enum class UserPreference(
    val key: Preferences.Key<Boolean>,
    val defaultValue: Boolean = false,
    val notificationTopic: NotificationTopic? = null
) {
    SHOW_LOCATION_TRACE(booleanPreferencesKey("show_location_trace")),
    RECEIVE_NOTIFICATIONS_FOR_NEW_REPORTS(
        key = booleanPreferencesKey("receive_notifications_for_new_reports"),
        notificationTopic = NotificationTopic.NEW_REPORTS
    );

    fun getValue(preferences: Preferences) = preferences[key] ?: defaultValue
}

class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
    }

    val preferencesFlow: Flow<UserPreferences> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) emit(emptyPreferences())
                else throw exception
            }.map { preferences -> mapUserPreferences(preferences) }

    suspend fun setPreference(preference: UserPreference, enabled: Boolean) {
        dataStore.edit { preferences -> preferences[preference.key] = enabled }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            fcmToken = preferences[FCM_TOKEN_KEY],
            showLocationTrace = UserPreference.SHOW_LOCATION_TRACE.getValue(preferences),
            receiveNotificationsForNewReports =
                UserPreference.RECEIVE_NOTIFICATIONS_FOR_NEW_REPORTS.getValue(preferences),
        )
    }

    suspend fun setFcmToken(token: String) {
        dataStore.edit { preferences -> preferences[FCM_TOKEN_KEY] = token }
    }
}