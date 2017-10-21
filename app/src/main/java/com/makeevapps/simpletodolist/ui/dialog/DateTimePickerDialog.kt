package com.makeevapps.simpletodolist.ui.dialog

import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ViewDatePickerTimeButtonBinding
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.extension.asString
import com.makeevapps.simpletodolist.utils.extension.endDayDate
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*


class DateTimePickerDialog() : DatePickerDialog(), DatePickerDialog.OnDateSetListener {
    var currentDate: Date? = null
    lateinit var newDateCalendar: Calendar
    lateinit var binding: ViewDatePickerTimeButtonBinding
    lateinit var onDateSelected: (date: Date, allDay: Boolean) -> Unit
    lateinit var onCanceled: () -> Unit
    var allDay: Boolean = false

    init {
        setOnDateSetListener(this)
    }

    constructor(oldDate: Date?, allDay: Boolean, onDateSelected: (date: Date, allDay: Boolean) -> Unit,
                onCanceled: () -> Unit) : this() {
        this.currentDate = oldDate
        this.allDay = allDay
        this.onDateSelected = onDateSelected
        this.onCanceled = onCanceled

        val calendar = Calendar.getInstance()
        if (oldDate != null) {
            calendar.time = oldDate

            initialize(this, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
        } else {
            calendar.time = DateUtils.endCurrentDayDate()

            initialize(this, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
        }

        newDateCalendar = calendar

        setVersion(Version.VERSION_1)
        minDate = Calendar.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        val view = super.onCreateView(inflater, container, state)
        val buttonContainer = view!!.findViewById<LinearLayout>(com.wdullaer.materialdatetimepicker.R.id.mdtp_done_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.view_date_picker_time_button, buttonContainer, false)
        binding.controller = this

        buttonContainer.addView(binding.root, 0)

        if (!allDay) {
            binding.time.text = currentDate?.asString(DateUtils.TIME_FORMAT)
        }

        return view
    }

    fun onTimeClickButton(view: View?) {
        val startDate = if (allDay) DateUtils.setCurrentTime(newDateCalendar.time) else newDateCalendar.time
        TimePickerDialog(startDate, onTimeSelected = { date ->
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                newDateCalendar = calendar
                allDay = false

                binding.time.text = calendar.time.asString(DateUtils.TIME_FORMAT)
            } else {
                newDateCalendar.time.endDayDate()

                allDay = true

                binding.time.text = getString(R.string.time)
            }
        }).show(fragmentManager, "TimePickerDialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        newDateCalendar.set(Calendar.YEAR, year)
        newDateCalendar.set(Calendar.MONTH, monthOfYear)
        newDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        //if (newDateCalendar.time.after(DateUtils.currentTime())) {
        onDateSelected(newDateCalendar.time, allDay)
        //dismiss()
        /*} else {
            ToastUtils.showSimpleToast(context, R.string.date_in_the_past)
        }*/
    }

    override fun onCancel(dialog: DialogInterface?) {
        onCanceled()
        super.onCancel(dialog)
    }
}
