package com.abhishekjagushte.engage.database.views

import android.graphics.Bitmap
import androidx.room.DatabaseView

@DatabaseView(value =
"""
    SELECT events.eventID, events.timeStamp, events.data, events.event_type ,
            events.senderID AS senderID, events.conversationID, events.status, conversations.conversationID AS conID, conversations.name,
         conversations.priority, conversations.dp_thmb, conversations.type AS conType, contacts.nickname 
    FROM 
    events INNER JOIN conversations ON events.conversationID = conversations.conversationID
    LEFT OUTER JOIN contacts WHERE contacts.username = senderID
""", viewName = "event_notification_view")
data class EventNotificationView(
    var eventID: String?,
    var timeStamp: Long?,
    var data: String?,
    var event_type: String?,
    var senderID: String?,
    var conversationID: String?,
    var status: Int?,
    var conID: String,
    var name: String?,
    var priority: Int?,
    var dp_thmb: Bitmap?,
    var conType: Int?,
    var nickname: String?
)