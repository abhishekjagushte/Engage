package com.abhishekjagushte.engage.datasource.remotedatasource

import android.util.Log
import com.abhishekjagushte.engage.database.entities.M2MSyncRequirement
import com.abhishekjagushte.engage.network.DateTest
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.util.*
import javax.inject.Inject

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


    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////
    fun addTestDateData() {
        firestore.collection("test").document().set(DateTest(name = "Panda1"))
        firestore.collection("test").document().set(DateTest(name = "Panda2"))

    }


    fun setChatListener(username: String): Query {
        val query = firestore
            .collection("messages121")
            .whereEqualTo("receiverID", username)
            .whereGreaterThan("timeStamp", Date())
            .limit(50)

        return query
    }

    fun set121ChatListener(last121MessageTimeStamp: Date, username: String): Query {
        val query = firestore
            .collection("messages121")
            .whereEqualTo("receiverID", username)
            .whereGreaterThan("timeStamp", last121MessageTimeStamp)

        return query
    }

    fun getNewConversationIDM2M(): String {
        return firestore.collection("groups").document().id
    }

    fun setM2MChatListener(conversationID: String, lastMessageTimeStamp: Date): Query {
        val query = firestore
            .collection("groups/$conversationID/chats")
            .whereGreaterThan("timeStamp", lastMessageTimeStamp)

        return query
    }

    fun setM2MEventListener(conversationID: String, lastMessageTimeStamp: Date): Query {
        val query = firestore
            .collection("groups/$conversationID/events")
            .whereGreaterThan("timeStamp", lastMessageTimeStamp)

        return query
    }

    fun syncM2MChat(syncMap: M2MSyncRequirement): Task<QuerySnapshot> {
        val query = firestore
            .collection("groups/${syncMap.conversationID}/chats")
            .whereGreaterThan("timeStamp", Date(syncMap.lastMessageTimeStamp))

        return query.get()
    }

    fun set121EventListener(last121EventTimestamp: Date, myUsername: String): Query {
        val query = firestore
            .collection("users/$myUsername/events121")
            .whereGreaterThan("timeStamp", last121EventTimestamp)

        return query
    }

    fun markReminderDone(eventID: String, myUsername: String): Task<Void> {
        val query = firestore.collection("user/$myUsername/events121")
            .document(eventID).update("status", Constants.REMINDER_STATUS_INACTIVE)

        return query
    }
}