package com.abhishekjagushte.engage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.utils.Constants

@Entity(tableName = "messages")
class Message(

    @PrimaryKey
    var messageID: String,
    var conversationID: String,
    var type: Int?, //states whether the message is mine or other's
    var status: Int?, //status of message required for if message read by other party
    var needs_push: Int?, //determines whether push needs to be done if device was offline
    var timeStamp: Long?, //this timestamp will be the timestamp while sending the message
    var serverTimestamp: Long? = null,
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

    var thumb_nail_uri: String?=null, //thumbnail for the media

    var reply_toID: String?=null,

    //@Ignore val name: String?=null, //for other messages

    var conType: Int
){
    fun convertNetworkMessage(): MessageNetwork {
        return MessageNetwork(
            messageID = messageID,
            data = data,
            senderID = senderID,
            receiverID = receiverID,
            mime_type = mime_type,
            server_url = server_url
        )
    }

    //These methods are shifted here from NotificationHandler to keep notificationHandler less crowded
    companion object {

        //{server_url=, thumb_nail=, conversationID=KqU3ipvZEs1bSfFZo4tF,
// messageID=NOJi0osuUGNRYksathbp, latitude=, reply_toID=, mime_type=text/plain,
// data=hio, type=3, timeStamp=1592412297920, longitude=, receiverID=pandaa25, senderID=pandaa24}
        fun mapToMessage121(data: Map<String, String>): Message{
            val messageID = data["messageID"]
            val serverUrl = data["server_url"]
            val thumbNail = data["thumb_nail"]
            val latitude = if((data["latitude"] ?: error("")).isEmpty()) null else (data["latitude"] ?: error("")).toDouble()
            val longitude = if((data["longitude"] ?: error("")).isEmpty()) null else (data["longitude"] ?: error("")).toDouble()
            val replyToid = data["reply_toID"]
            val receiverID = data["receiverID"]
            val senderID = data["senderID"]
            val timeStamp = data["timeStamp"]
            val mdata = data["data"]
            val mime_type = data["mime_type"]

            return Message(
                messageID = messageID!!,
                conversationID = senderID!!, //conversationId for 121 is username
                timeStamp = System.currentTimeMillis(),
                serverTimestamp = timeStamp!!.toLong(),
                data = mdata,
                senderID = senderID,
                receiverID = receiverID,
                mime_type = mime_type,
                server_url = serverUrl,
                latitude = latitude,
                longitude = longitude,
                thumb_nail_uri = null,
                reply_toID = replyToid,

                type = Constants.TYPE_OTHER_MSG,
                status = Constants.STATUS_RECEIVED_BUT_NOT_READ,
                local_uri = null,
                needs_push = Constants.NEEDS_PUSH_NO,
                deleted = Constants.DELETED_NO,
                conType = Constants.CONVERSATION_TYPE_121
            )
        }

        fun mapToMessageM2M(data: Map<String, String>): Message {
            val messageID = data["messageID"]
            val conversationID = data["conversationID"]
            val serverUrl = data["server_url"]
            val thumbNail = data["thumb_nail"]
            val latitude = if((data["latitude"] ?: error("")).isEmpty()) null else (data["latitude"] ?: error("")).toDouble()
            val longitude = if((data["longitude"] ?: error("")).isEmpty()) null else (data["longitude"] ?: error("")).toDouble()
            val replyToid = data["reply_toID"]
            val receiverID = data["receiverID"]
            val senderID = data["senderID"]
            val timeStamp = data["timeStamp"]
            val mdata = data["data"]
            val mime_type = data["mime_type"]

            return Message(
                messageID = messageID!!,
                conversationID = conversationID!!, //conversationId for M2M
                timeStamp = System.currentTimeMillis(),
                serverTimestamp = timeStamp!!.toLong(),
                data = mdata,
                senderID = senderID,
                receiverID = receiverID,
                mime_type = mime_type,
                server_url = serverUrl,
                latitude = latitude,
                longitude = longitude,
                thumb_nail_uri = null,
                reply_toID = replyToid,

                type = Constants.TYPE_OTHER_MSG,
                status = Constants.STATUS_RECEIVED_BUT_NOT_READ,
                local_uri = null,
                needs_push = Constants.NEEDS_PUSH_NO,
                deleted = Constants.DELETED_NO,
                conType = Constants.CONVERSATION_TYPE_M2M
            )
        }
    }

}