package com.example.youtubeapitesting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * restart reminders alarms when user's device reboots
 **/
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var remindersManager: RemindersManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            remindersManager.startReminder(context)
        }
    }
}