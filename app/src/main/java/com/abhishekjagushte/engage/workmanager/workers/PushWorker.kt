package com.abhishekjagushte.engage.workmanager.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.tasks.await

class PushWorker (
    context: Context,
    workerParams: WorkerParameters,
    private val dataRepository: DataRepository
): CoroutineWorker(context, workerParams){

    private var task: Task<Void>? = null
    private val TAG = "Pushworker"

    //earlier this was used to send messages everytime, but i observeed that even if messages are sent while
    //being offline, when back online again, they are sent so this is only requred when the app is not active, so this will be a periodic task

    override suspend fun doWork(): Result {
        val unsentMessages = dataRepository.getUnsentMessages()
        for(msg in unsentMessages){
            val messageID = msg.messageID
            messageID.let{
                task = dataRepository.pushMessage(messageID)
                task?.let{
                    it.await()
                    //TODO sending messages to M2M users is implemented but not tested
                    return if (it.isSuccessful){
                        dataRepository.setMessageSent(messageID)
                        Log.i("PushWorker", "success")
                        Result.success()
                    } else{
                        Log.i("PushWorker", "failure")
                        Result.failure()
                    }
                }
                
            }
        }
        Log.d(TAG, "doWork: Returning success: no new message to be sent")
        return Result.success()
    }
}

/*

 */