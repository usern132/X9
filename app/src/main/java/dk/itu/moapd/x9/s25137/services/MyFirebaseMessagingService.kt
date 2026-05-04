package dk.itu.moapd.x9.s25137.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import dk.itu.moapd.x9.s25137.R
import dk.itu.moapd.x9.s25137.data.repositories.NotificationsRepository
import javax.inject.Inject

private val TAG = MyFirebaseMessagingService::class.simpleName

private enum class FirebaseNotificationChannel(
    val id: String,
    @StringRes
    val nameResId: Int,
    val importance: Int,
    @StringRes
    val descriptionResId: Int
) {
    NEW_REPORTS(
        "new_reports",
        R.string.new_reports_notification_channel_name,
        NotificationManager.IMPORTANCE_LOW,
        R.string.new_reports_notification_channel_description
    );

    fun toNotificationChannel(context: Context) =
        NotificationChannel(id, context.getString(nameResId), importance).also {
            it.description = context.getString(descriptionResId)
        }

}

@AndroidEntryPoint
class MyFirebaseMessagingService @Inject constructor(
    val notificationsRepository: NotificationsRepository
) : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed FCM token: $token")
        notificationsRepository.updateFcmToken(token)
    }

    private fun createNotificationChannels() {
        val notificationChannels =
            FirebaseNotificationChannel.entries.map { it.toNotificationChannel(this) }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationChannels.forEach {
            notificationManager.createNotificationChannel(it)
        }
    }

}