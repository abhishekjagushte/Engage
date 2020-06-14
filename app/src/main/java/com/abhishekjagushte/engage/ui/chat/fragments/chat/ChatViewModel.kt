package com.abhishekjagushte.engage.ui.chat.fragments.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.*
import javax.inject.Inject

enum class ChatState{
    NEW, EXISTING
}

class ChatViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel(){

    lateinit var conversationID: String

    private val TAG = "ChatViewModel"

    var chatState: ChatState = ChatState.EXISTING //Set initially as existing as this will be in most of the cases


    val viewModelJob = Job()
    val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun setConversationID(username: String?, conversationID: String?){

        if(conversationID!=null) {
            this.conversationID = conversationID
            Log.d(TAG, "setConversationID: $conversationID")
        }
        else{
            getConversationIDFromUsername(username)
            Log.d(TAG, "setConversationID: $username")
        }
    }

    private fun getConversationIDFromUsername(username: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val conID = dataRepository.getConversationIDFromUsername(username = username!!)
                if(conID == null){
                    chatState = ChatState.NEW
                    Log.d(TAG, "getConversationIDFromUsername: Worked")

                    //createTestNewChat(username)
                }
                else{
                    conversationID = conID
                }
            }
        }
    }

    private fun createTestNewChat(username: String) {
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
}