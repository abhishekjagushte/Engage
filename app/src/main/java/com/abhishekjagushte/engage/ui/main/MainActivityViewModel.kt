package com.abhishekjagushte.engage.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.Message
import com.abhishekjagushte.engage.repository.DataRepository
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