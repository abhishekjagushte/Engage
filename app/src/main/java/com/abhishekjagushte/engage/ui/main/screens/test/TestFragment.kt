package com.abhishekjagushte.engage.ui.main.screens.test

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import javax.inject.Inject

class TestFragment : Fragment(R.layout.fragment_test) {

    @Inject
    lateinit var dataRepository: DataRepository
    private val TAG = "TestFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        val contactListFragment = ContactListFragment(ContactItemClickListener {
//            Log.d(TAG, "onViewCreated: $it")
//            Toast.makeText(context, "Clicked $it", Toast.LENGTH_SHORT).show()
//        }, dataRepository.getConfirmedContacts())
//
//        childFragmentManager.beginTransaction().add(R.id.fragment, contactListFragment).commit()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.inject(this)
    }

}
