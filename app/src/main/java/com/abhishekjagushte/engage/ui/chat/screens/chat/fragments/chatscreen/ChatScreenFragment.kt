package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.chatscreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
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

    var isScrolling = false

    //TODO
    val scrollListener = object : RecyclerView.OnScrollListener(){

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

            //sets scrolling to true if not at last position
            //this prevents scrolling to last position when viewing older messages
            isScrolling = linearLayoutManager.findFirstVisibleItemPosition() != 0

            Log.d(TAG, "onScrolled: ${isScrolling}")
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
                Log.d(TAG, "onScrollStateChanged: set truee*******")
            }
        }
    }

    fun scrollToEnd(){
        if(!isScrolling)
            recyclerView.smoothScrollToPosition(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewModel = (parentFragment as ChatFragment).viewModel

        //Setting shared viewmodel in this fragment's viewmodel

        val linearLayoutManager = LinearLayoutManager(context)
        //linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager
        chatsAdapter = ChatsAdapter()

        //adds the data change listener for scrolling when new msg available
        chatsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.d(TAG, "onItemRangeInserted: inserted")
                scrollToEnd()
            }
        })

        //Sets the adapter
        recyclerView.adapter = chatsAdapter

        recyclerView.addOnScrollListener(scrollListener)

        SharedViewModel.conversationID.observe(viewLifecycleOwner, Observer {
                it?.let{
                    observeChats()
                    markMessagesRead()
                }
        })

        send_button.setOnClickListener {
            val message = message_input.text.toString().trim()
            if(!message.isEmpty()) {

                isScrolling = false //this ensures we scroll to last if we send a message
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

    //Sets all messages read by me
    private fun markMessagesRead() {
        SharedViewModel.markMessagesRead()
    }

    private fun observeChats(){
        Log.d(TAG, "observeChats: observing chats")
        SharedViewModel.getChatsAll().let {
            it.observe(viewLifecycleOwner, Observer {l ->
                chatsAdapter.updateList(l)
            })
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireParentFragment().requireActivity().application as EngageApplication).appComponent.addChatScreenComponent().create().inject(this)

    }

}
