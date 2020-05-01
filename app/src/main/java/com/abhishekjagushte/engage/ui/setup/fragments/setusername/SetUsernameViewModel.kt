package com.abhishekjagushte.engage.ui.setup.fragments.setusername

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.network.convertDomainObject
import com.abhishekjagushte.engage.repository.AuthRepository
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetUsernameViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataRepository: DataRepository
): ViewModel() {

    private var viewModelJob = Job()
    private val TAG = "SetUsernameViewModel"
    private lateinit var mAuth: FirebaseAuth

    lateinit var email: String
    lateinit var password: String


    private val _noteText = MutableLiveData<String>()
    val noteText: LiveData<String>
        get() { return _noteText }

    private val _usernameValid = MutableLiveData<Boolean>()
    val usernameValid: LiveData<Boolean>
        get() { return _usernameValid }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun confirmSetup(name: String, username: String) {


    }

    fun checkUsername(username: String){
        dataRepository.checkUserName(username).addOnSuccessListener {
            //Log.d(TAG,"Inside vm checkusername ${it.size()}  ${it.isEmpty}")
            if (!it.isEmpty) {
                _noteText.value = "This username is already taken"
            }
            else
                _noteText.value = ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}


/*
    Confirm Setup

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
                        val profile = repository.firebaseAddDataSignUp(name, username, token)
                        if (profile != null){
                            repository.addDataLocalSignUp(profile.convertDomainObject(0),email, password)
                            Log.d(TAG, "Signup Completed")
                        }
                        else{
                            Log.d(TAG, "Profile is null")
                        }
                    }
                    Log.d(TAG, token)
                }
                else{
                    Log.d(TAG, "Failed to get token")
                }
            })
 */


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