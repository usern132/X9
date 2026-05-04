package dk.itu.moapd.x9.s25137.data.repositories

import dk.itu.moapd.x9.s25137.data.datasources.DatabaseRemoteDataSource
import dk.itu.moapd.x9.s25137.data.datasources.NotificationsRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationTopic(val topic: String) {
    NEW_REPORTS("new_reports"),
}

class NotificationsRepository @Inject constructor(
    val notificationsRemoteDataSource: NotificationsRemoteDataSource,
    val databaseRemoteDataSource: DatabaseRemoteDataSource,
    val preferencesRepository: PreferencesRepository
) {
    fun subscribeToTopic(topic: NotificationTopic, onResult: (success: Boolean) -> Unit = {}) {
        notificationsRemoteDataSource.subscribeToTopic(topic.topic) { success ->
            if (success) {
                CoroutineScope(Dispatchers.IO).launch {
                    notificationsRemoteDataSource.getToken { token ->
                        if (token != null) {
                            databaseRemoteDataSource.addTokenToTopic(
                                token = token,
                                topic = topic
                            )
                            onResult(true)
                        } else onResult(false)
                    }
                }
            }
        }
    }

    fun unsubscribeFromTopic(
        topic: NotificationTopic,
        onResult: (success: Boolean) -> Unit = {}
    ) {
        notificationsRemoteDataSource.unsubscribeFromTopic(topic.topic) { success ->
            if (success) {
                CoroutineScope(Dispatchers.IO).launch {
                    notificationsRemoteDataSource.getToken { token ->
                        if (token != null) {
                            databaseRemoteDataSource.removeTokenFromTopic(
                                token = token,
                                topic = topic
                            )
                            onResult(true)
                        } else onResult(false)
                    }
                }
            }
        }
    }

    fun updateFcmToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val preferences = preferencesRepository.preferencesFlow.first()
            val oldToken = preferences.fcmToken
            if (oldToken != token) {
                if (oldToken != null)
                    databaseRemoteDataSource.removeTokenFromAllTopics(oldToken)
                if (preferences.receiveNotificationsForNewReports)
                    databaseRemoteDataSource.addTokenToTopic(
                        token,
                        NotificationTopic.NEW_REPORTS
                    )
                preferencesRepository.setFcmToken(token)
            }
        }
    }
}