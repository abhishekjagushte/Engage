package com.abhishekjagushte.engage.datasource.remotedatasource.downloadmanager

import com.abhishekjagushte.engage.database.entities.Message

object DownloadManager {

    private val downloads: HashMap<String, MediaDownloader> = HashMap()

    fun removeDownloader(message: Message){
        downloads.remove(message.messageID)
    }

    fun getDownloader(messageID: String): MediaDownloader? {
        return downloads.get(messageID)
    }

    fun addDownloader(message: Message, uploader: MediaDownloader){
        downloads.put(message.messageID, uploader)
    }

}