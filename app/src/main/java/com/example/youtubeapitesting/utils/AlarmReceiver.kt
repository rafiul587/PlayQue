package com.example.youtubeapitesting.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.youtubeapitesting.R
import com.example.youtubeapitesting.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var remindersManager: RemindersManager

    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     * */
    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        intent.extras?.let {
            val videoTitle = it.getString("AlarmID")
            Log.d("TAG", "onReceive: $videoTitle")
            val channelTitle = "hjjhj"

            notificationManager.sendReminderNotification(
                applicationContext = context,
                channelId = context.getString(R.string.reminders_notification_channel_id),
                videoTitle = videoTitle,
                channelTitle = channelTitle
            )
            // Remove this line if you don't want to reschedule the reminder
            //remindersManager.startReminder(context.applicationContext)
        }


    }
}

fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
    videoTitle: String?,
    channelTitle: String?,
) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setContentTitle(applicationContext.getString(R.string.title_notification_reminder))
        .setContentText(applicationContext.getString(R.string.description_notification_reminder, videoTitle, channelTitle))
        .setSmallIcon(R.drawable.ic_menu_icon)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())

}


const val NOTIFICATION_ID = 101