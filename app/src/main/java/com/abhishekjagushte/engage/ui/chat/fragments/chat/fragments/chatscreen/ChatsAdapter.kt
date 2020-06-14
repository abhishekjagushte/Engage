package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.database.MessageView
import com.abhishekjagushte.engage.databinding.TextMessageContainerLeftBinding

class ChatsAdapter: ListAdapter<ChatDataItem, RecyclerView.ViewHolder>(ChatDiffCallBack()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

class MyTextMessageViewHolder private constructor(val binding: TextMessageContainerLeftBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(message: MessageView){
        binding.message = message
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): MyTextMessageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TextMessageContainerLeftBinding.inflate(layoutInflater, parent, false)

            return MyTextMessageViewHolder(binding)
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

    data class MyTextMessage(val message: MessageView): ChatDataItem() {
        override val id = message.messageID
        override val deleted: Int = message.deleted
    }

    data class OtherTextMessage(val message: MessageView): ChatDataItem(){
        override val id = message.messageID
        override val deleted: Int = message.deleted
    }

}