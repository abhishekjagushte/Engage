package com.abhishekjagushte.engage.database

import androidx.room.*

@Entity
data class Contact(

    @ColumnInfo(name = "network_id")
    var networkID: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var localID: Long = 0L,

    var username: String,
    var name: String,

    @ColumnInfo(defaultValue = "I am using Engage!")
    var bio: String = "I am using Engage!",

//    @ColumnInfo(name="dp_thmb", typeAffinity = ColumnInfo.BLOB)
//    var dp_thmb: String? = null,

    var type: Int
    //status specifications
    //0 for me
    //1 for confirmed
    //2 for pending
    //3 for requested (he/she requested me)
    //4 for unknown

)

@Entity(tableName = "user_data")
data class UserData(

    @PrimaryKey
    val email: String,
    val password: String)