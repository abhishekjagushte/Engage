package com.abhishekjagushte.engage.utils

import android.net.Uri
import android.util.Log
import com.abhishekjagushte.engage.repository.DataRepository
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*

class DisplayPictureUtil(
    private val dataRepository: DataRepository
) {

    val TAG = "DisplayPictureUtil"
    val job = Job()
    val scope = CoroutineScope(job + Dispatchers.Main)

    fun setProfilePhoto121(username: String, circleImageView: CircleImageView){
        setPhoto(username, circleImageView)
        //updateProfilePhoto121(username, circleImageView)
    }

    private fun setPhoto(
        username: String,
        circleImageView: CircleImageView
    ) {
        val profilePicUri =
            FilePathContract.getContactsProfilePhotoUri(username) //conversationID is username
        //if (profilePicUri != Uri.EMPTY){
            circleImageView.setImageURI(profilePicUri)
          //  Log.e(TAG, "setPhoto: URI empty", )
        //}
    }

    //TODO(Fix this)

    private fun updateProfilePhoto121(username: String, circleImageView: CircleImageView) {
        scope.launch {
            withContext(Dispatchers.IO){
            try {
                dataRepository.updateProfilePhotoThumbnailTask(username)?.addOnSuccessListener {
                    Log.e("****************", "updateProfilePhoto121: here", )
                    setPhoto(username, circleImageView)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            }
        }
    }

}