package com.makeevapps.simpletodolist.ui.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.makeevapps.simpletodolist.Keys.KEY_ALL_DAY
import com.makeevapps.simpletodolist.Keys.KEY_DUE_DATE_IN_MILLIS
import com.makeevapps.simpletodolist.Keys.KEY_POSITION
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivityDateTimeChooserBinding
import com.makeevapps.simpletodolist.ui.dialog.DateTimePickerDialog
import com.makeevapps.simpletodolist.viewmodel.DateTimeChooserViewModel
import java.util.*

class DateTimeChooserActivity : AppCompatActivity() {
    companion object {
        val CHOOSE_DATE_REQUEST_CODE = 222

        fun getActivityIntent(context: Context?, dueDate: Date? = null, allDay: Boolean = true,
                              itemPosition: Int? = null): Intent {
            val intent = Intent(context!!, DateTimeChooserActivity::class.java)
            dueDate?.let { intent.putExtra(KEY_DUE_DATE_IN_MILLIS, it.time) }
            allDay.let { intent.putExtra(KEY_ALL_DAY, it) }
            itemPosition?.let { intent.putExtra(KEY_POSITION, it) }
            return intent
        }
    }

    val model: DateTimeChooserViewModel by lazy {
        ViewModelProviders.of(this).get(DateTimeChooserViewModel::class.java)
    }

    val binding: ActivityDateTimeChooserBinding by lazy {
        DataBindingUtil.setContentView<ActivityDateTimeChooserBinding>(
                this, R.layout.activity_date_time_chooser
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = ""

        model.handleArgs(intent.extras)
        binding.controller = this
        binding.model = model

        var dateTimePicker = supportFragmentManager.findFragmentByTag(DateTimePickerDialog.TAG)
        if (dateTimePicker != null) {
            (dateTimePicker as DateTimePickerDialog).setListeners(onDateSelected, onCanceled)
        } else {
            dateTimePicker = DateTimePickerDialog.newInstance(model.dueDate, model.allDay, onDateSelected, onCanceled)
            dateTimePicker.show(supportFragmentManager, DateTimePickerDialog.TAG)
        }
    }

    private var onDateSelected: (date: Date, allDay: Boolean) -> Unit = { date, allDay ->
        model.updateDueDate(date, allDay)
        setResult(Activity.RESULT_OK, model.successResultIntent())
        finish()
    }

    private var onCanceled: () -> Unit = {
        setResult(Activity.RESULT_CANCELED, model.cancelResultIntent())
        finish()
    }

}