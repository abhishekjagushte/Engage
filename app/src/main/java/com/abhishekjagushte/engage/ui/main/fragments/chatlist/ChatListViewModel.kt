package com.abhishekjagushte.engage.ui.main.fragments.chatlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class ChatListViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val testText = MutableLiveData<String>()

    fun showUserDataCount(){
        uiScope.launch {
            withContext(Dispatchers.IO){
                testText.postValue("Number of Entries = ${dataRepository.getCountUserData()}")
            }
        }
    }

}
