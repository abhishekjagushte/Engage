package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.chatscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.MessageView
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.chat.screens.chat.ChatViewModel
import javax.inject.Inject

class ChatScreenViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    lateinit var sharedViewModel: ChatViewModel

    private var _chats =  MutableLiveData<List<MessageView>>()


}