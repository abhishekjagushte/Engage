package com.abhishekjagushte.engage.sync

import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.repository.DataRepository

class M2MChatsSynchronizer(
    private val dataRepository: DataRepository
){
        fun synchronize(){
        val syncMap = dataRepository.getM2MSyncMap()

        syncMap.forEach { conversation ->
            val query = dataRepository.syncM2MChat(conversation)

            query.addOnSuccessListener {
                it?.let{
                    it.documents.forEach {
                        it?.let {
                            dataRepository.receiveMessageM2M(it.toObject(MessageNetwork::class.java)!!.convertDomainMessageM2M(conversation.conversationID))
                        }
                    }
                }
            }
        }
    }
}