package com.abhishekjagushte.engage.datasource.remotedatasource.uploadmanager

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.datasource.remotedatasource.TYPE_DOCUMENT
import com.abhishekjagushte.engage.datasource.remotedatasource.TYPE_IMAGE
import com.abhishekjagushte.engage.datasource.remotedatasource.TYPE_VIDEO
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2

class MediaUploader constructor(
    private val message: Message,
    private val dataRepository: DataRepository,
    private val imageProperties: ImageProperties
){

    private val TAG = "MediaUploader"

    val progress: LiveData<Double>
        get() = _progress

    private val _progress: MutableLiveData<Double> = MutableLiveData()

    private fun createPathM2M(fileName: String, message: Message): String {
        return when(message.mime_type){
            TYPE_IMAGE -> "groups/${message.conversationID}/images/$fileName"
            TYPE_VIDEO -> "groups/${message.conversationID}/videos/$fileName"
            TYPE_DOCUMENT -> "groups/${message.conversationID}/documents/$fileName"
            else -> throw IllegalStateException("Storage upload type  incorrect")
        }
    }

    private fun createPath121(fileName: String, message: Message): String {
        return when(message.mime_type){
            TYPE_IMAGE -> "media121/images/$fileName"
            TYPE_VIDEO -> "media121/videos/$fileName"
            TYPE_DOCUMENT -> "media121/documents/$fileName"
            else -> throw IllegalStateException("Storage upload type  incorrect")
        }
    }

    private fun createImageFileName(message: Message): String {
        return "ENG_IMG_${message.timeStamp}.jpg"
    }

    private fun buildMetadata(): StorageMetadata{
        return StorageMetadata.Builder()
            .setContentType("image/jpg")
            .setCustomMetadata("height", "${imageProperties.height}")
            .setCustomMetadata("width", "${imageProperties.width}")
            .build()
    }

    fun start(){
        val fileName = createImageFileName(message)
        val path = createPath121(fileName, message)
        val metadata = buildMetadata()
        val uploader = dataRepository.uploadImageStorageSource(message, metadata, path)

        uploader.addOnCompleteListener {
            if(it.isSuccessful)
                UploadManager.removeUploader(message)
        }

        uploader.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100.0 * bytesTransferred) / totalByteCount
            _progress.value = progress
            Log.d(TAG, "Upload is $progress% done")
        }.addOnPausedListener {
            Log.d(TAG, "Upload is paused")
        }

        uploader.addOnCompleteListener {
            if(it.isSuccessful){
                message.server_url = path
                dataRepository.updateImageSent(message)
            }
        }

        UploadManager.addUploader(message, this)
    }

    class ImageProperties(
        val height: Int, val width: Int)

}