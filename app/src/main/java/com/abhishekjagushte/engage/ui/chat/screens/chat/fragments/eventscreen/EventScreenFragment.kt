package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.eventscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatFragment
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatViewModel
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.android.synthetic.main.fragment_event_screen.*
import javax.inject.Inject


class EventScreenFragment : Fragment(R.layout.fragment_event_screen) {

    private lateinit var SharedViewModel: ChatViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SharedViewModel = (parentFragment as ChatFragment).viewModel

        val speedDialView = speedDial
        speedDialView.inflate(R.menu.create_event_speeddial)
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.reminder -> {
                    findNavController().navigate(R.id.action_chatFragment_to_createReminderDialog)
                    speedDialView.close()
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireParentFragment().requireActivity().application as EngageApplication)
            .appComponent.addEventScreenComponent().create().inject(this)
    }

}
