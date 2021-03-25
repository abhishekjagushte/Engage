package com.abhishekjagushte.engage.network

import androidx.room.PrimaryKey
import com.abhishekjagushte.engage.database.entities.Contact
import com.abhishekjagushte.engage.database.entities.Event
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.utils.Constants
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

fun Profile.convertDomainObject(type: Int): Contact {
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
    val messageID: String?=null,

    @ServerTimestamp
    val timeStamp: Date?=null, //this timestamp will be the timestamp while sending the message

    val conversationID: String? = null, //used only for m2m messages

    val data: String?=null, //the data of message
    val senderID: String?=null, //the senderID of the message
    val receiverID: String?=null, //the receiverID for the message

    //Media
    val mime_type: String?=null, //the mime type
    val server_url: String?=null, // the cloud url for the media
    val latitude: Double?=null, //latitude for location sharing
    val longitude: Double?=null, //logitude for location sharing

    val thumb_nail: ByteArray?=null, //thumbnail for the media

    val reply_toID: String?=null
){

    fun convertDomainMessage121(): Message{

        val msg = Message(
            messageID = messageID!!,
            timeStamp = System.currentTimeMillis(),
            conversationID =  senderID!!,
            data = data,
            mime_type = mime_type,
            server_url = server_url,
            latitude = latitude,
            longitude = longitude,
            conType = Constants.CONVERSATION_TYPE_121,
            type = Constants.TYPE_OTHER_MSG,
            needs_push = Constants.NEEDS_PUSH_NO,
            receiverID = receiverID,
            senderID = senderID,
            serverTimestamp = timeStamp!!.time,
            status = Constants.STATUS_RECEIVED_BUT_NOT_READ
        )

        if(mime_type == Constants.MIME_TYPE_IMAGE_JPG)
            msg.status = Constants.STATUS_RECEIVED_MEDIA_NOT_DOWNLOADED

        return msg
    }

    fun convertDomainMessageM2M(conversationID: String): Message{
        return Message(
            messageID = messageID!!,
            timeStamp = System.currentTimeMillis(),
            conversationID =  conversationID,
            data = data,
            mime_type = mime_type,
            server_url = server_url,
            latitude = latitude,
            longitude = longitude,
            conType = Constants.CONVERSATION_TYPE_M2M,
            type = Constants.TYPE_OTHER_MSG,
            needs_push = Constants.NEEDS_PUSH_NO,
            receiverID = null,
            senderID = senderID,
            serverTimestamp = timeStamp!!.time,
            status = Constants.STATUS_RECEIVED_BUT_NOT_READ
        )
    }

}

data class CreateGroupRequest(
    val name: String,
    val conversationID: String,
    val creator: String,
    var participants: List<String>
)

class EventNetwork(
    var eventID: String? = null,
    var conversationID: String? = null,

    @ServerTimestamp
    var timeStamp: Date?=null, //this timestamp will be the timestamp while sending the message

    var data: String? = null, //the data of message
    var senderID: String? = null, //the senderID of the message
    var receiverID: String? = null, //the receiverID for the message
    var lastUpdatedTimestamp: Date? = null, //contains information about when this event was updated last time

    //Media
    var event_type: Int? = null, //the mime type
    var server_url: String?=null, // the cloud url for the media
){
    fun convertDomainEvent121(type: Int): Event {
        return Event(
            eventID = eventID!!,
            conversationID = senderID!!,
            type = type,
            status = Constants.STATUS_EVENT_RECEIVED,
            needs_push = Constants.NEEDS_PUSH_NO,
            timeStamp = timeStamp,
            serverTimestamp = timeStamp,
            data = data,
            senderID = senderID,
            receiverID = receiverID,
            deleted = Constants.DELETED_NO,
            lastUpdatedTimestamp = timeStamp,
            event_type = Constants.EVENT_TYPE_REMINDER,
            conType = Constants.CONVERSATION_TYPE_121
        )
    }
}
