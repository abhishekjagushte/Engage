package com.abhishekjagushte.engage.repository

import android.util.Log
import com.abhishekjagushte.engage.database.AppDatabase
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.database.UserData
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseDataSource
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.network.convertDomainObject
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.Date
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
