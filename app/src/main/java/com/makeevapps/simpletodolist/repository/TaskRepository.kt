package com.makeevapps.simpletodolist.repository

import com.makeevapps.simpletodolist.datasource.db.TaskDao
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.reminders.AlarmScheduler
import com.makeevapps.simpletodolist.utils.DateUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class TaskRepository(val taskDao: TaskDao, val alarmScheduler: AlarmScheduler) {
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
    }

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

            if (!task.isComplete && task.dueDate != null && task.dueDate!!.after(DateUtils.currentTime())) {
                alarmScheduler.scheduleAlarm(task.dueDate!!.time, task.id)
            } else {
                alarmScheduler.removeAlarm(task.id)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteTask(task: Task): Completable {
        return Completable.fromCallable({
            taskDao.delete(task)

            if (task.dueDate != null) {
                alarmScheduler.removeAlarm(task.id)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}