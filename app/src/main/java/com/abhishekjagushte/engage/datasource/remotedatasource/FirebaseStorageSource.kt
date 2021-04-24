package com.abhishekjagushte.engage.datasource.remotedatasource

import android.app.Application
import android.net.Uri
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.datasource.localdatasource.LocalDataSource
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.FilePathContract
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val TYPE_IMAGE = Constants.MIME_TYPE_IMAGE_JPG
const val TYPE_VIDEO = ""//TODO
const val TYPE_DOCUMENT = "s"//TODO

class FirebaseStorageSource @Inject constructor(
    private val storage: StorageReference,
    private val localDataSource: LocalDataSource,
    private val application: Application
) {

    private val job: Job = Job()
    private val storageScope = CoroutineScope(Dispatchers.Main + job)


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

    fun uploadImage(message: Message, metadata: StorageMetadata, path: String): UploadTask {
        val ref = storage.child(path)
        val task = ref.putFile(Uri.parse(message.local_uri), metadata)
        return task
    }

    fun downloadImage(path: String, uri: Uri): FileDownloadTask {
        val ref = storage.child(path)
        return ref.getFile(uri)
    }

    fun downloadImageThumbnail(path: String, uri: Uri): FileDownloadTask {
        val ref = storage.child(path)
        return ref.getFile(uri)
    }

    fun setProfilePicture(profilePictureUri: Uri, path: String): UploadTask {
        val ref = storage.child(path)
        return ref.putFile(profilePictureUri, StorageMetadata.Builder()
            .setContentType("image/jpg").build())
    }

    suspend fun updateProfilePhotoThumbnail(username: String, timestamp: Long): FileDownloadTask? {
        val path = Constants.PROFILE_PHOTO_PATH_CLOUD + Constants.THUMBNAIL_PREFIX + username + ".jpg"
        val ref = storage.child(path)

        try {
            val metadata = ref.metadata.await()
            metadata?.let {
                if(it.updatedTimeMillis>timestamp){
                    localDataSource.updateContactTimestamp(System.currentTimeMillis(), username)
                    val uri = FilePathContract.getContactsProfilePhotoUri(username)
                    return ref.getFile(uri)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

}