package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.abhishekjagushte.engage.database.ConversationView
import com.abhishekjagushte.engage.database.MessageView
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.StringFormatting

@BindingAdapter("setTimeString")
fun TextView.setTimeString(message: MessageView){
    val dateTime = message.timeStamp
    dateTime!!.let {
        text = StringFormatting.getTime(it)
    }
}

@BindingAdapter("setSenderName")
fun TextView.setSenderName(message: MessageView){
    text = message.nickname
}

