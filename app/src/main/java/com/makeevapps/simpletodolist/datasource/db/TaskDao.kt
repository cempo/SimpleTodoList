package com.makeevapps.simpletodolist.datasource.db

import android.arch.persistence.room.*
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.enums.TaskPriority
import io.reactivex.Flowable
import io.reactivex.Maybe
import java.util.*


@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun loadAll(): Flowable<List<Task>>

    @Query("SELECT * FROM task WHERE id IN (:taskIds)")
    fun loadByIds(vararg taskIds: String): Flowable<List<Task>>

    @Query("SELECT * FROM task WHERE id = :taskId LIMIT 1")
    fun loadById(taskId: String): Flowable<Task>

    @Query("SELECT * FROM task WHERE id = :taskId LIMIT 1")
    fun loadByIdOnce(taskId: String): Maybe<Task>

    @Query("SELECT * FROM task WHERE parentId = :taskId ORDER BY title ASC")
    fun loadSubTasksById(taskId: String): Flowable<List<Task>>

    @Query("SELECT * FROM task WHERE dueDateTimestamp = null")
    fun loadUnlimitedTasks(): Flowable<List<Task>>


    @Query("SELECT *, (SELECT Count(*) FROM task as sub_task WHERE parentId = parent_task.id ) as subTasksCount " +
            "FROM task as parent_task " +
            "WHERE ((dueDateTimestamp BETWEEN :from AND :to) OR " +
            "(dueDateTimestamp < :from AND isComplete = 0) OR " +
            "dueDateTimestamp is null) AND parentId is null")
    fun loadForTodayScreen(from: Date, to: Date): Flowable<List<Task>>

    @Query("SELECT Count (*) FROM task WHERE (dueDateTimestamp BETWEEN :from AND :to) AND parentId is null")
    fun getTasksCount(from: Date, to: Date): Flowable<Int>

    @Query("SELECT *, (SELECT Count(*) FROM task as sub_task WHERE parentId = parent_task.id ) as subTasksCount " +
            "FROM task as parent_task " +
            "WHERE dueDateTimestamp BETWEEN :from AND :to AND parentId is null")
    fun getTasksByDate(from: Date, to: Date): Flowable<List<Task>>

    @Query("SELECT * FROM task WHERE parentId IN (:parentTaskIds) AND id not null")
    fun loadSubTasks(parentTaskIds: List<String>): List<Task>

    @Query("SELECT * FROM task WHERE (dueDateTimestamp BETWEEN :from AND :to) AND isComplete = :isComplete")
    fun getTasksByDate(from: Date, to: Date, isComplete: Boolean): Flowable<List<Task>>

    @Query("SELECT * FROM task WHERE (dueDateTimestamp BETWEEN :from AND :to) AND priorityId = :priority")
    fun getTasksByDate(from: Date, to: Date, priority: TaskPriority): Flowable<List<Task>>

    /*@Query("SELECT * FROM task WHERE endDateTimestamp >= :startDayDate AND endDateTimestamp < :to AND statusId = 0")
    fun loadExpiredTasksForDay(startDayDate: Date, currentDate: Date): List<Task>

    @Query("SELECT * FROM task WHERE startDateTimestamp >= :dayDate AND endDateTimestamp < :dayDate AND priorityId = :priority")
    fun loadTasksForDayByPriority(dayDate: Date, priority: TaskPriority): List<Task>*/

    @Query("SELECT * FROM task WHERE title LIKE :title LIMIT 1")
    fun findByTitle(title: String): Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(task: Task)

    @Query("UPDATE task SET isComplete = :isComplete WHERE id = :taskId")
    fun updateTaskStatus(taskId: String, isComplete: Boolean)

    @Delete
    fun delete(task: Task)

    @Query("DELETE FROM task")
    fun deleteAll()
}