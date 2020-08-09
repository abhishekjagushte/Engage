package com.abhishekjagushte.engage.database.entities

import android.graphics.Bitmap
import androidx.room.*

@Entity(tableName = "conversations")
class Conversation(
    @PrimaryKey
    var conversationID: String, //the conversationID for the conversation
    //This conversation ID will contain the conversationID for M2M chats and username for 121 chats

    var name: String, //the name will be displayed on the chat_list so this can be name or nickname in case of 121
    // and group name in case of M2M

    var dp_thmb: Bitmap? = null, //the thumbnail of dp

    var type: Int, // determines whether the conversation is 121 or M2M
    var priority: Int = 0, //one can set priority if he wants to receive these messages atop
    var lastMessageID: String? = null, //the id of the last message that will contain the information about the message to be displayed
    var dp_url: String? = null, //the url to dp
    var desc: String? = null, //possible group description in case of M2M
    var active: Int //this if 1 says that this conversation should be displayed in chat_list and this being 0
    //is significant to store conversationID of inactive chats that are synced while first time insatll
    // of login or reinstall (during login, if backup not restored all 121 chats are active=0
    // and also if type = M2M and active = 0 that means conversation is not pushed yet

//    var needs_push: Int? //Only needed in 121 conversations. To reduce the delay in send the first message in 121
//                        //conversations with current mechanism that is fetching the conversationID first
//                        //from the firebase, now we will generate a temporary conversationID to be stored and set the needs_push
//                        // to 1. This will allow to get the correct conversationID with a listener and need to
//                        //replace the conversation ids of the messages sent
)

data class SearchResultConversation(
    var name:String,
    var type: Int,
    var conversationID: String
//    var lastMessageData: String? = null,
//    var lastMessageTime: String? = null,
//    var status: Int = -1 //the extra data will go into map for searchdata
)



@Entity(tableName = "contacts_chats_cross_ref", primaryKeys = ["username", "conversationID"])
data class ContactsConversationsCrossRef(
    var username: String,
    var conversationID: String
)

data class GroupMembers(
    @Embedded var conversation: Conversation,
    @Relation(
        parentColumn = "conversationID",
        entityColumn = "username",
        associateBy = Junction(ContactsConversationsCrossRef::class, parentColumn = "conversationID", entityColumn = "username")
    )
    var contacts: List<Contact>
)

data class CommonGroups(
    @Embedded var contact: Contact,
    @Relation(
        parentColumn = "username",
        entityColumn = "conversationID",
        associateBy = Junction(ContactsConversationsCrossRef::class, parentColumn = "username", entityColumn = "conversationID")
    )
    var groups: List<Conversation>
)