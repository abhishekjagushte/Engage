package com.abhishekjagushte.engage.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.datasource.localdatasource.FirebaseInstanceSource
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseDataSource
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.network.convertDomainObject
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import java.lang.Exception
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val localDataSource: LocalDataSource,
    private val firebaseDataSource: FirebaseDataSource,
    private val firebaseInstanceId: FirebaseInstanceSource
){
    private val TAG: String = "DataRepository"

    private val repoJob = Job()
    private val repoScope = CoroutineScope(Dispatchers.Main + repoJob)


    //Functions that send Status to Viewmodels.....

    fun login(email: String, password: String, completeStatus: MutableLiveData<String>){
        authDataSource.login(email, password).addOnSuccessListener {
            val uid = authDataSource.getCurrentUserUID()
            Log.d(TAG, "Login Success")
            repoScope.async(Dispatchers.IO){
                signUpAddCredentialsLocal(email, password)
            }

            firebaseDataSource.getLoginData(uid) .addOnSuccessListener {
                val profile = it.documents[0].toObject<Profile>()
                val contact = profile!!.convertDomainObject(Constants.CONTACTS_ME)
                repoScope.launch {
                    withContext(Dispatchers.IO){
                        localDataSource.addMyDetailsContactInContacts(contact)
                        completeStatus.postValue(Constants.LOCAL_DB_SUCCESS)
                    }
                }
            }
        }.addOnFailureListener {

        }
    }


    fun setNameAndUsername(name: String, username: String, completeStatus: MutableLiveData<String>){
        firebaseInstanceId.getNotificationChannelID()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.d(TAG,token?:"Not generated")

                if(token!=null){
                    authDataSource.setNameAndUsername(name,username, token).addOnSuccessListener {
                        completeStatus.value = Constants.FIREBASE_CHANGE_COMPLETE

                        repoScope.launch {
                            withContext(Dispatchers.IO) {
                                try {
                                    addMyDetailsInContacts(name, username, Constants.CONTACTS_ME)
                                    completeStatus.postValue(Constants.LOCAL_DB_SUCCESS)
                                } catch (e: Exception) {
                                    completeStatus.postValue(Constants.LOCAL_DB_FAILED)
                                }
                            }
                        }
                    }.addOnFailureListener {
                        completeStatus.value = Constants.FIREBASE_CHANGE_FAILED
                        Log.d(TAG,Constants.FIREBASE_CHANGE_FAILED)
                    }
                }
            })
    }




    //Internal Repo Functions

    fun signUpAddCredentialsLocal(email: String, password: String){
        localDataSource.signUpAddCredentialsLocal(email, password)
    }

    fun checkUserName(username: String): Task<QuerySnapshot> {
        return firebaseDataSource.checkUsername(username)
    }

    fun addMyDetailsInContacts(name: String, username: String, type: Int){
        val uid = getCurrentUserUID()
        localDataSource.addMyDetailsInContacts(name,username,type,uid)
    }

    fun getCurrentUserUID():String{
        return authDataSource.getCurrentUserUID()
    }

    fun signup(email: String, password:String): Task<AuthResult> {
        return authDataSource.signUp(email,password)
    }

}





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
