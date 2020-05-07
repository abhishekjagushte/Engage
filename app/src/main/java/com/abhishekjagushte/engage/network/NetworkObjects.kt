package com.abhishekjagushte.engage.network

import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.utils.Constants
import java.util.*

data class Profile constructor(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val bio: String = "I am using Engage!",
    val dp_thmb: String? = "",
    val dp_url: String? = "",
    val device_type: Int = 1,
    val joinTimeStamp: Date? = null,
    val notificationChannelID: String = ""
)

fun Profile.convertDomainObject(type: Int): Contact{
    return Contact(
        networkID = this.id,
        name = this.name,
        username = this.username,
        bio = this.bio,
        type = type)
}