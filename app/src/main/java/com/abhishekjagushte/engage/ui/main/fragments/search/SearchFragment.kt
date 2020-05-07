package com.abhishekjagushte.engage.ui.main.fragments.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.EngageApplication

import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.setup.fragments.login.LoginFragmentViewModel
import javax.inject.Inject

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SearchFragmentViewModel> { viewModelFactory }

    companion object {
        fun newInstance() = SearchFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)
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

        val adapter = SearchAdapter()
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