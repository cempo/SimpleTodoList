package com.makeevapps.simpletodolist.utils


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.receiver.NotificationReceiver
import com.makeevapps.simpletodolist.ui.activity.EditTaskActivity

class NotificationUtils {
    companion object {
        val DEFAULT_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val DEFAULT_NOTIFICATION_ID = 1
        val TASK_NOTIFICATION_ID = 21323
        val ADD_TASK_NOTIFICATION_ID = 28723
    }

    fun showTaskAlarmNotification(context: Context, task: Task) {
        val action = EditTaskActivity.getActivityIntent(context, task.id)
        val contentPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val snoozePendingIntent = NotificationReceiver.getSnoozeTaskPendingIntent(context, task.id)
        val donePendingIntent = NotificationReceiver.getDoneTaskPendingIntent(context, task.id)

        val notification = NotificationCompat.Builder(context)
                .setContentTitle(task.title)
                .setContentText(task.description)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.ic_notification_change_time, context.getString(R.string.snooze), snoozePendingIntent)
                .addAction(R.drawable.ic_notification_done, context.getString(R.string.done), donePendingIntent)
                .build()

        showNotification(context, TASK_NOTIFICATION_ID, notification)
    }

    fun showAddTaskNotification(context: Context) {
        val action = EditTaskActivity.getActivityIntent(context)
        val contentPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.create_new_task))
                .setContentText(context.getString(R.string.click_to_add_new_task))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(contentPendingIntent)
                .build()

        showNotification(context, ADD_TASK_NOTIFICATION_ID, notification)
    }

    fun showNotification(context: Context, id: Int?, notification: Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (id == null) {
            notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification)
        } else {
            notificationManager.notify(id, notification)
        }
    }

    fun hideNotification(context: Context, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    fun cancel(context: Context, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    /*fun getPendingIntent(context: Context, intent: Intent, parentClass: Class<*>?, intentFlag: Int): PendingIntent {
        return if (parentClass != null) {
            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(parentClass)
            stackBuilder.addNextIntent(intent)

            stackBuilder.getPendingIntent(0, intentFlag)
        } else {
            PendingIntent.getActivity(context, 0, intent, intentFlag)
        }
    }*/
}
