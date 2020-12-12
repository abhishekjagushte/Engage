package com.abhishekjagushte.engage.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.abhishekjagushte.engage.database.entities.*
import com.abhishekjagushte.engage.database.views.ConversationView
import com.abhishekjagushte.engage.database.views.MessageNotificationView
import com.abhishekjagushte.engage.database.views.MessageView
import com.abhishekjagushte.engage.datasource.localdatasource.FirebaseInstanceSource
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseStorageSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FunctionsSource
import com.abhishekjagushte.engage.listeners.M2MListener
import com.abhishekjagushte.engage.network.CreateGroupRequest
import com.abhishekjagushte.engage.network.convertDomainObject
import com.abhishekjagushte.engage.ui.main.screens.search.SearchData
import com.abhishekjagushte.engage.ui.main.screens.search.convertSearchDataContacts
import com.abhishekjagushte.engage.ui.main.screens.search.convertSearchDataConversations
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.iid.InstanceIdResult
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val localDataSource: LocalDataSource,
    private val firebaseDataSource: FirebaseDataSource,
    private val firebaseInstanceId: FirebaseInstanceSource,
    private val functionsSource: FunctionsSource,
    private val storageSource: FirebaseStorageSource
){
    private val TAG: String = "DataRepository"

    private val repoJob = Job()
    private val repoScope = CoroutineScope(Dispatchers.Main + repoJob)


    //Functions that send Status to Viewmodels.....
    fun login(email: String, password: String, completeStatus: MutableLiveData<String>){

        TODO("Implement the import of contacts and conversations while logging in compulsory!")

//        authDataSource.login(email, password).addOnSuccessListener {
//            val uid = authDataSource.getCurrentUserUID()
//            Log.d(TAG, "Login Success")
//            repoScope.launch {
//                withContext(Dispatchers.IO){
//                    signUpAddCredentialsLocal (email, password)
//                    val i = localDataSource.getCountUserData()
//                    Log.d(TAG,"Credentials Added number of entries = $i")
//                }
//            }
//
//            firebaseDataSource.getLoginData(uid).addOnSuccessListener {
//                val profile = it.documents[0].toObject<Profile>()
//                val contact = profile!!.convertDomainObject(Constants.CONTACTS_ME)
//                repoScope.launch {
//                    withContext(Dispatchers.IO){
//                        addMyDetailsInContacts(contact)
//                        completeStatus.postValue(Constants.LOCAL_DB_SUCCESS)
//                    }
//                }
//            }
//        }.addOnFailureListener {
//
//        }
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

                        Log.d(TAG, "setNameAndUsername: Firebase change complete")

                        repoScope.launch {
                            withContext(Dispatchers.IO) {
                                try {
                                    addMyDetailsInContacts(pair.second.convertDomainObject(Constants.CONTACTS_ME))
                                    completeStatus.postValue(Constants.LOCAL_DB_SUCCESS)

                                    Log.d(TAG, "setNameAndUsername: Local db changed")
                                } catch (e: Exception) {
                                    completeStatus.postValue(Constants.LOCAL_DB_FAILED)
                                    e.printStackTrace()
                                }
                            }
                        }
                    }.addOnFailureListener {
                        completeStatus.value = Constants.FIREBASE_CHANGE_FAILED
                        Log.d(TAG,Constants.FIREBASE_CHANGE_FAILED)
                        Log.d(TAG, "setNameAndUsername: firebase change failed")
                    }
                }
            })
    }

    fun updateNotificationChannelID(id: String){
        repoScope.launch {
            withContext(Dispatchers.IO){

                val c = localDataSource.getMyDetails()
                if(c!=null){
                    Log.d(TAG, c.username + "****************")

                    firebaseDataSource.updateNotificationChannelID(id, c.username)
                        .addOnFailureListener {

                            getNotificationChannelID().addOnSuccessListener {
                                Log.d(TAG, "failed ${it.token}")
                                firebaseDataSource.updateNotificationChannelID(it.token, c.username)
                            }.addOnSuccessListener {
                                Log.d(TAG, "Updated successfully")
                            }
                        }
                }
            }
        }
    }

    fun getNotificationChannelID(): Task<InstanceIdResult> {
        return firebaseInstanceId.getNotificationChannelID()
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

    //Profile Fragment
    fun getContactFromUsername(username: String): LiveData<Contact> {
        return localDataSource.getContactFromUsername(username)
    }

    fun getContact(username: String): Contact? {
        return localDataSource.getContact(username)
    }

    fun getContactFirestoreFromUsername(username: String): Task<DocumentSnapshot> {
        return firebaseDataSource.getContactFirestoreFromUsername(username)
    }

    fun updateContact(contact: Contact){
        localDataSource.updateContact(contact)
    }

    fun getMydetails(): ContactNameUsername?{
        return localDataSource.getMyDetails()
    }

    fun addFriend(request: HashMap<String, Any>): Task<DocumentReference> {
        return firebaseDataSource.addFriend(request)
    }


    fun getCurrentUser(): FirebaseUser? {
        return authDataSource.getCurrentUser()
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


    fun addContact(contact: Contact){
        localDataSource.addContact(contact)
    }

    fun getCountContacts(): Int {
        return localDataSource.getCountContacts()
    }

    //Notification Manager
    fun friendRequestReceived(data: MutableMap<String, String>) {
        val name = data.get("name")
        val username = data.get("username")

        if(name !=null && username!=null){
            val contact = Contact(
                name = name,
                username = username,
                type = Constants.CONTACTS_PENDING,
                networkID = "" //Will be updated when the data is fetched
            )

            repoScope.launch {
                withContext(Dispatchers.IO){
                    localDataSource.addContact(contact)
                }
            }
        }
    }

    fun friendRequestAccepted(data: Map<String, String>) {
        val name = data.get("name")
        val username = data.get("username")
        val conID = data.get("conversationID")

        Log.d(TAG, "friendRequestAccepted: $conID is the conversationID")

        if(name !=null && username!=null){
            val contact = Contact(
                name = name,
                username = username,
                type = Constants.CONTACTS_CONFIRMED,
                networkID = "" //Will be updated when the data is fetched
            )

            repoScope.launch {
                withContext(Dispatchers.IO){
                    localDataSource.updateContact(contact) //Replace strategy so no worries
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ChatList Fragment stuff
    ///////////////////////////////////////////////////////////////////////////

    fun getChats(conversationID: String): DataSource.Factory<Int, MessageView> {
        return localDataSource.getChats(conversationID)
    }

    fun saveTextMessage121Local(
        message: String,
        conversationID: String,
        myUsername: String,
        otherUsername: String
    ): Message {
        return localDataSource.saveTextMessage121Local(message, conversationID, myUsername, otherUsername)
    }

    fun saveImageMessage(message: Message): Message {
        return localDataSource.saveImageMessage(message)
    }

    fun uploadImage(message: Message){
        storageSource.uploadImage121(message)
    }

    fun saveTextMessageM2M(
        message: String,
        conversationID: String,
        replyToId: String?
    ): Message {
        return localDataSource.saveTextMessageM2MLocal(message, conversationID, replyToId)
    }

    fun addConversation121(username: String){
        localDataSource.addConversation121(username)
    }

    fun getUnsentMessages(): List<Message> {
        return localDataSource.getUnsentMessages()
    }


    fun setMessageSent(messageID: String) {
        localDataSource.setMessageSent(messageID)
    }


    fun pushMessage(messageID: String): Task<Void> {
        return localDataSource.pushMessage(messageID)//Since pushing messaage is more of a job for local databse updation, it is done in local db source
    }


    fun pushMessage(message: Message): Task<Void> {
        return localDataSource.pushMessage(message)
        //Since pushing messaage is more of a job for local databse updation, it is done in local db source
    }

    fun receiveMessage121(message: Message){
        if(localDataSource.getConversation(message.senderID!!)==null) {
            Log.d(TAG, "receiveMessage121: conversation not present")
            localDataSource.addConversation121(message.senderID!!)
        }
        localDataSource.insertMessage(message)
    }

    fun receiveMessageM2M(message: Message){
        if(localDataSource.getConversation(message.conversationID)==null) {
            Log.d(TAG, "receiveMessageM2M: conversation not present")
            //localDataSource.addGroupMessaageReceivedFirst(message.conversationID)
        }
        localDataSource.insertMessage(message)
    }

    fun markMessagesRead(conversationID: String) {
        localDataSource.markMessagesRead(conversationID)
    }

    fun updateConversation(conversation: Conversation) {
        localDataSource.updateConversation(conversation)
    }

    fun checkConversationExists(conID: String): Boolean {
        return localDataSource.checkConversationExists(conID)
    }

    fun getConversation(conversationID: String): Conversation? {
        return localDataSource.getConversation(conversationID = conversationID)
    }


    ///////////////////////////////////////////////////////////////////////////
    // Chat List fragment
    ///////////////////////////////////////////////////////////////////////////

    fun getConversationList(): LiveData<List<ConversationView>> {
        return localDataSource.getConversationList()
    }


    ///////////////////////////////////////////////////////////////////////////
    // Add Participants
    ///////////////////////////////////////////////////////////////////////////

    fun searchForFriends(query: String): List<ContactNameUsername> {
        return localDataSource.searchForFriends(query)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Set Group Info while creating
    ///////////////////////////////////////////////////////////////////////////

    fun createGroupLocal(request: CreateGroupRequest, pushed: Boolean) {
        localDataSource.createGroupLocal(request, pushed)
    }

    fun createGroup(request: JSONObject?): Task<HttpsCallableResult> {
        return functionsSource.createGroup(request)
    }

    fun getNewConversationIDM2M(): String{
        return firebaseDataSource.getNewConversationIDM2M()
    }


    ///////////////////////////////////////////////////////////////////////////
    // Sync
    ///////////////////////////////////////////////////////////////////////////


    fun getM2MSyncMap(): List<M2MSyncRequirement> {
        return localDataSource.getM2MSyncMap()
    }

    fun syncM2MChat(syncMap: M2MSyncRequirement): Task<QuerySnapshot> {
        return  firebaseDataSource.syncM2MChat(syncMap)
    }


    ///////////////////////////////////////////////////////////////////////////
    // Notifications
    ///////////////////////////////////////////////////////////////////////////

    fun addNewGroup(name: String?, conversationID: String?) {
        repoScope.launch {
            withContext(Dispatchers.IO){
                if(!localDataSource.checkConversationExists(conversationID!!))
                    localDataSource.addConversationM2M(name!!, conversationID)

            }
        }
    }

    fun getUnreadMessages(): List<MessageNotificationView> {
        return localDataSource.getUnreadMessages()
    }

    fun getMessageNotification(messageID: String): MessageNotificationView {
        return localDataSource.getMessageNotification(messageID)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////
    fun addTestDateData(){
        firebaseDataSource.addTestDateData();
    }


    fun setChatListener(username: String): Query {
        return firebaseDataSource.setChatListener(username)
    }

    fun getLatestChats121Query(username: String): Query {
        val timestamp = localDataSource.getLast121MessageTimestamp()
        val last121MessageTimeStamp = Date(timestamp)

        Log.d(TAG, "set121ChatListener: $last121MessageTimeStamp")
        
        return firebaseDataSource.set121ChatListener(last121MessageTimeStamp, username)
    }



    fun setM2MChatListener(conversationID: String): ListenerRegistration {
        val myUsername = localDataSource.getMyDetails()!!.username
        val timestamp = localDataSource.getLastM2MMessageTimestampForConversation(conversationID)
        val lastMessageTimeStamp = Date(timestamp)

        Log.d(TAG, "setM2MChatListener: $lastMessageTimeStamp")
        
        val query = firebaseDataSource.setM2MChatListener(conversationID, lastMessageTimeStamp)
        val listener = M2MListener(this, repoScope, conversationID, myUsername)
        return query.addSnapshotListener { value, error ->
            listener.m2mListener(value, error)
        }
    }


    fun getConfirmedContacts(): LiveData<List<Contact>> {
        return localDataSource.getConfirmedContacts()
    }

    fun testSync(): Task<HttpsCallableResult> {
        return functionsSource.testSync()
    }



}