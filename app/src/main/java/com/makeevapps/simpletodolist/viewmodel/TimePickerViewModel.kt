package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Bundle
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.Keys
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.utils.DateUtils
import java.util.*
import javax.inject.Inject

class TimePickerViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    lateinit var calendar: Calendar

    val is24HoursFormat: Boolean

    init {
        App.component.inject(this)

        is24HoursFormat = preferenceManager.is24HourFormat()
    }

    /**
     * Init logic
     * */
    fun getThemeResId(): Int = ThemeStyle.getThemeById(preferenceManager.getThemeId()).themeResId

    fun initData(arguments: Bundle) {
        val longDate = arguments.getLong(Keys.KEY_DUE_DATE_IN_MILLIS)
        val oldDate = if (longDate > 0) Date(longDate) else null

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = oldDate ?: DateUtils.endCurrentDayDate()
        calendar = tempCalendar
    }

    fun setTimeToCalendar(hourOfDay: Int, minute: Int, second: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }
}