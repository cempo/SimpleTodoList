package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.repository.TaskRepository
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class CalendarViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var taskRepository: TaskRepository

    private val tasksResponse = MutableLiveData<List<Task>>()
    private val compositeDisposable = CompositeDisposable()
    val is24HoursFormat: Boolean

    init {
        App.component.inject(this)

        is24HoursFormat = preferenceManager.is24HourFormat()
    }

    fun loadTasks(date: Date) {
        compositeDisposable.clear()
        compositeDisposable.add(taskRepository.getTasksByDate(date).subscribe({ result -> tasksResponse.value = result }))
    }

    fun insertOrUpdateTask(task: Task) {
        compositeDisposable.add(taskRepository.insertOrUpdateTask(task).subscribe())
    }

    fun removeTask(task: Task) {
        compositeDisposable.add(taskRepository.deleteTask(task).subscribe())
    }

    fun getTasksResponse(): MutableLiveData<List<Task>> = tasksResponse

    override fun onCleared() {
        compositeDisposable.clear()
    }
}