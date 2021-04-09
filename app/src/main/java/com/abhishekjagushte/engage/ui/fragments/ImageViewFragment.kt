package com.abhishekjagushte.engage.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.main.screens.profile.ProfileFragmentArgs
import com.abhishekjagushte.engage.utils.ImageUtil
import kotlinx.android.synthetic.main.fragment_image_view.*


class ImageViewFragment : Fragment(R.layout.fragment_image_view) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args by navArgs<ImageViewFragmentArgs>()
        val name = args.chatName
        val uri = Uri.parse(args.imageUri)
        val messageTime = args.messageTime
        
        val imageUtil = ImageUtil(requireActivity(), uri)
        imageView.setImageBitmap(imageUtil.resolveBitmap())
    }
}