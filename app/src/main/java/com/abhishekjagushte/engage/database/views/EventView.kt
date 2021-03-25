package com.abhishekjagushte.engage.database.views

import androidx.room.DatabaseView

@DatabaseView(value =
"""
    SELECT contacts.nickname, events.eventID, events.conversationID ,events.type, events.conType, events.status,
    events.timeStamp, events.data, events.senderID, events.receiverID, events.deleted, events.event_type, 
    events.server_url, events.local_uri FROM events LEFT JOIN contacts ON events.senderID = contacts.username
    """
    , viewName = "events_view")
class EventView(
    var nickname: String?,
    var eventID: String,
    var conversationID: String,
    var type: Int?,
    var conType: Int?,
    var status: Int,
    var timeStamp: Long?,
    var data: String?,
    var senderID: String?,
    var receiverID: String?,
    var deleted: Int,
    var event_type: Int?,
    var server_url: String?,
    var local_uri: String?
)