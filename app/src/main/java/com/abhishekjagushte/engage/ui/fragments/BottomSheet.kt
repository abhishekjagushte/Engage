package com.abhishekjagushte.engage.ui.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.ui.activity.MainActivity
import com.abhishekjagushte.engage.ui.setup.screens.setusername.SetUsernameFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import java.io.File


class BottomSheet : BottomSheetDialogFragment() {
    val REQUEST_IMAGE_CAPTURE = 1
    val IMAGE_PICK_CODE = 1000
    private lateinit var profileImageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)


        val btn_camera: Button = view.findViewById(R.id.btn_camera)
        val btn_gallery: Button = view.findViewById(R.id.btn_gallery)

        btn_camera.setOnClickListener {
            Toast.makeText(context, "Camera", Toast.LENGTH_SHORT).show()
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val mediaStorageDir = File(
                Environment.getExternalStorageDirectory().toString() + "//Engage",
                "Profile_pictures"
            )

            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }

            profileImageUri =
                Uri.fromFile(File(mediaStorageDir.getPath() + File.separator + "profile_img.jpg"));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                                    if(data!=null)
                                    {
                                        profileImageUri = data.data!!
                                        cropProfileImage(profileImageUri)
                                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                                        if (profileImageUri != null) cropProfileImage(profileImageUri)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                                    profileImageUri = CropImage.getActivityResult(data).uri
                                    findNavController().previousBackStackEntry?.savedStateHandle?.set("returnedProfileImageFromSlider", profileImageUri)
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
            CropImage.activity(profileImageUri).setFixAspectRatio(true).setAutoZoomEnabled(false)
                .getIntent(
                    requireContext()
                )
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
    }

}