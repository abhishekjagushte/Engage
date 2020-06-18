package com.abhishekjagushte.engage.ui.chat.fragments.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.Message
import com.abhishekjagushte.engage.database.MessageView
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

enum class ChatState{
    NEW, EXISTING
}

enum class ChatType{
    CHAT_TYPE_121, CHAT_TYPE_M2M
}

class ChatViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel(){

    var conversationID: String?=null

    private var username: String? = null//used to send in message 121
    private var myUsername: String?=null//used to send in message 121

    private val TAG = "ChatViewModel"

    var _chatState = MutableLiveData<ChatState>()
    val chatState: LiveData<ChatState>
        get() = _chatState//Set initially as existing as this will be in most of the cases


    val viewModelJob = Job()
    val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    lateinit var chats: LiveData<List<Message>>


    init {
        _chatState.value = ChatState.NEW
    }

    fun setConversationID(username: String?, conversationID: String?){

        if(conversationID!=null) {
            this.conversationID = conversationID
            //this is to set senderid and receiverid in messages
            getUsernameFromConversationID(conversationID)
        }
        else{
            getConversationIDFromUsername(username)
            this.username = username
            Log.d(TAG, "setConversationID: $username")
        }
    }

    private fun getUsernameFromConversationID(conversationID: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                myUsername = dataRepository.getMydetails()!!.username
                username = dataRepository.getUsernameFromConversationID(conversationID)
                _chatState.postValue(ChatState.EXISTING)
            }
        }
    }

    private fun getConversationIDFromUsername(username: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){

                //Sets the myUsername here
                myUsername = dataRepository.getMydetails()!!.username

                val conID = dataRepository.getConversationIDFromUsername(username = username!!)
                if(conID == null){
                    _chatState.postValue(ChatState.NEW)
                    Log.d(TAG, "getConversationIDFromUsername: Worked")
                    //createTestNewChat(username)
                }
                else{
                    conversationID = conID
                    _chatState.postValue(ChatState.EXISTING)
                }
            }
        }
    }

    private suspend fun createNewChat121(username: String) {
         val response = dataRepository.createNewChat121(hashMapOf(
                "myID" to myUsername!!,
                "otherID" to username
            )).await()

        response.data?.let {
            val res = (it as HashMap<*, *>).get("conversationID")
            Log.d(TAG, "createTestNewChat: $res is the new conversationID")
            conversationID = res as String
            dataRepository.addConversation121(username, res)
        }
    }


    fun getChatsAll(): LiveData<List<MessageView>> {
        return dataRepository.getChats(conversationID!!)
    }

    fun sendTextMessage121(message: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                if(chatState.value == ChatState.NEW){
                    createNewChat121(username!!) //suspend function sets the conversationID
                    Log.d(TAG, "sendMessage: The conversationID created and it is ${conversationID}")
                    _chatState.postValue(ChatState.EXISTING)
                }
                //sets the livedata for chats once the conversationID is initialized
                dataRepository.saveTextMessage121Local(message, conversationID!!, myUsername!!, username!!)
            }
        }
    }

}


/*
private fun createNewChat121(username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val u = dataRepository.getMydetails()!!.username

                dataRepository.createNewChat121(hashMapOf(
                    "myID" to u,
                    "otherID" to username
                )).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val res = (it.result!!.data as HashMap<*, *>).get("conversationID")
                        Log.d(TAG, "createTestNewChat: $res is the new conversationID")
                    }
                }
            }
        }
    }

 */