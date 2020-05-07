package com.abhishekjagushte.engage.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.database.SearchResultConversation
import com.abhishekjagushte.engage.datasource.localdatasource.FirebaseInstanceSource
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseDataSource
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.network.convertDomainObject
import com.abhishekjagushte.engage.ui.main.fragments.search.SearchData
import com.abhishekjagushte.engage.ui.main.fragments.search.convertSearchDataContacts
import com.abhishekjagushte.engage.ui.main.fragments.search.convertSearchDataConversations
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
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
            repoScope.launch {
                withContext(Dispatchers.IO){
                    signUpAddCredentialsLocal(email, password)
                    val i = localDataSource.getCountUserData()
                    Log.d(TAG,"Credentials Added number of entries = $i")
                }
            }

            firebaseDataSource.getLoginData(uid).addOnSuccessListener {
                val profile = it.documents[0].toObject<Profile>()
                val contact = profile!!.convertDomainObject(Constants.CONTACTS_ME)
                repoScope.launch {
                    withContext(Dispatchers.IO){
                        addMyDetailsInContacts(contact)
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
                    val pair = authDataSource.setNameAndUsername(name,username, token)

                    pair.first.addOnSuccessListener {
                        completeStatus.value = Constants.FIREBASE_CHANGE_COMPLETE

                        repoScope.launch {
                            withContext(Dispatchers.IO) {
                                try {
                                    addMyDetailsInContacts(pair.second.convertDomainObject(Constants.CONTACTS_ME))
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


    fun searchForConversations(query: String): List<SearchData> {
        return localDataSource.searchForConversations(query).convertSearchDataConversations()
    }

    fun searchForContacts(query: String): List<SearchData> {
        return localDataSource.searchForContacts(query).convertSearchDataContacts()
    }

    fun searchForSuggested(query: String): List<SearchData> {
        return localDataSource.searchForSuggestedContacts(query).convertSearchDataContacts()
    }

    fun searchUnknownContacts(query: String): Pair<Query, Query> {
        return firebaseDataSource.searchUnknownContacts(query)
    }



    //Internal Repo Functions

    fun getCurrentUser(): FirebaseUser? {
        return authDataSource.getCurrentUser()
    }

    fun getCountUserData():Int{
        return localDataSource.getCountUserData()
    }

    fun signUpAddCredentialsLocal(email: String, password: String){
        localDataSource.signUpAddCredentialsLocal(email, password)
    }

    fun checkUserName(username: String): Task<QuerySnapshot> {
        return firebaseDataSource.checkUsername(username)
    }

    private fun addMyDetailsInContacts(contact: Contact){
        localDataSource.addMyDetailsInContacts(contact)
    }

    private fun getCurrentUserUID():String{
        return authDataSource.getCurrentUserUID()
    }

    fun signup(email: String, password:String): Task<AuthResult> {
        return authDataSource.signUp(email,password)
    }



    fun addContactsTest(){
        repoScope.launch {
            withContext(Dispatchers.IO){
                localDataSource.addContactsTest()
                Log.d(TAG,"Added test contacts")
            }
        }
    }

    fun getCountContacts(): Int {
        return localDataSource.getCountContacts()
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
