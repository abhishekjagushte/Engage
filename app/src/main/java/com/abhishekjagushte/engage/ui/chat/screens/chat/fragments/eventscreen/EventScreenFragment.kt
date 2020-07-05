package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.eventscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatFragment
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatViewModel
import javax.inject.Inject


class EventScreenFragment : Fragment(R.layout.fragment_event_screen) {

    private lateinit var SharedViewModel: ChatViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<ChatViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewModel = (parentFragment as ChatFragment).viewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireParentFragment().requireActivity().application as EngageApplication)
            .appComponent.addEventScreenComponent().create().inject(this)
    }

}
