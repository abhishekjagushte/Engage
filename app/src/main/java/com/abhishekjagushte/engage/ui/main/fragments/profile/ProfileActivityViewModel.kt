package com.abhishekjagushte.engage.ui.main.fragments.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

const val FRIEND_REQUEST_SENT = 1

class ProfileActivityViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {


    private val TAG = "ProfileFragmentVM"
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _profileDisplay = MutableLiveData<ProfileDisplay>()
    val profileDisplay: LiveData<ProfileDisplay>
        get() = _profileDisplay


    private val _actionStatus = MutableLiveData<Int>()
    val actionStatus: LiveData<Int>
        get() = _actionStatus

    init {
        _actionStatus.value = -1
    }


    fun setProfileDisplay(username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val contacts = dataRepository.getContactFromUsername(username)

                if (contacts.isEmpty()) {

                    //In case of unknown contacts

                    dataRepository.getContactFirestoreFromUsername(username)
                        .addOnSuccessListener {
                            if (it != null) {
                                val profile = it.toObject(Profile::class.java)
                                if (profile != null) {
                                    _profileDisplay.postValue(profile
                                        .convertToProfileDisplay(Constants.CONTACTS_UNKNOWN))
                                }
                                Log.d(TAG, profile.toString())
                            }
                        }
                } else {
                    val contact = contacts[0]

                    val localProfileDisplay = contact.convertToProfileDisplay()

                    _profileDisplay.postValue(localProfileDisplay)

                    dataRepository.getContactFirestoreFromUsername(username)
                        .addOnSuccessListener {
                            if (it != null) {
                                val profile = it.toObject(Profile::class.java)
                                if (profile != null) {


                                    //This part is for updating contact information
                                    val networkProfileDisplay = profile.convertToProfileDisplay(contact.type)

                                    if(localProfileDisplay != networkProfileDisplay){
                                        _profileDisplay.postValue(networkProfileDisplay)

                                        contact.bio = networkProfileDisplay.bio
                                        contact.dp_thmb = networkProfileDisplay.dp_thmb
                                        contact.name = networkProfileDisplay.name

                                        dataRepository.updateContact(contact)
                                    }
                                }
                                Log.d(TAG, profile.toString())
                            }
                        }

                }
            }
        }
    }

    fun addFriend(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val myDetails = dataRepository.getMydetails()
                val request = hashMapOf<String, Any>(
                    "senderID" to myDetails.username,
                    "senderName" to myDetails.name,
                    "receiverID" to profileDisplay.value!!.username,
                    "timeStamp" to Date().toString(),
                    "type" to Constants.SEND_FR_TYPE
                )

                dataRepository.addFriend(request).addOnSuccessListener {
                    _actionStatus.value = FRIEND_REQUEST_SENT
                }

                //TODO("update local database")

            }
        }
    }

}

fun Contact.convertToProfileDisplay(): ProfileDisplay {

    val timeStampString = "Time Stamp String"
//    when (this.type) {
//        Constants.CONTACTS_CONFIRMED -> TODO()
//        Constants.CONTACTS_PENDING -> TODO()
//        Constants.CONTACTS_UNKNOWN -> TODO()
//        Constants.CONTACTS_REQUESTED -> TODO()
//    }

    return ProfileDisplay(
        name = this.name,
        username = this.username,
        bio = this.bio,
        timeStampString = timeStampString,
        type = this.type
    )
}

fun Profile.convertToProfileDisplay(type: Int): ProfileDisplay {
    return ProfileDisplay(
        name = this.name,
        username = this.username,
        bio = this.bio,
        timeStampString = this.joinTimeStamp.toString(),
        type = type
    )
}


class ProfileDisplay(
    val name: String,
    val username: String,
    val dp_thmb: ByteArray? = null,
    val bio: String,
    val timeStampString: String,
    val type: Int
)

