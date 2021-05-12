package com.abhishekjagushte.engage.ui.main.screens.settings

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.FilePathContract
import com.abhishekjagushte.engage.utils.ImageUtil
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var profileImageUri: Uri? = null
    private var TAG = "SettingsFragment"

    @Inject
    lateinit var dataRepository: DataRepository


    private var imageChanged: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uri = FilePathContract.getProfilePicturePath()
        if (uri != Uri.EMPTY)
            profile_image.setImageURI(uri)

        change_profile_image.setOnClickListener {
            findNavController().navigate(R.id.action_global_bottomSheet2)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Pair<Uri?, Bitmap?>>(
            "returnedProfileImageFromSlider"
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            result.second?.let {
                profileImageUri = result.first
                profile_image.setImageBitmap(it)
                imageChanged = true
            }
        }

        val handler = Handler(Looper.myLooper()!!)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val myDetails = dataRepository.getMydetails()
                myDetails?.let {
                    handler.post {
                        bio_input.setText(it.bio)
                    }
                }
            }
        }

        doneFAB.setOnClickListener {
            val bio = bio_input.text.toString()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        handler.post {
                            progressBar2.visibility = View.VISIBLE
                        }

                        if (bio != dataRepository.getMydetails()!!.bio)
                            dataRepository.updateBioCloud(bio).addOnSuccessListener {

                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        dataRepository.updateMyBioLocal(bio)

                                        if (imageChanged)
                                            updateImage(handler)
                                        else {
                                            handler.post {
                                                progressBar2.visibility = View.GONE
                                                findNavController().navigateUp()
                                            }
                                        }
                                    }
                                }

                            }
                        else
                            updateImage(handler)
                    }
            }
        }
    }

    private suspend fun updateImage(handler: Handler) {
        dataRepository.setProfilePicture(profileImageUri!!)
            .addOnSuccessListener {
                saveFinalImage(profileImageUri!!)
                handler.post {
                    progressBar2.visibility = View.GONE
                    findNavController().navigateUp()
                }
            }
    }

    private fun saveFinalImage(croppedImageUri: Uri): Uri {
        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory().toString() + "//Engage",
            "dp"
        )
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        val file = File(mediaStorageDir.path + File.separator + "profile_img.jpg")

        val imageUtil = ImageUtil(requireActivity(), croppedImageUri)
        val bitmap = imageUtil.resolveBitmap()

        val stream: OutputStream = FileOutputStream(file)
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, stream)
        stream.flush()
        stream.close()
        return file.toUri()
    }
}