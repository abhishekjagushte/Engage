package com.abhishekjagushte.engage.datasource.localdatasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.*
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import javax.inject.Inject

class LocalDataSource @Inject constructor (
    private val databaseDao: DatabaseDao,
    private val firestore: FirebaseFirestore
){

    private val job: Job = Job()
    private val localDBScope = CoroutineScope(Dispatchers.Main + job)

    private val TAG = "LocalDataSource"

    //Profile Fragment Viewmodel
    fun getContactFromUsername(username: String): LiveData<Contact> {
        return databaseDao.getContactFromUsername(username)
    }

    fun updateContact(contact: Contact){
        databaseDao.updateContact(contact)
    }

    fun addContact(contact: Contact){
        databaseDao.insertNewContact(contact)
    }

    fun getMyDetails(): ContactNameUsername?{
        return databaseDao.getMyDetails()
    }

    fun getCurrentLoggedInUserCredentials(userData: MutableLiveData<UserData?>){
        val users = databaseDao.getCurrentLoggedInUser()
        getCountUserData()
        if(users.isEmpty()){
           userData.postValue(null)
        }
        else{
            userData.postValue(users[0])
        }
    }

    fun getCountUserData(): Int{
        val i = databaseDao.getCredentialsCount()
        Log.d(TAG, "Number of entries = $i")
        return i
    }

    fun signUpAddCredentialsLocal(email: String, password: String){
        //databaseDao.truncateCredentials()
        databaseDao.insertCredentials(UserData(email, password))
    }

    fun addMyDetailsInContacts(contact: Contact){
        databaseDao.insertMeinContacts(contact)
    }

    fun searchForConversations(query: String): List<SearchResultConversation> {
        return databaseDao.searchForConversations(query)
    }

    fun searchForContacts(query: String): List<SearchResultContact> {
        return databaseDao.searchForContacts(query)
    }

    fun searchForSuggestedContacts(query: String): List<SearchResultContact> {
        return databaseDao.searchForSuggestedContacts(query)
    }


    fun addContactsTest(){
        databaseDao.insertNewContact(Contact(
            name = "Abhishek Jagushte",
            username = "abhijagushte",
            networkID = "dgvsgavsgdjvajaj",
            type = 1
        ))

        databaseDao.insertNewContact(Contact(
            name = "Taylor Swift",
            username = "taylorswift13",
            networkID = "sdbjewgvfjbnsdffff",
            type = 1
        ))

        databaseDao.insertNewContact(Contact(
            name = "Sam Smith",
            username = "samsmith",
            networkID = "bekhgflgewhkfweljvkhev",
            type = 2
        ))

        databaseDao.insertNewContact(Contact(
            name = "Panda",
            username = "panda",
            networkID = "sbfjhgfyksvhjafua",
            type = 1
        ))

        databaseDao.insertNewContact(Contact(
            name = "Prachi Pathare",
            username = "prachi",
            networkID = "dgvsgdsddsgdjvajaj",
            type = 3
        ))

        databaseDao.insertNewContact(Contact(
            name = "Kylie Jenner",
            username = "fvdsfvdshfvdshfv",
            networkID = "dgvsgadfeknfke ",
            type = 4
        ))
    }

    fun getCountContacts(): Int {
        return databaseDao.getCountContacts()
    }


    fun getChats(conversationID: String): LiveData<List<MessageView>> {
        return databaseDao.getChats(conversationID)
    }


    //conversationID is username in case of 121
    fun saveTextMessage121Local(
        message: String,
        conversationID: String,
        myUsername: String,
        otherUsername: String
    ) {
        val msg = Message(
            messageID = firestore.collection("messages121").document().id,
            conversationID = conversationID,
            type = Constants.TYPE_MY_MSG,
            status = Constants.STATUS_NOT_SENT,
            needs_push = Constants.NEEDS_PUSH_YES,
            timeStamp = System.currentTimeMillis(),
            serverTimestamp = System.currentTimeMillis(),
            data = message,
            senderID = myUsername,
            receiverID = otherUsername,
            deleted = Constants.DELETED_NO,
            mime_type = Constants.MIME_TYPE_TEXT,
            conType = Constants.CONVERSATION_TYPE_121
        )

        databaseDao.insertMessage(msg)
        try {
            Log.d(
                TAG,
                "saveTextMessage121Local: ${databaseDao.getChatsCount(conversationID).size}"
            )
        }catch (e: Exception){
            e.printStackTrace()
        }
        }

    fun addConversation121(username: String){
        val conversation = Conversation(
            conversationID = username,
            name = databaseDao.getNameFromUsername(username),
            type = Constants.CONVERSATION_TYPE_121,
            active = Constants.CONVERSATION_ACTIVE_YES
        )

        databaseDao.insertConversation(conversation)
    }

    fun getUnsentMessages(): LiveData<List<Message>> {
        return databaseDao.getUnsentMessages()
    }

    fun pushMessage(message: Message) {
        when(message.conType){
            Constants.CONVERSATION_TYPE_121 -> {
                firestore.collection("messages121")
                    .document(message.messageID)
                    .set(message.convertNetworkMessage121())
                    .addOnSuccessListener {
                        localDBScope.launch {
                            withContext(Dispatchers.IO) {
                                message.status = Constants.STATUS_SENT_BUT_NOT_DELIVERED
                                databaseDao.updateMessage(message)
                                Log.d(TAG, "pushMessage: ${message.data} sent")
                            }
                        }
                    }
            }

            Constants.CONVERSATION_TYPE_M2M -> {
                TODO("Send message M2M")

                /*
                firestore.collection("conversations121/${message.conversationID}/chats")
                    .document(message.messageID)
                    .set(message.convertNetworkMessage())
                    .addOnSuccessListener {

                        localDBScope.launch {
                            withContext(Dispatchers.IO) {
                                message.status = Constants.STATUS_SENT_BUT_NOT_DELIVERED
                                databaseDao.updateMessage(message)
                                Log.d(TAG, "pushMessage: ${message.data} sent")
                            }
                        }
                    }
                 */

            }
        }
    }

    fun getConversation(conversationID: String): Conversation? {
        return databaseDao.getConversation(conversationID)
    }

    fun insertMessage(message: Message) {
        databaseDao.insertMessage(message)
    }


    fun getTemporaryConversationID(): String {
        return firestore.collection("conversations121").document().id
    }

    fun getUnPushedConversations(): LiveData<List<Conversation>> {
        return databaseDao.getUnPushedConversations()
    }

//    fun isConversationPushed(conversationID: String): Boolean {
//        val conversation = databaseDao.getConversation(conversationID)
//        conversation?.let {
//            if(conversation.needs_push ==0)
//                return true
//        }
//        return false
//    }

    fun updateConversation(conversation: Conversation) {
        databaseDao.updateConversation(conversation)
    }


    fun checkConversationExists(conID: String): Int {
        return databaseDao.checkConversationExists(conID)
    }

    fun getConversationList(): LiveData<List<ConversationView>> {
        return databaseDao.getConversationList()
    }


    ///////////////////////////////////////////////////////////////////////////
    // add participants
    ///////////////////////////////////////////////////////////////////////////

    fun searchForFriends(query: String): List<ContactNameUsername> {
        return databaseDao.searchForFriends(query)
    }


    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////
    fun getConfirmedContacts(): LiveData<List<Contact>> {
        return databaseDao.getConfirmedContactsTest()
    }


}