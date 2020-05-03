package com.abhishekjagushte.engage.repository

import com.abhishekjagushte.engage.datasource.localdatasource.FirebaseInstanceSource
import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseAuthDataSource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val firebaseInstanceId: FirebaseInstanceSource
){
    private val TAG: String = "AuthRepository"
}