package com.abhishekjagushte.engage.ui.main.fragments.chatlist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.abhishekjagushte.engage.EngageApplication

import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.databinding.FragmentChatListBinding
import com.abhishekjagushte.engage.ui.main.MainActivity
import com.abhishekjagushte.engage.ui.main.MainActivityViewModel
import com.google.android.material.appbar.AppBarLayout
import javax.inject.Inject

class ChatListFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar

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
        //return inflater.inflate(R.layout.fragment_chat_list, container, false)

        (requireActivity() as MainActivity).viewModel.printPanda()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.chatListFragment).build()

        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }
}
