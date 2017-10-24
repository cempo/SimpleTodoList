package com.makeevapps.simpletodolist.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivitySnoozeBinding
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.ui.dialog.DateTimePickerDialog
import com.makeevapps.simpletodolist.viewmodel.SnoozeViewModel
import com.orhanobut.logger.Logger

class SnoozeActivity : AppCompatActivity() {
    lateinit var model: SnoozeViewModel
    lateinit var binding: ActivitySnoozeBinding

    companion object {
        fun getActivityIntent(context: Context, taskId: String): Intent {
            val intent = Intent(context, SnoozeActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("taskId", taskId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this).get(SnoozeViewModel::class.java)
        super.onCreate(savedInstanceState)

        title = ""

        val taskId = intent.extras.getString("taskId")

        model.loadTask(taskId)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_snooze)
        binding.controller = this
        binding.model = model

        observeTaskResponse()
    }

    private fun observeTaskResponse() {
        model.getTaskResponse().observe(this, Observer<Task> { task ->
            if (task != null) {
                binding.task = task

                showDateTimePicker()
            }
        })
    }

    fun showDateTimePicker() {
        val isAllDay = model.task.allDay
        val currentDate = model.task.dueDate

        val dateTimePicker = DateTimePickerDialog(currentDate, isAllDay, onDateSelected = { date, allDay ->

            model.task.allDay = allDay
            model.task.dueDate = date

            model.insertOrUpdateTask(onSuccess = {
                finish()
            })
        }, onCanceled = {
            finish()
        })
        dateTimePicker.show(supportFragmentManager, "DateTimePickerDialog")
    }
}