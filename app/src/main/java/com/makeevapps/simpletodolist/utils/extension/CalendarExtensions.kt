@file:JvmName("CalendarExtensions")

package com.makeevapps.simpletodolist.utils.extension

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toStartDay(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
    return this
}

fun Calendar.toEndDay(): Calendar {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
    return this
}

fun Calendar.asString(format: DateFormat): String = format.format(this.time)

fun Calendar.asString(format: String): String = asString(SimpleDateFormat(format, Locale.US))