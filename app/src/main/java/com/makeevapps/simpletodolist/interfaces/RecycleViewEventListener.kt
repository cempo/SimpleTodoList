package com.makeevapps.simpletodolist.interfaces

import android.view.View

interface RecycleViewEventListener {
    fun onItemSwipeRight(position: Int)

    fun onItemSwipeLeft(position: Int)

    fun onItemClicked(v: View?, position: Int)
}