package com.abhishekjagushte.engage.database.views

import android.graphics.Bitmap
import androidx.room.DatabaseView

@DatabaseView(value =
"""
    SELECT messages.messageID, messages.timeStamp, messages.data, messages.mime_type ,
            messages.senderID AS senderID, messages.conversationID, messages.status, conversations.conversationID AS conID, conversations.name,
         conversations.priority, conversations.dp_thmb, conversations.type AS conType, contacts.nickname 
    FROM 
    messages INNER JOIN conversations ON messages.conversationID = conversations.conversationID
    LEFT OUTER JOIN contacts WHERE contacts.username = senderID
""", viewName = "message_notification_view")
data class MessageNotificationView(
    var messageID: String?,
    var timeStamp: Long?,
    var data: String?,
    var mime_type: String?,
    var senderID: String?,
    var conversationID: String?,
    var status: Int?,
    var name: String?,
    var priority: Int?,
    var dp_thmb: Bitmap?,
    var conType: Int?,
    var nickname: String?
)