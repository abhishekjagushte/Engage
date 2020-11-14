package com.abhishekjagushte.engage.sync

import android.content.Context
import com.abhishekjagushte.engage.repository.DataRepository

class EngageSyncRequest(
    private val dataRepository: DataRepository,
    private val context: Context
) {
    suspend fun synchronize(){
        val m2MChatsSynchronizer = M2MChatsSynchronizer(dataRepository, context)
        m2MChatsSynchronizer.synchronize()

        val one21Synchronizer = One21Synchronizer(dataRepository, context)
        one21Synchronizer.synchronize()
    }

    suspend fun one21synchronize(){
        val one21Synchronizer = One21Synchronizer(dataRepository, context)
        one21Synchronizer.synchronize()
    }

    suspend fun M2Msynchronize(){
        val m2MChatsSynchronizer = M2MChatsSynchronizer(dataRepository, context)
        m2MChatsSynchronizer.synchronize()
    }
}