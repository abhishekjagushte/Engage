package com.abhishekjagushte.engage.di

import android.content.Context
import com.abhishekjagushte.engage.database.AppDatabase
import com.abhishekjagushte.engage.database.DatabaseDao
import dagger.Module
import dagger.Provides

@Module
class LocalStorageModule() {

    @Provides
    fun providesLocalDatabase(applicationContext: Context): DatabaseDao{
        return AppDatabase.getInstance(applicationContext).databaseDao
    }
}