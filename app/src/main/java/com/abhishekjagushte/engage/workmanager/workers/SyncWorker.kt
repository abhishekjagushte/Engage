package com.abhishekjagushte.engage.workmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.sync.M2MChatsSynchronizer
import com.abhishekjagushte.engage.sync.One21Synchronizer
import java.lang.Exception

class SyncWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val dataRepository: DataRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val m2MChatsSynchronizer = M2MChatsSynchronizer(dataRepository, context)
            m2MChatsSynchronizer.synchronize()
            val one21Synchronizer = One21Synchronizer(dataRepository, context)
            one21Synchronizer.synchronize()

            return Result.success()

        }catch (e: Exception){
            e.printStackTrace()
            return Result.retry()
        }
    }
}