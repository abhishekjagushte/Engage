package com.abhishekjagushte.engage.datasource.localdatasource

import com.abhishekjagushte.engage.database.AppDatabase
import com.abhishekjagushte.engage.database.DatabaseDao
import com.abhishekjagushte.engage.database.UserData
import javax.inject.Inject

class LocalDataSource @Inject constructor (
    private val databaseDao: DatabaseDao
){

    fun signUpAddCredentialsLocal(email: String, password: String){
        databaseDao.insertCredentials(UserData(email, password))
    }
}