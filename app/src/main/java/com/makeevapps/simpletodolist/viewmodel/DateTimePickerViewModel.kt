package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.Bundle
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.Keys.KEY_ALL_DAY
import com.makeevapps.simpletodolist.Keys.KEY_DUE_DATE_IN_MILLIS
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.extension.asString
import com.makeevapps.simpletodolist.utils.extension.toEndDay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject

class DateTimePickerViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    val timeText = ObservableField<String>()
    val allDay = ObservableBoolean()
    internal val calendar: BehaviorSubject<Calendar> = BehaviorSubject.create()

    private val compositeDisposable = CompositeDisposable()
    private val is24HoursFormat: Boolean

    init {
        App.component.inject(this)

        is24HoursFormat = preferenceManager.is24HourFormat()

        compositeDisposable.add(calendar.subscribe({
            val timeString = calendar.value.time?.let {
                if (is24HoursFormat) {
                    it.asString(DateUtils.TIME_24H_FORMAT)
                } else {
                    it.asString(DateUtils.TIME_12H_FORMAT)
                }
            }
            timeText.set(timeString)
        }))
    }

    /**
     * Init logic
     * */
    fun initData(arguments: Bundle) {
        val longDate = arguments.getLong(KEY_DUE_DATE_IN_MILLIS)
        val oldDate = if (longDate > 0) Date(longDate) else null
        val allDay = arguments.getBoolean(KEY_ALL_DAY, true)

        val tempCalendar = Calendar.getInstance()
        if (oldDate != null) {
            tempCalendar.time = oldDate
            this.allDay.set(allDay)
        } else {
            tempCalendar.time = DateUtils.endCurrentDayDate()
            this.allDay.set(true)
        }
        calendar.onNext(tempCalendar)
    }

    fun setDateToCalendar(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val tempCalendar = calendar.value
        tempCalendar.set(Calendar.YEAR, year)
        tempCalendar.set(Calendar.MONTH, monthOfYear)
        tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.onNext(tempCalendar)
    }

    fun setTimeToCalendar(date: Date?) {
        if (date != null) {
            val tempCalendar = Calendar.getInstance()
            tempCalendar.time = date

            calendar.onNext(tempCalendar)
            allDay.set(false)
        } else {
            calendar.onNext(calendar.value.toEndDay())
            allDay.set(true)
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}