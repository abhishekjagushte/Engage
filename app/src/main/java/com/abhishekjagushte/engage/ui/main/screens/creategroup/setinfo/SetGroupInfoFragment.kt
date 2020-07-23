package com.abhishekjagushte.engage.ui.main.screens.creategroup.setinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.viewmodels.AddParticipantSharedViewModel
import com.abhishekjagushte.engage.ui.viewmodels.LoadingState
import kotlinx.android.synthetic.main.fragment_set_group_info.*


class SetGroupInfoFragment : Fragment(R.layout.fragment_set_group_info) {

    val sharedViewModel: AddParticipantSharedViewModel by navGraphViewModels(R.id.addParticipantNavGraph)

    private val TAG = "SetGroupInfoFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCompleteState()

        doneFAB.setOnClickListener {
            if(groupNameText.text.toString().isNotEmpty())
                sharedViewModel.createGroup(groupNameText.text.toString())
        }
    }

    private fun observeCompleteState() {
        sharedViewModel.completeState.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    LoadingState.COMPLETED -> {
                        //TODO
                    }

                    LoadingState.LOADING -> {
                        //TODO
                    }
                }
            }
        })
    }


}