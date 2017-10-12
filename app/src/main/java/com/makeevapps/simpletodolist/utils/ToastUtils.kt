package com.makeevapps.simpletodolist.utils

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.Toast

object ToastUtils {

    fun showSimpleToast(context: Context, resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        showSimpleToast(context, Gravity.BOTTOM, resId, duration)
    }

    fun showSimpleToast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
        showSimpleToast(context, Gravity.BOTTOM, msg, duration)
    }

    fun showSimpleToast(context: Context?, garvity: Int, resId: Int, duration: Int) {
        if (context != null) {
            val toast = Toast.makeText(context, resId, duration)
            toast.setGravity(garvity, 0, 50)
            toast.show()
        }
    }

    fun showSimpleToast(context: Context?, garvity: Int, msg: String, duration: Int) {
        if (context != null && !TextUtils.isEmpty(msg)) {
            val toast = Toast.makeText(context, msg, duration)
            toast.setGravity(garvity, 0, 50)
            toast.show()
        }
    }


}
