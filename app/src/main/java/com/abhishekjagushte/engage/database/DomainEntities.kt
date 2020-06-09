package com.abhishekjagushte.engage.database

import androidx.room.*

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
    val networkID: String,
    val name: String,

    //Username for 121 chats so that we can use the username of the contact to update any required dp or bio
    val username: String? = null,

    @ColumnInfo(name="dp_thmb", typeAffinity = ColumnInfo.BLOB)
    val dp_thmb: ByteArray? = null,

    val type: Int,
    val status: Int,
    val priority: Int,
    val lastMessageID: String? = null,
    val dp_url: String? = null,
    val desc: String? = null
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