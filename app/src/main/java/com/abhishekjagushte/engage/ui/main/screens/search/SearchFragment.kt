package com.abhishekjagushte.engage.ui.main.screens.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.EngageApplication

import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var adapter: SearchAdapter

    private val viewModel by viewModels<SearchFragmentViewModel> { viewModelFactory }

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val searchBar = view.findViewById<EditText>(R.id.search_bar)

        var job: Job?= null
        searchBar.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_TIME_DELAY)
                editable?.let{
                    if(editable.toString().isNotEmpty())
                        viewModel.onSearchTextQueryChanged(editable.toString())
                }
            }
        }

        adapter = SearchAdapter( SearchDataListener { searchData ->
            Toast.makeText(context, searchData.title, Toast.LENGTH_SHORT).show()

            if(searchData.type == Constants.SEARCHDATA_CONTACT){
                Log.d(TAG, "Clicked ${searchData.title} type = ${searchData.type} "+ Constants.SEARCHDATA_CONTACT)

                val profileMap = hashMapOf<String, String>(
                    "name" to searchData.title,
                    "username" to searchData.subtitle
                )

                findNavController().navigate(SearchFragmentDirections
                    .actionSearchFragmentToProfileActivity(profileMap.get("name")!!, profileMap.get("username")!!))
            }
            else if(searchData.type == Constants.SEARCHDATA_CONVERSATION){
                val conID = searchData.extras.get("networkID") as String
                Log.d(TAG, "onCreateView: $conID")
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToChatFragment(conID))
            }


        })
        recyclerView.adapter = adapter
        //Layout

        viewModel.searchResults.observe(viewLifecycleOwner, Observer {
            it.let {
                adapter.submitList(it)
            }
        })

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addSearchComponent().create().inject(this)
    }


}


//val button = view.findViewById<Button>(R.id.testButton)

//        button.setOnClickListener{
//            findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
//            Log.d(TAG, "Navigated")
//        }