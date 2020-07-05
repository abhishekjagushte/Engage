package com.abhishekjagushte.engage.ui.main.screens.people

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

import com.abhishekjagushte.engage.R
import com.google.android.material.appbar.AppBarLayout

class PeopleFragment : Fragment() {

    companion object {
        fun newInstance() = PeopleFragment()
    }

    private lateinit var viewModel: PeopleViewModel

    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PeopleViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        //val appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()

        val appBarConfiguration = AppBarConfiguration.Builder(R.id.peopleFragment).build()

        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            navController.navigate(R.id.action_peopleFragment_to_testFragment)
        }
    }

}
