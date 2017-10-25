@file:JvmName("DateExtensions")

package com.makeevapps.simpletodolist.utils.extension

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private val calendar: Calendar by lazy {
    Calendar.getInstance()
}

fun Date.startDayDate(): Date {
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.endDayDate(): Date {
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}

fun Date.asString(format: DateFormat): String = format.format(this)

fun Date.asString(format: String): String = asString(SimpleDateFormat(format, Locale.US))