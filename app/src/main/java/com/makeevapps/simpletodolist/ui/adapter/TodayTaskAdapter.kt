package com.makeevapps.simpletodolist.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import com.makeevapps.simpletodolist.interfaces.RecycleViewEventListener

class TodayTaskAdapter(val eventListener: RecycleViewEventListener) : RecyclerView.Adapter<TodayTaskAdapter.MyViewHolder>(), SwipeableItemAdapter<TodayTaskAdapter.MyViewHolder> {
    var dataProvider = TaskDataProvider()

    companion object {
        private val TAG = "MySwipeableItemAdapter"
    }

    init {
        setHasStableIds(true)
    }

    fun setData(dataProvider: TaskDataProvider) {
        this.dataProvider = dataProvider
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val view: View) : AbstractSwipeableItemViewHolder(view), View.OnClickListener {
        val binding: ListItemTodayTaskBinding = DataBindingUtil.bind(view)

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

        val item = dataProvider.getItem(position)
        when (type) {
            SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND -> {
                backgroundColor = android.R.color.transparent
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND -> {
                backgroundColor = R.color.bg_swipe_todo_item_left
                actionImageResId = R.drawable.ic_clock_fast_24dp
            }
            SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND -> {
                actionImageResId = R.drawable.ic_done_24dp
                backgroundColor = if (item.task.isComplete) {
                    R.color.bg_swipe_done_item_right
                } else {
                    R.color.bg_swipe_todo_item_right
                }

            }
        }

        holder.binding.behindLayout.gravity = gravity
        holder.binding.behindLayout.setBackgroundResource(backgroundColor)
        holder.binding.swipeActionImageView.setImageResource(actionImageResId)
    }


    override fun onSwipeItem(holder: MyViewHolder, position: Int, result: Int): SwipeResultAction? {
        Log.d(TAG, "onSwipeItem(position = $position, result = $result)")

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

    private class SwipeRightResultAction internal constructor(private var adapterToday: TodayTaskAdapter?,
                                                              private val position: Int) : SwipeResultActionRemoveItem() {

        override fun onPerformAction() {
            super.onPerformAction()

            adapterToday!!.dataProvider.removeItem(position)
            adapterToday!!.notifyItemRemoved(position)
        }

        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()

            adapterToday!!.eventListener.onItemSwipeRight(position)
        }

        override fun onCleanUp() {
            super.onCleanUp()
            adapterToday = null
        }
    }
}
