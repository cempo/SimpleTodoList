package com.makeevapps.simpletodolist.dataproviders

import com.makeevapps.simpletodolist.datasource.db.table.Task
import java.util.*

class TaskDataProvider : AbstractDataProvider {
    private var data: MutableList<ConcreteData> = LinkedList()
    private var lastRemovedData: ConcreteData? = null
    private var lastRemovedPosition = -1

    constructor() {
        data = LinkedList()
    }

    constructor(tasks: List<Task>) {
        (0 until tasks.size)
                .mapTo(data) { ConcreteData(it.toLong(), tasks[it]) }
    }

    override fun getCount() = data.size

    override fun getItem(index: Int): AbstractDataProvider.Data {
        if (index < 0 || index >= getCount()) {
            throw IndexOutOfBoundsException("index = " + index)
        }

        return data[index]
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

    override fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            return
        }

        val item = data.removeAt(fromPosition)

        data.add(toPosition, item)
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
