package com.makeevapps.simpletodolist.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.BuildConfig
import com.makeevapps.simpletodolist.repository.TaskRepository
import com.makeevapps.simpletodolist.ui.activity.SnoozeActivity
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.NotificationUtils
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var taskRepository: TaskRepository
    @Inject
    lateinit var notificationUtils: NotificationUtils

    private val compositeDisposable = CompositeDisposable()

    init {
        App.component.inject(this)
    }

    companion object {
        val SNOOZE_NOTIFICATION_ACTION = BuildConfig.APPLICATION_ID + ".SNOOZE_NOTIFICATION"
        val DONE_NOTIFICATION_ACTION = BuildConfig.APPLICATION_ID + ".DONE_NOTIFICATION"

        fun getSnoozeTaskPendingIntent(context: Context, taskId: String): PendingIntent {
            val snoozeIntent = Intent(context, NotificationReceiver::class.java)
            snoozeIntent.action = SNOOZE_NOTIFICATION_ACTION
            snoozeIntent.putExtra("taskId", taskId)
            return PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), snoozeIntent, 0)
        }

        fun getDoneTaskPendingIntent(context: Context, taskId: String): PendingIntent {
            val snoozeIntent = Intent(context, NotificationReceiver::class.java)
            snoozeIntent.action = DONE_NOTIFICATION_ACTION
            snoozeIntent.putExtra("taskId", taskId)
            return PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), snoozeIntent, 0)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            SNOOZE_NOTIFICATION_ACTION -> {
                val taskId = intent.getStringExtra("taskId")
                context.startActivity(SnoozeActivity.getActivityIntent(context, taskId, true))

                notificationUtils.hideNotification(context, NotificationUtils.TASK_NOTIFICATION_ID)
                notificationUtils.collapsePanel(context)
            }
            DONE_NOTIFICATION_ACTION -> {
                val taskId = intent.getStringExtra("taskId")

                compositeDisposable.add(taskRepository.getTaskByIdOnce(taskId).subscribe { task ->
                    task.isComplete = true
                    task.doneDate = DateUtils.currentTime()
                    compositeDisposable.add(taskRepository.insertOrUpdateTask(task).subscribe {
                        compositeDisposable.dispose()
                    })

                    notificationUtils.hideNotification(context, NotificationUtils.TASK_NOTIFICATION_ID)
                    notificationUtils.collapsePanel(context)
                })
            }
        }
    }
}
