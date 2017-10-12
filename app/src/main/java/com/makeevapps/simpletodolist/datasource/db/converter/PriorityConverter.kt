package com.makeevapps.simpletodolist.datasource.db.converter

import android.arch.persistence.room.TypeConverter
import com.makeevapps.simpletodolist.enums.TaskPriority

class PriorityConverter {
    @TypeConverter
    fun toStatus(priorityId: Int): TaskPriority = TaskPriority.getPriorityById(priorityId)


    @TypeConverter
    fun toInteger(priority: TaskPriority): Int = priority.typeId
}