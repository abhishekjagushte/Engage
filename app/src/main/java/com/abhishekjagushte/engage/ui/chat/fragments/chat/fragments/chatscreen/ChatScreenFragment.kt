package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatFragment
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatState
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatViewModel
import kotlinx.android.synthetic.main.fragment_chat_screen.*
import javax.inject.Inject


class ChatScreenFragment : Fragment(R.layout.fragment_chat_screen) {

    private val TAG = "ChatScreenFragment";
    private lateinit var SharedViewModel: ChatViewModel
    private lateinit var chatsAdapter: ChatsAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<ChatScreenViewModel> { viewModelFactory }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewModel = (parentFragment as ChatFragment).viewModel

        //Setting shared viewmodel in this fragment's viewmodel
        viewModel.sharedViewModel = SharedViewModel

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        chatsAdapter = ChatsAdapter(recyclerView)
        recyclerView.adapter = chatsAdapter

        SharedViewModel.chatState.observe(viewLifecycleOwner, Observer {
            if(it==ChatState.EXISTING){
                it.let{
                    observeChats()
                }
            }
        })

        send_button.setOnClickListener {
            val message = message_input.text.toString()
            if(!message.isEmpty()) {
                SharedViewModel.sendTextMessage121(message)
                message_input.text.clear()
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
