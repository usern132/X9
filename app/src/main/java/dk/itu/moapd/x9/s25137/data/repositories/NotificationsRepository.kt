package dk.itu.moapd.x9.s25137.data.repositories

import dk.itu.moapd.x9.s25137.data.datasources.NotificationsRemoteDataSource
import javax.inject.Inject

enum class NotificationTopic(val topic: String) {
    NEW_REPORTS("new_reports"),
}

class NotificationsRepository @Inject constructor(
    val notificationsRemoteDataSource: NotificationsRemoteDataSource
) {
    fun subscribeToTopic(topic: NotificationTopic, onResult: (success: Boolean) -> Unit = {}) {
        notificationsRemoteDataSource.subscribeToTopic(topic.topic, onResult)
    }

    fun unsubscribeFromTopic(topic: NotificationTopic, onResult: (success: Boolean) -> Unit = {}) {
        notificationsRemoteDataSource.unsubscribeFromTopic(topic.topic, onResult)
    }
}