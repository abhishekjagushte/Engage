package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.chatscreen.features.attachment.images

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.models.ConversationInfo
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.android.synthetic.main.fragment_image_preview.*

class ImagePreviewFragment : Fragment(R.layout.fragment_image_preview) {

    private lateinit var conversationInfo: ConversationInfo
    private val TAG = "ImagePreviewFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageByteArray = arguments?.getByteArray(Constants.IMAGE_KEY)
        conversationInfo = arguments?.getParcelable<ConversationInfo>(Constants.CONVERSATION_INFO_KEY)!!

        Log.d(TAG, "onViewCreated: $conversationInfo")

        val imageToDisplay = BitmapFactory.decodeByteArray(imageByteArray,0, imageByteArray?.size!!)
        imageView.setImageBitmap(imageToDisplay)
    }
}