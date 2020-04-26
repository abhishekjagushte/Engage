package com.abhishekjagushte.engage.network

import com.abhishekjagushte.engage.database.Contact
import java.util.*

data class Profile(
    val id: String,
    val name: String,
    val username: String,
    val bio: String = "I am using Engage!",
    val dp_thmb: String? = "",
    val dp_url: String? = "",
    val device_type: Int = 1,
    val joinTimeStamp: Date,
    val notificationChannelID: String,
    val contacts: List<String> = listOf<String>(),
    val pending: List<String> = listOf<String>(),
    val requested: List<String> = listOf<String>(),
    val suggested: List<String> = listOf<String>()
)

fun Profile.convertDomainObject(type: Int): Contact{
    return Contact(
        networkID = this.id,
        name = this.name,
        username = this.username,
        bio = this.bio,
        type = type)
}