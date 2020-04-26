package com.abhishekjagushte.engage.ui.setup.fragments.setusername

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.database.DatabaseDao
import com.abhishekjagushte.engage.database.UserData
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.network.convertDomainObject
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.*
import java.util.*

class SetUsernameViewModel(private val databaseDao: DatabaseDao, application: Application): AndroidViewModel(application){

    private var viewModelJob = Job()
    private val TAG = "SetUsernameViewModel"
    private lateinit var mAuth: FirebaseAuth

    lateinit var email: String
    lateinit var password: String

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun confirmSetup(name: String, username: String) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token

                if (token != null) {
                    firebaseAddData(name, username, token)
                    Log.d(TAG, token)
                }
                else{
                    Log.d(TAG, "Failed to get token")
                }
            })
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
                    uiScope.launch {
                        addDataLocal(profile.convertDomainObject(0))
                        Log.d(TAG, "Completed")
                    }
                }
                else{
                    Log.d(TAG, "Failed")
                }
            })
    }

    private suspend fun addDataLocal(myself: Contact) {
        withContext(Dispatchers.IO) {
            databaseDao.insertMeinContacts(myself)
            databaseDao.insertCredentials(UserData(email, password))
        }
    }

}