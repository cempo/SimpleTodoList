package com.makeevapps.simpletodolist.ui.activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.makeevapps.simpletodolist.Keys.KEY_POSITION
import com.makeevapps.simpletodolist.Keys.KEY_TASK_ID
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivitySnoozeBinding
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.viewmodel.SnoozeViewModel

class SnoozeActivity : AppCompatActivity() {
    companion object {
        val SNOOZE_DATE_REQUEST_CODE = 444

        fun getActivityIntent(context: Context, taskId: String, newTask: Boolean, position: Int? = null): Intent {
            val intent = Intent(context, SnoozeActivity::class.java)
            if (newTask) {
                intent.flags = FLAG_ACTIVITY_NEW_TASK
            }
            intent.putExtra(KEY_TASK_ID, taskId)
            intent.putExtra(KEY_POSITION, position)
            return intent
        }
    }

    val model: SnoozeViewModel by lazy {
        ViewModelProviders.of(this).get(SnoozeViewModel::class.java)
    }

    val binding: ActivitySnoozeBinding by lazy {
        DataBindingUtil.setContentView<ActivitySnoozeBinding>(this, R.layout.activity_snooze)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = ""

        model.initTask(intent.extras)

        binding.controller = this
        binding.model = model

        if (savedInstanceState == null) {
            observeTaskResponse()
        }
    }

    private fun observeTaskResponse() {
        model.getTaskResponse().observe(this, Observer<Task> { task ->
            if (task != null) {
                binding.task = task

                val dueDate = model.task.dueDate
                val isAllDay = model.task.allDay

                startActivityForResult(DateTimeChooserActivity.getActivityIntent(this, dueDate, isAllDay),
                        DateTimeChooserActivity.CHOOSE_DATE_REQUEST_CODE)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DateTimeChooserActivity.CHOOSE_DATE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    model.updateDueDate(it.extras)
                    model.insertOrUpdateTask(onSuccess = {
                        setResult(Activity.RESULT_OK, model.resultIntent())
                        finish()
                    })
                }
            } else {
                setResult(Activity.RESULT_CANCELED, model.resultIntent())
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}