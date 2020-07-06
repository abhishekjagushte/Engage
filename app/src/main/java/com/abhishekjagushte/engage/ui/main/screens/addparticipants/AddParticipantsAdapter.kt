package com.abhishekjagushte.engage.ui.main.screens.addparticipants

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.ui.main.screens.search.SearchData

class AddParticipantsAdapter(val clickListener: AddParticipantClickListener) :
    ListAdapter<AddParticipantDataItem, RecyclerView.ViewHolder> (AddParticipantDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

class AddParticipantDiffCallback : DiffUtil.ItemCallback<AddParticipantDataItem>(){
    override fun areItemsTheSame(
        oldItem: AddParticipantDataItem,
        newItem: AddParticipantDataItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: AddParticipantDataItem,
        newItem: AddParticipantDataItem
    ): Boolean {
        TODO("Not yet implemented")
    }

}

sealed class AddParticipantDataItem{

    abstract val id: String

    data class Participant(val contact: Contact): AddParticipantDataItem(){
        override val id = contact.username
        var selected: Boolean = false
    }

}

class AddParticipantClickListener(val clickListener: (AddParticipantDataItem) -> Unit){
    fun onClick(participant: AddParticipantDataItem) = clickListener(participant)
}