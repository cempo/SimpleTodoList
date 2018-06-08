package com.makeevapps.simpletodolist.ui.fragment

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
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
import com.makeevapps.simpletodolist.Keys
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.FragmentTodayBinding
import com.makeevapps.simpletodolist.dataproviders.TaskDataProvider.TaskData
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.interfaces.RecycleViewEventListener
import com.makeevapps.simpletodolist.ui.activity.EditTaskActivity
import com.makeevapps.simpletodolist.ui.activity.MainActivity
import com.makeevapps.simpletodolist.ui.activity.SnoozeActivity
import com.makeevapps.simpletodolist.ui.activity.SnoozeActivity.Companion.SNOOZE_DATE_REQUEST_CODE
import com.makeevapps.simpletodolist.ui.adapter.TodayTaskAdapter
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.viewmodel.TodayViewModel
import kotlinx.android.synthetic.main.toolbar.*


class TodayFragment : Fragment(), RecycleViewEventListener {
    private lateinit var adapter: TodayTaskAdapter
    private lateinit var wrappedAdapter: RecyclerView.Adapter<*>
    private lateinit var swipeManager: RecyclerViewSwipeManager
    private lateinit var touchActionGuardManager: RecyclerViewTouchActionGuardManager

    companion object {
        fun newInstance(): TodayFragment = TodayFragment()
    }

    private lateinit var binding: FragmentTodayBinding
    val model: TodayViewModel by lazy {
        ViewModelProviders.of(this).get(TodayViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_today, container, false)
        binding.controller = this
        binding.model = model
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        activity.setToolbar(toolbar, true, true, getString(R.string.today))

        prepareRecyclerView()
        observeTasksResponse()
    }

    private fun prepareRecyclerView() {
        //Create managers
        swipeManager = RecyclerViewSwipeManager()
        touchActionGuardManager = RecyclerViewTouchActionGuardManager()

        touchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        touchActionGuardManager.isEnabled = true

        adapter = TodayTaskAdapter(context!!, model.is24HoursFormat(), this)
        wrappedAdapter = swipeManager.createWrappedAdapter(adapter)

        val animator = SwipeDismissItemAnimator()
        animator.supportsChangeAnimations = false

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = wrappedAdapter
        binding.recyclerView.itemAnimator = animator
        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.addItemDecoration(SimpleListDividerDecorator(ContextCompat.getDrawable(context!!, R
                .drawable.divider), true))

        touchActionGuardManager.attachRecyclerView(binding.recyclerView)
        swipeManager.attachRecyclerView(binding.recyclerView)
    }

    private fun observeTasksResponse() {
        model.getTasksResponse().observe(this, Observer<List<Task>> { tasks ->
            if (tasks != null && tasks.isNotEmpty()) {
                binding.noTasksLayout.visibility = View.GONE
                adapter.setData(tasks)
            } else {
                binding.noTasksLayout.visibility = View.VISIBLE
                adapter.setData(null)
            }
        })
    }

    override fun onItemSwipeRight(position: Int, newPosition: Int?, item: TaskData) {
        Snackbar.make(binding.coordinatorLayout, R.string.task_is_done, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, {
                    item.task.switchDoneState()
                    model.insertOrUpdateTask(item.task)
                }).show()

        model.insertOrUpdateTask(item.task)
    }

    override fun onItemSwipeLeft(position: Int) {
        showDateTimeDialog(position)
    }

    private fun showDateTimeDialog(position: Int) {
        val item = adapter.dataProvider.getItem(position) as TaskData
        val task = item.task

        startActivityForResult(SnoozeActivity.getActivityIntent(context!!, task.id, false, position),
                SNOOZE_DATE_REQUEST_CODE)
    }

    override fun onItemClicked(v: View?, position: Int) {
        val item = adapter.dataProvider.getItem(position)
        startActivity(EditTaskActivity.getActivityIntent(context!!, item.task.id))
    }

    fun onAddButtonClick() {
        startActivity(EditTaskActivity.getActivityIntent(context!!, null, DateUtils.currentTime()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SNOOZE_DATE_REQUEST_CODE) {
            data?.let {
                val position = data.extras.getInt(Keys.KEY_POSITION)

                if (resultCode == Activity.RESULT_CANCELED) {
                    val item = adapter.dataProvider.getItem(position) as TaskData
                    item.isPinned = false
                    adapter.notifyDataSetChanged()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        swipeManager.release()
        touchActionGuardManager.release()

        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = null

        WrapperAdapterUtils.releaseAll(wrappedAdapter)

        super.onDestroyView()
    }
}