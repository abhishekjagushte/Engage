package com.abhishekjagushte.engage.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
        return localDateTime.toString()
    }
}