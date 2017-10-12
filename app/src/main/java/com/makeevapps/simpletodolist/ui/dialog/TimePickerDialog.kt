package com.makeevapps.simpletodolist.ui.dialog

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ViewTimePickerNoTimeButtonBinding
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class TimePickerDialog() : TimePickerDialog(), TimePickerDialog.OnTimeSetListener {
    var currentDate: Date? = null
    lateinit var newDateCalendar: Calendar
    lateinit var binding: ViewTimePickerNoTimeButtonBinding
    lateinit var onTimeSelected: (date: Date?) -> Unit

    var is24TimeFormat = true

    init {
        setOnTimeSetListener(this)
    }

    constructor(date: Date?, onTimeSelected: (date: Date?) -> Unit) : this() {
        currentDate = date
        this.onTimeSelected = onTimeSelected

        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            newDateCalendar = calendar

            initialize(this, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    0,
                    is24TimeFormat)
        } else {
            val now = Calendar.getInstance()
            newDateCalendar = now

            initialize(this, now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    0,
                    is24TimeFormat)
        }

        version = Version.VERSION_1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        val view = super.onCreateView(inflater, container, state)
        val buttonContainer = view!!.findViewById<LinearLayout>(com.wdullaer.materialdatetimepicker.R.id.mdtp_done_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.view_time_picker_no_time_button, buttonContainer, false)
        binding.controller = this

        buttonContainer.addView(binding.root, 0)

        return view
    }

    fun onRemoveTimeClickButton(view: View?) {
        onTimeSelected(null)
        dismiss()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        newDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newDateCalendar.set(Calendar.MINUTE, minute)
        newDateCalendar.set(Calendar.SECOND, 0)
        newDateCalendar.set(Calendar.MILLISECOND, 0)
        onTimeSelected(newDateCalendar.time)
    }
}