package com.abhishekjagushte.engage.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishekjagushte.engage.database.Contact
import com.abhishekjagushte.engage.database.ContactNameUsername
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.FieldPosition

class AddParticipantSharedViewModel /*@Inject constructor*/(
    private val dataRepository: DataRepository
): ViewModel(){

    private val TAG = "AddParticipantSharedViewModel"

    val participantsHash = HashMap<String, Boolean>()

    private val _participants = MutableLiveData<MutableList<ContactNameUsername>>()
    val participants: LiveData<MutableList<ContactNameUsername>>
        get() = _participants

    private val _queryList = MutableLiveData<List<Contact>>()
    val queryList: LiveData<List<Contact>>
        get() = _queryList

    init {
        _participants.value = mutableListOf()
        Log.d(TAG, "Add Participant shared viewmodel init")
        setInitialQuery()
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
                    contact.selected = participantsHash[contact.name] == true
                    return@map contact
                })
            }
        }
    }


    fun addParticipant(contact: ContactNameUsername){
        participantsHash[contact.username] = true
        _participants.value?.add(contact)
        _participants.value = _participants.value

        Log.d(TAG, "addParticipant: $contact ${participants.value}")
    }

    fun removeParticipant(contact: ContactNameUsername){
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

    fun createGroup() {

    }

}
