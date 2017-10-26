package com.makeevapps.simpletodolist.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivityEditTaskBinding
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.interfaces.CheckBoxCheckedListener
import com.makeevapps.simpletodolist.interfaces.RecycleViewItemClickListener
import com.makeevapps.simpletodolist.ui.adapter.SubTaskAdapter
import com.makeevapps.simpletodolist.ui.dialog.CancelChangesDialog
import com.makeevapps.simpletodolist.ui.dialog.DateTimePickerDialog
import com.makeevapps.simpletodolist.ui.dialog.EditSubTaskDialog
import com.makeevapps.simpletodolist.ui.dialog.EditTaskPriorityDialog
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.ToastUtils
import com.makeevapps.simpletodolist.utils.extension.asString
import com.makeevapps.simpletodolist.utils.extension.endDayDate
import com.makeevapps.simpletodolist.viewmodel.EditTaskViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class EditTaskActivity : BaseActivity(), RecycleViewItemClickListener, CheckBoxCheckedListener {
    lateinit var model: EditTaskViewModel
    lateinit var binding: ActivityEditTaskBinding
    lateinit var subTaskAdapter: SubTaskAdapter

    companion object {
        fun getActivityIntent(context: Context, taskId: String? = null, dueDate: Date? = null): Intent {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("taskId", taskId)
            if (dueDate != null) {
                intent.putExtra("dueDate", dueDate.time)
            }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this).get(EditTaskViewModel::class.java)
        setTheme(model.getThemeResId())
        super.onCreate(savedInstanceState)

        val taskId = intent.extras.getString("taskId")
        val longDate = intent.extras.getLong("dueDate")
        val dueDate = if (longDate > 0) Date(longDate).endDayDate() else null

        model.loadTask(taskId, dueDate)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_task)
        binding.controller = this
        binding.model = model

        setSupportActionBar(toolbar, true, true, null)

        subTaskAdapter = SubTaskAdapter(this, this)

        binding.scrollView.isNestedScrollingEnabled = false
        binding.subTasksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.subTasksRecyclerView.adapter = subTaskAdapter
        binding.subTasksRecyclerView.isNestedScrollingEnabled = false

        observeTaskResponse()
        observeSubTaskResponse()
    }

    private fun observeTaskResponse() {
        model.getTaskResponse().observe(this, Observer<Task> { task ->
            if (task != null) {
                binding.task = task
                Logger.e("Refresh task")
            }
        })
    }

    private fun observeSubTaskResponse() {
        model.getSubTaskResponse().observe(this, Observer<List<Task>> { tasks ->
            subTaskAdapter.clearAndAdd(tasks)
            Logger.e("Refresh sub task")
        })
    }

    fun onTitleTextChanged(text: CharSequence) {
        model.newTask.title = text.toString()
    }

    fun onDescriptionTextChanged(text: CharSequence) {
        model.newTask.description = text.toString()
    }

    fun onDateTimeButtonClick(view: View?) {
        val currentDate = model.newTask.dueDate
        val isAllDay = model.newTask.allDay

        val dateTimePicker = DateTimePickerDialog(currentDate, isAllDay, onDateSelected = { date, allDay ->

            model.newTask.allDay = allDay
            model.newTask.dueDate = date

            binding.task = model.newTask
        }, onCanceled = {

        })
        dateTimePicker.show(supportFragmentManager, "DateTimePickerDialog")
    }

    fun onRemoveDateButtonClick(view: View?) {
        model.newTask.allDay = true
        model.newTask.dueDate = null

        binding.task = model.newTask
    }

    fun changeTaskPriority(view: View?) {
        EditTaskPriorityDialog.showDialog(this, model.newTask.priority, onSuccess = { priority ->
            model.newTask.priority = priority
            binding.task = model.newTask
        })
    }

    override fun onItemClick(view: View, position: Int) {
        val task = subTaskAdapter.getItem(position)
        showAddSubTaskDialog(task)
    }

    override fun onCheckBoxChecked(view: View, position: Int, isChecked: Boolean) {
        ToastUtils.showSimpleToast(this, "Position: $position")

        val task = subTaskAdapter.getItem(position)
        model.insertOrUpdateSubTask(task)
    }

    private fun showAddSubTaskDialog(existSubTask: Task?) {
        EditSubTaskDialog.showDialog(this, existSubTask, onSuccess = { subTask ->
            subTask.parentId = model.newTask.id
            model.insertOrUpdateSubTask(subTask)
        }, onDelete = {
            model.deleteSubTask(existSubTask)
        })
    }

    fun getDueDate(task: Task?): String {
        return if (task != null && task.isPlaned()) {
            if (task.allDay) {
                task.dueDate!!.asString(DateUtils.SHORT_DATE_FORMAT3)
            } else {
                task.dueDate!!.asString(DateUtils.DATE_TIME_FORMAT)
            }
        } else {
            getString(R.string.select_date)
        }
    }

    fun saveTask(view: View?) {
        model.insertOrUpdateTask(onSuccess = {
            finish()
        }, onError = { message ->
            ToastUtils.showSimpleToast(this, message)
        })
    }

    fun addSubTaskButtonClick(view: View?) {
        showAddSubTaskDialog(null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.delete -> model.deleteTask(onSuccess = { finish() })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (model.newTask != model.oldTask) {
            CancelChangesDialog.showDialog(this, onSuccess = {
                super.onBackPressed()
            })
        } else {
            super.onBackPressed()
        }
    }
}