package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.database.Message
import com.abhishekjagushte.engage.database.MessageView
import com.abhishekjagushte.engage.databinding.TextMessageContainerLeftBinding
import com.abhishekjagushte.engage.databinding.TextMessageContainerRightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val MY_TEXT_MESSAGE = 1
const val OTHER_TEXT_MESSAGE = 2

class ChatsAdapter: ListAdapter<ChatDataItem, RecyclerView.ViewHolder>(ChatDiffCallBack()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MY_TEXT_MESSAGE -> MyTextMessageViewHolder.from(parent)
            OTHER_TEXT_MESSAGE -> OtherTextMessageViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MyTextMessageViewHolder -> {
                val item = getItem(position) as ChatDataItem.MyTextMessage
                holder.bind(item.message)
            }

            is OtherTextMessageViewHolder -> {
                val item = getItem(position) as ChatDataItem.OtherTextMessage
                holder.bind(item.message)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is ChatDataItem.MyTextMessage -> MY_TEXT_MESSAGE
            is ChatDataItem.OtherTextMessage -> OTHER_TEXT_MESSAGE
        }
    }

    fun updateList(list: List<Message>?) {
        adapterScope.launch {
            if(list!=null)
                withContext(Dispatchers.Main) {
                    submitList(list.map{
                        if(it.type == 0)
                            ChatDataItem.MyTextMessage(it)
                        else
                            ChatDataItem.OtherTextMessage(it)
                    })
            }
        }
    }

}

class MyTextMessageViewHolder private constructor(val binding: TextMessageContainerRightBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(message: Message){
        binding.message = message
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): MyTextMessageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TextMessageContainerRightBinding.inflate(layoutInflater, parent, false)

            return MyTextMessageViewHolder(binding)
        }
    }
}

class OtherTextMessageViewHolder private constructor(val binding: TextMessageContainerLeftBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(message: Message){
        binding.message = message
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): OtherTextMessageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TextMessageContainerLeftBinding.inflate(layoutInflater, parent, false)

            return OtherTextMessageViewHolder(binding)
        }
    }
}

class ChatDiffCallBack: DiffUtil.ItemCallback<ChatDataItem>() {
    override fun areItemsTheSame(oldItem: ChatDataItem, newItem: ChatDataItem): Boolean {
        return oldItem.id == newItem.id // this checks if message is deleted than changes it
    }

    override fun areContentsTheSame(oldItem: ChatDataItem, newItem: ChatDataItem): Boolean {
        return oldItem.deleted == newItem.deleted
    }

}

sealed class ChatDataItem{
    abstract val id: String
    abstract val deleted: Int //for deleted status we will show the message is deleted

    data class MyTextMessage(val message: Message): ChatDataItem() {
        override val id = message.messageID
        override val deleted: Int = message.deleted
    }

    data class OtherTextMessage(val message: Message): ChatDataItem(){
        override val id = message.messageID
        override val deleted: Int = message.deleted
    }

}