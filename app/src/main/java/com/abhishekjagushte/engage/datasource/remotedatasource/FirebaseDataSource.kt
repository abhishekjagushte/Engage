package com.abhishekjagushte.engage.datasource.remotedatasource

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){

    fun checkUsername(username: String): Task<QuerySnapshot> {
        //TODO replace users with constant
        val query = firestore.collection("users").whereEqualTo("username", username).limit(1)
        return query.get()
    }

}