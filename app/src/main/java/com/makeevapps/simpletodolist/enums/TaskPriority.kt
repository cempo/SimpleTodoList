package com.makeevapps.simpletodolist.enums

import com.makeevapps.simpletodolist.R

enum class TaskPriority(var typeId: Int, var colorResId: Int, var textResId: Int) {
    NONE(0, R.color.none_priority, R.string.none_priority),
    LOW(1, R.color.low_priority, R.string.low_priority),
    MEDIUM(2, R.color.medium_priority, R.string.medium_priority),
    MAJOR(3, R.color.major_priority, R.string.major_priority),
    CRITICAL(4, R.color.critical_priority, R.string.critical_priority);

    companion object {
        fun getPriorityById(typeId: Int): TaskPriority {
            var resultStatus = defaultValue()
            TaskPriority.values()
                    .filter { typeId == it.typeId }
                    .forEach { resultStatus = it }
            return resultStatus
        }

        fun defaultValue(): TaskPriority = NONE
    }
}