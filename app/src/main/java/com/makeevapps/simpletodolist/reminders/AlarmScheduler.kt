package com.makeevapps.simpletodolist.reminders

import android.app.AlarmManager
import android.content.Context
import com.makeevapps.simpletodolist.App

/**
 * Helper to manage scheduling the reminder alarm
 */
object AlarmScheduler {

    /**
     * Schedule a reminder alarm at the specified time for the given task.
     *
     * @param context        Local application or activity context
     * @param alarmTime      Alarm start time
     * @param reminderTaskId Id referencing the task in the DB
     */
    fun scheduleAlarm(alarmTime: Long, reminderTaskId: String) {
        //Schedule the alarm. Will update an existing item for the same task.
        val manager = AlarmManagerProvider.getAlarmManager(App.instance)
        val operation = ReminderAlarmService.getReminderPendingIntent(App.instance, reminderTaskId)
        manager.setExact(AlarmManager.RTC, alarmTime, operation)
    }

    fun removeAlarm(taskId: String) {
        //Cancel any reminders that might be set for this item
        val manager = AlarmManagerProvider.getAlarmManager(App.instance)
        val operation = ReminderAlarmService.getReminderPendingIntent(App.instance, taskId)
        manager.cancel(operation)
    }
}
