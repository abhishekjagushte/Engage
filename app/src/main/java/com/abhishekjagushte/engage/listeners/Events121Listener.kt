package com.abhishekjagushte.engage.listeners

import android.util.Log
import com.abhishekjagushte.engage.network.EventNetwork
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*

class Events121Listener(private val dataRepository: DataRepository, private val username: String) {
    val job: Job = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val TAG = "One21Listener"

    fun set121Listener(): ListenerRegistration {
        val listener121 = dataRepository.setEvents121Listener(username)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.d(TAG, "setChatListener: Error ${exception.stackTrace}")
                    return@addSnapshotListener
                }
                snapshot?.let {
//                                for (doc in snapshot.documents) {
//                                    Log.d(TAG, "setChatListener: ${doc.data}")
//                                }
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            for (dc in snapshot.documentChanges) {
                                when (dc.type) {
                                    DocumentChange.Type.ADDED -> {
                                        //Log.w(TAG, "Added ${dc.document.data}")
                                        dataRepository.receiveEvent121(
                                            dc.document.toObject<EventNetwork>()
                                                .convertDomainEvent121(Constants.EVENT_OTHER)
                                        )
                                    }
                                    //DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified${dc.document.data}")
                                    //DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed ${dc.document.data}")
                                }
//                        Log.d(TAG, "test: ${dc.data}")
                            }
                        }
                    }
                    //Log.d(TAG, "testSetMessageListener: ${snapshot.documents.size}")
                }
            }

        return listener121

    }
}