package com.abhishekjagushte.engage.ui.main.screens.events


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

import com.abhishekjagushte.engage.R
import com.google.android.material.appbar.AppBarLayout

class EventsFragment : Fragment(R.layout.fragment_events) {

    companion object {
        fun newInstance() = EventsFragment()
    }

    private lateinit var viewModel: EventsViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EventsViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
