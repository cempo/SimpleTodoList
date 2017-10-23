package com.makeevapps.simpletodolist.ui.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ListItemTodayTaskBinding
import com.makeevapps.simpletodolist.dataproviders.TaskDataProvider
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.interfaces.RecycleViewEventListener
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.extension.asString

class TodayTaskAdapter(val context: Context,
                       val eventListener: RecycleViewEventListener) : RecyclerView.Adapter<TodayTaskAdapter.MyViewHolder>(), SwipeableItemAdapter<TodayTaskAdapter.MyViewHolder> {
    var dataProvider = TaskDataProvider()

    var textColorSecondary = ContextCompat.getColor(context, R.color.text_color_secondary)
    var textColorPrimary = ContextCompat.getColor(context, R.color.text_color_primary)
    var textColorExpiredTask = ContextCompat.getColor(context, R.color.text_color_expired_task)

    init {
        setHasStableIds(true)
    }

    fun setData(tasks: List<Task>?) {
        if (tasks != null) {
            this.dataProvider.setData(tasks)
        } else {
            this.dataProvider.clearData()
        }
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val view: View) : AbstractSwipeableItemViewHolder(view), View.OnClickListener {
        val binding: ListItemTodayTaskBinding = DataBindingUtil.bind(view)

        init {
            binding.contentLayout.setOnClickListener(this)
        }

        override fun getSwipeableContainerView(): View? = binding.contentLayout

        override fun onClick(view: View?) {
            val vh = RecyclerViewAdapterUtils.getViewHolder(view)
            if (vh != null) {
                val flatPosition = vh.adapterPosition
                if (flatPosition != RecyclerView.NO_POSITION) {
                    eventListener.onItemClicked(view, flatPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTodayTaskBinding.inflate(inflater, parent, false)
        //val binding: ListItemTodayTaskBinding = DataBindingUtil.inflate(inflater, R.layout.list_item_today_task, parent, false)
        return MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = dataProvider.getItem(position)
        holder.binding.task = item.task

        val isExpired = item.task.isExpired()

        var dateString: String = when {
            item.task.isDone() -> ""
            isExpired -> item.task.dueDate?.asString(DateUtils.SHORT_DATE_FORMAT) ?: ""
            item.task.isPlaned() -> if (item.task.allDay) {
                context.getString(R.string.all_day)
            } else {
                item.task.dueDate?.asString(DateUtils.TIME_FORMAT) ?: ""
            }
            else -> ""
        }

        if (!dateString.isEmpty()) {
            holder.binding.dateTextView?.visibility = View.VISIBLE
            holder.binding.dateTextView?.text = dateString
        } else {
            holder.binding.dateTextView?.visibility = View.GONE
        }

        holder.binding.dateTextView?.setTextColor(if (isExpired) textColorExpiredTask else textColorSecondary)

        when {
            item.task.isDone() -> {
                holder.binding.nameTextView.paintFlags = holder.binding.nameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.nameTextView.setTextColor(textColorSecondary)
            }
            isExpired -> {
                holder.binding.nameTextView.paintFlags = holder.binding.nameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.binding.nameTextView.setTextColor(textColorExpiredTask)
            }
            else -> {
                holder.binding.nameTextView.paintFlags = holder.binding.nameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.binding.nameTextView.setTextColor(textColorPrimary)
            }
        }

        val swipeState = holder.swipeStateFlags

        if (swipeState and SwipeableItemConstants.STATE_FLAG_IS_UPDATED != 0) {
            holder.binding.contentLayout.setBackgroundResource(R.drawable.bg_today_item_normal_state)
        }

        holder.swipeItemHorizontalSlideAmount =
                if (item.isPinned) SwipeableItemConstants.OUTSIDE_OF_THE_WINDOW_LEFT else 0F
    }

    override fun getItemId(position: Int): Long = dataProvider.getItem(position).id

    override fun getItemViewType(position: Int): Int = 0

    override fun getItemCount(): Int = dataProvider.getCount()

    override fun onSwipeItemStarted(holder: MyViewHolder, position: Int) {
        notifyDataSetChanged()
    }

    override fun onGetSwipeReactionType(holder: MyViewHolder, position: Int, x: Int, y: Int): Int {
        val item = dataProvider.getItem(position)

        return if (item.task.isComplete)
            SwipeableItemConstants.REACTION_CAN_SWIPE_RIGHT
        else
            SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H
    }

    override fun onSetSwipeBackground(holder: MyViewHolder, position: Int, type: Int) {
        var backgroundColor = 0
        var actionImageResId = 0
        var gravity = Gravity.CENTER
        var visibility = View.GONE

        val item = dataProvider.getItem(position)
        when (type) {
            SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND -> {
                backgroundColor = android.R.color.transparent
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND -> {
                visibility = View.VISIBLE
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                backgroundColor = R.color.bg_swipe_todo_item_left
                actionImageResId = R.drawable.ic_clock_fast_24dp
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND -> {
                visibility = View.VISIBLE
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
                actionImageResId = R.drawable.ic_done_24dp
                backgroundColor = if (item.task.isComplete) {
                    R.color.bg_swipe_done_item_right
                } else {
                    R.color.bg_swipe_todo_item_right
                }
            }
        }

        holder.binding.behindLayout.visibility = visibility
        if (visibility == View.VISIBLE) {
            holder.binding.behindLayout.gravity = gravity
            holder.binding.behindLayout.setBackgroundResource(backgroundColor)
            holder.binding.swipeActionImageView.setImageResource(actionImageResId)
        }
    }


    override fun onSwipeItem(holder: MyViewHolder, position: Int, result: Int): SwipeResultAction? {
        return when (result) {
            SwipeableItemConstants.RESULT_SWIPED_RIGHT -> SwipeRightResultAction(this, position)
            SwipeableItemConstants.RESULT_SWIPED_LEFT -> SwipeLeftResultAction(this, position)
            else -> null
        }
    }

    private class SwipeLeftResultAction internal constructor(private var adapterToday: TodayTaskAdapter?,
                                                             private val position: Int) : SwipeResultActionMoveToSwipedDirection() {
        private var pinned: Boolean = false

        override fun onPerformAction() {
            super.onPerformAction()

            val item = adapterToday!!.dataProvider.getItem(position)

            if (!item.isPinned) {
                item.isPinned = true
                adapterToday!!.notifyItemChanged(position)
                pinned = true
            }
        }

        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()

            if (pinned) {
                adapterToday!!.eventListener.onItemSwipeLeft(position)
            }
        }

        override fun onCleanUp() {
            super.onCleanUp()
            adapterToday = null
        }
    }

    private class SwipeRightResultAction internal constructor(private var adapter: TodayTaskAdapter?,
                                                              private val position: Int) : SwipeResultActionRemoveItem() {
        private var item = adapter!!.dataProvider.getItem(position) as TaskDataProvider.ConcreteData
        private var newPosition: Int? = null

        override fun onPerformAction() {
            super.onPerformAction()

            val task = item.task
            if (task.isComplete) {
                task.isComplete = false
                task.doneDate = null
            } else {
                task.isComplete = true
                task.doneDate = DateUtils.currentTime()
            }

            val toPosition = adapter!!.dataProvider.getValidPosition(item)
            newPosition = toPosition

            if (toPosition != null) {
                adapter!!.dataProvider.moveItem(position, toPosition)
            } else {
                adapter!!.dataProvider.removeItem(position)
            }
        }

        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()

            adapter!!.eventListener.onItemSwipeRight(position, newPosition, item)
        }

        override fun onCleanUp() {
            super.onCleanUp()
            adapter = null
        }
    }

    fun onUndoTaskStatus(position: Int, newPosition: Int?, onSuccess: (task: Task) -> Unit) {
        if (newPosition != null) {
            dataProvider.undoLastMovement()
        } else {
            dataProvider.undoLastRemoval()
        }

        val itemData = dataProvider.getItem(position)
        val task = itemData.task
        if (task.isComplete) {
            task.isComplete = false
            task.doneDate = null
        } else {
            task.isComplete = true
            task.doneDate = DateUtils.currentTime()
        }

        onSuccess(task)
    }

    fun unPinGroupItem(fromPosition: Int, item: TaskDataProvider.ConcreteData) {
        item.isPinned = false

        val newPosition = dataProvider.getValidPosition(item)
        if (newPosition != null) {
            if (fromPosition != newPosition) {
                dataProvider.moveItem(fromPosition, newPosition)
            }
        } else {
            dataProvider.removeItem(fromPosition)
        }
    }
}
