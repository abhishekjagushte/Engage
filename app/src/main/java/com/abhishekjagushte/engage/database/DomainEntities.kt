package com.abhishekjagushte.engage.database

import androidx.room.*
import com.abhishekjagushte.engage.network.MessageNetwork
import java.time.LocalDateTime

@Entity(tableName = "contacts")
class Contact(

    @ColumnInfo(name = "network_id")
    val networkID: String,

    @PrimaryKey
    val username: String,

    var name: String,
    val nickname: String = name,

    @ColumnInfo(defaultValue = "I am using Engage!")
    var bio: String = "I am using Engage!",

    @ColumnInfo(name="dp_thmb", typeAffinity = ColumnInfo.BLOB)
    var dp_thmb: ByteArray? = null,
    val type: Int,

    @ColumnInfo(name="time_stamp")
    val timeStamp: String? = null
)

@Entity(tableName = "suggested_contacts")
data class SuggestedContacts(
    @PrimaryKey
    val username: String,
    val name: String,
    val score: Int
)

@Entity(tableName = "user_data")
data class UserData(

    @PrimaryKey
    val email: String,
    val password: String
)

@Entity(tableName = "conversations")
class Conversation(
    @PrimaryKey
    val networkID: String, //the conversationID for the conversation
    val name: String, //the name will be displayed on the chat_list so this can be name or nickname in case of 121
                        // and group name in case of M2M

    //Username for 121 chats so that we can use the username of the contact to update any required dp or bio
    val username: String? = null,

    @ColumnInfo(name="dp_thmb", typeAffinity = ColumnInfo.BLOB)
    val dp_thmb: ByteArray? = null, //the thumbnail of dp

    val type: Int, // determines whether the conversation is 121 or M2M
    val priority: Int = 0, //one can set priority if he wants to receive these messages atop
    val lastMessageID: String? = null, //the id of the last message that will contain the information about the message to be displayed
    val dp_url: String? = null, //the url to dp
    val desc: String? = null, //possible group description in case of M2M
    val active: Int //this if 1 says that this conversation should be displayed in chat_list and this being 0
                    //is significant to store conversationID of inactive chats that are synced while first time insatll
                    // of login or reinstall (during login, if backup not restored all 121 chats are active=0
)

@Entity(tableName = "contacts_chats_cross_ref", primaryKeys = ["username", "networkID"])
data class ContactsConversationsCrossRef(
    val username: String,
    val networkID: String
)

data class GroupMembers(
    @Embedded val conversation: Conversation,
    @Relation(
        parentColumn = "networkID",
        entityColumn = "username",
        associateBy = Junction(ContactsConversationsCrossRef::class, parentColumn = "networkID", entityColumn = "username")
    )
    val contacts: List<Contact>
)

data class CommonGroups(
    @Embedded val contact: Contact,
    @Relation(
        parentColumn = "username",
        entityColumn = "networkID",
        associateBy = Junction(ContactsConversationsCrossRef::class, parentColumn = "username", entityColumn = "networkID")
    )
    val groups: List<Conversation>
)


@Entity(tableName = "messages")
class Message(

    @PrimaryKey
    val messageID: String,
    val conversationID: String,
    val type: Int?, //states whether the message is mine or other's
    var status: Int?, //status of message required for if message read by other party
    val needs_push: Int?, //determines whether push needs to be done if device was offline
    val timeStamp: Long?, //this timestamp will be the timestamp while sending the message
    val data: String?, //the data of message
    val senderID: String?, //the senderID of the message
    val receiverID: String?, //the receiverID for the message
    val deleted: Int = 0, //if 1 then the message is deleted else 0 or null

    //Media
    val mime_type: String?, //the mime type
    val server_url: String?=null, // the cloud url for the media
    val local_uri: String?=null, //local uri (the file path) for the media
    val latitude: Double?=null, //latitude for location sharing
    val longitude: Double?=null, //logitude for location sharing

    @ColumnInfo(name="dp_thmb", typeAffinity = ColumnInfo.BLOB)
    val thumb_nail: ByteArray?=null, //thumbnail for the media

    val reply_toID: String?=null,

    //@Ignore val name: String?=null, //for other messages

    val conType: Int
){
    fun convertNetworkMessage(): MessageNetwork{
        return MessageNetwork(
            messageID = messageID,
            conversationID = conversationID,
            data = data,
            senderID = senderID,
            receiverID = receiverID,
            mime_type = mime_type
        )
    }
}

@DatabaseView(value = "SELECT contacts.nickname, messages.messageID, messages.conversationID ,messages.type, messages.status," +
        " messages.timeStamp, messages.data, messages.senderID, messages.receiverID, messages.deleted, messages.mime_type, " +
        "messages.server_url, messages.local_uri, messages.latitude, messages.longitude," +
        " messages.reply_toID FROM contacts INNER JOIN messages ON messages.receiverID = contacts.username", viewName = "message_view")
data class MessageView(
    val nickname: String?,
    val messageID: String,
    val conversationID: String,
    val type: Int?,
    val status: Int?,
    val timeStamp: Long?,
    val data: String?,
    val senderID: String?,
    val receiverID: String?,
    val deleted: Int,
    val mime_type: String?,
    val server_url: String?,
    val local_uri: String?,
    val latitude: Double?,
    val longitude: Double?,
    val reply_toID: String?
)


data class SearchResultConversation(
    val name:String,
    val username: String,
    val type: Int,
    val networkID: String
//    val lastMessageData: String? = null,
//    val lastMessageTime: String? = null,
//    val status: Int = -1 //the extra data will go into map for searchdata
)


data class ContactNameUsername(
    val name: String,
    val username: String
)

data class SearchResultContact(
    val name: String,
    val username: String
)