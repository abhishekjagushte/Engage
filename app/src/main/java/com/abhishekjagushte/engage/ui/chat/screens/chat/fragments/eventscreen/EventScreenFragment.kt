package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.eventscreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.chat.adapters.EventsAdapter
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatFragment
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatFragmentDirections
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatType
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatViewModel
import com.abhishekjagushte.engage.utils.Constants
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.fragment_event_screen.*
import javax.inject.Inject


class EventScreenFragment : Fragment(R.layout.fragment_event_screen) {

    private lateinit var SharedViewModel: ChatViewModel
    private lateinit var eventsAdapter: EventsAdapter
    private val TAG = "EventScreenFragment"

    @Inject
    lateinit var dataRepository: DataRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewModel = (parentFragment as ChatFragment).viewModel

        events_recyclerview.layoutManager = LinearLayoutManager(context)
        eventsAdapter = EventsAdapter(dataRepository, false)
        events_recyclerview.adapter = eventsAdapter

        val speedDialView = speedDial
        speedDialView.inflate(R.menu.create_event_speeddial)
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.reminder -> {
                    findNavController().navigate(
                        ChatFragmentDirections.actionChatFragmentToCreateReminderDialog(
                            if (SharedViewModel.chatType.value == ChatType.CHAT_TYPE_121) Constants.CONVERSATION_TYPE_121 else Constants.CONVERSATION_TYPE_M2M,
                            SharedViewModel.conversationID.value
                        )
                    )
                    return@OnActionSelectedListener true
                }
                R.id.poll -> {
                    speedDialView.close()
                    return@OnActionSelectedListener true
                }
                else -> {
                    speedDialView.close()
                    return@OnActionSelectedListener true
                }
            }
        })

        SharedViewModel.conversationID.observe(viewLifecycleOwner, Observer {
            it?.let{
                observeEvents()
            }
        })
    }

    private fun observeEvents(){
        Log.d(TAG, "observeChats: observing chats")
        SharedViewModel.getEvents().let {
            it.observe(viewLifecycleOwner, Observer { l ->
                Log.d(TAG, "observeEvents: $l")
                eventsAdapter.updateList(l)
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireParentFragment().requireActivity().application as EngageApplication)
            .appComponent.addEventScreenComponent().create().inject(this)
    }

}
