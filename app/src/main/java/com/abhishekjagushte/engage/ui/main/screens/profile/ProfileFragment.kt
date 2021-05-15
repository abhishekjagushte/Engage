package com.abhishekjagushte.engage.ui.main.screens.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.GlideApp
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dataRepository: DataRepository

    private val viewModel by viewModels<ProfileFragmentViewModel> { viewModelFactory }
    private val TAG = "ProfileFragment"
    var name: String? = null
    var username: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val application = requireActivity().application
        (application as EngageApplication).appComponent.addProfileComponent().create().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                //This tries to find the local copy of profile information
                viewModel.setProfileDisplay(it, username!!)

                //if local copy of contact is available
                if (it != null) {
                    viewModel.localContact = it
                    if (!updated) {
                        viewModel.getUpdatedProfile(username!!)
                        updated = true
                    }
                }
            }
        })

        name_text.text = name as String
        username_text.text = username

        viewModel.actionStatus.observe(viewLifecycleOwner, {
            when (it) {
                FRIEND_REQUEST_SENT -> {
                    add_friend_button.text = requireContext().resources.getString(R.string.sent)
                    add_friend_button.isClickable = false //TODO add unsend functionality
                }

                FRIEND_REQUEST_ACCEPTED -> {
                    add_friend_button.text = requireContext().resources.getString((R.string.chat))
                    add_friend_button.isClickable = true
                }
            }
        })

        viewModel.profileDisplay.observe(viewLifecycleOwner, {
            if (it != null) {
                name_text.text = it.name
                username_text.text = it.username
                bio_text.text = it.bio
                //timestamp_text.text = it.timeStampString
                setProfilePhoto(it.username, it.dp_thmb_url)

                when (it.type) {
                    Constants.CONTACTS_UNKNOWN -> {
                        add_friend_button.isClickable = true
                        add_friend_button.text = requireContext().resources.getString(R.string.add_friend)
                    }

                    Constants.CONTACTS_CONFIRMED -> {
                        add_friend_button.isClickable = true
                        add_friend_button.text = requireContext().resources.getString(R.string.chat)
                    }

                    Constants.CONTACTS_REQUESTED -> {
                        add_friend_button.text = requireContext().resources.getString(R.string.sent)
                        add_friend_button.isClickable = false
                    }

                    Constants.CONTACTS_PENDING -> {
                        add_friend_button.isClickable = true
                        add_friend_button.text = requireContext().resources.getString(R.string.accept)
                    }
                }

            }
        })

        add_friend_button.setOnClickListener {
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

    fun setProfilePhoto(username: String, dp_thmb_url: String?){
            image_progress_bar.visibility = View.VISIBLE
            if(dp_thmb_url==null){
                dataRepository.getImageThumbnailDownloadURL(username).addOnSuccessListener {thumbnailUrl ->

                    GlideApp.with(this)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.ic_mail_profile_picture_male)
                        .error(R.drawable.ic_mail_profile_picture_male)
                        .into(display_picture_imageView)

                    dataRepository.getImageDownloadURL(username).addOnSuccessListener {imageUrl ->

                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_mail_profile_picture_male)
                            .error(R.drawable.ic_mail_profile_picture_male)
                            .into(display_picture_imageView)

                        image_progress_bar.visibility = View.GONE
                    }
                }
            }
            else{
                Glide.with(this)
                    .load(dp_thmb_url)
                     .error(R.drawable.ic_mail_profile_picture_male)
                    .into(display_picture_imageView)

                dataRepository.getImageDownloadURL(username).addOnSuccessListener {
                    Glide.with(this)
                        .load(it)
                        .error(R.drawable.ic_mail_profile_picture_male)
                        .into(display_picture_imageView)

                    image_progress_bar.visibility = View.GONE


                }
            }
        }

}
