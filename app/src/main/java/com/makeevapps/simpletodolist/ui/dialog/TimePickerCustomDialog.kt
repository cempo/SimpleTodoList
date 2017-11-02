package com.makeevapps.simpletodolist.ui.dialog

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.makeevapps.simpletodolist.Keys
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ViewTimePickerAllDayButtonBinding
import com.makeevapps.simpletodolist.viewmodel.TimePickerViewModel
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class TimePickerCustomDialog : TimePickerDialog(), TimePickerDialog.OnTimeSetListener {
    lateinit var binding: ViewTimePickerAllDayButtonBinding
    lateinit var onTimeSelected: (date: Date?) -> Unit

    companion object {
        val TAG: String = TimePickerDialog::class.java.simpleName

        fun newInstance(date: Date?, onTimeSelected: (date: Date?) -> Unit): TimePickerCustomDialog {
            val fragment = TimePickerCustomDialog()
            fragment.setListener(onTimeSelected)

            val args = Bundle()
            date?.let { args.putLong(Keys.KEY_DUE_DATE_IN_MILLIS, it.time) }
            fragment.arguments = args

            return fragment
        }
    }

    val model: TimePickerViewModel by lazy {
        ViewModelProviders.of(this).get(TimePickerViewModel::class.java)
    }

    init {
        setOnTimeSetListener(this)
        version = Version.VERSION_1
    }

    fun setListener(onTimeSelected: (date: Date?) -> Unit) {
        this.onTimeSelected = onTimeSelected
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState == null) {
            model.initData(arguments!!)

            val calendar = model.calendar
            initialize(this, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    0,
                    model.is24HoursFormat)
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        val view = super.onCreateView(inflater, container, state)
        val buttonContainer = view!!.findViewById<LinearLayout>(com.wdullaer.materialdatetimepicker.R.id.mdtp_done_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.view_time_picker_all_day_button, buttonContainer, false)
        binding.controller = this

        buttonContainer.addView(binding.root, 0)

        return view
    }

    fun onAllDayButtonClick(view: View?) {
        onTimeSelected(null)
        dismiss()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        model.setTimeToCalendar(hourOfDay, minute, second)
        onTimeSelected(model.calendar.time)
    }
}