package com.abhishekjagushte.engage.ui.chat.adapters

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class EventsAdapter: PagedListAdapter<EventDataItem, RecyclerView.ViewHolder>(EventsDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

}

class EventsDiffCallback: DiffUtil.ItemCallback<EventDataItem>() {
    override fun areItemsTheSame(oldItem: EventDataItem, newItem: EventDataItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(oldItem: EventDataItem, newItem: EventDataItem): Boolean {
        TODO("Not yet implemented")
    }

}

sealed class EventDataItem {
    abstract val id: String
}