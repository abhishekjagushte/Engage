package com.abhishekjagushte.engage.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.Contact
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactItemClickListener
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactListAdapter
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactListDataItem
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactLongItemClickListener
import kotlinx.android.synthetic.main.fragment_contact_list.*
import javax.inject.Inject


class ContactListFragment(
    val clickListener: ContactItemClickListener,
    val longClickListener: ContactLongItemClickListener,
    val mode: Int) : Fragment(R.layout.fragment_contact_list) {

    private val TAG = "ContactListFragment"
    @Inject
    lateinit var dataRepository: DataRepository

    lateinit var adapter: ContactListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Inside ContactList")

        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        adapter = ContactListAdapter(clickListener, longClickListener, mode, requireContext())

        recyclerView.adapter = adapter
    }

    fun updateList(list: List<Contact>){
        adapter.updateList(list)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireParentFragment().requireActivity().application as EngageApplication).appComponent.inject(this)
    }

    fun removeUsername(username: String){
        for(c in adapter.currentList){
            if((c as ContactListDataItem.ContactItem).contact.username.equals(username)) {
                c.selected = false
                c.contact.selected = false
            }
        }
        adapter.notifyDataSetChanged()
    }
}