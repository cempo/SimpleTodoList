package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.repository.TaskRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TodayViewModel : ViewModel() {
    @Inject
    lateinit var taskRepository: TaskRepository

    private val tasksResponse = MutableLiveData<List<Task>>()

    private val compositeDisposable = CompositeDisposable()

    init {
        App.component.inject(this)
    }

    fun loadTasks() {
        compositeDisposable.add(taskRepository.getTodayTasks().subscribe({ result -> tasksResponse.value = result }))
    }

    fun updateTaskStatus(taskId: String, isComplete: Boolean) {
        compositeDisposable.add(taskRepository.updateTaskStatus(taskId, isComplete).subscribe())
    }

    fun insertTask(task: Task) {
        compositeDisposable.add(taskRepository.insertOrUpdateTask(task).subscribe())
    }

    fun removeTask(task: Task) {
        compositeDisposable.add(taskRepository.deleteTask(task).subscribe())
    }

    fun deleteAllTasks() {
        taskRepository.deleteAllTasks()
    }

    fun getTasksResponse(): MutableLiveData<List<Task>> = tasksResponse

    override fun onCleared() {
        compositeDisposable.clear()
    }
}