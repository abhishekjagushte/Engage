package com.abhishekjagushte.engage.datasource.remotedatasource.downloadmanager

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.FilePathContract
import java.lang.IllegalStateException

class MediaDownloader(
    private val message: Message,
    private val dataRepository: DataRepository
){

    private val TAG = "MediaDownloader"

    private val _progress = MutableLiveData<Double>()
    val progress: LiveData<Double>
        get() = _progress

    fun start(): Message {
        when(message.mime_type){
            Constants.MIME_TYPE_IMAGE_JPG -> {
                val uri = FilePathContract.getImageReceivedPathUri(message.timeStamp!!)
                message.local_uri = uri.toString()
                Log.d(TAG, "start: $uri")
                val task = dataRepository.downloadImage(message, uri)
                registerDownloader()
                task.addOnProgressListener {
                    _progress.value = (100.0 * it.bytesTransferred) / it.totalByteCount
                }

                return message
            }
            else -> throw IllegalStateException("DownloadManager mime type not defined")
        }
    }

    private fun registerDownloader(){
        DownloadManager.addDownloader(message, this)
    }

}