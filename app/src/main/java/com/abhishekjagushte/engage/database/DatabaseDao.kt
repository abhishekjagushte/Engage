package com.abhishekjagushte.engage.database

import androidx.lifecycle.LiveData
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
    fun getMyDetails(): ContactNameUsername?


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


    ///////////////////////////////////////////////////////////////////////////
    // ChatScreen Fragment
    ///////////////////////////////////////////////////////////////////////////

    @Query("SELECT networkID from conversations WHERE username == :username")
    fun getConversationIDFromUsername(username: String): String?

    @Query("SELECT * FROM message_view WHERE conversationID == :conversationID ORDER BY timeStamp DESC")
    fun getChats(conversationID: String): LiveData<List<MessageView>>

    @Insert
    fun insertConversation(conversation: Conversation)

    @Query("SELECT * FROM messages WHERE conversationID == :conversationID ORDER BY timeStamp")
    fun getChatsCount(conversationID: String): List<Message>

    @Insert
    fun insertMessage(message: Message)

    @Query("SELECT COUNT(*) FROM message_view")
    fun testCOuntMessages(): Int

    @Query("SELECT nickname FROM contacts WHERE username == :username")
    fun getNameFromUsername(username: String): String


    @Query("SELECT * FROM messages WHERE status == 0")
    fun getUnsentMessages(): LiveData<List<Message>>

    @Update
    fun updateMessage(message: Message)

    @Query("SELECT * FROM conversations WHERE networkID == :conversationID")
    fun getConversation(conversationID: String):Conversation?

    @Query("SELECT username FROM conversations WHERE networkID == :conversationID")
    fun getUsernameFromConversationID(conversationID: String): String

}
