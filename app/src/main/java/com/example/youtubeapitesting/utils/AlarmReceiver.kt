package com.example.youtubeapitesting.utils

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_DAY
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.youtubeapitesting.R
import com.example.youtubeapitesting.data.local.PlayListDao
import com.example.youtubeapitesting.ui.MainActivity
import com.example.youtubeapitesting.ui.screens.home.cancelExistingAlarms
import com.example.youtubeapitesting.ui.screens.home.generateWeekdays
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var playlistDao: PlayListDao

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
            val videoTitle = it.getString("title")
            val channelTitle = it.getString("channelTitle")

            notificationManager.sendReminderNotification(
                applicationContext = context,
                channelId = context.getString(R.string.reminders_notification_channel_id),
                videoTitle = videoTitle,
                channelTitle = channelTitle
            )
            val playlistId = it.getString("playlistId", "")
            val daysMask = it.getInt("daysMask")
            val alarmDay = it.getInt("day")
            val endDate = it.getLong("endDate")
            val days = generateWeekdays(daysMask)

            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                alarmIntent.identifier = playlistId
            } else alarmIntent.addCategory(playlistId)

            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            //Similar as currentTimeMillis + INTERVAL_DAY >= (endDate + INTERVAL_DAY) and INTERVAL_DAY * 7. Removed 1 INTERVAL_DAY from all sides. So it became INTERVAL_DAY * 6
            if (System.currentTimeMillis() >= endDate) {
                cancelExistingAlarms(context, days, alarmIntent, alarmManager);
            } else if (days.size != 7 && System.currentTimeMillis() + (INTERVAL_DAY * 6) >= endDate) {
                Log.d("TAG", "onReceive: $alarmDay")
                cancelExistingAlarms(context, listOf(alarmDay), alarmIntent, alarmManager )
            }
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
        .setContentText(
            applicationContext.getString(
                R.string.description_notification_reminder,
                videoTitle,
                channelTitle
            )
        )
        .setSmallIcon(R.drawable.ic_menu_icon)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())

}


const val NOTIFICATION_ID = 101