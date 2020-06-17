package com.abhishekjagushte.engage.datasource.localdatasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.*
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class LocalDataSource @Inject constructor (
    private val databaseDao: DatabaseDao,
    private val firestore: FirebaseFirestore
){

    private val job: Job = Job()
    private val localDBScope = CoroutineScope(Dispatchers.Main + job)

    private val TAG = "LocalDataSource"

    //Profile Fragment Viewmodel
    fun getContactFromUsername(username: String): List<Contact> {
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

    fun getConversationIDFromUsername(username: String): String? {
        return databaseDao.getConversationIDFromUsername(username)
    }

    fun getChats(conversationID: String): LiveData<List<Message>> {
        return databaseDao.getChats(conversationID)
    }

    fun saveTextMessage121Local(
        message: String,
        conversationID: String,
        myUsername: String,
        otherUsername: String
    ) {
        val msg = Message(
            messageID = firestore.collection("conversations121/$conversationID/chats").document().id,
            conversationID = conversationID,
            type = Constants.TYPE_MY_MSG,
            status = Constants.STATUS_NOT_SENT,
            needs_push = Constants.NEEDS_PUSH_YES,
            timeStamp = System.currentTimeMillis(),
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

    fun addConversation121(username: String, conversationID: String){
        val conversation = Conversation(
            networkID = conversationID,
            name = databaseDao.getNameFromUsername(username),
            username = username,
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
            }

            Constants.CONVERSATION_TYPE_M2M -> {

            }
        }
    }

    fun getConversation(conversationID: String): Conversation? {
        return databaseDao.getConversation(conversationID)
    }

    fun insertMessage(message: Message) {
        databaseDao.insertMessage(message)
    }

}