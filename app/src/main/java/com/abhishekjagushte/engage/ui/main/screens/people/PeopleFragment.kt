package com.abhishekjagushte.engage.ui.main.screens.people

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.ContactNameUsername
import com.abhishekjagushte.engage.ui.fragments.ContactListFragment
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactItemClickListener
import com.abhishekjagushte.engage.ui.fragments.adapters.ContactLongItemClickListener
import com.abhishekjagushte.engage.ui.main.screens.profile.ProfileFragmentDirections
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.android.synthetic.main.fragment_add_participants.*

class PeopleFragment : Fragment(R.layout.fragment_people) {

    lateinit var contactListFragment: ContactListFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeContactListFragment()
    }

    private fun initializeContactListFragment() {
        contactListFragment = ContactListFragment(
            ContactItemClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionGlobalProfileFragment(it.name, it.username))
            },
            ContactLongItemClickListener { /*Nothing*/ }, Constants.CONTACT_LIST_MODE_NORMAL
        )

        childFragmentManager.beginTransaction().add(R.id.fragmentContainer, contactListFragment)
            .commit()
    }

}
