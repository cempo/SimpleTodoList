package com.makeevapps.simpletodolist.binding

import android.databinding.BindingAdapter
import android.view.View

object Bindings {
    @BindingAdapter("isVisible")
    @JvmStatic
    fun setIsVisible(v: View, isVisible: Boolean) {
        v.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}