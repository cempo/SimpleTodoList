package com.makeevapps.simpletodolist.datasource.db.table

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.makeevapps.simpletodolist.enums.TaskPriority
import com.makeevapps.simpletodolist.utils.DateUtils
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "task")
class Task() {
    @Ignore
    constructor(title: String) : this() {
        this.id = UUID.randomUUID().toString()
        this.title = title
    }

    @PrimaryKey
    var id: String = ""

    @ColumnInfo(name = "orderId")
    var orderId: Int = 0

    @ColumnInfo(name = "parentId")
    var parentId: String? = null

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "isComplete")
    var isComplete: Boolean = false

    @ColumnInfo(name = "priorityId")
    var priority: TaskPriority = TaskPriority.defaultValue()

    @ColumnInfo(name = "dueDateTimestamp")
    var dueDate: Date? = null

    @ColumnInfo(name = "doneDateTimestamp")
    var doneDate: Date? = null

    @ColumnInfo(name = "notifyDateTimestamp")
    var notifyDate: Date? = null

    @ColumnInfo(name = "allDay")
    var allDay: Boolean = true

    //@Relation(parentColumn = "id", entityColumn = "parentId", entity = Task::class)
    @Ignore
    var subTasks: ArrayList<Task> = ArrayList()

    var subTasksCount: Int = 0

    fun isDone(): Boolean = isComplete

    fun isExpired(): Boolean {
        val currentDate = DateUtils.currentTime()
        return isExpiredForDate(currentDate)
    }

    fun isExpiredBeforeToday(): Boolean {
        val startDayDate = DateUtils.startCurrentDayDate()
        return isExpiredForDate(startDayDate)
    }

    fun isExpiredForCurrentTime(): Boolean {
        val currentDate = DateUtils.currentTime()
        return isExpiredForDate(currentDate)
    }

    private fun isExpiredForDate(date: Date): Boolean = dueDate != null && dueDate!!.before(date)

    fun isNotPlaned() = dueDate == null

    fun isPlaned() = dueDate != null

    fun isPlanedForToday(): Boolean = isPlanedForDay(DateUtils.currentTime())

    fun isPlanedForDay(date: Date): Boolean {
        val startDayDate = DateUtils.startDayDate(date)
        val endDayDate = DateUtils.endDayDate(date)
        return dueDate != null && dueDate!! > startDayDate && dueDate!! <= endDayDate
    }

    fun switchDoneState() {
        if (isDone()) {
            isComplete = false
            doneDate = null
        } else {
            isComplete = true
            doneDate = DateUtils.currentTime()
        }
    }

    fun markAsDone() {
        isComplete = true
        doneDate = DateUtils.currentTime()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false
        if (orderId != other.orderId) return false
        if (parentId != other.parentId) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (isComplete != other.isComplete) return false
        if (priority != other.priority) return false
        if (dueDate != other.dueDate) return false
        if (doneDate != other.doneDate) return false
        if (notifyDate != other.notifyDate) return false
        if (allDay != other.allDay) return false
        if (subTasks != other.subTasks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + orderId
        result = 31 * result + (parentId?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + isComplete.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + (dueDate?.hashCode() ?: 0)
        result = 31 * result + (doneDate?.hashCode() ?: 0)
        result = 31 * result + (notifyDate?.hashCode() ?: 0)
        result = 31 * result + allDay.hashCode()
        result = 31 * result + subTasks.hashCode()
        return result
    }

    override fun toString(): String {
        return "Task(id='$id', parentId=$parentId, title='$title')"
    }
}