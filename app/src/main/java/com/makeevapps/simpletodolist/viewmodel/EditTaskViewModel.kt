package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.Keys
import com.makeevapps.simpletodolist.Keys.KEY_DUE_DATE_IN_MILLIS
import com.makeevapps.simpletodolist.Keys.KEY_TASK_ID
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.enums.TaskPriority
import com.makeevapps.simpletodolist.repository.TaskRepository
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.extension.asString
import com.makeevapps.simpletodolist.utils.extension.endDayDate
import com.orhanobut.logger.Logger
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject


class EditTaskViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var taskRepository: TaskRepository

    private val taskResponse = MutableLiveData<Task>()
    private val subTaskResponse = MutableLiveData<List<Task>>()
    private val compositeDisposable = CompositeDisposable()
    var oldTask: Task = Task("")
    var newTask: Task = oldTask

    private val is24HoursFormat: Boolean

    init {
        App.component.inject(this)

        is24HoursFormat = preferenceManager.is24HourFormat()
    }

    /**
     * Init logic
     * */
    fun initStartDate(extras: Bundle) {
        val longDate = extras.getLong(KEY_DUE_DATE_IN_MILLIS)
        val dueDate = if (longDate > 0) Date(longDate).endDayDate() else null

        newTask.dueDate = dueDate
        newTask.allDay = true
    }

    fun initTask(extras: Bundle) {
        val taskId = extras.getString(KEY_TASK_ID)
        if (!taskId.isNullOrEmpty()) {
            compositeDisposable.add(taskRepository.getTaskByIdOnce(taskId!!).subscribe(
                    { result ->
                        oldTask = result
                        newTask = result
                        taskResponse.value = result

                        loadSubTasks(newTask.id)
                    }
            ))
        } else {
            taskResponse.value = newTask

            loadSubTasks(newTask.id)
        }
    }

    /**
     * Data logic
     * */
    fun updateDueDate(extras: Bundle) {
        val dueDateTimestamp = extras.getLong(KEY_DUE_DATE_IN_MILLIS)
        val allDay = extras.getBoolean(Keys.KEY_ALL_DAY, true)

        newTask.dueDate = if (dueDateTimestamp > 0) Date(dueDateTimestamp) else null
        newTask.allDay = allDay

        taskResponse.value = newTask
    }

    fun getDueDateText(): String? = newTask.dueDate?.asString(DateUtils.SHORT_DATE_FORMAT2)

    fun getDueTimeText(): String? = newTask.dueDate?.let {
        if (is24HoursFormat) {
            it.asString(DateUtils.TIME_24H_FORMAT)
        } else {
            it.asString(DateUtils.TIME_12H_FORMAT)
        }
    }

    fun removeDate() {
        newTask.dueDate = null
        newTask.allDay = true

        taskResponse.value = newTask
    }

    fun updatePriority(priority: TaskPriority) {
        newTask.priority = priority
        taskResponse.value = newTask
    }

    fun updateTitle(title: String) {
        newTask.title = title
    }

    fun updateDescription(description: String) {
        newTask.description = description
    }

    private fun loadSubTasks(taskId: String) {
        compositeDisposable.add(taskRepository.getSubTasks(taskId).subscribe(
                { result ->
                    subTaskResponse.value = result
                }
        ))
    }

    fun insertOrUpdateTask(onSuccess: () -> Unit, onError: (message: Int) -> Unit) {
        if (newTask.title.isEmpty()) {
            onError(R.string.error_title_empty)
        } else {
            taskRepository.insertOrUpdateTask(newTask).subscribe {
                Logger.e(newTask.toString())
                onSuccess()
            }
        }
    }

    fun deleteTask(onSuccess: () -> Unit) {
        compositeDisposable.add(taskRepository.deleteTask(newTask).subscribe({ onSuccess() }))
    }

    fun insertOrUpdateSubTask(subTask: Task?) {
        if (subTask != null) {
            compositeDisposable.add(taskRepository.insertOrUpdateTask(subTask).subscribe())
        }
    }

    fun deleteSubTask(subTask: Task?) {
        if (subTask != null) {
            compositeDisposable.add(taskRepository.deleteTask(subTask).subscribe())
        }
    }

    fun getTaskResponse(): MutableLiveData<Task> = taskResponse
    fun getSubTaskResponse(): MutableLiveData<List<Task>> = subTaskResponse

    override fun onCleared() {
        compositeDisposable.clear()
    }
}