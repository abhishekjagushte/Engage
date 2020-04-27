package com.abhishekjagushte.engage.ui.setup.fragments.setusername

import android.util.Log
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SetUsernameViewModel(/*private val databaseDao: DatabaseDao, application: Application*/): /*AndroidViewModel(application)*/
    ViewModel() {

    private var viewModelJob = Job()
    private val TAG = "SetUsernameViewModel"
    private lateinit var mAuth: FirebaseAuth

    lateinit var email: String
    lateinit var password: String

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    lateinit var repository: DataRepository

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
                    uiScope.launch {
                        repository.firebaseAddDataSignUp(name, username, token, email, password)
                    }
                    Log.d(TAG, token)
                }
                else{
                    Log.d(TAG, "Failed to get token")
                }
            })
    }

//
//    private fun firebaseAddData(name: String, username: String, token: String){
//        mAuth = FirebaseAuth.getInstance()
//        val uid = mAuth.currentUser!!.uid
//
//        val profile = Profile(
//            id = uid,
//            name = name,
//            username = username,
//            joinTimeStamp = Date(),
//            notificationChannelID = token
//        )
//
//        val firestore = FirebaseFirestore.getInstance()
//
//        firestore.collection("users").document().set(profile)
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (task.isSuccessful) {
//
//                    uiScope.launch {
//                        addDataLocal(profile.convertDomainObject(0))
//                        Log.d(TAG, "Completed")
//                    }
//
//                } else {
//                    Log.d(TAG, "Failed")
//                }
//            })
//    }
//
//
//
//    private suspend fun addDataLocal(myself: Contact) {
//        withContext(Dispatchers.IO) {
//            databaseDao.insertMeinContacts(myself)
//            databaseDao.insertCredentials(UserData(email, password))
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}