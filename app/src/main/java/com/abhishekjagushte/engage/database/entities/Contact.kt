package com.abhishekjagushte.engage.database.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
class Contact(

    @ColumnInfo(name = "network_id")
    var networkID: String,

    @PrimaryKey
    var username: String,

    var name: String,
    var nickname: String = name,

    @ColumnInfo(defaultValue = "I am using Engage!")
    var bio: String = "I am using Engage!",

    var dp_thmb_url:String? = null,
    var type: Int,

    var dp_timeStamp: Long? = null

    //conversation ID removed from the contacts
){
    @Ignore
    var selected: Boolean? = false
}

data class ContactDetails(
    var name: String,
    var username: String,
    var bio: String = ""
)

data class SearchResultContact(
    var name: String,
    var username: String
)