package com.abhishekjagushte.engage.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface DatabaseDao{

    @Insert
    fun insertNewContact(contact: Contact)

    @Insert
    fun insertMeinContacts(contact: Contact)

    @Insert
    fun insertCredentials(userData: UserData)

}
