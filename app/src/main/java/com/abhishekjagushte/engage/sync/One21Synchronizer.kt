package com.abhishekjagushte.engage.sync

import android.content.Context
import com.abhishekjagushte.engage.network.MessageNetwork
import com.abhishekjagushte.engage.notifications.EngageMessageNotification
import com.abhishekjagushte.engage.repository.DataRepository

class One21Synchronizer(
    private val dataRepository: DataRepository,
    private val context: Context
){
    fun synchronize(){
        dataRepository.getLatestChats121Query(dataRepository.getMydetails()!!.username).get().addOnSuccessListener {
            it?.let {
                it.documents.forEach {
                    it?.let {
                        val message = it.toObject(MessageNetwork::class.java)!!.convertDomainMessage121()
                        dataRepository.receiveMessage121(message)
                        EngageMessageNotification(dataRepository, context).makeMessageNotification(message)
                    }
                }
            }
        }
    }
}