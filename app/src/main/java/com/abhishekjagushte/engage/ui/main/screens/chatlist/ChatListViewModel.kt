package com.abhishekjagushte.engage.ui.main.screens.chatlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.views.ConversationView
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class ChatListViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val testText = MutableLiveData<String>()

    fun getConversationList(): LiveData<List<ConversationView>> {
        return dataRepository.getConversationList()
    }

}
