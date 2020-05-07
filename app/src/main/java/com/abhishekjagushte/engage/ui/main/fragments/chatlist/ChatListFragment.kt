package com.abhishekjagushte.engage.ui.main.fragments.chatlist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abhishekjagushte.engage.EngageApplication

import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.databinding.ChatListFragmentBinding
import com.abhishekjagushte.engage.ui.setup.fragments.login.LoginFragmentViewModel
import javax.inject.Inject

class ChatListFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ChatListViewModel> { viewModelFactory }

    companion object {
        fun newInstance() = ChatListFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<ChatListFragmentBinding>(inflater,R.layout.chat_list_fragment, container, false)
        //return inflater.inflate(R.layout.chat_list_fragment, container, false)

        viewModel.showUserDataCount()

        viewModel.testText.observe(viewLifecycleOwner, Observer {
            binding.test.text = it
        })

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addChatListComponent().create().inject(this)
    }
}
