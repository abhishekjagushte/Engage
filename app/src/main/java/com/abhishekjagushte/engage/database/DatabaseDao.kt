package com.abhishekjagushte.engage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewContact(contact: Contact)

    @Insert
    fun insertMeinContacts(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCredentials(userData: UserData)

    @Query(value = "DELETE FROM user_data")
    fun truncateCredentials()

    @Query("SELECT * FROM user_data")
    fun getCurrentLoggedInUser(): List<UserData>

    @Query("SELECT COUNT(*) FROM user_data")
    fun getCredentialsCount(): Int


    // Search Queries
    @Query("SELECT name, username, type FROM conversations WHERE name LIKE :query")
    fun searchForConversations(query: String): List<SearchResultConversation>

    @Query("SELECT name, username FROM contacts WHERE name LIKE '%' || :query || '%' ")// AND type > 0 ORDER BY type ASC
    fun searchForContacts(query: String): List<SearchResultContact>

    @Query("SELECT name, username FROM suggested_contacts WHERE name LIKE :query ORDER BY score")
    fun searchForSuggestedContacts(query: String): List<SearchResultContact>

    @Query("SELECT COUNT(*) FROM contacts")
    fun getCountContacts(): Int
}
