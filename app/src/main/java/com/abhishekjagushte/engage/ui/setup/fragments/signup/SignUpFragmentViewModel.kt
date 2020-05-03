package com.abhishekjagushte.engage.ui.setup.fragments.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.AuthRepository
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class SignUpFragmentViewModel @Inject constructor(
    private val dataRepository: DataRepository) : ViewModel() {
    private val TAG: String = "SignUpFragmentViewModel"

    private val _noteText = MutableLiveData<String>()
    val noteText: LiveData<String>
        get() { return _noteText }

    private val _signUpComplete = MutableLiveData<Boolean>()
    val signUpComplete: LiveData<Boolean>
        get() { return _signUpComplete }


    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    fun firebaseSignup(email: String, password: String) {
        dataRepository.signup(email, password).addOnSuccessListener {
            Log.d(TAG,"Sign up success")

            coroutineScope.launch {
                withContext(Dispatchers.IO){
                    dataRepository.signUpAddCredentialsLocal(email, password)
                    _signUpComplete.postValue(true)
                }
            }
        }.addOnFailureListener {
            _noteText.value =  it.message ?: ""
        }
    }
}
