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
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.interfaces.RecycleViewItemClickListener
import com.makeevapps.simpletodolist.ui.adapter.SubTaskAdapter
import com.makeevapps.simpletodolist.ui.dialog.CancelChangesDialog
import com.makeevapps.simpletodolist.ui.dialog.DateTimePickerDialog
import com.makeevapps.simpletodolist.ui.dialog.EditSubTaskDialog
import com.makeevapps.simpletodolist.ui.dialog.EditTaskPriorityDialog
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.ToastUtils
import com.makeevapps.simpletodolist.utils.extension.asString
import com.makeevapps.simpletodolist.viewmodel.EditTaskViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.toolbar.*


class EditTaskActivity : BaseActivity(), RecycleViewItemClickListener {
    lateinit var model: EditTaskViewModel
    lateinit var binding: ActivityEditTaskBinding
    lateinit var todayTaskAdapter: SubTaskAdapter

    companion object {
        fun getActivityIntent(context: Context, taskId: String? = null, parentId: String? = null): Intent {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("taskId", taskId)
            intent.putExtra("parentId", parentId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this).get(EditTaskViewModel::class.java)
        setTheme(ThemeStyle.getThemeById(model.preferenceManager.getThemeId()).themeResId)
        super.onCreate(savedInstanceState)

        model.loadTask(intent.extras.getString("taskId"))

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_task)
        binding.controller = this
        binding.model = model

        setSupportActionBar(toolbar, true, true, getString(R.string.new_task))

        todayTaskAdapter = SubTaskAdapter(this)

        binding.subTasksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.subTasksRecyclerView.adapter = todayTaskAdapter

        observeTaskResponse()
        observeSubTaskResponse()
    }

    private fun observeTaskResponse() {
        model.getTaskResponse().observe(this, Observer<Task> { task ->
            if (task != null) {
                binding.taskTitleTextView.setText(task.title)
                binding.taskDescriptionTextView.setText(task.description)
                binding.dateTimeTextView.text = task.dueDate?.asString(DateUtils.DATE_TIME_FORMAT)
                binding.priorityNameTextView.setText(task.priority.textResId)
                binding.priorityColorView.setBackgroundResource(task.priority.colorResId)
                Logger.e("Refresh task")
            }
        })
    }

    private fun observeSubTaskResponse() {
        model.getSubTaskResponse().observe(this, Observer<List<Task>> { tasks ->
            todayTaskAdapter.clearAndAdd(tasks)
            Logger.e("Refresh sub task")
        })
    }

    fun onTitleTextChanged(text: CharSequence) {
        model.newTask.title = text.toString()
    }

    fun onDescriptionTextChanged(text: CharSequence) {
        model.newTask.description = text.toString()
    }

    /*fun onAllDayCheckedChanged(isCheck: Boolean) {
        model.newTask.allDay = isCheck
        ToastUtils.showSimpleToast(this, "onAllDayCheckedChanged: $isCheck")
    }*/

    fun onDateTimeButtonClick(view: View?) {
        val currentDate = model.newTask.dueDate
        val isAllDay = model.newTask.allDay

        val dateTimePicker = DateTimePickerDialog(currentDate, isAllDay, onDateSelected = { date, allDay ->

            model.newTask.allDay = allDay
            model.newTask.dueDate = date

            if (allDay) {
                binding.dateTimeTextView.text = date?.asString(DateUtils.SHORT_DATE_FORMAT3) ?: "No date"
            } else {
                binding.dateTimeTextView.text = date?.asString(DateUtils.DATE_TIME_FORMAT) ?: "No date"
            }

        }, onCanceled = {

        })
        dateTimePicker.show(supportFragmentManager, "DateTimePickerDialog")
    }

    fun changeTaskPriority(view: View?) {
        EditTaskPriorityDialog.showDialog(this, model.newTask.priority, onSuccess = { priority ->
            model.newTask.priority = priority
            binding.priorityNameTextView.setText(priority.textResId)
            binding.priorityColorView.setBackgroundResource(priority.colorResId)
        })
    }

    override fun onItemClick(view: View, position: Int) {
        val task = todayTaskAdapter.getItem(position)
        showAddSubTaskDialog(task)
    }

    private fun showAddSubTaskDialog(existSubTask: Task?) {
        EditSubTaskDialog.showDialog(this, existSubTask, onSuccess = { subTask ->
            subTask.parentId = model.newTask.id
            model.insertSubTask(subTask)
        })
    }

    private fun saveTask() {
        val parentId = intent.extras.getString("parentId")
        if (!parentId.isNullOrEmpty()) {
            model.newTask.parentId = parentId
        }

        model.insertOrUpdateTask(onSuccess = {
            finish()
        }, onError = { message ->
            ToastUtils.showSimpleToast(this, message)
        })
    }

    fun addSubTaskButtonClick(view: View?) {
        showAddSubTaskDialog(null)
    }

    fun deleteTask(view: View?) {
        model.deleteTask(onSuccess = { finish() })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.save -> saveTask()
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