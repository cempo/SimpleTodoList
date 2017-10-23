package com.makeevapps.simpletodolist.reminders

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.repository.TaskRepository
import com.makeevapps.simpletodolist.ui.activity.EditTaskActivity
import com.makeevapps.simpletodolist.utils.NotificationUtils
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ReminderAlarmService : IntentService(ReminderAlarmService::class.java.simpleName) {
    @Inject
    lateinit var taskRepository: TaskRepository
    @Inject
    lateinit var notificationUtils: NotificationUtils
    private val compositeDisposable = CompositeDisposable()

    init {
        App.component.inject(this)
    }

    companion object {
        fun getReminderPendingIntent(context: Context, taskId: String): PendingIntent {
            val action = Intent(context, ReminderAlarmService::class.java)
            action.putExtra("taskId", taskId)
            return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return

        val taskId = intent.getStringExtra("taskId")
        compositeDisposable.add(taskRepository.getTaskByIdOnce(taskId).subscribe { task ->
            notificationUtils.showTaskAlarmNotification(this, task)

            compositeDisposable.dispose()
        })
    }
}
