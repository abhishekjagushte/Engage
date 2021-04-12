package com.abhishekjagushte.engage.ui.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.utils.ImageUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import java.io.*


class BottomSheet : BottomSheetDialogFragment() {
    val REQUEST_IMAGE_CAPTURE = 1
    val IMAGE_PICK_CODE = 1000
    private lateinit var tempImageUri: Uri
    private lateinit var pickedImageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)


        val btn_camera: Button = view.findViewById(R.id.btn_camera)
        val btn_gallery: Button = view.findViewById(R.id.btn_gallery)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory().toString() + "//Engage",
            "Temp"
        )

        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }

        val file = File(mediaStorageDir.path + File.separator + "profile_img.jpg")
        tempImageUri = file.toUri()

        btn_camera.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }

        btn_gallery.setOnClickListener {
            Toast.makeText(context, "Gallery", Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
        return view
    }

    private fun saveCroppedImage(croppedImageUri: Uri): Uri {

        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory().toString() + "//Engage",
            "Temp"
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    if (data != null) {
                        pickedImageUri = data.data!!
                        cropProfileImage(pickedImageUri)
                    }
                }

                REQUEST_IMAGE_CAPTURE -> {
                    cropProfileImage(tempImageUri)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val croppedImageUri = CropImage.getActivityResult(data).uri
                    val imageUtil = ImageUtil(requireActivity(), croppedImageUri)

                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "returnedProfileImageFromSlider",
                        Pair(croppedImageUri, imageUtil.resolveBitmap())
                    )
                    findNavController().navigateUp()
                }

                else -> {
                    Log.d(TAG, "Image import failed")
                }
            }
        }
    }

    private fun cropProfileImage(profileImageUri: Uri) {
        val intent =
            CropImage.activity(profileImageUri).setOutputCompressQuality(40).setFixAspectRatio(true).setAutoZoomEnabled(
                false
            )
                .getIntent(
                    requireContext()
                )
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
    }

}