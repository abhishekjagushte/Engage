package com.abhishekjagushte.engage.workmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abhishekjagushte.engage.repository.DataRepository

class UploadWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val dataRepository: DataRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}