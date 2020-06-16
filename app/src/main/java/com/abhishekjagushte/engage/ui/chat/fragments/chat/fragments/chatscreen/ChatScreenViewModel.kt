package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.MessageView
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatState
import com.abhishekjagushte.engage.ui.chat.fragments.chat.ChatViewModel
import javax.inject.Inject

class ChatScreenViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    lateinit var sharedViewModel: ChatViewModel

    private var _chats =  MutableLiveData<List<MessageView>>()

//    fun getChats(){
//        if(sharedViewModel.chatState == ChatState.EXISTING)
//        {
//            chats = dataRepository.getChats(sharedViewModel.conversationID.va)
//        }
//    }
//
//    fun sendMessage(message: String){
//        if(sharedViewModel.chatState == ChatState.NEW){
//
//        }
//    }

}