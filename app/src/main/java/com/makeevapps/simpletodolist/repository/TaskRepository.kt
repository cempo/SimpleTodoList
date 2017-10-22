package com.makeevapps.simpletodolist.repository

import com.makeevapps.simpletodolist.datasource.db.TaskDao
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.utils.DateUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class TaskRepository(val taskDao: TaskDao) {
    init {
        println("This ($this) is a singleton")
    }

    fun getTodayTasks(): Flowable<List<Task>> {
        val startDay = DateUtils.startCurrentDayDate()
        val endDay = DateUtils.endCurrentDayDate()
        return taskDao.loadForTodayScreen(startDay, endDay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        //.doOnSubscribe(s -> loadingStatus.setValue(true))
        //.doAfterTerminate(() -> loadingStatus.setValue(false))
    }

    fun getTodayTasksCount(): Flowable<Int> {
        val startDay = DateUtils.startCurrentDayDate()
        val endDay = DateUtils.endCurrentDayDate()
        return taskDao.getTasksCount(startDay, endDay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTasksByDate(date: Date): Flowable<List<Task>> {
        val startDay = DateUtils.startDayDate(date)
        val endDay = DateUtils.endDayDate(date)
        return getTasksByDate(startDay, endDay)
    }

    fun getTasksByDate(startDay: Date, endDay: Date): Flowable<List<Task>> {
        return taskDao.getTasksByDate(startDay, endDay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        //.doOnSubscribe(s -> loadingStatus.setValue(true))
        //.doAfterTerminate(() -> loadingStatus.setValue(false))
    }

    /*fun getTodayTasks(): Flowable<List<Task>> {
        val startDay = DateUtils.startCurrentDayDate()
        val endDay = DateUtils.endCurrentDayDate()
        val currentTime = DateUtils.currentTime()
        return taskDao.loadAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //.map { Result.success(it) }
    }*/

    /*Work with update*/
    /*fun getTodayTasks(): Flowable<List<Task>> {
        return Flowable.create({ emitter ->
            val startDay = DateUtils.startCurrentDayDate()
            val endDay = DateUtils.endCurrentDayDate()

            taskDao.loadForTodayScreen(startDay, endDay)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { parentTaskList ->
                        val parentsTaskIds: List<String> = parentTaskList
                                .filter { !it.id.isEmpty() }
                                .map { it.id }

                        taskDao.loadSubTasks(parentsTaskIds).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { childTaskList ->

                                    for (parentTask in parentTaskList) {
                                        childTaskList
                                                .filter { parentTask.id == it.parentId }
                                                .forEach { parentTask.subTasks.add(it) }
                                    }

                                    emitter.onNext(parentTaskList)
                                }
                    }
        }, BackpressureStrategy.BUFFER)
    }*/

    /*fun getTodayTasks2(): Flowable<Result<List<Task>>> {
        return Flowable.create({ emitter ->
            val startDay = DateUtils.startCurrentDayDate()
            val endDay = DateUtils.endCurrentDayDate()
            taskDao.getTasksByDate(startDay, endDay)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { Result.success(it) }
                    .subscribe({
                        emitter.onNext(it)
                        //emitter.onComplete()
                    })

        }, BackpressureStrategy.BUFFER)
    }*/

    fun getTaskById(taskId: String): Flowable<Task> {
        return taskDao.loadById(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTaskByIdOnce(taskId: String): Maybe<Task> {
        return taskDao.loadByIdOnce(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSubTasks(taskId: String): Flowable<List<Task>> {
        return taskDao.loadSubTasksById(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun getUnlimitedTasks(): Flowable<List<Task>> {
        return taskDao.loadUnlimitedTasks()
    }

    fun insertOrUpdateTask(task: Task): Completable {
        return Completable.fromCallable({
            taskDao.insert(task)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateTaskStatus(taskId: String, isComplete: Boolean): Completable {
        return Completable.fromCallable({
            taskDao.updateTaskStatus(taskId, isComplete)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteAllTasks() {
        Maybe.fromCallable({
            taskDao.deleteAll()
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun deleteTask(task: Task): Completable {
        return Completable.fromCallable({
            taskDao.delete(task)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}