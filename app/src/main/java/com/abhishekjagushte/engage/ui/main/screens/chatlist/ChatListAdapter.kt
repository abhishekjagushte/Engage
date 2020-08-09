package com.abhishekjagushte.engage.ui.main.screens.chatlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.database.views.ConversationView
import com.abhishekjagushte.engage.databinding.ConversationItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val CHAT_LIST_ITEM = 1
const val NOT_DECIDED = 2

class ChatListAdapter : ListAdapter<ChatListDataItem, RecyclerView.ViewHolder> (ChatListDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
private val TAG = "ChatListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: Called")
        return when(viewType){
            CHAT_LIST_ITEM -> ChatListItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun updateList(list: List<ConversationView>?){
        adapterScope.launch {
            if(list!=null){
                withContext(Dispatchers.Main){
                    submitList(list.map {
                        ChatListDataItem.ChatListItem(it)
                    })
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ChatListItemViewHolder -> {
                val item = getItem(position) as ChatListDataItem.ChatListItem
                holder.bind(item.conversationView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is ChatListDataItem.ChatListItem -> CHAT_LIST_ITEM
            else -> NOT_DECIDED
        }
    }

}

class ChatListItemViewHolder(val binding: ConversationItemBinding): RecyclerView.ViewHolder(binding.root){

    lateinit var navController: NavController

    fun bind(conversationView: ConversationView){
        binding.conversationView = conversationView
        binding.executePendingBindings()

        binding.dataContainer.setOnClickListener {
            navController.navigate(ChatListFragmentDirections
                .actionChatListFragmentToChatFragment(conversationView.conversationID))
        }
    }

    companion object{
        fun from(parent: ViewGroup): ChatListItemViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ConversationItemBinding.inflate(layoutInflater, parent, false)

            val vh = ChatListItemViewHolder(binding)
            vh.navController = Navigation.findNavController(parent)

            return vh
        }
    }

}

class ChatListDiffCallback: DiffUtil.ItemCallback<ChatListDataItem>() {
    override fun areItemsTheSame(oldItem: ChatListDataItem, newItem: ChatListDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatListDataItem, newItem: ChatListDataItem): Boolean {
        return oldItem.lastMessageID == newItem.lastMessageID
        //TODO consider the case when the last message content changes and you may have to include hwther the message is deleted
    }

}

sealed class ChatListDataItem{
    abstract val id: String
    abstract val lastMessageID: String?

    data class ChatListItem(val conversationView: ConversationView): ChatListDataItem() {
        override val id = conversationView.conversationID
        override val lastMessageID = conversationView.messageID
    }

}