package com.abhishekjagushte.engage.datasource.remotedatasource

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.datasource.remotedatasource.uploadmanager.MediaUploader
import com.abhishekjagushte.engage.datasource.remotedatasource.uploadmanager.UploadManager
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val TYPE_IMAGE = Constants.MIME_TYPE_IMAGE_JPG
const val TYPE_VIDEO = ""//TODO
const val TYPE_DOCUMENT = "s"//TODO

class FirebaseStorageSource @Inject constructor(
    private val storage: StorageReference,
    private val localDataSource: LocalDataSource
) {

    private val TAG = "FirebaseStorageSource"

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

    private fun createImageFileName(message: Message): String{
        return "ENG_IMG_${message.timeStamp}.jpg"
    }

    fun uploadImage121(message: Message) {
        val fileName = createImageFileName(message)
        val path = createPath121(fileName, message)
        val uri = Uri.parse(message.local_uri)
        val ref = storage.child(path)
        val task = ref.putFile(uri)
        val uploader = MediaUploader(message, task)
        UploadManager.addUploader(message, uploader)

        task.addOnCompleteListener {
            if(it.isSuccessful){
                message.server_url = path
                localDataSource.pushMessage(message)
            }
        }
    }

}