package com.abhishekjagushte.engage.datasource.remotedatasource

import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){

    fun checkUsername(username: String): Task<QuerySnapshot> {
        //TODO replace users with constant
        val query = firestore.collection("users").whereEqualTo("username", username).limit(1)
        return query.get()
    }

    fun getLoginData(uid: String): Task<QuerySnapshot> {
       return  firestore.collection(Constants.FIREBASE_USERS_COLLECTION)
            .whereEqualTo(Constants.FIREBASE_USER_ID_FIELD_NAME, uid)
            .limit(1)
            .get()

    }

    fun searchUnknownContacts(query: String): Pair<Query, Query> {
        val start = query.substring(0..query.length-2)
        val end = query + (query.get(query.length-1) + 1)

        val nameQuery = firestore.collection("users")
            .whereGreaterThanOrEqualTo("name", start)
            .whereLessThan("name", end)
            .orderBy("name").limit(5)

        val usernameQuery = firestore.collection("users")
            .whereGreaterThanOrEqualTo("username", start)
            .whereLessThan("username", end)
            .orderBy("username").limit(5)

        return Pair(first = nameQuery, second = usernameQuery)
    }

}