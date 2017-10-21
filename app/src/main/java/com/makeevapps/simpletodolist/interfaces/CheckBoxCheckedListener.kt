package com.makeevapps.simpletodolist.interfaces

import android.view.View

interface CheckBoxCheckedListener {
    fun onCheckBoxChecked(view: View, position: Int, isChecked: Boolean)
}