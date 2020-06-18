package com.abhishekjagushte.engage.network

import com.abhishekjagushte.engage.database.Contact
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Profile constructor(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val bio: String = "I am using Engage!",
    val dp_thmb: String? = "",
    val dp_url: String? = "",
    val device_type: Int = 1,

    @ServerTimestamp
    val joinTimeStamp: Date? = null,

    val notificationChannelID: String = "",
    val conversations121: List<HashMap<String, String>> = listOf(),
    val conversationsM2M: List<HashMap<String, String>> = listOf()
)

fun Profile.convertDomainObject(type: Int): Contact{
    return Contact(
        networkID = this.id,
        name = this.name,
        username = this.username,
        bio = this.bio,
        type = type)
}

data class DateTest constructor(
    @ServerTimestamp
    val offsetDateTime: Date? = null,
    val name: String? = null
)


class MessageNetwork(
    val messageID: String,
    val conversationID: String,

    @ServerTimestamp
    val timeStamp: Date?=null, //this timestamp will be the timestamp while sending the message

    val data: String?, //the data of message
    val senderID: String?, //the senderID of the message
    val receiverID: String?, //the receiverID for the message

    //Media
    val mime_type: String?, //the mime type
    val server_url: String?=null, // the cloud url for the media
    val latitude: Double?=null, //latitude for location sharing
    val longitude: Double?=null, //logitude for location sharing

    val thumb_nail: ByteArray?=null, //thumbnail for the media

    val reply_toID: String?=null
)