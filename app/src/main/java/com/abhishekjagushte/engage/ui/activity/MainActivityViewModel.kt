package com.abhishekjagushte.engage.ui.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.entities.Conversation
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.*

class MainActivityViewModel(
    private val dataRepository: DataRepository
): ViewModel(){

    private val TAG = "MainActivityViewModel"

    val job: Job = Job()
    val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)

    fun printPanda(){
        Log.d(TAG, "printPanda: Printed")
    }

    fun getUnsentMessages(): LiveData<List<Message>> {
        return dataRepository.getUnsentMessages()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: MainActivity ViewModel onCleared")
    }

    fun sendMessages(unsentMessages: List<Message>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                for(m in unsentMessages){
                    dataRepository.pushMessage(m)
                }
            }
        }
    }
}

//    private fun createNewChat121(con: Conversation) {
//        val myUsername = dataRepository.getMydetails()!!.username
//
//        dataRepository.createNewChat121(hashMapOf(
//            "myID" to myUsername,
//            "otherID" to con.username!!,
//            "conversationID" to con.networkID
//        )).addOnSuccessListener {
//            it.data?.let{
//                val res = (it as HashMap<*, *>).get("conversationID") as String
//                Log.d(TAG, "createTestNewChat: $res is the new conversationID")
//            }
//
//            viewModelScope.launch {
//                withContext(Dispatchers.IO){
//                    con.needs_push = Constants.CONVERSATION_NEEDS_PUSH_NO
//                    dataRepository.updateConversation(con)
//                }
//            }
//
//        }

//        response.data?.let {
//            val res = (it as HashMap<*, *>).get("conversationID")
//            Log.d(TAG, "createTestNewChat: $res is the new conversationID")
//            conversationID = res as String
//            dataRepository.addConversation121(username, res)
//        }
//    }

//    fun pushConversations(it: List<Conversation>) {
//        viewModelScope.launch {
//            withContext(Dispatchers.IO){
//                for(con in it){
//                    createNewChat121(con)
//                }
//            }
//        }
//    }