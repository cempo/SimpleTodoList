package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.repository.TaskRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TodayViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    @Inject
    lateinit var taskRepository: TaskRepository

    private val tasksResponse = MutableLiveData<List<Task>>()

    private val compositeDisposable = CompositeDisposable()

    init {
        App.component.inject(this)

        compositeDisposable.add(taskRepository.getTodayTasks().subscribe({ result -> tasksResponse.value = result }))
    }

    fun is24HoursFormat(): Boolean = preferenceManager.is24HourFormat()

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