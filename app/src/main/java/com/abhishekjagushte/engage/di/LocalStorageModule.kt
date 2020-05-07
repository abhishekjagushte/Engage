package com.abhishekjagushte.engage.di

import android.app.Application
import android.content.Context
import com.abhishekjagushte.engage.database.AppDatabase
import com.abhishekjagushte.engage.database.DatabaseDao
import dagger.Module
import dagger.Provides

@Module
class LocalStorageModule() {

    @Provides
    fun providesLocalDatabase(application: Application): DatabaseDao{
        return AppDatabase.getInstance(application.applicationContext).databaseDao
    }
}