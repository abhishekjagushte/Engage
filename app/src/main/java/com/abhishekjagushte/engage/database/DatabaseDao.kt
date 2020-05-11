package com.abhishekjagushte.engage.database

import androidx.room.*

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

    @Query("SELECT * FROM contacts WHERE username = :username")
    fun getContactFromUsername(username: String): List<Contact>

    @Update
    fun updateContact(contact: Contact)

    @Query("SELECT name, username FROM contacts WHERE type == 0")
    fun getMyDetails(): List<ContactNameUsername>


    // Search Queries
    //Pass username when type is 121 and pass networkID when M2M
    //in both cases when entering the chats activity, we need to fetch data from room
    //TODO later implement a join query for message data too
    @Query("SELECT name, username, type, networkID FROM conversations WHERE name LIKE '%' || :query || '%' ")
    fun searchForConversations(query: String): List<SearchResultConversation>

    @Query("SELECT name, username FROM contacts WHERE name LIKE '%' || :query || '%' AND type > 0 ORDER BY type ASC")
    fun searchForContacts(query: String): List<SearchResultContact>

    @Query("SELECT name, username FROM suggested_contacts WHERE name LIKE '%' || :query || '%' ORDER BY score")
    fun searchForSuggestedContacts(query: String): List<SearchResultContact>

    @Query("SELECT COUNT(*) FROM contacts")
    fun getCountContacts(): Int
}
