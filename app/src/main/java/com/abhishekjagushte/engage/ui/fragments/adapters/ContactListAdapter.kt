package com.abhishekjagushte.engage.ui.fragments.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.Contact
import com.abhishekjagushte.engage.databinding.ItemContactListBinding
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val CHAT_LIST_ITEM = 1
const val NOT_DECIDED = 2

class ContactListAdapter(private val clickListener: ContactItemClickListener,
                         private val longClickListener: ContactLongItemClickListener,
                         private val mode: Int,
                         val  context: Context
) :
    ListAdapter<ContactListDataItem, RecyclerView.ViewHolder> (ContactListDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val TAG = "ChatListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: Called")
        return when(viewType){
            CHAT_LIST_ITEM -> ContactListItemViewHolder.from(parent, mode)
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
                holder.bind(item, clickListener)
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

class ContactListItemViewHolder(val binding: ItemContactListBinding, private val mode: Int): RecyclerView.ViewHolder(binding.root){

    lateinit var navController: NavController

    private val TAG = "ContactListItemViewHolder"
    private lateinit var contactDataItem: ContactListDataItem.ContactItem

    fun bind(
        contactDataItem: ContactListDataItem.ContactItem,
        clickListener: ContactItemClickListener
    ){
        this.contactDataItem = contactDataItem
        if(contactDataItem.selected) {
            binding.constraintLayout.background =
                binding.root.context.getDrawable(R.drawable.selected_background)
        }
        else{
            binding.constraintLayout.background = binding.root.context.getDrawable(android.R.color.transparent)
        }

        binding.contact = contactDataItem.contact
        setupListeners(clickListener)
        binding.executePendingBindings()
    }

    private fun setupListeners(clickListener: ContactItemClickListener){
        when(mode){
            Constants.CONTACT_LIST_MODE_SELECTION -> {
                binding.constraintLayout.setOnClickListener {
                    clickListener.onClick(contactDataItem.contact)
                    if(!contactDataItem.selected){
                        binding.constraintLayout.background =
                            binding.root.context.getDrawable(R.drawable.selected_background)
                    }
                    else{
                        binding.constraintLayout.background = binding.root.context.getDrawable(android.R.color.transparent)
                    }
                }
            }

            Constants.CONTACT_LIST_MODE_NORMAL -> {
                binding.constraintLayout.setOnClickListener {
                    clickListener.onClick(contactDataItem.contact)
                }
                //TODO set long click listeners if any
            }
        }
    }

    companion object{
        fun from(parent: ViewGroup, mode: Int): ContactListItemViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemContactListBinding.inflate(layoutInflater, parent, false)

            val vh = ContactListItemViewHolder(binding, mode)
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
        //Log.d("ContactListDiffCallback", "areContentsTheSame: ${oldItem.selected} ${newItem.selected}")
        return oldItem.selected == newItem.selected
        //TODO consider the case when by chance name or bio changes
    }

}

sealed class ContactListDataItem{
    abstract val id: String
    abstract var selected: Boolean

    data class ContactItem(val contact: Contact): ContactListDataItem() {
        override val id = contact.username
        override var selected = contact.selected?: true
    }
}

class ContactItemClickListener(val clickListener: (contact: Contact) -> Unit){
    fun onClick(contact: Contact) = clickListener(contact)
}

class ContactLongItemClickListener(val clickListener: (contact: Contact) -> Unit){
    fun onClick(contact: Contact) = clickListener(contact)
}