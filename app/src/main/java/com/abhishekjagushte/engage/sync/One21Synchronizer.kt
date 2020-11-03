package com.abhishekjagushte.engage.sync

import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.repository.DataRepository

class One21Synchronizer(
    private val dataRepository: DataRepository
){
    fun synchronize(){
        dataRepository.getLatestChats121Query(dataRepository.getMydetails()!!.username).get().addOnSuccessListener {
            it?.let {
                it.documents.forEach {
                    it?.let {
                        dataRepository.receiveMessage121(it.toObject(MessageNetwork::class.java)!!.convertDomainMessage121())
                    }
                }
            }
        }
    }
}