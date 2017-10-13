package com.makeevapps.simpletodolist.dataproviders

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import com.makeevapps.simpletodolist.datasource.db.table.Task
import java.util.*

class TaskDataProvider : AbstractDataProvider {
    private var data: MutableList<ConcreteData> = LinkedList()
    private var lastRemovedData: ConcreteData? = null
    private var lastRemovedPosition = -1

    private var lastMovedFromPosition = -1
    private var lastMovedToPosition = -1

    constructor() {
        data = LinkedList()
    }

    constructor(tasks: List<Task>) {
        (0 until tasks.size)
                .mapTo(data) { ConcreteData(it.toLong(), tasks[it]) }
                .sortWith(taskComparator)
    }

    private val taskComparator = compareBy<ConcreteData> { it.task.doneDate }
            .thenByDescending { it.task.priority.typeId }
            .thenBy { it.task.dueDate }
            .thenBy { it.task.allDay }
            .thenBy { it.task.title }

    override fun getCount() = data.size

    override fun getItem(index: Int): AbstractDataProvider.Data {
        if (index < 0 || index >= getCount()) {
            throw IndexOutOfBoundsException("index = " + index)
        }

        return data[index]
    }

    fun getValidPosition(concreteData: ConcreteData): Int? {
        var toPosition: Int? = null

        val task = concreteData.task
        if ((task.isPlanedForToday() || task.isNotPlaned() || task.isExpiredForCurrentTime() || task.isDone()) &&
                !task.isExpiredBeforeToday()) {
            val tempDataList = LinkedList(data)

            if (!tempDataList.contains(concreteData)) {
                tempDataList.add(concreteData)
            }
            val dataList = tempDataList.sortedWith(taskComparator)

            toPosition = dataList.indexOf(concreteData)
        }

        return toPosition
    }

    override fun undoLastRemoval(): Int {
        if (lastRemovedData != null) {
            val insertedData = lastRemovedData!!
            val insertedPosition: Int = if (lastRemovedPosition >= 0 && lastRemovedPosition < data.size) {
                lastRemovedPosition
            } else {
                data.size
            }

            data.add(insertedPosition, insertedData)

            lastRemovedData = null
            lastRemovedPosition = -1

            return insertedPosition
        } else {
            return -1
        }
    }

    fun undoLastMovement(): Long {
        val insertedPosition = if (lastMovedFromPosition >= 0 && lastMovedFromPosition < data.size) {
            lastMovedFromPosition
        } else {
            data.size
        }

        if (lastMovedToPosition != -1) {
            val itemData = data.removeAt(lastMovedToPosition)
            data.add(insertedPosition, itemData)
        }

        lastMovedToPosition = -1
        lastMovedFromPosition = -1

        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(insertedPosition)
    }

    override fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }

        val lastMovedData = data.removeAt(fromPosition)
        lastMovedFromPosition = fromPosition
        lastMovedToPosition = toPosition

        data.add(toPosition, lastMovedData)
        lastRemovedPosition = -1
    }

    override fun swapItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }

        Collections.swap(data, toPosition, fromPosition)
        lastRemovedPosition = -1
    }

    override fun removeItem(position: Int) {

        val removedItem = data.removeAt(position)

        lastRemovedData = removedItem
        lastRemovedPosition = position
    }

    class ConcreteData internal constructor(override val id: Long, override val task: Task) :
            AbstractDataProvider.Data() {

        override var isPinned: Boolean = false
        override val isSectionHeader = false

        override fun toString(): String = task.title
    }
}
