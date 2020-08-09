package com.abhishekjagushte.engage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggested_contacts")
data class SuggestedContacts(
    @PrimaryKey
    var username: String,
    var name: String,
    var score: Int
)