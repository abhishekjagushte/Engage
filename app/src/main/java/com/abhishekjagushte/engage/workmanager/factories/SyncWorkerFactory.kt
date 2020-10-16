package com.abhishekjagushte.engage.workmanager.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.workmanager.workers.SyncWorker

class SyncWorkerFactory(
    private val dataRepository: DataRepository
): WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return SyncWorker(appContext, workerParameters, dataRepository)
    }
}