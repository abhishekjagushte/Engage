package com.abhishekjagushte.engage.sync

import android.content.Context
import android.util.Log
import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.notifications.EngageMessageNotification
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.tasks.await

class One21Synchronizer(
    private val dataRepository: DataRepository,
    private val context: Context
){
    private val TAG = "One21Synchronizer"

    suspend fun synchronize(){
        val result = dataRepository.getLatestChats121Query(dataRepository.getMydetails()!!.username).get().await()
        result?.let {snap ->
            snap.documents.forEach {docSnap ->
                docSnap?.let { doc ->
                    val message = doc.toObject(MessageNetwork::class.java)!!.convertDomainMessage121()
                    Log.e(TAG, "synchronize: $message", )
                    dataRepository.receiveMessage121(message)
                    EngageMessageNotification(dataRepository, context).makeMessageNotification(message)
                    Log.e(TAG, "synchronize: Added in 121 Sync" )
                }
            }
        }

        Log.e(TAG, "synchronize: One21Synchronizer completed")
    }
}