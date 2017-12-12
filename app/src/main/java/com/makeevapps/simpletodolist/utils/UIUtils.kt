package com.makeevapps.simpletodolist.utils

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.util.TypedValue


object UIUtils {
    fun getClearFlags(): Int {
        return Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent
                .FLAG_ACTIVITY_CLEAR_TOP
    }

    fun getColorStateListResource(context: Context, colorResource: Int): ColorStateList {
        var color: ColorStateList
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.resources.getColorStateList(colorResource, context.theme)
        } else {
            color = context.resources.getColorStateList(colorResource)
        }
        return color
    }

    fun convertDpToPx(context: Context, dip: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun getDimen(context: Context, resId: Int): Int {
        return (context.resources.getDimension(resId) / context.resources.displayMetrics.density).toInt()
    }
}
