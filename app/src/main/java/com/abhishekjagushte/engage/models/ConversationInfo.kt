package com.abhishekjagushte.engage.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConversationInfo(
    var conversationID: String,
    var conType: Int,
    var receiverID: String?,

    //senderID will be mine always

    //In case reply to is there
    var replyToMessageID: String?,
    var ChatState: String
): Parcelable