package com.abhishekjagushte.engage.database.views

import androidx.room.DatabaseView
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.utils.Constants

@DatabaseView(value =
"""
    SELECT contacts.nickname, messages.messageID, messages.conversationID ,messages.type, messages.conType, messages.status,
    messages.timeStamp, messages.data, messages.senderID, messages.receiverID, messages.deleted, messages.mime_type, 
    messages.server_url, messages.local_uri, messages.latitude, messages.longitude,
    messages.reply_toID, messages.thumb_nail_uri FROM messages LEFT JOIN contacts ON messages.senderID = contacts.username
    """
    , viewName = "message_view")
data class MessageView(
    var nickname: String?,
    var messageID: String,
    var conversationID: String,
    var type: Int?,
    var conType: Int?,
    var status: Int,
    var timeStamp: Long?,
    var data: String?,
    var senderID: String?,
    var receiverID: String?,
    var deleted: Int,
    var mime_type: String?,
    var server_url: String?,
    var local_uri: String?,
    var thumb_nail_uri: String?,
    var latitude: Double?,
    var longitude: Double?,
    var reply_toID: String?
){
    fun convertDomainMessage(): Message{
        return Message(
            messageID = messageID,
            conversationID = conversationID,
            type = type,
            conType = conType!!,
            status = status,
            timeStamp = timeStamp,
            data = data,
            senderID = senderID,
            receiverID = receiverID,
            deleted = deleted,
            mime_type = mime_type,
            server_url = server_url,
            local_uri = local_uri,
            latitude = latitude,
            longitude = longitude,
            reply_toID = reply_toID,
            needs_push = Constants.NEEDS_PUSH_NO
        )
    }
}