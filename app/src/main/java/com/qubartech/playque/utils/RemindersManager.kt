package com.qubartech.playque.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

class RemindersManager(context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun startReminder(
        context: Context,
        reminderTime: String = "08:00",
        reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
    ) {

        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    reminderId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
        }

        // If the trigger time you specify is in the past, the alarm triggers immediately.
        // if soo just add one day to required calendar
        // Note: i'm also adding one min cuz if the user clicked on the notification as soon as
        // he receive it it will reschedule the alarm to fire another notification immediately
        if (Calendar.getInstance(Locale.ENGLISH).apply
            { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
                    intent
                )
            }
        } else {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
                intent
            )
        }
    }

    fun stopReminder(
        context: Context,
        reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
        alarmManager.cancel(intent)
    }

    companion object {
        const val REMINDER_NOTIFICATION_REQUEST_CODE = 123
    }
}