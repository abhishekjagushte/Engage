package com.abhishekjagushte.engage.database.views

import android.graphics.Bitmap
import androidx.room.DatabaseView

@DatabaseView("""
    SELECT conversations.name, conversations.dp_thmb, conversations.dp_url,
        conversations.conversationID, conversations.type AS conType,  messages.messageID, 
        messages.timeStamp, messages.data, messages.mime_type ,messages.type,
        messages.status, messages.senderID, contacts.nickname, contacts.dp_timeStamp , contacts.dp_thmb_url FROM conversations
        LEFT JOIN messages ON conversations.lastMessageID == messages.messageID
        LEFT JOIN contacts ON conversations.conversationID == contacts.username
""", viewName = "conversation_view")
data class ConversationView(
    var name: String?,
    var dp_thmb: Bitmap?, //dp_thmb will work only for M2M chats
    var dp_url: String?,
    var conversationID: String,
    var conType: Int?,
    var messageID: String?,
    var timeStamp: Long?,
    var dp_timeStamp: Long?,
    var dp_thmb_url: String?, //This will work only for 121 chats
    var data: String?,
    var mime_type: String?,
    var type: Int?,
    var status: Int?,
    var senderID: String?,
    var nickname: String?
)
