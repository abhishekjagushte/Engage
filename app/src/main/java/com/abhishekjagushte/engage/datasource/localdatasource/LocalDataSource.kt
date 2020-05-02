package com.abhishekjagushte.engage.datasource.localdatasource

import com.abhishekjagushte.engage.database.AppDatabase
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.database.DatabaseDao
import com.abhishekjagushte.engage.database.UserData
import javax.inject.Inject

class LocalDataSource @Inject constructor (
    private val databaseDao: DatabaseDao
){

    fun signUpAddCredentialsLocal(email: String, password: String){
        databaseDao.insertCredentials(UserData(email, password))
    }

    fun addMyDetailsInContacts(
        name: String,
        username: String,
        type: Int,
        uid: String
    ){
        val contact = Contact(
            networkID = uid,
            name = name,
            username = username,
            type = type
        )
        databaseDao.insertMeinContacts(contact)
    }
}