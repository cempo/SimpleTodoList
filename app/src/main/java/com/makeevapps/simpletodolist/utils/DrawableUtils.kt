package com.makeevapps.simpletodolist.utils

import android.graphics.drawable.Drawable

object DrawableUtils {
    private val EMPTY_STATE = intArrayOf()

    fun clearState(drawable: Drawable?) {
        if (drawable != null) {
            drawable.state = EMPTY_STATE
        }
    }
}
