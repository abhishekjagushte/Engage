package com.abhishekjagushte.engage.ui.main.fragments.profile

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


const val ARGUMENT_NAME = "name"
const val ARGUMENT_USERNAME = "username"

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

        val name_text = view.findViewById<TextView>(R.id.name_text)
        val username_text = view.findViewById<TextView>(R.id.username_text)
        val timestampSteing_Text = view.findViewById<TextView>(R.id.timestamp_text)
        val bio_text = view.findViewById<TextView>(R.id.bio_text)
        val button = view.findViewById<Button>(R.id.add_friend_button)

        val args by navArgs<ProfileFragmentArgs>()
        name = args.name
        username = args.username

        if(name.isNullOrBlank() || username.isNullOrBlank()){
            name = arguments?.getString(Constants.ARGUMENT_NAME)
            username = arguments?.getString(Constants.ARGUMENT_USERNAME)
        }

//        val args by navArgs<ProfileActivityArgs>()
//        name = args.name
//        username = args.username

        viewModel.setProfileDisplay(username as String)

        name_text.text = name as String
        username_text.text = username

        viewModel.actionStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                FRIEND_REQUEST_SENT -> {
                    button.text = application.resources.getString(R.string.sent)
                    button.isClickable = false //TODO add unsend functionality
                }
            }
        })

        viewModel.profileDisplay.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                name_text.text = it.name
                username_text.text = it.username
                bio_text.text = it.bio
                timestampSteing_Text.text = it.timeStampString

                when (it.type) {
                    Constants.CONTACTS_UNKNOWN -> {
                        button.text = application.resources.getString(R.string.add_friend)
                    }

                    Constants.CONTACTS_CONFIRMED -> {
                        //button.visibility = View.GONE
                        button.text = application.resources.getString(R.string.chat)
                    }

                    Constants.CONTACTS_REQUESTED -> {
                        button.text = application.resources.getString(R.string.sent);
                        button.isClickable = false
                    }

                    Constants.CONTACTS_PENDING -> {
                        button.text = application.resources.getString(R.string.accept);
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
                    navController.navigate(R.id.action_profileActivity_to_chatFragment)
                }
            }
        }



    }

}


/*
const val ARGUMENT_NAME = "name"
const val ARGUMENT_USERNAME = "username"

class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ProfileActivityViewModel> { viewModelFactory }
    private val TAG = "ProfileFragment"
    var name: String? = null
    var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as EngageApplication).appComponent.addProfileComponent().create().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        val name_text = findViewById<TextView>(R.id.name_text)
        val username_text = findViewById<TextView>(R.id.username_text)
        val timestampSteing_Text = findViewById<TextView>(R.id.timestamp_text)
        val bio_text = findViewById<TextView>(R.id.bio_text)
        val button = findViewById<Button>(R.id.add_friend_button)

       val argsIntent = intent.extras

        name = argsIntent?.getString(Constants.ARGUMENT_NAME)
        username = argsIntent?.getString(Constants.ARGUMENT_USERNAME)

        if(name.isNullOrBlank() || username.isNullOrBlank()){
              val args by navArgs<ProfileActivityArgs>()
            name = args.name
            username = args.username
        }

//        val args by navArgs<ProfileActivityArgs>()
//        name = args.name
//        username = args.username

        viewModel.setProfileDisplay(username as String)

        name_text.text = name as String
        username_text.text = username

        viewModel.actionStatus.observe(this, Observer {
            when (it) {
                FRIEND_REQUEST_SENT -> {
                    button.text = application.resources.getString(R.string.sent)
                    button.isClickable = false //TODO add unsend functionality
                }
            }
        })

        viewModel.profileDisplay.observe(this, Observer {
            if (it != null) {
                name_text.text = it.name
                username_text.text = it.username
                bio_text.text = it.bio
                timestampSteing_Text.text = it.timeStampString

                when (it.type) {
                    Constants.CONTACTS_UNKNOWN -> {
                        button.text = application.resources.getString(R.string.add_friend)
                    }

                    Constants.CONTACTS_CONFIRMED -> {
                        //button.visibility = View.GONE
                        button.text = application.resources.getString(R.string.chat)
                    }

                    Constants.CONTACTS_REQUESTED -> {
                        button.text = application.resources.getString(R.string.sent);
                        button.isClickable = false
                    }

                    Constants.CONTACTS_PENDING -> {
                        button.text = application.resources.getString(R.string.accept);
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
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra(Constants.ARGUMENT_NAME, viewModel.localContact.name)
                    intent.putExtra(Constants.ARGUMENT_USERNAME, viewModel.localContact.username)
                    startActivity(intent)
                }
            }
        }

    }
}

 */