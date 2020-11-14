package com.abhishekjagushte.engage.sync

import android.content.Context
import android.util.Log
import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.notifications.EngageMessageNotification
import com.abhishekjagushte.engage.repository.DataRepository
import kotlinx.coroutines.tasks.await

class M2MChatsSynchronizer(
    private val dataRepository: DataRepository,
    private val context: Context
){

        private val TAG = "M2MChatsSynchronizer"

        suspend fun synchronize(){
            val syncMap = dataRepository.getM2MSyncMap()

            Log.e(TAG, "synchronize: $syncMap", )

            syncMap.forEach { conversation ->

                val query = dataRepository.syncM2MChat(conversation)
                val result = query.await()

                result.let{
                    it.documents.forEach {
                        it?.let {
                            val message = it.toObject(MessageNetwork::class.java)!!.convertDomainMessageM2M(conversation.conversationID)
                            dataRepository.receiveMessageM2M(message)
                            EngageMessageNotification(dataRepository, context).makeMessageNotification(message)
                            Log.e(TAG, "synchronize: Added in M2M Sync" )
                        }
                    }
                }

            }
        }
}