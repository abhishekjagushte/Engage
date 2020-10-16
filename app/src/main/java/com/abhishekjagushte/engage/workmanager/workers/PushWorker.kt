package com.abhishekjagushte.engage.workmanager.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.tasks.await

class PushWorker (
    context: Context,
    workerParams: WorkerParameters,
    private val dataRepository: DataRepository
): CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {
        val messageID = inputData.getString(Constants.MESSAGE_ID)

        val task = dataRepository.pushMessage(messageID!!)
        task.await()

        //TODO sending messages to M2M users is implemented but not tested

        return if (task.isSuccessful){
            dataRepository.setMessageSent(messageID)
            Log.i("PushWorker", "success")
            Result.success()
        }
        else{
            Log.i("PushWorker", "failure")
            Result.failure()
        }
    }
}