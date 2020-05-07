package com.abhishekjagushte.engage.ui.main.fragments.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import javax.inject.Inject

class SearchFragmentViewModel @Inject constructor(
    private val app: Application,
    private val dataRepository: DataRepository
) : AndroidViewModel(app) {

    private val TAG = "SearchFragmentViewModel"

    val viewModelJob = Job()
    val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)



    val _searchResults = MutableLiveData<List<DataItem>>()

    val searchResults: LiveData<List<DataItem>>
        get() = _searchResults

    fun onSearchTextQueryChanged(query: String){

        //First Check in conversation list

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                var searchList = listOf<DataItem>()

                Log.d(TAG,"Inside ${dataRepository.getCountContacts()} ${query}")

                val conversations = dataRepository.searchForConversations(query)
                if(conversations.isNotEmpty()){
                    searchList = searchList.plus(DataItem.Header(app.resources.getString(R.string.conversations)))

                    for(conversation in conversations){
                        searchList = searchList.plus(DataItem.SearchDataItem(conversation))
                    }
                    Log.d(TAG,"Contacts not empty ${conversations.size} SearchList size = ${searchList.size}")
                }


                val contacts = dataRepository.searchForContacts(query)
                if(contacts.isNotEmpty()){
                    searchList = searchList.plus(DataItem.Header(app.resources.getString(R.string.contacts)))

                    for(contact in contacts){
                        searchList = searchList.plus(DataItem.SearchDataItem(contact))
                    }
                    Log.d(TAG,"Contacts not empty ${contacts.size} SearchList size = ${searchList.size}")
                }


                val suggested = dataRepository.searchForSuggested(query)
                if(suggested.isNotEmpty()){
                    searchList = searchList.plus(DataItem.Header(app.resources.getString(R.string.suggested)))

                    for(suggestion in suggested){
                        searchList = searchList.plus(DataItem.SearchDataItem(suggestion))
                    }
                    Log.d(TAG,"Contacts not empty ${suggested.size} SearchList size = ${searchList.size}")
                }

                _searchResults.postValue(searchList)
                Log.d(TAG,"Size = ${_searchResults.value?.size}")


                val queries = dataRepository.searchUnknownContacts(query)

                queries.first.addSnapshotListener { nameQuerySnapshot, firebaseFirestoreException ->

                    Log.d(TAG,"Querying Succeeded")

                    if(nameQuerySnapshot!=null && nameQuerySnapshot.size()>0){

                        _searchResults.value = _searchResults.value?.plus(DataItem.Header(app.resources.getString(R.string.addcontacts)))

                        for(doc in nameQuerySnapshot.documents){
                            _searchResults.value = _searchResults.value?.plus(
                                DataItem.SearchDataItem(SearchData(
                                doc.get("name").toString(),
                                null,
                                    doc.get("username").toString())
                            )) }
                        }

                        if(searchList.size<10) {
                            queries.second.addSnapshotListener { usernameQuerySnapshot, firebaseFirestoreException2 ->

                                if(usernameQuerySnapshot!=null && usernameQuerySnapshot.size()>0){
                                    for(doc in usernameQuerySnapshot.documents!!){
                                        _searchResults.value = _searchResults.value?.plus(
                                            DataItem.SearchDataItem(SearchData(
                                                doc.get("name").toString(),
                                                null,
                                                doc.get("username").toString())
                                        )) }
                                }
                        }

                    }
                }

            }
        }

    }

}
