package com.abhishekjagushte.engage.ui.setup.fragments.setusername

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.AppDatabase
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.database.UserData
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.network.convertDomainObject
import com.abhishekjagushte.engage.ui.setup.fragments.SetUsernameFragmentArgs
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

class SetUsernameFragment: Fragment() {

    private val TAG: String = "SetUsernameFragment"
    private lateinit var mAuth: FirebaseAuth
    lateinit var args: SetUsernameFragmentArgs


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.set_username_fragment, container, false)

        val nameInput = view.findViewById<TextInputEditText>(R.id.name_input)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.username_input)

        val confirmButton = view.findViewById<Button>(R.id.confirm_button)

        //check username to be unique from firebase
        usernameInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, s.toString())
            }

        })

        confirmButton.setOnClickListener {
            if(checkInputs(nameInput.text.toString() , usernameInput.text.toString())){

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.d(TAG, "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new Instance ID token
                        val token = task.result?.token
                        Log.d(TAG, token)
                        if (token != null) {
                            firebaseAddData(nameInput.text.toString() , usernameInput.text.toString(), token)                        }
                    })
            }

        }

        return view
    }

    private fun firebaseAddData(name: String, username: String, token: String){
        mAuth = FirebaseAuth.getInstance()

        val uid = mAuth.currentUser!!.uid

        val profile = Profile(
            id = uid,
            name = name,
            username = username,
            joinTimeStamp = Date(),
            notificationChannelID = token
        )

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users").document(uid).set(profile)
            .addOnCompleteListener(OnCompleteListener { task ->
                if(task.isSuccessful){
                    addDataLocal(profile.convertDomainObject(0))
                    Log.d(TAG, "Completed")
                }
                else{
                    Log.d(TAG, "Failed")
                }
            })
    }

    private fun addDataLocal(myself: Contact) {
        val application = requireNotNull(this.activity).application
        val databaseDao = AppDatabase.getInstance(application).databaseDao

        args =
            SetUsernameFragmentArgs.fromBundle(
                requireArguments()
            )

        databaseDao.insertMeinContacts(myself)
        databaseDao.insertCredentials(UserData(args.email, args.password))
    }

    private fun checkInputs(name: String, username: String): Boolean {
        return checkName(name) && checkUsername(username)
    }

    private fun checkName(name: String): Boolean{
        return name.isNotEmpty()
    }

    private fun checkUsername(username: String): Boolean {
        //Check from firebase
        return true
    }
}