package com.abhishekjagushte.engage.ui.main.fragments.search

import com.abhishekjagushte.engage.database.SearchResultContact
import com.abhishekjagushte.engage.database.SearchResultConversation
import com.abhishekjagushte.engage.database.SuggestedContacts

data class SearchData constructor(
    val title: String,
    val dp_thmb: ByteArray? = null,
    val subtitle: String

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

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (dp_thmb?.contentHashCode() ?: 0)
        result = 31 * result + subtitle.hashCode()
        return result
    }
}

fun List<SearchResultContact>.convertSearchDataContacts(): List<SearchData>{
    return this.map{
        SearchData(
            title = it.name,
            subtitle = it.username
        )
    }
}

fun List<SearchResultConversation>.convertSearchDataConversations(): List<SearchData>{
    return this.map{
        SearchData(
            title = it.name,
            //TODO implement messages - last message
            subtitle = ""
        )
    }
}




