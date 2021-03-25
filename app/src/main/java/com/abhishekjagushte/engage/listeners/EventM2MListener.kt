package com.abhishekjagushte.engage.listeners

import android.util.Log
import com.abhishekjagushte.engage.network.EventNetwork
import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventM2MListener(
    dataRepository: DataRepository,
    repositoryScope: CoroutineScope,
    myUsername: String
){

    val TAG = "EventM2MListener"

    val m2mListener: (snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) -> Unit =
        { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "setChatListener: Error")
            } else {
                snapshot?.let {
//                  for (doc in snapshot.documents) {
//                       Log.d(TAG, "setChatListener: ${doc.data}")
//                  }
                    repositoryScope.launch {
                        withContext(Dispatchers.IO) {
                            for (dc in snapshot.documentChanges) {
                                when (dc.type) {
                                    DocumentChange.Type.ADDED -> {
                                        //Log.w(TAG, "Added ${dc.document.data}")

                                        Log.d(TAG, "${dc.document.data}")

                                        val event = dc.document.toObject<EventNetwork>()
                                            .convertDomainEvent121(Constants.TYPE_OTHER_MSG)

                                        if (!event.senderID.equals(myUsername))
                                            dataRepository.receiveEventM2M(event)
                                    }
                                }
//                          }
                            }
                        }
                    }

                }
            }
        }

}