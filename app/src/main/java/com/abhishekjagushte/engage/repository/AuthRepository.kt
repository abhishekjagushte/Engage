package com.abhishekjagushte.engage.repository

import androidx.lifecycle.LiveData
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
){

    private val TAG: String = "AuthRepository"

    fun firebaseSignUp(email: String, password:String){

    }
}