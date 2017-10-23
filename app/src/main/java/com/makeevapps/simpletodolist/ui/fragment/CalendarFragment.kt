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
import com.makeevapps.simpletodolist.databinding.FragmentCalendarBinding
import com.makeevapps.simpletodolist.dataproviders.TaskDataProvider.ConcreteData
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.interfaces.RecycleViewEventListener
import com.makeevapps.simpletodolist.ui.activity.EditTaskActivity
import com.makeevapps.simpletodolist.ui.activity.MainActivity
import com.makeevapps.simpletodolist.ui.adapter.TodayTaskAdapter
import com.makeevapps.simpletodolist.ui.dialog.DateTimePickerDialog
import com.makeevapps.simpletodolist.viewmodel.CalendarViewModel
import com.orhanobut.logger.Logger
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.HorizontalCalendarListener
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*


class CalendarFragment : Fragment(), RecycleViewEventListener {
    private lateinit var model: CalendarViewModel
    private lateinit var binding: FragmentCalendarBinding

    private lateinit var adapter: TodayTaskAdapter
    private lateinit var wrappedAdapter: RecyclerView.Adapter<*>
    private lateinit var swipeManager: RecyclerViewSwipeManager
    private lateinit var touchActionGuardManager: RecyclerViewTouchActionGuardManager

    lateinit var horizontalCalendar: HorizontalCalendar

    companion object {
        fun newInstance(): CalendarFragment = CalendarFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this).get(CalendarViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        binding.controller = this
        binding.model = model

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        activity.setToolbar(toolbar, true, true, getString(R.string.calendar))

        setupCalendar()

        prepareRecyclerView()
        observeTasksResponse()
    }

    fun setupCalendar() {
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)

        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)

        horizontalCalendar = HorizontalCalendar.Builder(activity, R.id.calendarView)
                .startDate(startDate.time)
                .endDate(endDate.time)
                .build()
        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Date, position: Int) {
                model.loadTasks(date)
            }
        }
    }

    private fun prepareRecyclerView() {
        swipeManager = RecyclerViewSwipeManager()
        touchActionGuardManager = RecyclerViewTouchActionGuardManager()

        touchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        touchActionGuardManager.isEnabled = true

        adapter = TodayTaskAdapter(context, this)
        wrappedAdapter = swipeManager.createWrappedAdapter(adapter)

        val animator = SwipeDismissItemAnimator()
        animator.supportsChangeAnimations = false

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = wrappedAdapter
        binding.recyclerView.itemAnimator = animator
        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.addItemDecoration(SimpleListDividerDecorator(ContextCompat.getDrawable(context, R.drawable.divider), true))

        touchActionGuardManager.attachRecyclerView(binding.recyclerView)
        swipeManager.attachRecyclerView(binding.recyclerView)
    }

    private fun observeTasksResponse() {
        model.getTasksResponse().observe(this, Observer<List<Task>> { tasks ->
            if (tasks != null && tasks.isNotEmpty()) {
                Logger.e("Refresh task list. Size: " + tasks.size)

                adapter.setData(tasks)
            } else {
                adapter.setData(null)
            }
        })
    }

    override fun onItemSwipeRight(position: Int, newPosition: Int?, item: ConcreteData) {
        Snackbar.make(binding.coordinatorLayout, R.string.task_is_done, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, {
                    adapter.onUndoTaskStatus(position, newPosition, onSuccess = { task ->
                        model.insertTask(task)
                    })
                }).show()

        model.insertTask(item.task)
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

            adapter.unPinGroupItem(position, item)

            model.insertTask(task)
        }, onCanceled = {
            adapter.unPinGroupItem(position, item)
            adapter.notifyDataSetChanged()
        })
        dateTimePicker.show(fragmentManager, "DateTimePickerDialog")
    }

    override fun onItemClicked(v: View?, position: Int) {
        val item = adapter.dataProvider.getItem(position)
        activity.startActivity(EditTaskActivity.getActivityIntent(activity, item.task.id))
    }

    fun onAddButtonClick(view: View?) {
        activity.startActivity(EditTaskActivity.getActivityIntent(activity, null, horizontalCalendar.selectedDate))
    }

    private fun releaseRecyclerView() {
        swipeManager.release()
        touchActionGuardManager.release()

        if (binding.recyclerView != null) {
            binding.recyclerView.itemAnimator = null
            binding.recyclerView.adapter = null

            WrapperAdapterUtils.releaseAll(wrappedAdapter)
        }
    }

    override fun onDestroyView() {
        releaseRecyclerView()

        super.onDestroyView()
    }
}