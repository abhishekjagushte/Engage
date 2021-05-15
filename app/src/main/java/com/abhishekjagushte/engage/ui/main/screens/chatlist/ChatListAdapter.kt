package com.abhishekjagushte.engage.ui.main.screens.chatlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.views.ConversationView
import com.abhishekjagushte.engage.databinding.ConversationItemBinding
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.GlideApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val CHAT_LIST_ITEM = 1
const val NOT_DECIDED = 2

class ChatListAdapter(private val dataRepository: DataRepository, val lifecycleCoroutineScope: LifecycleCoroutineScope) : ListAdapter<ChatListDataItem, RecyclerView.ViewHolder> (ChatListDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val TAG = "ChatListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: Called")
        return when(viewType){
            CHAT_LIST_ITEM -> ChatListItemViewHolder.from(parent, dataRepository, lifecycleCoroutineScope)
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

class ChatListItemViewHolder(
    val binding: ConversationItemBinding,
    val dataRepository: DataRepository,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
): RecyclerView.ViewHolder(binding.root){

    lateinit var navController: NavController

    private val TAG = "ChatListItemViewHolder"

    fun bind(conversationView: ConversationView){
        binding.conversationView = conversationView
        binding.executePendingBindings()

        binding.dataContainer.setOnClickListener {
            navController.navigate(ChatListFragmentDirections
                .actionChatListFragmentToChatFragment(conversationView.conversationID))
        }
        Log.e(TAG, "bind: ${conversationView.dp_thmb_url}", )
        setProfilePhoto(conversationView)
    }

    private fun setProfilePhoto(conversationView: ConversationView) {
        GlideApp
            .with(binding.root)
            .load(conversationView.dp_thmb_url)
            .placeholder(R.drawable.ic_mail_profile_picture_male)
            .into(binding.profileImage)

        if(conversationView.conType==Constants.CONVERSATION_TYPE_121)
            if(conversationView.dp_thmb_url == null)
                dataRepository.updateDpThumbnailURLIfDifferent(conversationView.conversationID, conversationView.dp_thmb_url)
    }

    companion object{
        fun from(
            parent: ViewGroup,
            dataRepository: DataRepository,
            lifecycleCoroutineScope: LifecycleCoroutineScope
        ): ChatListItemViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ConversationItemBinding.inflate(layoutInflater, parent, false)

            val vh = ChatListItemViewHolder(binding, dataRepository, lifecycleCoroutineScope)
            vh.navController = Navigation.findNavController(parent)

            return vh
        }
    }

}

class ChatListDiffCallback: DiffUtil.ItemCallback<ChatListDataItem>() {
    val TAG = "ChatListDiffCallback"
    override fun areItemsTheSame(oldItem: ChatListDataItem, newItem: ChatListDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatListDataItem, newItem: ChatListDataItem): Boolean {
        Log.d(TAG, "areContentsTheSame: ${oldItem.dp_timestamp} ${newItem.dp_timestamp}")
        return oldItem.lastMessageID == newItem.lastMessageID && oldItem.dp_timestamp == newItem.dp_timestamp
        //TODO consider the case when the last message content changes and you may have to include hwther the message is deleted
    }

}

sealed class ChatListDataItem{
    abstract val id: String
    abstract val lastMessageID: String?
    abstract val dp_timestamp: Long?

    data class ChatListItem(val conversationView: ConversationView): ChatListDataItem() {
        override val id = conversationView.conversationID
        override val lastMessageID = conversationView.messageID
        override val dp_timestamp = conversationView.dp_timeStamp
    }

}