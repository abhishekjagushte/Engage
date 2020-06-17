package com.abhishekjagushte.engage.database

import android.util.Log
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Converters {

    private val TAG="Converters"

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        val l =  LocalDateTime.parse(value)
        Log.d(TAG, "toLocalDateTime: ${l.atZone(ZoneId.systemDefault()).toString()}")
        return l
    }

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
        return localDateTime.toString()
    }
}