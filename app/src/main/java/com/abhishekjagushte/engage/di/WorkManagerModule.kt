package com.abhishekjagushte.engage.di

import android.app.Application
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides

@Module
class WorkManagerModule(){

    @Provides
    fun providesWorkManager(application: Application): WorkManager{
        return WorkManager.getInstance(application.applicationContext)
    }

}