package com.abhishekjagushte.engage

import android.app.Application
import com.abhishekjagushte.engage.di.AppComponent
import com.abhishekjagushte.engage.di.DaggerAppComponent
import com.abhishekjagushte.engage.di.LocalStorageModule
import com.abhishekjagushte.engage.di.NetworkModule
import com.abhishekjagushte.engage.repository.DataRepository
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class EngageApplication : Application(){

//    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//        DaggerAppComponent.builder().application(this)
//        return DaggerAppComponent.builder().build()
//    }


    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerAppComponent.factory().create(this)
    }


//    val appComponent: AppComponent by lazy {
//        // Creates an instance of AppComponent using its Factory constructor
//        // We pass the applicationContext that will be used as Context in the graph
//        DaggerAppComponent.factory().create(applicationContext)
//    }



}