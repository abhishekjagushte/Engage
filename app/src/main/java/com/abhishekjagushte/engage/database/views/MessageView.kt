package com.abhishekjagushte.engage.database.views

import androidx.room.DatabaseView

@DatabaseView(value =
"""
    SELECT contacts.nickname, messages.messageID, messages.conversationID ,messages.type, messages.conType, messages.status,
    messages.timeStamp, messages.data, messages.senderID, messages.receiverID, messages.deleted, messages.mime_type, 
    messages.server_url, messages.local_uri, messages.latitude, messages.longitude,
    messages.reply_toID FROM messages LEFT JOIN contacts ON messages.senderID = contacts.username
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
    var latitude: Double?,
    var longitude: Double?,
    var reply_toID: String?
)