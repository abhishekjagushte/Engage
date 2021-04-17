package com.abhishekjagushte.engage.ui.main.screens.events


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.chat.adapters.EventsAdapter
import kotlinx.android.synthetic.main.fragment_event_screen.*
import javax.inject.Inject

class EventsFragment : Fragment(R.layout.fragment_events) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<EventsFragmentViewModel> {viewModelFactory}
    private lateinit var eventsAdapter: EventsAdapter

    private val TAG = "EventsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        events_recyclerview.layoutManager = LinearLayoutManager(context)
        eventsAdapter = EventsAdapter(viewModel.dataRepository, true)
        events_recyclerview.adapter = eventsAdapter


        viewModel.getAllEvents().let {
            it.observe(viewLifecycleOwner, Observer { l ->
                Log.d(TAG, "observeEvents: $l")
                eventsAdapter.updateList(l)
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addEventFragmentComponent().create().inject(this)
    }
}
