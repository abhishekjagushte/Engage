package com.abhishekjagushte.engage.ui.setup.screens.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

class LoginFragmentViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel(){

    //private val coriutine

    private val TAG: String = "LoginFragmentViewModel"
    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val completeStatus = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    init {
        completeStatus.value = Constants.NOT_INITIATED
        errorMessage.value = ""
    }

    fun login(email: String, password: String){
        dataRepository.login(email, password, completeStatus)
    }

}
