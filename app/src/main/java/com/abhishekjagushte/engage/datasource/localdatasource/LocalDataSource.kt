package com.abhishekjagushte.engage.datasource.localdatasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.abhishekjagushte.engage.database.*
import com.abhishekjagushte.engage.database.entities.*
import com.abhishekjagushte.engage.database.views.ConversationView
import com.abhishekjagushte.engage.database.views.MessageNotificationView
import com.abhishekjagushte.engage.database.views.MessageView
import com.abhishekjagushte.engage.network.CreateGroupRequest
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.lang.IllegalStateException
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

    fun getContact(username: String): Contact? {
        return databaseDao.getContact(username)
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

    fun getCountContacts(): Int {
        return databaseDao.getCountContacts()
    }


    fun getChats(conversationID: String): DataSource.Factory<Int, MessageView> {
        return databaseDao.getChats(conversationID)
    }


    //conversationID is username in case of 121
    fun saveTextMessage121Local(
        message: String,
        conversationID: String,
        myUsername: String,
        otherUsername: String
    ): Message {

        val messageID = firestore.collection("messages121").document().id
        val msg = Message(
            messageID = messageID,
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

        return msg
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

    fun getUnsentMessages(): List<Message> {
        return databaseDao.getUnsentMessages()
    }


    fun setMessageSent(messageID: String) {
        databaseDao.setMessageSent(messageID)
    }


    fun pushMessage(messageID: String): Task<Void> {

        val message = databaseDao.getMessage(messageID)

        return when (message.conType) {
            Constants.CONVERSATION_TYPE_121 -> {
                firestore.collection("messages121")
                    .document(message.messageID)
                    .set(message.convertNetworkMessage())
            }

            Constants.CONVERSATION_TYPE_M2M -> {

                firestore.collection("groups/${message.conversationID}/chats")
                    .document(message.messageID)
                    .set(message.convertNetworkMessage())
            }
            else -> throw IllegalStateException("Message category is other than 121 or M2M")
        }.addOnSuccessListener {
            localDBScope.launch {
                withContext(Dispatchers.IO){
                    setMessageSent(message.messageID)
                }
            }
        }
    }

    fun pushMessage(message: Message): Task<Void> {
        return when (message.conType) {
            Constants.CONVERSATION_TYPE_121 -> {
                firestore.collection("messages121")
                    .document(message.messageID)
                    .set(message.convertNetworkMessage())
            }

            Constants.CONVERSATION_TYPE_M2M -> {

                firestore.collection("groups/${message.conversationID}/chats")
                    .document(message.messageID)
                    .set(message.convertNetworkMessage())
            }
            else -> throw IllegalStateException("Message category is other than 121 or M2M")
        }.addOnSuccessListener {
            localDBScope.launch {
                withContext(Dispatchers.IO){
                    setMessageSent(message.messageID)
                }
            }
        }
    }


//    fun pushMessage(message: Message) {
//        when(message.conType){
//            Constants.CONVERSATION_TYPE_121 -> {
//                firestore.collection("messages121")
//                    .document(message.messageID)
//                    .set(message.convertNetworkMessage())
//                    .addOnSuccessListener {
//                        localDBScope.launch {
//                            withContext(Dispatchers.IO) {
//                                message.status = Constants.STATUS_SENT_BUT_NOT_DELIVERED
//                                databaseDao.updateMessage(message)
//                                Log.d(TAG, "pushMessage: ${message.data} sent")
//                            }
//                        }
//                    }
//            }
//
//            Constants.CONVERSATION_TYPE_M2M -> {
//
//                firestore.collection("groups/${message.conversationID}/chats")
//                    .document(message.messageID)
//                    .set(message.convertNetworkMessage())
//                    .addOnSuccessListener {
//
//                        localDBScope.launch {
//                            withContext(Dispatchers.IO) {
//                                message.status = Constants.STATUS_SENT_BUT_NOT_DELIVERED
//                                databaseDao.updateMessage(message)
//                                Log.d(TAG, "pushMessage: ${message.data} sent")
//                            }
//                        }
//                    }
//
//
//            }
//        }
//    }

    fun getConversation(conversationID: String): Conversation? {
        return databaseDao.getConversation(conversationID)
    }

    fun insertMessage(message: Message) {
        databaseDao.insertMessage(message)
    }

    fun updateConversation(conversation: Conversation) {
        databaseDao.updateConversation(conversation)
    }

    fun checkConversationExists(conID: String): Boolean {
        return databaseDao.checkConversationExists(conID) == 1
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

    fun createGroupLocal(request: CreateGroupRequest, pushed: Boolean) {
        //The initial Message will be set when the group will be pushed
        val conversation: Conversation?
        val initialMessage: Message?

        if(pushed) {
            val lmid = firestore.collection("groups/${request.conversationID}/chats").document().id

            conversation = Conversation(
                name = request.name,
                conversationID = request.conversationID,
                type = Constants.CONVERSATION_TYPE_M2M,
                active = Constants.CONVERSATION_ACTIVE_YES,
                lastMessageID = lmid
            )

            val initialMessage = getInitialMessage(request.conversationID, lmid)
            insertMessage(initialMessage)
        }
        else{
            conversation = Conversation(
                name = request.name,
                conversationID = request.conversationID,
                type = Constants.CONVERSATION_TYPE_M2M,
                active = Constants.CONVERSATION_ACTIVE_NO_NEEDS_PUSH
            )
        }

        databaseDao.insertConversation(conversation)

        for(username in request.participants){
            databaseDao.insertConversationCrossRef(ContactsConversationsCrossRef(username, request.conversationID))
        }

    }

    private fun getInitialMessage(conversationID: String, lastMessageID: String): Message{
        return Message(
            messageID = lastMessageID,
            conversationID = conversationID,
            type = Constants.TYPE_NOTICE,
            status = Constants.STATUS_NOT_SENT,
            needs_push = Constants.NEEDS_PUSH_YES,
            timeStamp = System.currentTimeMillis(),
            serverTimestamp = System.currentTimeMillis(),
            data = Constants.I_CREATED_GROUP,
            senderID = getMyDetails()!!.username,
            receiverID = null,
            deleted = Constants.DELETED_NO,
            mime_type = Constants.MIME_TYPE_TEXT,
            conType = Constants.CONVERSATION_TYPE_M2M
        )
    }

    fun saveTextMessageM2MLocal(
        message: String,
        conversationID: String,
        replyToId: String?
    ): Message {
        val messageID = firestore.collection("groups/${conversationID}/chats").document().id
        val msg = Message(
            messageID = messageID,
            conversationID = conversationID,
            type = Constants.TYPE_MY_MSG,
            status = Constants.STATUS_NOT_SENT,
            needs_push = Constants.NEEDS_PUSH_YES,
            timeStamp = System.currentTimeMillis(),
            serverTimestamp = System.currentTimeMillis(),
            data = message,
            senderID = getMyDetails()!!.username,
            receiverID = null,
            deleted = Constants.DELETED_NO,
            mime_type = Constants.MIME_TYPE_TEXT,
            conType = Constants.CONVERSATION_TYPE_M2M,
            reply_toID = replyToId
        )

        databaseDao.insertMessage(msg)

        return msg
    }

    fun addConversationM2M(name: String, conversationID: String) {
        val conversation = Conversation(
            name = name,
            conversationID = conversationID,
            type = Constants.CHATLIST_TYPE_M2M,
            active = Constants.CONVERSATION_ACTIVE_YES
        )

        databaseDao.insertConversation(conversation)
    }

    fun markMessagesRead(conversationID: String) {
        databaseDao.markMessagesRead(conversationID)
    }

    fun getUnreadMessages(): List<MessageNotificationView> {
        return databaseDao.getUnreadMessages()
    }

    fun getMessageNotification(messageID: String): MessageNotificationView {
        return databaseDao.getMessageNotification(messageID)
    }

    fun getLast121MessageTimestamp(): Long {
        return databaseDao.getLast121MessageTimestamp()
    }

    fun getLastM2MMessageTimestampForConversation(conversationID: String): Long {
        return databaseDao.getLastM2MMessageTimestampForConversation(conversationID)
    }

    fun getM2MSyncMap(): List<M2MSyncRequirement> {
        return databaseDao.getM2MSyncRequirement()
    }


    fun saveImageMessage(message: Message): Message {
        var messageID = ""
        if(message.conType == Constants.CONVERSATION_TYPE_121)
            messageID = firestore.collection("messages121").document().id
        else
            messageID = firestore.collection("groups/${message.conversationID}/chats").document().id

        message.messageID = messageID
        databaseDao.insertMessage(message)
        return message
    }

    fun setMessageReceived(messageID: String) {
        databaseDao.setMessageReceived(messageID)
    }

    fun updateMessage(message: Message) {
        databaseDao.updateMessage(message)
    }

}