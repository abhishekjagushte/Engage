package com.abhishekjagushte.engage.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abhishekjagushte.engage.database.entities.*
import com.abhishekjagushte.engage.database.views.ConversationView
import com.abhishekjagushte.engage.database.views.MessageNotificationView
import com.abhishekjagushte.engage.database.views.MessageView
import androidx.paging.DataSource

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

    @Query("SELECT * FROM contacts WHERE username = :username")
    fun getContactFromUsername(username: String): LiveData<Contact>

    @Query("SELECT * FROM contacts WHERE username = :username")
    fun getContact(username: String): Contact?

    @Update
    fun updateContact(contact: Contact)

    @Query("SELECT name, username FROM contacts WHERE type == 0")
    fun getMyDetails(): ContactNameUsername?

    // Search Queries
    //Pass username when type is 121 and pass networkID when M2M
    //in both cases when entering the chats activity, we need to fetch data from room
    //TODO later implement a join query for message data too
    @Query("SELECT name, type, conversationID FROM conversations WHERE name LIKE '%' || :query || '%' ")
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

    @Query("SELECT * FROM message_view WHERE conversationID == :conversationID ORDER BY timeStamp DESC")
    fun getChats(conversationID: String): DataSource.Factory<Int, MessageView>
            //LiveData<List<MessageView>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConversation(conversation: Conversation)

    @Query("SELECT * FROM messages WHERE conversationID == :conversationID ORDER BY timeStamp")
    fun getChatsCount(conversationID: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)

    @Query("SELECT COUNT(*) FROM message_view")
    fun testCOuntMessages(): Int

    @Query("SELECT nickname FROM contacts WHERE username == :username")
    fun getNameFromUsername(username: String): String


    @Query("SELECT * FROM messages WHERE status == 0")
    fun getUnsentMessages(): List<Message>

    @Update
    fun updateMessage(message: Message)

    @Query("SELECT * FROM conversations WHERE conversationID == :conversationID")
    fun getConversation(conversationID: String):Conversation?

    @Query("SELECT * FROM conversations WHERE type == 1")
    fun getUnPushedConversations(): LiveData<List<Conversation>>

    @Update
    fun updateConversation(conversation: Conversation)

    @Query("SELECT COUNT(*) FROM conversations WHERE conversationID == :conID")
    fun checkConversationExists(conID: String): Int

    @Query("SELECT * FROM conversation_view ORDER BY timeStamp DESC")
    fun getConversationList() : LiveData<List<ConversationView>>


    ///////////////////////////////////////////////////////////////////////////
    // Search for friends
    ///////////////////////////////////////////////////////////////////////////
    @Query("SELECT name, username FROM contacts WHERE type == 1 AND (name LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%')")
    fun searchForFriends(query: String): List<ContactNameUsername>

    ///////////////////////////////////////////////////////////////////////////
    // Test
    ///////////////////////////////////////////////////////////////////////////

    @Query("SELECT * FROM contacts WHERE type == 1")
    fun getConfirmedContactsTest(): LiveData<List<Contact>>

    @Insert
    fun insertConversationCrossRef(contactsConversationsCrossRef: ContactsConversationsCrossRef)

    //5 means read_by_me and 4 means not read ...see constants file
    @Query("UPDATE messages SET status = 5 WHERE status = 4 AND conversationID = :conversationID ")
    fun markMessagesRead(conversationID: String)

    //gets all the messages unread by me
    @Query("SELECT * FROM message_notification_view where status = 4")
    fun getUnreadMessages(): List<MessageNotificationView>

    @Query("SELECT * FROM message_notification_view WHERE messageID = :messageID")
    fun getMessageNotification(messageID: String): MessageNotificationView

    @Query("SELECT * FROM messages WHERE messageID = :messageID")
    fun getMessage(messageID: String): Message

    @Query("UPDATE messages SET status = 1 WHERE messageID = :messageID")
    fun setMessageSent(messageID: String)

    @Query("SELECT MAX(timeStamp) FROM messages WHERE conType = 1")//contype 1 = 121
    fun getLast121MessageTimestamp(): Long


    //gets timestamp of last message for a specific m2m con
    @Query("SELECT MAX(timeStamp) FROM messages WHERE conversationID = :conversationID")
    fun getLastM2MMessageTimestampForConversation(conversationID: String): Long

    @Query("SELECT conversations.conversationID, timeStamp AS lastMessageTimeStamp FROM conversations INNER JOIN messages ON conversations.lastMessageID == messages.messageID AND conversations.type =2 ")
    fun getM2MSyncRequirement(): List<M2MSyncRequirement>

    @Query("UPDATE messages SET status = 4 WHERE messageID = :messageID")
    fun setMessageReceived(messageID: String)
}
