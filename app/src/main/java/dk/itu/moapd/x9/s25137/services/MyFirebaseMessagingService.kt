package dk.itu.moapd.x9.s25137.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import dk.itu.moapd.x9.s25137.R

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
        NotificationManager.IMPORTANCE_HIGH,
        R.string.new_reports_notification_channel_description
    );

    fun toNotificationChannel(context: Context) =
        NotificationChannel(id, context.getString(nameResId), importance).also {
            it.description = context.getString(descriptionResId)
        }

}

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels(this)
    }

    companion object {
        fun createNotificationChannels(context: Context) {
            val notificationChannels =
                FirebaseNotificationChannel.entries.map { it.toNotificationChannel(context) }

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationChannels.forEach {
                notificationManager.createNotificationChannel(it)
            }
        }
    }

}