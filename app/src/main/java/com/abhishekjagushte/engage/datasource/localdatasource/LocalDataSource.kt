package com.abhishekjagushte.engage.datasource.localdatasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.*
import javax.inject.Inject

class LocalDataSource @Inject constructor (private val databaseDao: DatabaseDao){

    private val TAG = "LocalDataSource"

    fun getCurrentLoggedInUserCredentials(userData: MutableLiveData<UserData?>){
        val users = databaseDao.getCurrentLoggedInUser()
        getCountUserData()
        if(users.isEmpty()){
           userData.postValue(null)
        }
        else{
            userData.postValue(users[0])
        }
    }

    fun getCountUserData(): Int{
        val i = databaseDao.getCredentialsCount()
        Log.d(TAG, "Number of entries = $i")
        return i
    }

    fun signUpAddCredentialsLocal(email: String, password: String){
        //databaseDao.truncateCredentials()
        databaseDao.insertCredentials(UserData(email, password))
    }

    fun addMyDetailsInContacts(contact: Contact){
        databaseDao.insertMeinContacts(contact)
    }

    fun searchForConversations(query: String): List<SearchResultConversation> {
        return databaseDao.searchForConversations(query)
    }

    fun searchForContacts(query: String): List<SearchResultContact> {
        return databaseDao.searchForContacts(query)
    }

    fun searchForSuggestedContacts(query: String): List<SearchResultContact> {
        return databaseDao.searchForSuggestedContacts(query)
    }


    fun addContactsTest(){
        databaseDao.insertNewContact(Contact(
            name = "Abhishek Jagushte",
            username = "abhijagushte",
            networkID = "dgvsgavsgdjvajaj",
            type = 1
        ))

        databaseDao.insertNewContact(Contact(
            name = "Taylor Swift",
            username = "taylorswift13",
            networkID = "sdbjewgvfjbnsdffff",
            type = 1
        ))

        databaseDao.insertNewContact(Contact(
            name = "Sam Smith",
            username = "samsmith",
            networkID = "bekhgflgewhkfweljvkhev",
            type = 2
        ))

        databaseDao.insertNewContact(Contact(
            name = "Panda",
            username = "panda",
            networkID = "sbfjhgfyksvhjafua",
            type = 1
        ))

        databaseDao.insertNewContact(Contact(
            name = "Prachi Pathare",
            username = "prachi",
            networkID = "dgvsgdsddsgdjvajaj",
            type = 3
        ))

        databaseDao.insertNewContact(Contact(
            name = "Kylie Jenner",
            username = "fvdsfvdshfvdshfv",
            networkID = "dgvsgadfeknfke ",
            type = 4
        ))
    }

    fun getCountContacts(): Int {
        return databaseDao.getCountContacts()
    }
}