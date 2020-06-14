package com.abhishekjagushte.engage.ui.chat.fragments.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.abhishekjagushte.engage.EngageApplication

import com.abhishekjagushte.engage.R
import com.google.android.material.appbar.AppBarLayout
import javax.inject.Inject

class ChatFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel by viewModels<ChatViewModel> { viewModelFactory }
    private lateinit var appBar: AppBarLayout
    private lateinit var toolbar: Toolbar

    private val TAG = "ChatFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewpager = view.findViewById<ViewPager2>(R.id.viewpager)
        viewpager.adapter = ChatStateAdapter(this)

        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.searchFragment).build()

        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        val args = ChatFragmentArgs.fromBundle(requireArguments())
        viewModel.setConversationID(args.username, args.conversationID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addChatComponent().create().inject(this)
    }

}
