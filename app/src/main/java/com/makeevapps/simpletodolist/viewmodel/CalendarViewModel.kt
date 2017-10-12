package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.repository.TaskRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CalendarViewModel : ViewModel() {
    @Inject
    lateinit var taskRepository: TaskRepository

    private val tasksResponse = MutableLiveData<List<Task>>()

    val compositeDisposable = CompositeDisposable()

    init {
        App.component.inject(this)

        compositeDisposable.add(taskRepository.getTodayTasks().subscribe({ result -> tasksResponse.value = result }))
    }

    fun addTask(task: Task) {
        taskRepository.insertOrUpdateTask(task)
    }

    fun deleteAllTasks() {
        taskRepository.deleteAllTasks()
    }

    fun getTasksResponse(): MutableLiveData<List<Task>> = tasksResponse

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}