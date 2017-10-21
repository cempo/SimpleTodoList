package com.makeevapps.simpletodolist.utils

import java.util.*

object DateUtils {
    const val DATE_TIME_FORMAT = "d MMM HH:mm"
    const val FULL_DATE_FORMAT = "MMMM-dd-yyyy"
    const val SHORT_DATE_FORMAT = "MMM d"
    const val SHORT_DATE_FORMAT2 = "MMM d EEE"
    const val SHORT_DATE_FORMAT3 = "d MMM"
    const val TIME_FORMAT = "HH:mm"

    fun currentTime(): Date = Calendar.getInstance().time

    fun startDayDate(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun endDayDate(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    fun startCurrentDayDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun endCurrentDayDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    /* Calculation */
    fun changeDate(date: Date, addDays: Int, addHours: Int, addMinutes: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_MONTH, addDays)
        cal.add(Calendar.HOUR_OF_DAY, addHours)
        cal.add(Calendar.MINUTE, addMinutes)
        return cal.time
    }

    fun setCurrentTime(date: Date): Date {
        val currentCal = Calendar.getInstance()
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.HOUR_OF_DAY, currentCal.get(Calendar.HOUR_OF_DAY))
        cal.add(Calendar.MINUTE, currentCal.get(Calendar.MINUTE))
        cal.add(Calendar.SECOND, 0)
        cal.add(Calendar.MILLISECOND, 0)
        return cal.time
    }
}