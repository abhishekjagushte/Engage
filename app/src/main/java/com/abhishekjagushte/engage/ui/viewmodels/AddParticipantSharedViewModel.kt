package com.abhishekjagushte.engage.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishekjagushte.engage.database.entities.Contact
import com.abhishekjagushte.engage.database.entities.ContactDetails
import com.abhishekjagushte.engage.network.CreateGroupRequest
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

const val INCOMPLETE_NETWORK_ERROR = 1

enum class LoadingState{
    NOT_LOADING, LOADING, COMPLETED
}

class CompleteStatus(
    val conversationID: String? = null,
    val message: String? = null,
    val messageCode: Int? = null,
    val loadingState: LoadingState,
    val complete: Boolean = false
)

class AddParticipantSharedViewModel /*@Inject constructor*/(
    private val dataRepository: DataRepository
): ViewModel(){

    private val TAG = "AddParticipantSharedViewModel"

    val participantsHash = HashMap<String, Boolean>()

    private val _participants = MutableLiveData<MutableList<ContactDetails>>()
    val participants: LiveData<MutableList<ContactDetails>>
        get() = _participants

    private val _queryList = MutableLiveData<List<Contact>>()
    val queryList: LiveData<List<Contact>>
        get() = _queryList

    private val _completeState = MutableLiveData<CompleteStatus>()
    val completeState: LiveData<CompleteStatus>
        get() = _completeState


    init {
        _participants.value = mutableListOf()
        Log.d(TAG, "Add Participant shared viewmodel init")
        setInitialQuery()
        _completeState.value = CompleteStatus(loadingState = LoadingState.NOT_LOADING)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: Add Participant shared viewmodel cleared")
    }

    private fun setInitialQuery(){
        query("")
    }

    fun query(query: String) {
        viewModelScope.launch {
            Log.d(TAG, "query: called")
            
            withContext(Dispatchers.IO) {
                _queryList.postValue(dataRepository.searchForFriends(query).map {
                    val contact =
                        Contact(name = it.name, username = it.username, networkID = "", type = 1)
                    //Sets selected
                    contact.selected = participantsHash[contact.username] == true
                    Log.d(TAG, "query: ${contact.selected}")
                    return@map contact
                })
            }
        }
    }

    fun addParticipant(contact: ContactDetails){
        participantsHash[contact.username] = true
        _participants.value?.add(contact)
        _participants.value = _participants.value

        Log.d(TAG, "addParticipant: $contact ${participants.value}")
    }

    fun removeParticipant(contact: ContactDetails){
        participantsHash.remove(contact.username)
        for(i in 0..participants.value!!.size)
        {
            //Log.d(TAG, "removeParticipant: $i ${participants.value!!.get(i)} $contact")
            if(participants.value!!.get(i).username.equals(contact.username)){
                _participants.value!!.removeAt(i)
                break
            }
        }
        _participants.value = _participants.value
        Log.d(TAG, "removeParticipant: ${participants.value}")
    }

    fun createGroup(name: String) {
        viewModelScope.launch {

            _completeState.postValue(CompleteStatus(loadingState = LoadingState.LOADING))

            withContext(Dispatchers.IO){
                val myUsername = dataRepository.getMydetails()!!.username

                var participantsList = participants.value!!.map {
                    it.username
                }

                participantsList = participantsList.plus(myUsername)

                //TODO use the prior uproach add first in local db, get the conversationID and
                // let the mainactivity push the unpushed conversations

                val conversationID: String = dataRepository.getNewConversationIDM2M()

                val request =  CreateGroupRequest(
                    name = name,
                    conversationID = conversationID,
                    creator = myUsername,
                    participants = participantsList
                )

                val gson = Gson()
                val jsonString = gson.toJson(request)
                var jsonObj: JSONObject? = null
                try {
                    jsonObj = JSONObject(jsonString)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                dataRepository.createGroup(jsonObj).addOnSuccessListener {
                    viewModelScope.launch {
                        it.data?.let {
                            if((it as HashMap<String, String>)["status"].equals("success")) {
                                Log.d(TAG, "createGroup: success")
                                withContext(Dispatchers.IO) {
                                    dataRepository.createGroupLocal(request, true)
                                    _completeState.postValue(
                                        CompleteStatus(
                                            conversationID = conversationID,
                                            loadingState = LoadingState.NOT_LOADING,
                                            complete = true))
                                    //TODO complete the observer for completestate
                                }
                            }
                            else if(it["status"].equals("failure")){

                                Log.d(TAG, "createGroup: Failure")

                                viewModelScope.launch {
                                    withContext(Dispatchers.IO){
                                        dataRepository.createGroupLocal(request, false)
                                        _completeState.postValue(
                                            CompleteStatus(
                                                message = "Failure",
                                                messageCode = INCOMPLETE_NETWORK_ERROR,
                                                loadingState = LoadingState.NOT_LOADING))
                                    }
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }

            }
        }
    }

}
