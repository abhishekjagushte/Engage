package com.abhishekjagushte.engage.ui.main.screens.creategroup.addparticipants

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.ContactNameUsername
import com.abhishekjagushte.engage.ui.activity.MainActivity
import com.abhishekjagushte.engage.ui.fragments.ContactListFragment
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactItemClickListener
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactLongItemClickListener
import com.abhishekjagushte.engage.ui.viewmodels.AddParticipantSharedViewModel
import com.abhishekjagushte.engage.ui.viewmodels.factory.AddParticipantSharedViewModelFactory
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_add_participants.*

class AddParticipantsFragment : Fragment(R.layout.fragment_add_participants) {

    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar

    lateinit var sharedViewModel: AddParticipantSharedViewModel

    lateinit var adapter: AddedParticipantsAdapter

    private val TAG = "AddParticipantsFragment"
    lateinit var contactListFragment: ContactListFragment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.searchFragment).build()

        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)


        participantsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = AddedParticipantsAdapter(ParticipantRemoveListener{
            sharedViewModel.removeParticipant(it)
        })
        participantsRecyclerView.adapter = adapter

        //Initializes the contact list fragment
        initializeContactListFragment()

        //Initializes the shared viewmodel
        initializeViewModel()

        observeQueryList()
        observeParticipants()
        listenQuery()

        doneFAB.setOnClickListener {
            it.findNavController().navigate(R.id.action_addParticipantsFragment_to_setGroupInfoFragment)
        }
    }

    private fun listenQuery(){
        search_bar.addTextChangedListener { editable ->
            editable?.let {
                sharedViewModel.query(it.toString())
            }
        }
    }

    private fun initializeContactListFragment() {
        contactListFragment = ContactListFragment(ContactItemClickListener {
            if(sharedViewModel.participantsHash[it.username] == null)
                sharedViewModel.addParticipant(ContactNameUsername(it.name, it.username))
            else
                sharedViewModel.removeParticipant(ContactNameUsername(it.name, it.username))

            sharedViewModel.query(search_bar.text.toString())

        },
            ContactLongItemClickListener { /*Nothing*/ }, Constants.CONTACT_LIST_MODE_SELECTION)

        childFragmentManager.beginTransaction().add(R.id.fragmentContainer, contactListFragment)
            .commit()
    }

    private fun initializeViewModel(){
        val viewModelFactory =
            AddParticipantSharedViewModelFactory(
                (activity as MainActivity).dataRepository
            )

        val sv: AddParticipantSharedViewModel by navGraphViewModels(R.id.addParticipantNavGraph){ viewModelFactory }
        sharedViewModel = sv

    }

    private fun observeParticipants(){
        sharedViewModel.participants.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it.isEmpty())
                    participantsRecyclerView.visibility = View.GONE
                else
                    participantsRecyclerView.visibility = View.VISIBLE

                adapter.updateList(it)
            }
        })
    }

    private fun observeQueryList(){
        sharedViewModel.queryList.observe(viewLifecycleOwner, Observer {
            it?.let{
                contactListFragment.updateList(it)
            }
        })
    }
}


//        activity?.run {
//            val viewModelFactory =
//                AddParticipantSharedViewModelFactory(
//                    (activity as MainActivity).dataRepository
//                )
//            sharedViewModel = ViewModelProvider(this, viewModelFactory).get(
//                AddParticipantSharedViewModel::class.java)
//        }