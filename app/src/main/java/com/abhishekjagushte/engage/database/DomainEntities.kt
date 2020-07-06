package com.abhishekjagushte.engage.database

import android.graphics.Bitmap
import androidx.room.*
import com.abhishekjagushte.engage.network.MessageNetwork121

///////////////////////////////////////////////////////////////////////////
// I changes the networkID to conversationID so there might be errors
///////////////////////////////////////////////////////////////////////////

@Entity(tableName = "contacts")
class Contact(

    @ColumnInfo(name = "network_id")
    var networkID: String,

    @PrimaryKey
    var username: String,

    var name: String,
    var nickname: String = name,

    @ColumnInfo(defaultValue = "I am using Engage!")
    var bio: String = "I am using Engage!",

    var dp_thmb: Bitmap? = null,
    var type: Int,

    @ColumnInfo(name="time_stamp")
    var timeStamp: String? = null

    //conversation ID removed from the contacts

)

@Entity(tableName = "suggested_contacts")
data class SuggestedContacts(
    @PrimaryKey
    var username: String,
    var name: String,
    var score: Int
)

@Entity(tableName = "user_data")
data class UserData(

    @PrimaryKey
    var email: String,
    var password: String
)

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

//    var needs_push: Int? //Only needed in 121 conversations. To reduce the delay in send the first message in 121
//                        //conversations with current mechanism that is fetching the conversationID first
//                        //from the firebase, now we will generate a temporary conversationID to be stored and set the needs_push
//                        // to 1. This will allow to get the correct conversationID with a listener and need to
//                        //replace the conversation ids of the messages sent
)


@DatabaseView("""
    SELECT conversations.name, conversations.dp_thmb, conversations.dp_url,
      conversations.conversationID, conversations.type AS conType,  messages.messageID, messages.timeStamp, messages.data, messages.mime_type ,messages.type,
      messages.status, messages.senderID, contacts.nickname FROM conversations
      LEFT JOIN messages ON conversations.lastMessageID == messages.messageID
      LEFT JOIN contacts ON conversations.conversationID == contacts.username
""", viewName = "conversation_view")
data class ConversationView(
    var name: String?,
    var dp_thmb: Bitmap?,
    var dp_url: String?,
    var conversationID: String,
    var conType: Int?,
    var messageID: String?,
    var timeStamp: Long?,
    var data: String?,
    var mime_type: String?,
    var type: Int?,
    var status: Int?,
    var senderID: String?,
    var nickname: String?
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


@Entity(tableName = "messages")
class Message(

    @PrimaryKey
    var messageID: String,
    var conversationID: String,
    var type: Int?, //states whether the message is mine or other's
    var status: Int?, //status of message required for if message read by other party
    var needs_push: Int?, //determines whether push needs to be done if device was offline
    var timeStamp: Long?, //this timestamp will be the timestamp while sending the message
    var serverTimestamp: Long?,
    var data: String?, //the data of message
    var senderID: String?, //the senderID of the message
    var receiverID: String?, //the receiverID for the message
    var deleted: Int = 0, //if 1 then the message is deleted else 0 or null

    //Media
    var mime_type: String?, //the mime type
    var server_url: String?=null, // the cloud url for the media
    var local_uri: String?=null, //local uri (the file path) for the media
    var latitude: Double?=null, //latitude for location sharing
    var longitude: Double?=null, //logitude for location sharing

    var thumb_nail: Bitmap?=null, //thumbnail for the media

    var reply_toID: String?=null,

    //@Ignore val name: String?=null, //for other messages

    var conType: Int
){
    fun convertNetworkMessage121(): MessageNetwork121{
        return MessageNetwork121(
            messageID = messageID,
            data = data,
            senderID = senderID,
            receiverID = receiverID,
            mime_type = mime_type
        )
    }
}


@DatabaseView(value =
    """
    SELECT contacts.nickname, messages.messageID, messages.conversationID ,messages.type, messages.status,
    messages.timeStamp, messages.data, messages.senderID, messages.receiverID, messages.deleted, messages.mime_type, 
    messages.server_url, messages.local_uri, messages.latitude, messages.longitude,
    messages.reply_toID FROM contacts INNER JOIN messages ON messages.receiverID = contacts.username
    """
    , viewName = "message_view")
data class MessageView(
    var nickname: String?,
    var messageID: String,
    var conversationID: String,
    var type: Int?,
    var status: Int?,
    var timeStamp: Long?,
    var data: String?,
    var senderID: String?,
    var receiverID: String?,
    var deleted: Int,
    var mime_type: String?,
    var server_url: String?,
    var local_uri: String?,
    var latitude: Double?,
    var longitude: Double?,
    var reply_toID: String?
)


data class SearchResultConversation(
    var name:String,
    var type: Int,
    var conversationID: String
//    var lastMessageData: String? = null,
//    var lastMessageTime: String? = null,
//    var status: Int = -1 //the extra data will go into map for searchdata
)


data class ContactNameUsername(
    var name: String,
    var username: String
)

data class SearchResultContact(
    var name: String,
    var username: String
)