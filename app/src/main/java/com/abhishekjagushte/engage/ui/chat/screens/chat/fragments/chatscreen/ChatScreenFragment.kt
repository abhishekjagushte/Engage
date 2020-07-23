package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.chatscreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatFragment
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatType
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatViewModel
import kotlinx.android.synthetic.main.fragment_chat_screen.*


class ChatScreenFragment : Fragment(R.layout.fragment_chat_screen) {

    private val TAG = "ChatScreenFragment";
    private lateinit var SharedViewModel: ChatViewModel
    private lateinit var chatsAdapter: ChatsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewModel = (parentFragment as ChatFragment).viewModel

        //Setting shared viewmodel in this fragment's viewmodel

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        chatsAdapter = ChatsAdapter(recyclerView)
        chatsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                super.onChanged()
                Log.d(TAG, "onChanged: inside onChanged")
                recyclerView.scrollToPosition(0)
            }
        })

        recyclerView.adapter = chatsAdapter

        SharedViewModel.conversationID.observe(viewLifecycleOwner, Observer {
                it?.let{
                    observeChats()
                }
        })

        send_button.setOnClickListener {
            val message = message_input.text.toString().trim()
            if(!message.isEmpty()) {

                if(SharedViewModel.chatType.value == ChatType.CHAT_TYPE_121){
                    SharedViewModel.sendTextMessage121(message)
                    message_input.text.clear()
                }
                else{
                    SharedViewModel.sendTextMessageM2M(message, null)
                    message_input.text.clear()
                }

            }
        }

    }
    
    private fun observeChats(){
        Log.d(TAG, "observeChats: observing chats")
        SharedViewModel.getChatsAll().let {
            it.observe(viewLifecycleOwner, Observer {l ->
                Log.d(TAG, "Size of list = ${l.size}")
                chatsAdapter.updateList(l)
            })
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireParentFragment().requireActivity().application as EngageApplication).appComponent.addChatScreenComponent().create().inject(this)

    }

}
