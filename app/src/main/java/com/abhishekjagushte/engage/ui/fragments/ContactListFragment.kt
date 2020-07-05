package com.abhishekjagushte.engage.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactItemClickListener
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactListAdapter
import kotlinx.android.synthetic.main.fragment_contact_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ContactListFragment(
    val clickListener: ContactItemClickListener,
    val contactsList: LiveData<List<Contact>>) : Fragment(R.layout.fragment_contact_list) {

    private val TAG = "ContactListFragment"
    @Inject
    lateinit var dataRepository: DataRepository


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Inside ContactList")
        setList()
    }

    fun setList(){
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        val adapter = ContactListAdapter(clickListener)

        recyclerView.adapter = adapter
        contactsList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.updateList(it)
                Log.d(TAG, "setList: Size = ${it.size}")

            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireParentFragment().requireActivity().application as EngageApplication).appComponent.inject(this)
    }

}