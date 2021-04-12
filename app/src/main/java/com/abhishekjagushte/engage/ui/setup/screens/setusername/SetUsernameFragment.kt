package com.abhishekjagushte.engage.ui.setup.screens.setusername

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.permissions.WriteExternalStoragePermissionHelper
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.abhishekjagushte.engage.utils.ImageUtil
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_set_username.*
import kotlinx.android.synthetic.main.fragment_set_username.profile_image
import kotlinx.android.synthetic.main.fragment_set_username.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


class SetUsernameFragment: Fragment() {

    private val TAG: String = "SetUsernameFragment"
    private var profileImageUri:Uri? = null
    private lateinit var profileImageView: CircleImageView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SetUsernameViewModel> { viewModelFactory }

    @Inject
    lateinit var dataRepository: DataRepository

    private lateinit var progressBar2: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_username, container, false)
        val nameInput = view.findViewById<TextInputEditText>(R.id.name_input)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.username_input)
        val confirmButton = view.findViewById<Button>(R.id.confirm_button)
        val noteText = view.findViewById<TextView>(R.id.noteText)
        progressBar2 = view.findViewById<ProgressBar>(R.id.progressBar2)

        WriteExternalStoragePermissionHelper(
            requireActivity(),
            requireContext(),
            Constants.WRITE_PERMISSION_REQUEST_CODE
        ).permissionsForSave()

        profileImageView = view.findViewById(R.id.profile_image)


        val args = SetUsernameFragmentArgs.fromBundle(requireArguments())
        viewModel.email = args.email
        viewModel.password = args.password

        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        //Set Profile Picture
        view.change_profile_image.setOnClickListener() {
            findNavController().navigate(R.id.action_setUsernameFragment_to_bottomSheet)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Pair<Uri?, Bitmap?>>("returnedProfileImageFromSlider")?.observe(
            viewLifecycleOwner) { result ->
            result.second?.let {
                profileImageUri = result.first
                profile_image.setImageBitmap(it)
            }
        }

        //check username to be unique from firebase
        usernameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, s.toString())
                viewModel.checkUsername(s.toString())
            }

        })

        viewModel.noteText.observe(viewLifecycleOwner, Observer {
            noteText.setText(it ?: "")
            Log.d(TAG, it)
        })

        viewModel.changeCompleteStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                Constants.LOCAL_DB_SUCCESS -> {
                    uploadAndSaveProfilePicture()
                }
                Constants.FIREBASE_CHANGE_FAILED -> confirmFailed()
                Constants.LOCAL_DB_FAILED -> confirmFailed()
                else -> Log.d(TAG, "Not initiated")
            }
        })

        confirmButton.setOnClickListener {
            val name = nameInput.text.toString()
            val username = usernameInput.text.toString()
            progressBar2.visibility = View.VISIBLE //will be visible till dp uploads
            if(checkInputs(name, username)){
                viewModel.confirmSetup(name, username, profileImageUri)
            }
        }

        return view
    }

    private fun uploadAndSaveProfilePicture(){
        val handler = Handler(Looper.myLooper()!!)
        if(profileImageUri!=null)
        profileImageUri?.let{imageUri ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO){

                    dataRepository.setProfilePicture(imageUri).addOnSuccessListener {
                        saveFinalImage(imageUri)
                        handler.post {
                            progressBar2.visibility = View.GONE
                            updateUI()
                        }
                    }
                }
            }
        }
        else
            updateUI()
    }


    private fun confirmFailed() {
        Log.d(TAG, "Failed")
    }


    private fun updateUI() {
        Log.d(TAG, "Updated Successfully")
        findNavController().navigate(R.id.action_global_chatListFragment)
    }


    private fun checkInputs(name: String, username: String): Boolean {
        return checkName(name) && viewModel.usernameValid.value?:false 
    }

    private fun checkName(name: String): Boolean{
        return name.isNotEmpty()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addSetUsernameComponent()
            .create().inject(this)
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