package com.abhishekjagushte.engage.ui.fragments.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.databinding.ItemContactListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val CHAT_LIST_ITEM = 1
const val NOT_DECIDED = 2

class ContactListAdapter(val clickListener: ContactItemClickListener) :
    ListAdapter<ContactListDataItem, RecyclerView.ViewHolder> (ContactListDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val TAG = "ChatListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: Called")
        return when(viewType){
            CHAT_LIST_ITEM -> ContactListItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun updateList(list: List<Contact>?){
        adapterScope.launch {
            if(list!=null){
                withContext(Dispatchers.Main){
                    submitList(list.map {
                        ContactListDataItem.ContactItem(it)
                    })
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ContactListItemViewHolder -> {
                val item = getItem(position) as ContactListDataItem.ContactItem
                holder.bind(item.contact, clickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is ContactListDataItem.ContactItem -> CHAT_LIST_ITEM
            else -> NOT_DECIDED
        }
    }

}

class ContactListItemViewHolder(val binding: ItemContactListBinding): RecyclerView.ViewHolder(binding.root){

    lateinit var navController: NavController

    fun bind(contact: Contact, clickListener: ContactItemClickListener){
        binding.contact = contact
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ContactListItemViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemContactListBinding.inflate(layoutInflater, parent, false)

            val vh = ContactListItemViewHolder(binding)
            vh.navController = Navigation.findNavController(parent)
            return vh
        }
    }

}

class ContactListDiffCallback: DiffUtil.ItemCallback<ContactListDataItem>() {
    override fun areItemsTheSame(oldItem: ContactListDataItem, newItem: ContactListDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ContactListDataItem, newItem: ContactListDataItem): Boolean {
        return oldItem.id == newItem.id
        //TODO consider the case when by chance name or bio changes
    }

}

sealed class ContactListDataItem{
    abstract val id: String

    data class ContactItem(val contact: Contact): ContactListDataItem() {
        override val id = contact.username
    }
}

class ContactItemClickListener(val clickListener: (username: String) -> Unit){
    fun onClick(contact: Contact) = clickListener(contact.username)
}

