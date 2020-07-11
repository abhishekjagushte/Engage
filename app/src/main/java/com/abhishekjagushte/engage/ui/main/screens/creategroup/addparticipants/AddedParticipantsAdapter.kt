package com.abhishekjagushte.engage.ui.main.screens.creategroup.addparticipants

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.database.ContactNameUsername
import com.abhishekjagushte.engage.databinding.ItemParticipantBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddedParticipantsAdapter(val clickListener: ParticipantRemoveListener) :
    ListAdapter<ContactNameUsername, RecyclerView.ViewHolder>(ParticipantsDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val TAG = "ChatListAdapter"
    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: Called")
        return ParticipantViewHolder.from(parent)
    }

    fun updateList(list: List<ContactNameUsername>?){
        adapterScope.launch {
            if(list!=null){
                submitList(list)
                handler.post {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ParticipantViewHolder -> {
                val item = getItem(position)
                holder.bind(item, clickListener)
            }
        }
    }

}

class ParticipantViewHolder(val binding: ItemParticipantBinding): RecyclerView.ViewHolder(binding.root){

    lateinit var navController: NavController

    fun bind(participant: ContactNameUsername, clickListener: ParticipantRemoveListener){
        binding.nickname.text = participant.name
        binding.cancelButton.setOnClickListener {
            clickListener.onClick(participant)
        }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ParticipantViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemParticipantBinding.inflate(layoutInflater, parent, false)

            val vh = ParticipantViewHolder(binding)
            vh.navController = Navigation.findNavController(parent)
            return vh
        }
    }

}

class ParticipantsDiffCallback: DiffUtil.ItemCallback<ContactNameUsername>() {

    override fun areItemsTheSame(
        oldItem: ContactNameUsername,
        newItem: ContactNameUsername
    ): Boolean {
        return oldItem.username.equals(newItem.username)
    }

    override fun areContentsTheSame(
        oldItem: ContactNameUsername,
        newItem: ContactNameUsername
    ): Boolean {
        return oldItem.username.equals(newItem.username)
    }

}

class ParticipantRemoveListener(val clickListener: (participant: ContactNameUsername) -> Unit){
    fun onClick(participant: ContactNameUsername) = clickListener(participant)
}

