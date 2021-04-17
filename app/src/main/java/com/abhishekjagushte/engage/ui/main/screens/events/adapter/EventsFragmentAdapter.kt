package com.abhishekjagushte.engage.ui.main.screens.events.adapter

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.database.entities.jsonmodels.Reminder
import com.abhishekjagushte.engage.database.views.EventView
import com.abhishekjagushte.engage.databinding.ItemEventReminderReceiverBinding
import com.abhishekjagushte.engage.databinding.ItemEventReminderSenderBinding
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val REMINDER_EVENT_ITEM_SENDER = 1
const val REMINDER_EVENT_ITEM_RECEIVER = 2

class EventsFragmentAdapter (private val dataRepository: DataRepository) : PagedListAdapter<EventDataItem, RecyclerView.ViewHolder>(EventsDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun updateList(list: PagedList<EventDataItem>?){
        adapterScope.launch {
            list?.let {
                withContext(Dispatchers.Main) {
                    submitList(list)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            REMINDER_EVENT_ITEM_SENDER -> ReminderItemSenderViewHolder.from(parent)
            REMINDER_EVENT_ITEM_RECEIVER -> ReminderItemReceiverViewHolder.from(parent)
            else -> throw IllegalStateException("Event type is incorrect")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ReminderItemSenderViewHolder -> holder.bind((getItem(position) as EventDataItem.ReminderItem).eventView)
            is ReminderItemReceiverViewHolder -> holder.bind((getItem(position) as EventDataItem.ReminderItem).eventView, dataRepository)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(val eventDataItem = getItem(position)){
            is EventDataItem.ReminderItem -> {
                if(eventDataItem.eventView.type==Constants.EVENT_MINE)
                    REMINDER_EVENT_ITEM_SENDER
                else
                    REMINDER_EVENT_ITEM_RECEIVER
            }
            else -> throw IllegalStateException("Event item type not found")
        }
    }

}

class ReminderItemSenderViewHolder(private val binding: ItemEventReminderSenderBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(eventView: EventView){
        val json = eventView.data
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<Reminder> = moshi.adapter(Reminder::class.java)
        val reminder = jsonAdapter.fromJson(json!!)

        reminder!!.status = eventView.status
        Log.d("***", "bind: ${eventView.status}")
        if(eventView.status == Constants.REMINDER_STATUS_INACTIVE) {
            binding.reminderDescriptionTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.reminderTitleTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
        binding.reminder = reminder

        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ReminderItemSenderViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemEventReminderSenderBinding.inflate(layoutInflater, parent, false)
            return ReminderItemSenderViewHolder(binding)
        }
    }
}

class ReminderItemReceiverViewHolder(private val binding: ItemEventReminderReceiverBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(eventView: EventView, dataRepository: DataRepository){
        val json = eventView.data
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<Reminder> = moshi.adapter(Reminder::class.java)
        val reminder = jsonAdapter.fromJson(json!!)

        binding.markDoneButton.setOnClickListener {
            Log.e("**", "bind: clicked", )
            dataRepository.markReminderDone(eventView.eventID, eventView.senderID!!)
        }

        if(eventView.status == Constants.REMINDER_STATUS_INACTIVE) {
            binding.reminderDescriptionTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.reminderTitleTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        binding.reminder = reminder
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ReminderItemReceiverViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemEventReminderReceiverBinding.inflate(layoutInflater, parent, false)
            return ReminderItemReceiverViewHolder(binding)
        }
    }
}

class EventsDiffCallback: DiffUtil.ItemCallback<EventDataItem>() {
    override fun areItemsTheSame(oldItem: EventDataItem, newItem: EventDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EventDataItem, newItem: EventDataItem): Boolean {
        return oldItem.status == newItem.status
    }

}

sealed class EventDataItem {
    abstract val id: String
    abstract val status: Int

    data class ReminderItem(val eventView: EventView): EventDataItem(){
        override val id: String = eventView.eventID
        override val status: Int = eventView.status
    }

    data class PollItem(val eventView: EventView): EventDataItem(){
        override val status: Int = eventView.status
        override val id: String = "1"
    }
}