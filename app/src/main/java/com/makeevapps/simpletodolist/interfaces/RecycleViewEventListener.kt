package com.makeevapps.simpletodolist.interfaces

import android.view.View
import com.makeevapps.simpletodolist.dataproviders.TaskDataProvider

interface RecycleViewEventListener {
    fun onItemSwipeRight(position: Int, newPosition: Int?, item: TaskDataProvider.TaskData)

    fun onItemSwipeLeft(position: Int)

    fun onItemClicked(v: View?, position: Int)
}