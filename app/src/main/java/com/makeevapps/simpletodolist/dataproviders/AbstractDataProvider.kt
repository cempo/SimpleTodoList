package com.makeevapps.simpletodolist.dataproviders

import com.makeevapps.simpletodolist.datasource.db.table.Task

abstract class AbstractDataProvider {

    abstract fun getCount(): Int

    abstract class Data {
        abstract val id: Long

        abstract val task: Task

        abstract val isSectionHeader: Boolean

        abstract var isPinned: Boolean
    }

    abstract fun getItem(index: Int): Data

}
