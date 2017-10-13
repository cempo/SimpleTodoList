package com.makeevapps.simpletodolist.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.FragmentTodayBinding
import com.makeevapps.simpletodolist.dataproviders.TaskDataProvider
import com.makeevapps.simpletodolist.dataproviders.TaskDataProvider.ConcreteData
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.interfaces.RecycleViewEventListener
import com.makeevapps.simpletodolist.ui.activity.EditTaskActivity
import com.makeevapps.simpletodolist.ui.activity.MainActivity
import com.makeevapps.simpletodolist.ui.adapter.TodayTaskAdapter
import com.makeevapps.simpletodolist.ui.dialog.DateTimePickerDialog
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.viewmodel.TodayViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.toolbar.*


class TodayFragment : Fragment(), RecycleViewEventListener {
    private lateinit var model: TodayViewModel
    private lateinit var binding: FragmentTodayBinding

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: TodayTaskAdapter
    private lateinit var wrappedAdapter: RecyclerView.Adapter<*>
    private lateinit var swipeManager: RecyclerViewSwipeManager
    private lateinit var touchActionGuardManager: RecyclerViewTouchActionGuardManager

    companion object {
        fun newInstance(): TodayFragment = TodayFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this).get(TodayViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_today, container, false)
        binding.controller = this
        binding.model = model
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        activity.setToolbar(toolbar, true, true, getString(R.string.today))

        prepareRecyclerView(savedInstanceState)
        observeTasksResponse()
    }

    override fun onResume() {
        super.onResume()
        model.loadTasks()
    }

    private fun prepareRecyclerView(savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context)

        //Create managers
        swipeManager = RecyclerViewSwipeManager()
        touchActionGuardManager = RecyclerViewTouchActionGuardManager()

        touchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        touchActionGuardManager.isEnabled = true

        adapter = TodayTaskAdapter(context, this)
        wrappedAdapter = swipeManager.createWrappedAdapter(adapter)

        val animator = SwipeDismissItemAnimator()
        animator.supportsChangeAnimations = false

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = wrappedAdapter
        binding.recyclerView.itemAnimator = animator
        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.addItemDecoration(SimpleListDividerDecorator(ContextCompat.getDrawable(context, R.drawable.divider), true))

        touchActionGuardManager.attachRecyclerView(binding.recyclerView)
        swipeManager.attachRecyclerView(binding.recyclerView)

        observeTasksResponse()
    }

    private fun observeTasksResponse() {
        model.getTasksResponse().observe(this, Observer<List<Task>> { tasks ->
            if (tasks != null && tasks.isNotEmpty()) {
                Logger.e("Refresh task list. Size: " + tasks.size)
                adapter.setData(TaskDataProvider(tasks))
            } else {
                adapter.setData(TaskDataProvider())
            }
        })
    }

    override fun onItemSwipeRight(position: Int, newPosition: Int?) {
        val item = adapter.dataProvider.getItem(position)
        Snackbar.make(binding.coordinatorLayout, R.string.task_is_done, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, { onItemUndoTaskStatus(position, newPosition) })
                .setActionTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .show()

        model.insertTask(item.task)
    }

    private fun onItemUndoTaskStatus(position: Int, newPosition: Int?) {
        if (newPosition != null) {
            adapter.dataProvider.undoLastMovement()
            adapter.notifyItemMoved(newPosition, position)
        } else {
            adapter.dataProvider.undoLastRemoval()
            adapter.notifyItemInserted(position)
        }

        val itemData = adapter.dataProvider.getItem(position)
        val task = itemData.task
        if (task.isComplete) {
            task.isComplete = false
            task.doneDate = null
        } else {
            task.isComplete = true
            task.doneDate = DateUtils.currentTime()
        }

        model.insertTask(task)

        adapter.notifyItemChanged(position)
    }

    override fun onItemSwipeLeft(position: Int) {
        showDateTimeDialog(position)
    }

    private fun showDateTimeDialog(position: Int) {
        val item = adapter.dataProvider.getItem(position) as ConcreteData
        val task = item.task
        val dateTimePicker = DateTimePickerDialog(task.dueDate, task.allDay, onDateSelected = { date, allDay ->
            task.allDay = allDay
            task.dueDate = date

            unPinGroupItem(position, item)

            model.insertTask(task)
        }, onCanceled = {
            unPinGroupItem(position, item)
        })
        dateTimePicker.show(fragmentManager, "DateTimePickerDialog")
    }

    private fun unPinGroupItem(fromPosition: Int, item: ConcreteData) {
        item.isPinned = false

        val newPosition = adapter.dataProvider.getValidPosition(item)
        if (newPosition != null) {
            if (fromPosition != newPosition) {
                adapter.dataProvider.moveItem(fromPosition, newPosition)
                adapter.notifyItemMoved(fromPosition, newPosition)
                adapter.notifyItemChanged(newPosition)
            } else {
                adapter.notifyItemChanged(fromPosition)
            }
        } else {
            adapter.dataProvider.removeItem(fromPosition)
            adapter.notifyItemRemoved(fromPosition)
        }
    }

    override fun onItemClicked(v: View?, position: Int) {
        val item = adapter.dataProvider.getItem(position)
        activity.startActivity(EditTaskActivity.getActivityIntent(activity, item.task.id))
    }

    fun onAddButtonClick(view: View) {
        activity.startActivity(EditTaskActivity.getActivityIntent(activity))
        //ToastUtils.showSimpleToast(activity, "Add clicked")
    }

    override fun onDestroyView() {
        swipeManager.release()
        touchActionGuardManager.release()

        if (binding.recyclerView != null) {
            binding.recyclerView.itemAnimator = null
            binding.recyclerView.adapter = null

            WrapperAdapterUtils.releaseAll(wrappedAdapter)

            super.onDestroyView()
        }
    }
}