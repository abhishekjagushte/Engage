package com.abhishekjagushte.engage.database.entities.jsonmodels

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Reminder(
    val title: String,
    val description: String,
    var status: Int,
    val createdTime: Long,
    val reminderTime: Long,
    val timeOffset: Long?
)