package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.abhishekjagushte.engage.database.MessageView

@BindingAdapter("setTimeString")
fun TextView.setTimeString(message: MessageView){
    val localDateTime = message.timeStamp
    localDateTime!!.let {
        text = "${it.hour} : ${it.minute}"
    }
}