package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.repository.TaskRepository
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

    init {
        App.component.inject(this)
    }

    fun getThemeResId(): Int = ThemeStyle.getThemeById(preferenceManager.getThemeId()).themeResId

    fun loadTask(taskId: String?, dueDate: Date?) {
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
            if (dueDate != null) {
                newTask.dueDate = dueDate
                taskResponse.value = newTask
            }

            loadSubTasks(newTask.id)
        }
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