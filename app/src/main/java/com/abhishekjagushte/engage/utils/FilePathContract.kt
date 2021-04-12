package com.abhishekjagushte.engage.utils

import android.net.Uri
import android.os.Environment
import java.io.File
import java.lang.IllegalStateException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

object FilePathContract {

    fun getImageReceivedPathUri(time: Long): Uri {
        val strgstate = Environment.getExternalStorageState()
        if (strgstate == Environment.MEDIA_MOUNTED) {
            val root = Environment.getExternalStorageDirectory().path
            val storageDirectory = File(root + Constants.IMAGE_RECEIVED_STORAGE_PATH)

            if (!storageDirectory.exists())
                storageDirectory.mkdirs()

            val file = File(
                storageDirectory, "ENG_IMG_${
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                        Date(time)
                    )
                }" + ".jpg"
            )

            return Uri.parse(file.toURI().toString())!!
        }
        else
            throw IllegalStateException("Media not mounted")
    }


    fun getProfilePicturePath(): Uri {
        val strgstate = Environment.getExternalStorageState()
        if (strgstate == Environment.MEDIA_MOUNTED) {
            val root = Environment.getExternalStorageDirectory().path
            val storageDirectory = File(root + Constants.PROFILE_PHOTO_LOCAL_STORAGE_PATH)

            if (!storageDirectory.exists())
                storageDirectory.mkdirs()

            val file = File(
                storageDirectory, Constants.PROFILE_PHOTO_FILENAME)

            return Uri.parse(file.toURI().toString())!!
        }
        else
            throw IllegalStateException("Media not mounted")
    }

    fun getContactsProfilePhotoUri(username: String): Uri{
        val strgstate = Environment.getExternalStorageState()
        if (strgstate == Environment.MEDIA_MOUNTED) {
            val root = Environment.getExternalStorageDirectory().path
            val storageDirectory = File(root + Constants.PROFILE_PHOTO_PATH_OTHERS_LOACL)

            if (!storageDirectory.exists())
                storageDirectory.mkdirs()

            val file = File(storageDirectory, username)
            return Uri.parse(file.toURI().toString())!!
        }
        else
            throw IllegalStateException("Media not mounted")
    }

}