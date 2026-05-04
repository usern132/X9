package dk.itu.moapd.x9.s25137.data.datasources

import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject

class NotificationsRemoteDataSource @Inject constructor(
    val messaging: FirebaseMessaging
) {
    fun getToken(onResult: (token: String?) -> Unit) {
        messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(task.result)
            } else {
                onResult(null)
            }
        }
    }

    fun subscribeToTopic(topic: String, onResult: (success: Boolean) -> Unit = {}) {
        messaging.subscribeToTopic(topic).addOnCompleteListener { task ->
            onResult(task.isSuccessful)
        }
    }

    fun unsubscribeFromTopic(topic: String, onResult: (success: Boolean) -> Unit = {}) {
        messaging.unsubscribeFromTopic(topic).addOnCompleteListener { task ->
            onResult(task.isSuccessful)
        }
    }
}