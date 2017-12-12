package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.Keys
import com.makeevapps.simpletodolist.Keys.KEY_POSITION
import com.makeevapps.simpletodolist.Keys.KEY_TASK_ID
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.repository.TaskRepository
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class SnoozeViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var taskRepository: TaskRepository

    private val taskResponse = MutableLiveData<Task>()
    private val compositeDisposable = CompositeDisposable()

    lateinit var task: Task
    var position: Int? = null

    init {
        App.component.inject(this)
    }

    fun initTask(extras: Bundle) {
        position = extras.getInt(KEY_POSITION)
        val taskId = extras.getString(KEY_TASK_ID)

        compositeDisposable.add(taskRepository.getTaskByIdOnce(taskId).subscribe(
                { result ->
                    task = result
                    taskResponse.value = result
                }
        ))
    }

    /**
     * Data logic
     * */
    fun updateDueDate(extras: Bundle) {
        val dueDateTimestamp = extras.getLong(Keys.KEY_DUE_DATE_IN_MILLIS)
        val allDay = extras.getBoolean(Keys.KEY_ALL_DAY, true)

        task.dueDate = if (dueDateTimestamp > 0) Date(dueDateTimestamp) else null
        task.allDay = allDay
    }

    fun insertOrUpdateTask(onSuccess: () -> Unit) {
        taskRepository.insertOrUpdateTask(task).subscribe {
            onSuccess()
        }
    }

    fun getTaskResponse(): MutableLiveData<Task> = taskResponse

    fun resultIntent(): Intent {
        val intent = Intent()
        intent.putExtra(KEY_TASK_ID, task.id)
        position?.let { intent.putExtra(Keys.KEY_POSITION, it) }
        return intent
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}