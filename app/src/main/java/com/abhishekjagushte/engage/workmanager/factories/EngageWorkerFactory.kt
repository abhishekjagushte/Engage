package com.abhishekjagushte.engage.workmanager.factories

import androidx.work.DelegatingWorkerFactory
import com.abhishekjagushte.engage.repository.DataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EngageWorkerFactory @Inject constructor(
    dataRepository: DataRepository
): DelegatingWorkerFactory(){
    init {
        addFactory(PushWorkerFactory(dataRepository))
        addFactory(SyncWorkerFactory(dataRepository))
    }
}