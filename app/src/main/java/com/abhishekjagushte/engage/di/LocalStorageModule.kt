package com.abhishekjagushte.engage.di

import android.app.Application
import android.content.Context
import com.abhishekjagushte.engage.database.AppDatabase
import dagger.Module
import dagger.Provides

@Module
class LocalStorageModule() {

    @Provides
    fun providesLocalDatabase(applicationContext: Context): AppDatabase{
        return AppDatabase.getInstance(applicationContext)
    }
}