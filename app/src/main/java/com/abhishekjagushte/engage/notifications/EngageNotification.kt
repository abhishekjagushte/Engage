package com.abhishekjagushte.engage.notifications

interface EngageNotification {
    fun makeNotification(title: String, content: String, destination: Int)
}