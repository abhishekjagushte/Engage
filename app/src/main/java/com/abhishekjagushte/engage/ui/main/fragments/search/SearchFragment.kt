package com.abhishekjagushte.engage.ui.main.fragments.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
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
import javax.inject.Inject

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var appBar: AppBarLayout
    lateinit var toolbar: Toolbar

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

        searchBar.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty())
                    viewModel.onSearchTextQueryChanged(s.toString())
            }

        })

        val adapter = SearchAdapter( SearchDataListener { searchData ->
            Toast.makeText(context, searchData.title, Toast.LENGTH_SHORT).show()

            if(searchData.type == Constants.SEARCHDATA_CONTACT){
                Log.d(TAG, "Clicked ${searchData.title} type = ${searchData.type} "+ Constants.SEARCHDATA_CONTACT)

                val profileMap = hashMapOf<String, String>(
                    "name" to searchData.title,
                    "username" to searchData.subtitle
                )

//                val intent = Intent(context, ProfileActivity::class.java)
//                intent.putExtra(ARGUMENT_NAME, profileMap.get("name"))
//                intent.putExtra(ARGUMENT_USERNAME, profileMap.get("username"))
//                startActivity(intent)

                findNavController().navigate(SearchFragmentDirections
                    .actionSearchFragmentToProfileActivity(profileMap.get("name")!!, profileMap.get("username")!!))
            }
            else if(searchData.type == Constants.SEARCHDATA_CONVERSATION){
                TODO("Implement navigate to chat activity")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.searchFragment).build()

        appBar = view.findViewById(R.id.app_bar)
        toolbar = view.findViewById(R.id.toolbar)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

}


//val button = view.findViewById<Button>(R.id.testButton)

//        button.setOnClickListener{
//            findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
//            Log.d(TAG, "Navigated")
//        }