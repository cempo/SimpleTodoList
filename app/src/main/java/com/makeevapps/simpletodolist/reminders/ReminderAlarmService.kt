package com.makeevapps.simpletodolist.reminders

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.repository.TaskRepository
import com.makeevapps.simpletodolist.ui.activity.EditTaskActivity
import io.reactivex.android.schedulers.AndroidSchedulers.from
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ReminderAlarmService : IntentService(TAG) {
    @Inject
    lateinit var taskRepository: TaskRepository
    private val compositeDisposable = CompositeDisposable()

    init {
        App.component.inject(this)
    }

    companion object {
        private val TAG = ReminderAlarmService::class.java.simpleName
        private val NOTIFICATION_ID = 42

        //This is a deep link intent, and needs the task stack
        fun getReminderPendingIntent(context: Context, taskId: String): PendingIntent {
            val action = Intent(context, ReminderAlarmService::class.java)
            action.putExtra("taskId", taskId)
            return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val taskId = intent.getStringExtra("taskId")

        //Display a notification to view the task details
        val action = EditTaskActivity.getActivityIntent(this, taskId)
        val operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        compositeDisposable.add(taskRepository.getTaskByIdOnce(taskId).subscribe { task ->
                    val note = NotificationCompat.Builder(this)
                            .setContentTitle(task.title)
                            .setContentText(task.description)
                            .setSmallIcon(R.drawable.ic_done_24dp)
                            .setContentIntent(operation)
                            .setAutoCancel(true)
                            .build()

                    manager.notify(NOTIFICATION_ID, note)

                    compositeDisposable.dispose()
                })
    }
}
