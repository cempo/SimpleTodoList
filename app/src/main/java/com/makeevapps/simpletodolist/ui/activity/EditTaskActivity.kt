package com.makeevapps.simpletodolist.ui.activity

import android.app.Activity
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
import com.makeevapps.simpletodolist.Keys.KEY_DUE_DATE_IN_MILLIS
import com.makeevapps.simpletodolist.Keys.KEY_TASK_ID
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivityEditTaskBinding
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.interfaces.CheckBoxCheckedListener
import com.makeevapps.simpletodolist.interfaces.RecycleViewItemClickListener
import com.makeevapps.simpletodolist.ui.activity.DateTimeChooserActivity.Companion.CHOOSE_DATE_REQUEST_CODE
import com.makeevapps.simpletodolist.ui.adapter.SubTaskAdapter
import com.makeevapps.simpletodolist.ui.dialog.CancelChangesDialog
import com.makeevapps.simpletodolist.ui.dialog.EditSubTaskDialog
import com.makeevapps.simpletodolist.ui.dialog.EditTaskPriorityDialog
import com.makeevapps.simpletodolist.utils.ToastUtils
import com.makeevapps.simpletodolist.viewmodel.DateTimeChooserViewModel
import com.makeevapps.simpletodolist.viewmodel.EditTaskViewModel
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class EditTaskActivity : BaseActivity(), RecycleViewItemClickListener, CheckBoxCheckedListener {
    private lateinit var subTaskAdapter: SubTaskAdapter

    companion object {
        fun getActivityIntent(context: Context, taskId: String? = null, dueDate: Date? = null): Intent {
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra(KEY_TASK_ID, taskId)
            if (dueDate != null) {
                intent.putExtra(KEY_DUE_DATE_IN_MILLIS, dueDate.time)
            }
            return intent
        }
    }

    val model: EditTaskViewModel by lazy {
        ViewModelProviders.of(this).get(EditTaskViewModel::class.java)
    }

    val binding: ActivityEditTaskBinding by lazy {
        DataBindingUtil.setContentView<ActivityEditTaskBinding>(this, R.layout.activity_edit_task)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            model.initStartDate(intent.extras)
            model.initTask(intent.extras)
        }

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
            }
        })
    }

    private fun observeSubTaskResponse() {
        model.getSubTaskResponse().observe(this, Observer<List<Task>> { tasks ->
            subTaskAdapter.clearAndAdd(tasks)
        })
    }

    fun onTitleTextChanged(text: CharSequence) {
        model.updateTitle(text.toString())
    }

    fun onDescriptionTextChanged(text: CharSequence) {
        model.updateDescription(text.toString())
    }

    fun onDateTimeButtonClick(view: View) {
        val dueDate = model.newTask.dueDate
        val isAllDay = model.newTask.allDay

        startActivityForResult(DateTimeChooserActivity.getActivityIntent(this, dueDate, isAllDay),
                CHOOSE_DATE_REQUEST_CODE)
    }

    fun removeDueDate(view: View) {
        model.removeDate()
    }

    fun changeTaskPriority(view: View) {
        EditTaskPriorityDialog.showDialog(this, model.newTask.priority, onSuccess = { priority ->
            model.updatePriority(priority)
        })
    }

    override fun onItemClick(view: View, position: Int) {
        val task = subTaskAdapter.getItem(position)
        showAddSubTaskDialog(task)
    }

    override fun onCheckBoxChecked(view: View, position: Int, isChecked: Boolean) {
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

    fun saveTask(view: View) {
        model.insertOrUpdateTask(onSuccess = {
            finish()
        }, onError = { message ->
            ToastUtils.showSimpleToast(this, message)
        })
    }

    fun addSubTask(view: View) {
        showAddSubTaskDialog(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_DATE_REQUEST_CODE) {
            data?.let {
                model.updateDueDate(it.extras)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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