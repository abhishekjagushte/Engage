package com.abhishekjagushte.engage.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.*

class Converters {

    private val TAG="Converters"

    @TypeConverter
    fun fromBitMap(bmp: Bitmap?): ByteArray?{
        if(bmp == null)
            return null
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitMap(byteArray: ByteArray?): Bitmap?{
        if(byteArray==null)
            return null

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    @TypeConverter
    fun fromLong(timestamp: Long?): Date?{
        if (timestamp==null)
            return null
        else
            return Date(timestamp)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long?{
        if (date==null)
            return null
        else
            return date.time
    }

}