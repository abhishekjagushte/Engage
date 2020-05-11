package com.abhishekjagushte.engage.ui.main.fragments.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.utils.Constants
import javax.inject.Inject


const val ARGUMENT_NAME = "name"
const val ARGUMENT_USERNAME = "username"

class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ProfileActivityViewModel> { viewModelFactory }
    private val TAG = "ProfileFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        (application as EngageApplication).appComponent.addProfileComponent().create().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val name_text = findViewById<TextView>(R.id.name_text)
        val username_text = findViewById<TextView>(R.id.username_text)
        val timestampSteing_Text = findViewById<TextView>(R.id.timestamp_text)
        val bio_text = findViewById<TextView>(R.id.bio_text)
        val button = findViewById<Button>(R.id.add_friend_button)


        val args = intent.extras
        val name = args!!.get(ARGUMENT_NAME)
        val username = args.get(ARGUMENT_USERNAME)

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
                        button.setText(application.resources.getString(R.string.add_friend))
                    }

                    Constants.CONTACTS_CONFIRMED -> {
                        button.visibility = View.GONE
                    }
                }

            }
        })


        button.setOnClickListener {
            when (viewModel.profileDisplay.value!!.type) {
                Constants.CONTACTS_UNKNOWN -> {
                    viewModel.addFriend()
                }
            }
        }

    }
}
