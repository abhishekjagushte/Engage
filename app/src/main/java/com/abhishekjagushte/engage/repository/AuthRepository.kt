package com.abhishekjagushte.engage.repository

import androidx.lifecycle.LiveData
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
){

    private val TAG: String = "AuthRepository"

    fun signup(email: String, password:String): Task<AuthResult> {
        return authDataSource.signUp(email,password)
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return authDataSource.login(email, password)
    }

}