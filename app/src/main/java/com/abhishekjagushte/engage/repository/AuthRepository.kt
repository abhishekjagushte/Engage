package com.abhishekjagushte.engage.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.datasource.localdatasource.FirebaseInstanceSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firebaseInstanceId: FirebaseInstanceSource
){

    private val TAG: String = "AuthRepository"

    fun getCurrentUserUID():String{
        return authDataSource.getCurrentUserUID()
    }

    fun signup(email: String, password:String): Task<AuthResult> {
        return authDataSource.signUp(email,password)
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return authDataSource.login(email, password)
    }

    fun setNameAndUsername(
        name: String,
        username: String,
        completeStatus: MutableLiveData<String>
    ){
        Log.d(TAG, "Inside auth repo setusername")
        firebaseInstanceId.getNotificationChannelID()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.d(TAG,token?:"Not generated")
                if(token!=null){
                    authDataSource.setNameAndUsername(name,username, token).addOnSuccessListener {
                        completeStatus.value = Constants.FIREBASE_CHANGE_COMPLETE
                        Log.d(TAG,Constants.FIREBASE_CHANGE_COMPLETE)
                    }.addOnFailureListener {
                        completeStatus.value = Constants.FIREBASE_CHANGE_FAILED
                        Log.d(TAG,Constants.FIREBASE_CHANGE_FAILED)
                    }
                }
            })
    }

}