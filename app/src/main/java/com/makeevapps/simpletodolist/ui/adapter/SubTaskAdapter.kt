package com.makeevapps.simpletodolist.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makeevapps.simpletodolist.databinding.ListItemSubTaskBinding
import com.makeevapps.simpletodolist.datasource.db.table.Task
import com.makeevapps.simpletodolist.interfaces.RecycleViewItemClickListener

class SubTaskAdapter(private val itemClickListener: RecycleViewItemClickListener) : RecyclerView.Adapter<SubTaskAdapter.ViewHolder>() {
    private var items: ArrayList<Task> = ArrayList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent!!.context)
        val binding = ListItemSubTaskBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = items[position]

        holder.binding.task = task
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val binding: ListItemSubTaskBinding = DataBindingUtil.bind(view)

        override fun onClick(view: View?) {
            if (view != null) {
                itemClickListener.onItemClick(view, adapterPosition)
            }
        }
    }

    fun clearAndAdd(newItems: List<Task>?) {
        val initPosition = itemCount

        items.clear()
        notifyItemRangeRemoved(0, initPosition)

        if (newItems != null) {
            items.addAll(newItems)
        }

        notifyItemRangeInserted(0, items.size)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Task? = if (position < itemCount) items[position] else null

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = position.toLong()
}