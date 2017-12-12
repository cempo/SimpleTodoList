package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.Keys
import com.makeevapps.simpletodolist.Keys.KEY_ALL_DAY
import com.makeevapps.simpletodolist.Keys.KEY_DUE_DATE_IN_MILLIS
import com.makeevapps.simpletodolist.Keys.KEY_POSITION
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import java.util.*
import javax.inject.Inject

class DateTimeChooserViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val is24HoursFormat: Boolean

    var allDay: Boolean = true
    var dueDate: Date? = null
    var position: Int? = null

    init {
        App.component.inject(this)

        is24HoursFormat = preferenceManager.is24HourFormat()
    }

    fun updateDueDate(date: Date, allDay: Boolean) {
        this.dueDate = date
        this.allDay = allDay
    }

    fun handleArgs(extras: Bundle?) {
        if (extras != null && extras.containsKey(KEY_DUE_DATE_IN_MILLIS)) {
            val dueDateTimestamp = extras.getLong(KEY_DUE_DATE_IN_MILLIS)
            dueDate = Date(dueDateTimestamp)
        }

        if (extras != null && extras.containsKey(KEY_ALL_DAY)) {
            allDay = extras.getBoolean(KEY_ALL_DAY, true)
        }

        if (extras != null && extras.containsKey(KEY_POSITION)) {
            position = extras.getInt(KEY_POSITION, 0)
        }
    }

    fun successResultIntent(): Intent {
        val intent = Intent()
        dueDate?.let { intent.putExtra(KEY_DUE_DATE_IN_MILLIS, it.time) }
        intent.putExtra(KEY_ALL_DAY, allDay)
        position?.let { intent.putExtra(KEY_POSITION, it) }
        return intent
    }

    fun cancelResultIntent(): Intent {
        val intent = Intent()
        position?.let { intent.putExtra(Keys.KEY_POSITION, it) }
        return intent
    }
}