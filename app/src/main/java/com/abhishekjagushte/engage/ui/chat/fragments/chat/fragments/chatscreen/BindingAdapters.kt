package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.abhishekjagushte.engage.database.Message
import com.abhishekjagushte.engage.database.MessageView
import com.abhishekjagushte.engage.utils.StringFormatting
import java.util.*

@BindingAdapter("setTimeString")
fun TextView.setTimeString(message: Message){
    val localDateTime = message.timeStamp
    localDateTime!!.let {
        text = StringFormatting.getTime(it)
    }
}

@BindingAdapter("setSenderName")
fun TextView.setSenderName(message: Message){
    text = ""
}

