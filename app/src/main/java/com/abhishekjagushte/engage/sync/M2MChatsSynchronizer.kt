package com.abhishekjagushte.engage.sync

import android.content.Context
import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.notifications.EngageMessageNotification
import com.abhishekjagushte.engage.repository.DataRepository

class M2MChatsSynchronizer(
    private val dataRepository: DataRepository,
    private val context: Context
){
        fun synchronize(){
        val syncMap = dataRepository.getM2MSyncMap()

        syncMap.forEach { conversation ->
            val query = dataRepository.syncM2MChat(conversation)

            query.addOnSuccessListener {
                it?.let{
                    it.documents.forEach {
                        it?.let {
                            val message = it.toObject(MessageNetwork::class.java)!!.convertDomainMessageM2M(conversation.conversationID)
                            dataRepository.receiveMessageM2M(message)
                            EngageMessageNotification(dataRepository, context).makeMessageNotification(message)
                        }
                    }
                }
            }
        }
    }
}