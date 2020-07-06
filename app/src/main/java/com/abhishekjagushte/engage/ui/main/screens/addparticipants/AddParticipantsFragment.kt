package com.abhishekjagushte.engage.ui.main.screens.addparticipants

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.google.android.material.appbar.AppBarLayout
import javax.inject.Inject

class AddParticipantsFragment : Fragment(R.layout.fragment_add_participants) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar

    private val viewModel by viewModels<AddParticipantsViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.searchFragment).build()

        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addParticipantsComponent().create().inject(this)
    }

}