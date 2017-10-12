@file:JvmName("IntExtensions")

package com.makeevapps.simpletodolist.utils.extension

import android.content.Context
import com.makeevapps.simpletodolist.utils.UIUtils


fun Int.dpToPx(context: Context): Int {
    return UIUtils.convertDpToPx(context, this)
}
