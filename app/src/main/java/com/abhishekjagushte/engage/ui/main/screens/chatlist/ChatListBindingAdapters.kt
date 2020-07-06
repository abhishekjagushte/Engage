package com.abhishekjagushte.engage.ui.main.screens.chatlist

import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.abhishekjagushte.engage.database.ConversationView
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.StringFormatting
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("setMessageDataString")
fun TextView.setMessageDataString(conversationView: ConversationView){
    var string = ""
    when(conversationView.mime_type){
        Constants.MIME_TYPE_TEXT -> string+=conversationView.data
    }
    text = string
}

@BindingAdapter("setMessageTime")
fun TextView.setMessageTime(conversationView: ConversationView){
    val dateTime = conversationView.timeStamp
    dateTime?.let {
        text = StringFormatting.getTime(it)
    }
}

@BindingAdapter("setChatName")
fun TextView.setChatName(conversationView: ConversationView){
    Log.d("SetChatNameAdapter", "setChatName: ${conversationView.name}  ${conversationView.nickname}, type = ${conversationView.type}")
    when(conversationView.conType){
        Constants.CONVERSATION_TYPE_121 -> text = conversationView.nickname
        Constants.CONVERSATION_TYPE_M2M -> text = conversationView.name
    }
}