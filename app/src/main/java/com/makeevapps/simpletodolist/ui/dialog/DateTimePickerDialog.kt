package com.makeevapps.simpletodolist.ui.dialog

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.makeevapps.simpletodolist.Keys.KEY_ALL_DAY
import com.makeevapps.simpletodolist.Keys.KEY_DUE_DATE_IN_MILLIS
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ViewDatePickerTimeButtonBinding
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.ToastUtils
import com.makeevapps.simpletodolist.viewmodel.DateTimePickerViewModel
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*


class DateTimePickerDialog : DatePickerDialog(), DatePickerDialog.OnDateSetListener {
    lateinit var binding: ViewDatePickerTimeButtonBinding
    lateinit var onDateSelected: (date: Date, allDay: Boolean) -> Unit
    lateinit var onCanceled: () -> Unit

    companion object {
        val TAG: String = DateTimePickerDialog::class.java.simpleName
        fun newInstance(oldDate: Date?, allDay: Boolean,
                        onDateSelected: (date: Date, allDay: Boolean) -> Unit,
                        onCanceled: () -> Unit): DateTimePickerDialog {
            val fragment = DateTimePickerDialog()
            fragment.setListeners(onDateSelected, onCanceled)
            val args = Bundle()
            oldDate?.let { args.putLong(KEY_DUE_DATE_IN_MILLIS, it.time) }
            args.putBoolean(KEY_ALL_DAY, allDay)
            fragment.arguments = args
            return fragment
        }
    }

    val model: DateTimePickerViewModel by lazy {
        ViewModelProviders.of(this).get(DateTimePickerViewModel::class.java)
    }

    init {
        setOnDateSetListener(this)
        setVersion(Version.VERSION_1)
        minDate = Calendar.getInstance()
        //setCancelText(R.string.clear)
    }

    fun setListeners(onDateSelected: (date: Date, allDay: Boolean) -> Unit, onCanceled: () -> Unit) {
        this.onDateSelected = onDateSelected
        this.onCanceled = onCanceled
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState == null) {
            model.initData(arguments!!)

            val calendar = model.calendar.value
            initialize(this, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
        }

        val timePickerDialog = fragmentManager?.findFragmentByTag(TimePickerCustomDialog.TAG)
        if (timePickerDialog != null) {
            (timePickerDialog as TimePickerCustomDialog).setListener(onTimeSelected)
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        val view = super.onCreateView(inflater, container, state)
        val buttonContainer = view!!.findViewById<LinearLayout>(com.wdullaer.materialdatetimepicker.R.id.mdtp_done_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.view_date_picker_time_button, buttonContainer, false)
        binding.model = model
        binding.controller = this

        buttonContainer.addView(binding.root, 0)

        return view
    }

    fun setTime(view: View) {
        val allDay = model.allDay.get()
        val selectedDate = model.calendar.value.time
        val startDate = if (allDay) DateUtils.setCurrentTime(selectedDate) else selectedDate

        TimePickerCustomDialog.newInstance(startDate, onTimeSelected).show(fragmentManager, TimePickerCustomDialog.TAG)
    }

    private var onTimeSelected: (date: Date?) -> Unit = { date ->
        model.setTimeToCalendar(date)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        model.setDateToCalendar(year, monthOfYear, dayOfMonth)

        val selectedDate = model.calendar.value.time
        if (selectedDate.after(DateUtils.currentTime())) {
            onDateSelected(selectedDate, model.allDay.get())
        } else {
            ToastUtils.showSimpleToast(context!!, R.string.date_in_the_past)
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        onCanceled()
        super.onCancel(dialog)
    }
}
