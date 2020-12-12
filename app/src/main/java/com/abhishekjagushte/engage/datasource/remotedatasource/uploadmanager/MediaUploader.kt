package com.abhishekjagushte.engage.datasource.remotedatasource.uploadmanager

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.entities.Message
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.component1

class MediaUploader constructor(
    private val message: Message,
    private val uploader: UploadTask
){

    private val TAG = "MediaUploader"

    val progress: LiveData<Double>
        get() = _progress

    private val _progress: MutableLiveData<Double> = MutableLiveData()


    init {
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
    }

}