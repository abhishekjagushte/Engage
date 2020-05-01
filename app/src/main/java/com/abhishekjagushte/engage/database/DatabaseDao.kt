package com.abhishekjagushte.engage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface DatabaseDao{

    @Insert
    fun insertNewContact(contact: Contact)

    @Insert
    fun insertMeinContacts(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCredentials(userData: UserData)

}
