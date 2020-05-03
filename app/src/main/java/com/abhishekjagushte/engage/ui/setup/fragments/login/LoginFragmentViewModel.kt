package com.abhishekjagushte.engage.ui.setup.fragments.login

import android.provider.SyncStateContract
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.AuthRepository
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

//FirebaseSignin Content
//mAuth = FirebaseAuth.getInstance()
//mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
//
//    coroutineScope.launch {
//        val profile = repository.fireBaseGetDataSignIn(mAuth.currentUser!!.uid)
//
//        if(profile !== null){
//            repository.addDataLocalSignIn(profile,email,password)
//            Log.d(TAG, "Login Completed")
//        }
//        else{
//            Log.d(TAG, "Error encountered, please try again")
//        }
//    }
//}.addOnFailureListener {
//    exception -> Log.d(TAG, exception.stackTrace.toString())
//}
//
