package com.abhishekjagushte.engage.database.views

import android.graphics.Bitmap
import androidx.room.DatabaseView

@DatabaseView("""
    SELECT conversations.name, conversations.dp_thmb, conversations.dp_url,
        conversations.conversationID, conversations.type AS conType,  messages.messageID, 
        messages.timeStamp, messages.data, messages.mime_type ,messages.type,
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
