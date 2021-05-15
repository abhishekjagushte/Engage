package com.abhishekjagushte.engage.ui.main.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.entities.Contact
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.network.convertDomainObject
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

const val FRIEND_REQUEST_SENT = 1
const val FRIEND_REQUEST_ACCEPTED = 2

class ProfileFragmentViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val TAG = "ProfileFragmentVM"
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var networkProfile: Profile? = null
    lateinit var localContact: Contact ////////////////////////////////Local contact variable

    private val _profileDisplay = MutableLiveData<ProfileDisplay>()
    val profileDisplay: LiveData<ProfileDisplay>
        get() = _profileDisplay

    private val _actionStatus = MutableLiveData<Int>()
    val actionStatus: LiveData<Int>
        get() = _actionStatus

    init {
        _actionStatus.value = -1
    }

    fun getProfileLive(username: String): LiveData<Contact>{
        return dataRepository.getContactFromUsername(username)
    }

    fun setProfileDisplay(contact: Contact?, username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                //If the contact is not available locally
                if (contact == null) {
                    //In case of unknown contacts
                    dataRepository.getContactFirestoreFromUsername(username)
                        .addOnSuccessListener {
                            if (it != null) {
                                val profile = it.toObject(Profile::class.java)
                                networkProfile = profile

                                if (profile != null) {
                                    _profileDisplay.postValue(profile
                                        .convertToProfileDisplay(Constants.CONTACTS_UNKNOWN))
                                }
                                Log.d(TAG, profile.toString())
                            }
                        }
                } else {
                    localContact = contact
                    val localProfileDisplay = contact.convertToProfileDisplay()
                    _profileDisplay.postValue(localProfileDisplay)
                }
            }
        }
    }

    fun addFriend() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val myDetails = dataRepository.getMydetails()
                val request = hashMapOf<String, Any>(
                    "senderID" to myDetails!!.username,
                    "senderName" to myDetails.name,
                    "receiverID" to profileDisplay.value!!.username,
                    "timeStamp" to Date().toString(),
                    "type" to Constants.SEND_FR_TYPE
                )

                dataRepository.addFriend(request).addOnSuccessListener {
                    _actionStatus.value = FRIEND_REQUEST_SENT
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            dataRepository.addContact(networkProfile!!.convertDomainObject(Constants.CONTACTS_REQUESTED))
                            _profileDisplay.value!!.type = Constants.CONTACTS_REQUESTED
                        }
                    }
                }
            }
        }
    }

    fun acceptRequest() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val myDetails = dataRepository.getMydetails()

                val request = hashMapOf<String, Any>(
                    "senderID" to myDetails!!.username,
                    "senderName" to myDetails.name,
                    "receiverID" to profileDisplay.value!!.username,
                    "timeStamp" to Date().toString(),
                    "type" to Constants.ACCEPT_FR_TYPE
                )

                dataRepository.addFriend(request).addOnSuccessListener {
                    _actionStatus.value = FRIEND_REQUEST_ACCEPTED

                    //Replace Strategy already there so i can use addContact
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            localContact.type = Constants.CONTACTS_CONFIRMED
                            dataRepository.addContact(localContact)
                            _profileDisplay.value!!.type = Constants.CONTACTS_CONFIRMED
                        }
                    }
                }
            }
        }
    }


    fun getUpdatedProfile(username: String){
        dataRepository.getContactFirestoreFromUsername(username)
            .addOnSuccessListener {
                if (it != null) {
                    val profile = it.toObject(Profile::class.java)
                    if (profile != null) {
                        networkProfile = profile
                        //this won't happen if localcontact isn't initialized
                        try {
                            val p = networkProfile!!.convertDomainObject(localContact.type)
                            localContact.name = p.name
                            localContact.bio = p.bio
                            localContact.dp_thmb_url = p.dp_thmb_url
                            localContact.networkID = p.networkID
                            localContact.dp_timeStamp = p.dp_timeStamp
                            viewModelScope.launch {
                                withContext(Dispatchers.IO) {
                                    dataRepository.updateContact(localContact)
                                }
                            }
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    fun test() {
        dataRepository.getImageThumbnailDownloadURL("rheaseahorn").addOnSuccessListener {
            Log.e(TAG, "test: $it", )
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
        dp_thmb_url = this.dp_thmb_url,
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

data class ProfileDisplay(
    val name: String,
    val username: String,
    val dp_thmb_url: String?=null,
    val bio: String,
    val timeStampString: String,
    var type: Int
)