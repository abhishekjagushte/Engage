package com.abhishekjagushte.engage.database.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


//Planning to keep this same object as the EventNetwork Object
@Entity(tableName = "events")
class Event (
    @PrimaryKey
    var eventID: String,
    var conversationID: String,
    var type: Int?, //states whether the message is mine or other's
    var status: Int?, //status of message required for if message read by other party
    var needs_push: Int?, //determines whether push needs to be done if device was offline
    var timeStamp: Date?, //this timestamp will be the timestamp while sending the message
    var serverTimestamp: Date? = null,
    var data: String?, //the data of message
    var senderID: String?, //the senderID of the message
    var receiverID: String?, //the receiverID for the message
    var deleted: Int = 0, //if 1 then the message is deleted else 0 or null
    var lastUpdatedTimestamp: Date?, //contains information about when this event was updated last time

    //Media
    var event_type: String?, //the mime type
    var server_url: String?=null, // the cloud url for the media
    var local_uri: String?=null, //local uri (the file path) for the media

    var conType: Int
)