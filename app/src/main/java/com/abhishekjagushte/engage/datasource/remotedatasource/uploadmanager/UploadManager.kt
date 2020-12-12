package com.abhishekjagushte.engage.datasource.remotedatasource.uploadmanager

import com.abhishekjagushte.engage.database.entities.Message


object UploadManager {
    private val uploads: HashMap<String, MediaUploader> = HashMap()

    fun removeUploader(message: Message){
        uploads.remove(message.messageID)
    }

    fun getUploader(messageID: String): MediaUploader? {
        return uploads.get(messageID)
    }

    fun addUploader(message: Message, uploader: MediaUploader){
        uploads.put(message.messageID, uploader)
    }
}