package com.abhishekjagushte.engage.ui.main.screens.search

import com.abhishekjagushte.engage.database.SearchResultContact
import com.abhishekjagushte.engage.database.SearchResultConversation
import com.abhishekjagushte.engage.utils.Constants

data class SearchData constructor(
    val title: String,
    val dp_thmb: ByteArray? = null,
    val subtitle: String,
    val type: Int,
    val flag: Int=-1,
    val rhsText: String = "",

    val extras: Map<String, Any?> = mapOf<String, Any?>() //to store extra information for when entering the next screen
    //Anything that doesn't need to be displayed will go in extras
    //Overriding just in case the dp gets updated while searching
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchData

        if (title != other.title) return false
        if (dp_thmb != null) {
            if (other.dp_thmb == null) return false
            if (!dp_thmb.contentEquals(other.dp_thmb)) return false
        } else if (other.dp_thmb != null) return false
        if (subtitle != other.subtitle) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (dp_thmb?.contentHashCode() ?: 0)
        result = 31 * result + subtitle.hashCode()
        result = 31 * result + type
        return result
    }
}

fun SearchData.toSearchResultContact(): SearchResultContact{
    return SearchResultContact(
        name = this.title,
        username = this.subtitle
    )
}

fun SearchData.toSearchResultConversation(): SearchResultConversation{
    return SearchResultConversation(
        name = this.title,
        //status = this.flag,
        conversationID = this.extras.get("networkID") as String,
        type = this.extras.get("conversation_type") as Int
    )
}


fun List<SearchResultContact>.convertSearchDataContacts(): List<SearchData>{
    return this.map{
        SearchData(
            title = it.name,
            subtitle = it.username,
            type = Constants.SEARCHDATA_CONTACT
            //TODO Implement flags and rhs text
        )
    }
}

fun List<SearchResultConversation>.convertSearchDataConversations(): List<SearchData>{
    return this.map{
        SearchData(
            title = it.name,
            //TODO implement messages - last message
            subtitle = "",
            type = Constants.SEARCHDATA_CONVERSATION, //This is type of searchdata
            //TODO implement flags and rhs text
            //flag = it.status,

            extras = mapOf(
                "networkID" to it.conversationID,
                "conversation_type" to it.type
            )
        )
    }
}


