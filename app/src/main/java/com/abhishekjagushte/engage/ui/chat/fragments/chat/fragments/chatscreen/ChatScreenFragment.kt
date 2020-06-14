package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.EngageApplication

import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatFragment
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatViewModel
import javax.inject.Inject


class ChatScreenFragment : Fragment(R.layout.fragment_chat_screen) {

    private val TAG = "ChatScreenFragment";
    private lateinit var SharedViewModel: ChatViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<ChatViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewModel = (parentFragment as ChatFragment).viewModel

        val messageInput: EditText = view.findViewById(R.id.message_input)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val sendButton: ImageButton = view.findViewById(R.id.send_button)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireParentFragment().requireActivity().application as EngageApplication).appComponent.addChatScreenComponent().create().inject(this)
    }

}
