package com.makeevapps.simpletodolist.reminders

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.repository.TaskRepository
import com.makeevapps.simpletodolist.utils.NotificationUtils
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ReminderReceiver : BroadcastReceiver() {
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
            val action = Intent(context, ReminderReceiver::class.java)
            action.putExtra("taskId", taskId)
            return PendingIntent.getBroadcast(context, 0, action, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val taskId = intent.getStringExtra("taskId")
        compositeDisposable.add(taskRepository.getTaskByIdOnce(taskId).subscribe { task ->
            notificationUtils.showTaskAlarmNotification(context, task)

            compositeDisposable.dispose()
        })
    }
}
