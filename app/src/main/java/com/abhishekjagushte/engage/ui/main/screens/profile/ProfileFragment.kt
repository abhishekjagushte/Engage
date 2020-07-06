package com.abhishekjagushte.engage.ui.main.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.material.appbar.AppBarLayout
import javax.inject.Inject



class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ProfileFragmentViewModel> { viewModelFactory }
    private val TAG = "ProfileFragment"
    var name: String? = null
    var username: String? = null
    private lateinit var toolbar: Toolbar
    private lateinit var appBar: AppBarLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        //val appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()

        val application = requireActivity().application

        (application as EngageApplication).appComponent.addProfileComponent().create().inject(this)

        val nameText = view.findViewById<TextView>(R.id.name_text)
        val usernameText = view.findViewById<TextView>(R.id.username_text)
        val timestampText = view.findViewById<TextView>(R.id.timestamp_text)
        val bioText = view.findViewById<TextView>(R.id.bio_text)
        val button = view.findViewById<Button>(R.id.add_friend_button)


        val args by navArgs<ProfileFragmentArgs>()
        name = args.name
        username = args.username

        if(name.isNullOrBlank() || username.isNullOrBlank()){
            name = arguments?.getString(Constants.ARGUMENT_NAME)
            username = arguments?.getString(Constants.ARGUMENT_USERNAME)
        }

        var updated = false
        viewModel.getProfileLive(username!!).observe(viewLifecycleOwner, Observer {
            it.let{
                viewModel.setProfileDisplay(it, username!!)
                if(it!=null) {
                    viewModel.localContact = it
                    if(!updated) {
                        viewModel.getUpdatedProfile(username!!)
                        updated = true
                    }
                }
            }
        })

        nameText.text = name as String
        usernameText.text = username

        viewModel.actionStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                FRIEND_REQUEST_SENT -> {
                    button.text = application.resources.getString(R.string.sent)
                    button.isClickable = false //TODO add unsend functionality
                }

                FRIEND_REQUEST_ACCEPTED -> {
                    button.text = application.resources.getString((R.string.chat))
                    button.isClickable = true
                }
            }
        })

        viewModel.profileDisplay.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                nameText.text = it.name
                usernameText.text = it.username
                bioText.text = it.bio
                timestampText.text = it.timeStampString

                when (it.type) {
                    Constants.CONTACTS_UNKNOWN -> {
                        button.isClickable = true
                        button.text = application.resources.getString(R.string.add_friend)
                    }

                    Constants.CONTACTS_CONFIRMED -> {
                        button.isClickable = true
                        button.text = application.resources.getString(R.string.chat)
                    }

                    Constants.CONTACTS_REQUESTED -> {
                        button.text = application.resources.getString(R.string.sent)
                        button.isClickable = false
                    }

                    Constants.CONTACTS_PENDING -> {
                        button.isClickable = true
                        button.text = application.resources.getString(R.string.accept)
                    }
                }

            }
        })

        button.setOnClickListener {
            when (viewModel.profileDisplay.value!!.type) {
                Constants.CONTACTS_UNKNOWN -> {
                    viewModel.addFriend()
                }

                Constants.CONTACTS_PENDING -> viewModel.acceptRequest()

                Constants.CONTACTS_CONFIRMED -> {
                    navController.navigate(ProfileFragmentDirections
                        .actionProfileActivityToChatFragment(viewModel.localContact.username))
                }
            }
        }
    }

    private fun getUpdatedProfile(){

    }
}