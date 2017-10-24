package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.repository.TaskRepository
import com.orhanobut.logger.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SnoozeViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var taskRepository: TaskRepository

    private val taskResponse = MutableLiveData<Task>()
    private val compositeDisposable = CompositeDisposable()

    lateinit var task: Task

    init {
        App.component.inject(this)
    }

    fun getThemeResId(): Int = ThemeStyle.getThemeById(preferenceManager.getThemeId()).themeResId

    fun loadTask(taskId: String) {
        compositeDisposable.add(taskRepository.getTaskByIdOnce(taskId).subscribe(
                { result ->
                    task = result
                    taskResponse.value = result
                }
        ))
    }

    fun insertOrUpdateTask(onSuccess: () -> Unit) {
        taskRepository.insertOrUpdateTask(task).subscribe {
            onSuccess()
        }
    }

    fun getTaskResponse(): MutableLiveData<Task> = taskResponse

    override fun onCleared() {
        compositeDisposable.clear()
    }
}