package com.abhishekjagushte.engage.repository

import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val firebaseDataSource: FirebaseDataSource
){
    private val TAG: String = "DataRepository"

    fun signUpAddCredentialsLocal(email: String, password: String){
        localDataSource.signUpAddCredentialsLocal(email, password)
    }

    fun checkUserName(username: String): Task<QuerySnapshot> {
        return firebaseDataSource.checkUsername(username)
    }

    fun addMyDetailsInContacts(
        name: String,
        username: String,
        type: Int,
        uid: String
    ){
        localDataSource.addMyDetailsInContacts(name,username,type,uid)
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
