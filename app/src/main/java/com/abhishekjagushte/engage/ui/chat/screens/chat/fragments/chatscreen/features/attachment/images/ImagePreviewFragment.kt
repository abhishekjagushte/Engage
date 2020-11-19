package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.chatscreen.features.attachment.images

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.models.ConversationInfo
import com.abhishekjagushte.engage.permissions.WriteExternalStoragePermissionHelper
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.ImageUtil
import kotlinx.android.synthetic.main.fragment_image_preview.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ImagePreviewFragment : Fragment(R.layout.fragment_image_preview) {

    private lateinit var conversationInfo: ConversationInfo
    private val TAG = "ImagePreviewFragment"
    private lateinit var imageToDisplay: Bitmap

    private val job: Job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main+job)

    @Inject
    lateinit var dataRepository: DataRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = arguments?.getString(Constants.IMAGE_URI_KEY)?.toUri()
        val imageUtil = ImageUtil(requireActivity(), uri)
        imageToDisplay = imageUtil.resolveBitmap()!!

        conversationInfo = arguments?.getParcelable(Constants.CONVERSATION_INFO_KEY)!!

        imageView.setImageBitmap(imageToDisplay)

        image_preview_fab.setOnClickListener(fabListener)
    }

    private val fabListener = View.OnClickListener {
        if (WriteExternalStoragePermissionHelper(
                requireActivity(),
                requireContext(),
                Constants.WRITE_PERMISSION_REQUEST_CODE
            ).permissionsForSave()
        ) {
            initiateSending()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Toast.makeText(getActivity(), "Code${requestCode.toString()}", Toast.LENGTH_SHORT).show()
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateSending()
            } else {
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initiateSending() {
        val time = System.currentTimeMillis()
        val uri = saveImage(time)
        if (uri == null)
            Toast.makeText(requireActivity(), "Image wasn't saved", Toast.LENGTH_SHORT).show()
        else {
            constructMessage(time, uri)
        }
    }

    private fun constructMessage(time: Long, uri: String){
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                val msgTxt = image_preview_message_input.text.toString().trim()
                val message = Message(
                    messageID = "set while sending",
                    conversationID = conversationInfo.conversationID,
                    type = Constants.TYPE_MY_MSG,
                    status = Constants.STATUS_NOT_SENT,
                    needs_push = Constants.NEEDS_PUSH_YES,
                    timeStamp = time,
                    serverTimestamp = time,
                    data = msgTxt,
                    senderID = dataRepository.getMydetails()!!.username,
                    receiverID = conversationInfo.receiverID,
                    deleted = Constants.DELETED_NO,
                    mime_type = Constants.MIME_TYPE_IMAGE_JPEG,
                    conType = conversationInfo.conType,
                    local_uri = uri,
                    reply_toID = conversationInfo.replyToMessageID
                )
                //dataRepository.saveImageMessage(message)
            }
        }
    }

    private fun saveImage(time: Long): String?{
        val strgstate = Environment.getExternalStorageState()
        if (strgstate == Environment.MEDIA_MOUNTED){
            val root = Environment.getExternalStorageDirectory().path
            val storageDirectory = File(root + Constants.IMAGE_SENT_STORAGE_PATH)

            if(!storageDirectory.exists())
                storageDirectory.mkdirs()

            val file = File(storageDirectory,"ENG_IMG_${
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                    Date(time)
                )}"+".jpg")

            val uri = file.toURI().toString()
            Log.d(TAG, "saveImage: $uri")

            try {
                val stream: OutputStream = FileOutputStream(file)
                imageToDisplay.compress(Bitmap.CompressFormat.JPEG,40,stream)
                stream.flush()
                stream.close()
                Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show()

                return uri
            }
            catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(getActivity(), "Unable To Access", Toast.LENGTH_SHORT).show()
        }

        return null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.inject(this)
    }
}