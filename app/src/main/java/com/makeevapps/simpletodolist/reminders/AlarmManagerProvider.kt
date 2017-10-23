package com.makeevapps.simpletodolist.reminders

import android.app.AlarmManager
import android.content.Context

/**
 * Interface to provide access to an [AlarmManager] instance that can be configured
 * during automated unit tests.
 *
 *
 * NO MODIFICATIONS SHOULD BE MADE TO THIS CLASS OR ITS USAGE.
 */
object AlarmManagerProvider {
    private var alarmManager: AlarmManager? = null

    @Synchronized internal fun getAlarmManager(context: Context): AlarmManager =
            alarmManager ?: context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Synchronized
    fun injectAlarmManager(alarmManager: AlarmManager) {
        if (this.alarmManager != null) {
            throw IllegalStateException("Alarm Manager Already Set")
        }
        this.alarmManager = alarmManager
    }

}
