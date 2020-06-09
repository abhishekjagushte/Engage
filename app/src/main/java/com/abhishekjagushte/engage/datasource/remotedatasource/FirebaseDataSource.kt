package com.abhishekjagushte.engage.datasource.remotedatasource

import android.util.Log
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){

    private val TAG: String = "FirebaseDataSource"

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
            .whereGreaterThanOrEqualTo("username", start.toLowerCase(Locale.ROOT))
            .whereLessThan("username", end.toLowerCase(Locale.ROOT))
            .orderBy("username").limit(5)

        //TODO Make this query a single query

        return Pair(first = nameQuery, second = usernameQuery)
    }

    fun getContactFirestoreFromUsername(username: String): Task<DocumentSnapshot> {
        return firestore.collection(Constants.FIREBASE_USERS_COLLECTION).document(username).get()
    }

    fun addFriend(request: HashMap<String, Any>): Task<DocumentReference> {
        return firestore.collection(Constants.FIREBASE_CONNECTION_REQUEST_COLLECTION).add(request)
    }

    fun updateNotificationChannelID(id: String, username: String): Task<Void> {
        Log.d(TAG, "Inside firebase datasource")
        return firestore.collection("users").document(username).update("notificationChannelID", id)
    }
}