package com.abhishekjagushte.engage.utils

import android.icu.text.SimpleDateFormat
import java.util.*
import android.icu.util.TimeZone

class StringFormatting {
    companion object{
        fun getTime(millis: Long): String{
            val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            sdf.setTimeZone(TimeZone.getDefault())
            return sdf.format(millis)
        }

        fun getDateTimeString(millis: Long): String{
            val sdf = SimpleDateFormat("E, dd MMM yyyy HH:mm", Locale.ENGLISH)
            sdf.setTimeZone(TimeZone.getDefault())
            return sdf.format(millis)

        }

//        public fun getDate(millis: Long): String{
//            val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
//            sdf.setTimeZone(TimeZone.getDefault())
//            return sdf.format(millis)
//
//        }

    }
}