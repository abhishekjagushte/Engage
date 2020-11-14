package com.abhishekjagushte.engage.utils

import android.app.Activity
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.lang.IllegalStateException

class ImageUtil (
    private val activity: Activity,
    private val uri: Uri?
){
    private var image: Bitmap?=null
    private val TAG = "ImageUtil"

    private fun getBitmapLegacy(uri: Uri, contentResolver: ContentResolver): Bitmap {
        return MediaStore.Images.Media.getBitmap(contentResolver, uri)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getBitmap(uri: Uri, contentResolver: ContentResolver): Bitmap {
        val source = ImageDecoder.createSource(contentResolver, uri)
        return ImageDecoder.decodeBitmap(source)
    }

    fun resolveBitmap(){
        val contentResolver = activity.contentResolver!!
        if(uri!=null){
            Log.d(TAG, "resolveBitmap: URI is $uri")
            if(Build.VERSION.SDK_INT < 28) {
                image = getBitmapLegacy(uri, contentResolver)
            }
            else{
                image = getBitmap(uri, contentResolver)
            }
        }
        else{
            throw IllegalStateException("Image Uri is null inside BitmapResolver")
        }
    }

    fun getCompressedImageByteArray(): ByteArray{
        val bytes = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 40, bytes)
        val byteArray = bytes.toByteArray()
        bytes.flush()
        bytes.close()
        return byteArray
    }
}