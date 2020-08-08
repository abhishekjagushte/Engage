package com.abhishekjagushte.engage.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.abhishekjagushte.engage.database.*
import com.abhishekjagushte.engage.datasource.localdatasource.FirebaseInstanceSource
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FunctionsSource
import com.abhishekjagushte.engage.network.CreateGroupRequest
import com.abhishekjagushte.engage.network.Profile
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
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.iid.InstanceIdResult
import kotlinx.coroutines.*
import org.json.JSONObject
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val localDataSource: LocalDataSource,
    private val firebaseDataSource: FirebaseDataSource,
    private val firebaseInstanceId: FirebaseInstanceSource,
    private val functionsSource: FunctionsSource
){
    private val TAG: String = "DataRepository"

    private val repoJob = Job()
    private val repoScope = CoroutineScope(Dispatchers.Main + repoJob)


    //Functions that send Status to Viewmodels.....
    fun login(email: String, password: String, completeStatus: MutableLiveData<String>){

        TODO("Implement the import of contacts and conversations while logging in compulsory!")

        authDataSource.login(email, password).addOnSuccessListener {
            val uid = authDataSource.getCurrentUserUID()
            Log.d(TAG, "Login Success")
            repoScope.launch {
                withContext(Dispatchers.IO){
                    signUpAddCredentialsLocal (email, password)
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


    fun addContact(contact: Contact){
        localDataSource.addContact(contact)
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

    //Notification Manager

    fun friendRequestRecieved(data: MutableMap<String, String>) {
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


    // TODO: 6/11/2020 implement this after completing the checking for already available thing
    fun createNewChat121(request: HashMap<String, String>): Task<HttpsCallableResult> {
        return functionsSource.createNewChat121(request)
    }

    fun getChats(conversationID: String): DataSource.Factory<Int, MessageView> {
        return localDataSource.getChats(conversationID)
    }

    fun saveTextMessage121Local(
        message: String,
        conversationID: String,
        myUsername: String,
        otherUsername: String
    ){
        localDataSource.saveTextMessage121Local(message, conversationID, myUsername, otherUsername)
    }

    fun saveTextMessageM2M(
        message: String,
        conversationID: String,
        replyToId: String?
    ){
        localDataSource.saveTextMessageM2MLocal(message, conversationID, replyToId)
    }

    fun addConversation121(username: String){
        localDataSource.addConversation121(username)
    }

    fun getUnsentMessages(): LiveData<List<Message>> {
        return localDataSource.getUnsentMessages()
    }

    fun pushMessage(message: Message) {
        localDataSource.pushMessage(message)//Since pushing messaage is more of a job for local databse updation, it is done in local db source
    }

    fun receiveMessage121(message: Message){
        repoScope.launch {
            withContext(Dispatchers.IO){

                if(localDataSource.getConversation(message.senderID!!)==null) {
                    Log.d(TAG, "receiveMessage121: conversation not present")
                    localDataSource.addConversation121(message.senderID!!)
                }

                localDataSource.insertMessage(message)

            }
        }
    }

    fun receiveMessageM2M(message: Message){

        if(localDataSource.getConversation(message.conversationID)==null) {
            Log.d(TAG, "receiveMessage121: conversation not present")
            //localDataSource.addGroupMessaageReceivedFirst(message.conversationID)
        }

        localDataSource.insertMessage(message)
    }

    fun markMessagesRead(conversationID: String) {
        localDataSource.markMessagesRead(conversationID)
    }


    fun getTemporaryConversationID(): String {
        return localDataSource.getTemporaryConversationID()
    }

    fun getUnPushedConversations(): LiveData<List<Conversation>> {
        return localDataSource.getUnPushedConversations()
    }

    fun updateConversation(conversation: Conversation) {
        localDataSource.updateConversation(conversation)
    }


    fun checkConversationExists(conID: String): Int {
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

    suspend fun createGroupLocal(request: CreateGroupRequest, pushed: Boolean) {
        localDataSource.createGroupLocal(request, pushed)
    }

    fun createGroup(request: JSONObject?): Task<HttpsCallableResult> {
        return functionsSource.createGroup(request)
    }

    fun getNewConversationIDM2M(): String{
        return firebaseDataSource.getNewConversationIDM2M()
    }


    ///////////////////////////////////////////////////////////////////////////
    // Notifications
    ///////////////////////////////////////////////////////////////////////////

    fun addNewGroup(name: String?, conversationID: String?) {
        repoScope.launch {
            withContext(Dispatchers.IO){
                if(localDataSource.checkConversationExists(conversationID!!)==0)
                    localDataSource.addConversationM2M(name!!, conversationID)

            }
        }
    }

    fun getUnreadMessages(): List<MessageView> {
        return localDataSource.getUnreadMessages()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////
    fun addTestDateData(){
        firebaseDataSource.addTestDateData();
    }


    fun setChatListener(conversationID: String): Query {
        return firebaseDataSource.setChatListener(conversationID)
    }

    fun getConfirmedContacts(): LiveData<List<Contact>> {
        return localDataSource.getConfirmedContacts()
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
