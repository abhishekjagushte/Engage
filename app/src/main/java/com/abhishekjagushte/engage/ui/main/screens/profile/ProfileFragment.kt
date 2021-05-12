package com.abhishekjagushte.engage.ui.main.screens.profile

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.FilePathContract
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ProfileFragmentViewModel> { viewModelFactory }
    private val TAG = "ProfileFragment"
    var name: String? = null
    var username: String? = null
    val handler = Handler(Looper.getMainLooper())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        if (name.isNullOrBlank() || username.isNullOrBlank()) {
            name = arguments?.getString(Constants.ARGUMENT_NAME)
            username = arguments?.getString(Constants.ARGUMENT_USERNAME)
        }

        var updated = false
        viewModel.getProfileLive(username!!).observe(viewLifecycleOwner, {
            it.let {
                viewModel.setProfileDisplay(it, username!!)
                if (it != null) {
                    viewModel.localContact = it
                    if (!updated) {
                        viewModel.getUpdatedProfile(username!!)
                        updated = true
                    }
                }
            }
        })

        nameText.text = name as String
        usernameText.text = username

        viewModel.actionStatus.observe(viewLifecycleOwner, {
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

        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                handler.post {
                    image_progress_bar.visibility=View.VISIBLE
                }
                viewModel.getUpdatedProfile(username!!)
                val profilePicUri =
                    FilePathContract.getContactsProfilePhotoUri(username!!)
                if (profilePicUri != Uri.EMPTY) {
                    handler.post {
                        display_picture_imageView.setImageURI(profilePicUri)
                    }
                }
                else{
                    Log.e(TAG, "onViewCreated: Empty")
                }
                handler.post {
                    image_progress_bar.visibility=View.GONE
                }
            }
        }

        viewModel.profileDisplay.observe(viewLifecycleOwner, {
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
                    findNavController().navigate(
                        ProfileFragmentDirections
                            .actionProfileActivityToChatFragment(viewModel.localContact.username)
                    )
                }
            }
        }
    }
}