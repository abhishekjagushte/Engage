package com.abhishekjagushte.engage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserData(

    @PrimaryKey
    var email: String,
    var password: String
)