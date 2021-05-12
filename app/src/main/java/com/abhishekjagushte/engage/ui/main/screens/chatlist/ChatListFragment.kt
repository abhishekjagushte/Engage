package com.abhishekjagushte.engage.ui.main.screens.chatlist

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.databinding.FragmentChatListBinding
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_chat_list.*
import javax.inject.Inject


class ChatListFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dataRepository: DataRepository

    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar
    private lateinit var chatListAdapter: ChatListAdapter

    private val viewModel by viewModels<ChatListViewModel> { viewModelFactory }
    private val TAG = "ChatListFragment"

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
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        chatListAdapter = ChatListAdapter(dataRepository, lifecycleScope)

        val mDividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(mDividerItemDecoration)

        recyclerView.adapter = chatListAdapter

        observeChatList()
        floatingActionButtonOnCLick()
    }

    private fun observeChatList(){
        viewModel.getConversationList().observe(viewLifecycleOwner, Observer {
            it?.let{
                //Log.d(TAG, "observeChatList: ${it.size} + $it")
                chatListAdapter.updateList(it)
            }
        })
    }

    fun floatingActionButtonOnCLick(){
        floatingActionButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_chatListFragment_to_addParticipantsFragment)
        }
    }
}
