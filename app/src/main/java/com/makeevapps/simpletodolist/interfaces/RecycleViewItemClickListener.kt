package com.makeevapps.simpletodolist.interfaces

import android.view.View

interface RecycleViewItemClickListener {
    fun onItemClick(view: View, position: Int)
}