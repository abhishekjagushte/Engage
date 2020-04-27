package com.abhishekjagushte.engage.repository

import android.util.Log
import com.abhishekjagushte.engage.database.AppDatabase
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.database.UserData
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.network.convertDomainObject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*

class DataRepository (private val database: AppDatabase){

    private lateinit var mAuth: FirebaseAuth
    private val TAG: String = "DataRepository"
    private val repoJob = Job()
    private val repoScope = CoroutineScope(Dispatchers.IO + repoJob)

    suspend fun firebaseAddDataSignUp(name: String, username: String, token: String, email: String, password: String){
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
        firestore.collection("users").document().set(profile)
        addDataLocalSignUp(profile.convertDomainObject(0), email, password)

        Log.d(TAG,"completed")
    }

    private suspend fun addDataLocalSignUp(myself: Contact, email: String, password: String) {
        withContext(Dispatchers.IO) {
            database.databaseDao.insertMeinContacts(myself)
            database.databaseDao.insertCredentials(UserData(email, password))
        }
    }


    //TODO: Complete this function and add the required in login fragment
    suspend fun fireBaseGetDataSignIn(uid: String, email: String, password: String): Boolean{
        val firestore = FirebaseFirestore.getInstance()

        val query = firestore.collection("users").whereEqualTo("id", uid)
        val result = query.get().addOnSuccessListener {

            if(it == null){

            }
            else{
                val doc = it.documents.get(0)
                val profile = doc.toObject(Profile::class.java)

                if(profile!=null) {
                    repoScope.launch {
                        addDataLocalSignIn(profile, email, password)
                    }
                }

            }
        }

        //TODO: This is wrong, read about kotlin co-routines and then complete this
        return result.isSuccessful
    }

    private suspend fun addDataLocalSignIn(profile: Profile, email: String, password: String) {
        withContext(Dispatchers.IO) {
            val firestore = FirebaseFirestore.getInstance()
            val myself = profile.convertDomainObject(0)
            database.databaseDao.insertMeinContacts(myself)
            database.databaseDao.insertCredentials(UserData(email, password))

            //TODO: Add the user connections on a background Thread
//            for(contact in profile.contacts){
//                firestore.collection("users").document(contact).addSnapshotListener {
//                        documentSnapshot, firebaseFirestoreException ->
//
//                    val conn = documentSnapshot?.toObject(Profile::class.java).convertDomainObject(1)
//                }
//            }
        }
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
