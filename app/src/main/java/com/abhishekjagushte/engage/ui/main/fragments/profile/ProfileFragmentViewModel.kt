package com.abhishekjagushte.engage.ui.main.fragments.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.database.Contact
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
                val request = hashMapOf(
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
                        }
                        _profileDisplay.value!!.type = Constants.CONTACTS_REQUESTED
                    }
                }
            }
        }
    }

    fun acceptRequest() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val myDetails = dataRepository.getMydetails()
                val conID = dataRepository.getTemporaryConversationID()

                val request = hashMapOf<String, Any>(
                    "senderID" to myDetails!!.username,
                    "senderName" to myDetails.name,
                    "receiverID" to profileDisplay.value!!.username,
                    "timeStamp" to Date().toString(),
                    "type" to Constants.ACCEPT_FR_TYPE,
                    "conversationID" to conID
                )

                dataRepository.addFriend(request).addOnSuccessListener {
                    _actionStatus.value = FRIEND_REQUEST_ACCEPTED

                    //Replace Strategy already there so i can use addContact
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            localContact.type = Constants.CONTACTS_CONFIRMED
                            localContact.conversationID = conID

                            dataRepository.addContact(localContact)
                        }
                        _profileDisplay.value!!.type = Constants.CONTACTS_CONFIRMED
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
                        //this won't happen if localcontact isnt initialized
                        try {
                            val p = networkProfile!!.convertDomainObject(localContact.type)
                            localContact.name = p.name
                            localContact.bio = p.bio
                            localContact.dp_thmb = p.dp_thmb
                            localContact.networkID = p.networkID
                            localContact.timeStamp = p.timeStamp
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


data class ProfileDisplay(
    val name: String,
    val username: String,
    val dp_thmb: ByteArray? = null,
    val bio: String,
    val timeStampString: String,
    var type: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfileDisplay

        if (name != other.name) return false
        if (username != other.username) return false
        if (dp_thmb != null) {
            if (other.dp_thmb == null) return false
            if (!dp_thmb.contentEquals(other.dp_thmb)) return false
        } else if (other.dp_thmb != null) return false
        if (bio != other.bio) return false
        if (timeStampString != other.timeStampString) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + (dp_thmb?.contentHashCode() ?: 0)
        result = 31 * result + bio.hashCode()
        result = 31 * result + timeStampString.hashCode()
        result = 31 * result + type
        return result
    }
}

/*
      dataRepository.getContactFirestoreFromUsername(username)
                                .addOnSuccessListener {
                                    if (it != null) {
                                        val profile = it.toObject(Profile::class.java)
                                        if (profile != null) {
                                            Log.d(TAG, "${profile.name} ${profile.username}")

                                            networkProfile = profile
                                            //This part is for updating contact information
                                            val networkProfileDisplay = profile.convertToProfileDisplay(contact.type)

                                            if(!localProfileDisplay.equals(networkProfileDisplay)){
                                                _profileDisplay.postValue(networkProfileDisplay)
                                                Log.d(TAG, "Changed + ${networkProfileDisplay.timeStampString}")
                                                contact.bio = networkProfileDisplay.bio
                                                contact.dp_thmb = networkProfileDisplay.dp_thmb
                                                contact.name = networkProfileDisplay.name
                                                viewModelScope.launch {
                                                    withContext(Dispatchers.IO){
                                                        dataRepository.updateContact(contact)
                                                        updated = true//Ensures no infinite loop
                                                    }
                                                }
                                            }
                                        }
                                        Log.d(TAG, profile.toString())
                                    }
                                }
 */


