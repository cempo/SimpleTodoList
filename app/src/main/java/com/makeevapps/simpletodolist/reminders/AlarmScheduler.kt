package com.makeevapps.simpletodolist.reminders

import android.app.AlarmManager
import android.content.Context
import com.makeevapps.simpletodolist.App

/**
 * Helper to manage scheduling the reminder alarm
 */
class AlarmScheduler(val context: Context) {
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarmTime: Long, reminderTaskId: String) {
        val pendingIntent = ReminderReceiver.getReminderPendingIntent(context, reminderTaskId)
        alarmManager.setExact(AlarmManager.RTC, alarmTime, pendingIntent)
    }

    fun removeAlarm(taskId: String) {
        val pendingIntent = ReminderReceiver.getReminderPendingIntent(context, taskId)
        alarmManager.cancel(pendingIntent)
    }
}
