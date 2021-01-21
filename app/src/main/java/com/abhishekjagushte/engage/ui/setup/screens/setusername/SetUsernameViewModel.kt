package com.abhishekjagushte.engage.ui.setup.screens.setusername

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants.NOT_INITIATED
import javax.inject.Inject


class SetUsernameViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    private val TAG = "SetUsernameViewModel"

    lateinit var email: String
    lateinit var password: String

    lateinit var name: String
    lateinit var username: String

    private val _noteText = MutableLiveData<String>()
    val noteText: LiveData<String>
        get() { return _noteText }

    private val _usernameValid = MutableLiveData<Boolean>()
    val usernameValid: LiveData<Boolean>
        get() { return _usernameValid }

    val changeCompleteStatus = MutableLiveData<String>()


    init {
        changeCompleteStatus.value = NOT_INITIATED
    }

    fun confirmSetup(name: String, username: String,profileImageUri: Uri?) {


        //check if the noteText is empty to get proper status
        Log.d(TAG, noteText.value?:"Notetext value is null")
        if(noteText.value == ""){
            this.name = name
            this.username = username
            dataRepository.setNameAndUsername(name, username, null, changeCompleteStatus)
            //The status of this task is observed from the fragment
        }
    }

    fun checkUsername(username: String){
        dataRepository.checkUserName(username).addOnSuccessListener {
            //Log.d(TAG,"Inside vm checkusername ${it.size()}  ${it.isEmpty}")
            if (!it.isEmpty) {
                _noteText.value = "This username is already taken"
                _usernameValid.value = false
            }
            else{
                _noteText.value = ""
                _usernameValid.value = true
            }
        }
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