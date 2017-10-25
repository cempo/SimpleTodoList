package com.makeevapps.simpletodolist.dataproviders

import com.makeevapps.simpletodolist.datasource.db.table.Task
import java.math.BigInteger
import java.util.*

class TaskDataProvider : AbstractDataProvider() {
    private var data: MutableList<TaskData> = LinkedList()

    fun setData(tasks: List<Task>) {
        clearData()
        (0 until tasks.size)
                .mapTo(data) {
                    val task = tasks[it]
                    TaskData(prepareId(task.id.hashCode()), task)
                }
                .sortWith(taskComparator)
    }

    private fun prepareId(value: Int): Long {
        val byteArray = byteArrayOf((value shr 16).toByte(), (value shr 8).toByte(), value.toByte())
        return BigInteger(1, byteArray).toLong()
    }

    fun clearData() {
        data.clear()
    }


    private val taskComparator = compareBy<TaskData> { it.task.isComplete }
            .thenByDescending { it.task.doneDate }
            .thenBy { it.task.dueDate }
            .thenByDescending { it.task.priority.typeId }
            .thenBy { it.task.allDay }
            .thenBy { it.task.title }

    override fun getCount() = data.size

    override fun getItem(index: Int): AbstractDataProvider.Data {
        if (index < 0 || index >= getCount()) {
            throw IndexOutOfBoundsException("index = " + index)
        }

        return data[index]
    }

    fun getValidPosition(taskData: TaskData): Int? {
        var toPosition: Int? = null

        val task = taskData.task
        if ((task.isPlanedForToday() || task.isNotPlaned() || task.isExpiredForCurrentTime() || task.isDone()) &&
                !task.isExpiredBeforeToday()) {
            val tempDataList = LinkedList(data)

            if (!tempDataList.contains(taskData)) {
                tempDataList.add(taskData)
            }
            val dataList = tempDataList.sortedWith(taskComparator)

            toPosition = dataList.indexOf(taskData)
        }

        return toPosition
    }

    class TaskData internal constructor(override val id: Long, override val task: Task) :
            AbstractDataProvider.Data() {

        override var isPinned: Boolean = false
        override val isSectionHeader = false

        override fun toString(): String = task.title
    }
}
