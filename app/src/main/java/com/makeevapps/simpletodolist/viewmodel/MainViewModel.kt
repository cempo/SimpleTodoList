package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.repository.TaskRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var taskRepository: TaskRepository

    private val tasksCountResponse = MutableLiveData<Int>()

    private val compositeDisposable = CompositeDisposable()

    init {
        App.component.inject(this)

        compositeDisposable.add(taskRepository.getTodayTasksCount().subscribe({ result -> tasksCountResponse.value = result }))
    }

    fun getTasksCount(): MutableLiveData<Int> = tasksCountResponse

    override fun onCleared() {
        compositeDisposable.clear()
    }
}