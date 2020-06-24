package com.abhishekjagushte.engage.ui.main.fragments.chatlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.databinding.FragmentChatListBinding
import com.abhishekjagushte.engage.ui.main.MainActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_chat_list.*
import javax.inject.Inject

class ChatListFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar
    private lateinit var chatListAdapter: ChatListAdapter

    private val viewModel by viewModels<ChatListViewModel> { viewModelFactory }

    companion object {
        fun newInstance() = ChatListFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentChatListBinding>(inflater,
            R.layout.fragment_chat_list, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addChatListComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.chatListFragment).build()

        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        recyclerView.layoutManager = LinearLayoutManager(context)
        chatListAdapter = ChatListAdapter()
        recyclerView.adapter = chatListAdapter

        observeChatList()
    }

    private fun observeChatList(){
        viewModel.getConversationList().observe(viewLifecycleOwner, Observer {
            it?.let{
                println(it)
                chatListAdapter.updateList(it)
            }
        })
    }
}
