package com.abhishekjagushte.engage.ui.chat.screens.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class ChatFragment : Fragment(R.layout.fragment_chat) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<ChatViewModel> { viewModelFactory }

    private val TAG = "ChatFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewpager = view.findViewById<ViewPager2>(R.id.viewpager)
        viewpager.adapter = ChatStateAdapter(this)

        val args = ChatFragmentArgs.fromBundle(requireArguments())
        Log.d(TAG, "onViewCreated: ${args.conversationID}")
        viewModel.setupScreen(args.conversationID)

        setupUI()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addChatComponent().create().inject(this)
    }

    fun setupUI(){
        viewModel.conversation.observe(viewLifecycleOwner, Observer {
            it?.let {
                requireActivity().toolbar.title = it.name
            }
        })
    }


}
