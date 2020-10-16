package com.abhishekjagushte.engage

import android.app.Application
import androidx.work.Configuration
import com.abhishekjagushte.engage.di.AppComponent
import com.abhishekjagushte.engage.di.DaggerAppComponent
import com.abhishekjagushte.engage.workmanager.factories.EngageWorkerFactory
import javax.inject.Inject

class EngageApplication : Application(), Configuration.Provider {

    @Inject lateinit var engageWorkerFactory: EngageWorkerFactory

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        val component =  DaggerAppComponent.factory().create(this)
        component.inject(this)
        return component
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(engageWorkerFactory)
            .build()

}


//    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//        DaggerAppComponent.builder().application(this)
//        return DaggerAppComponent.builder().build()
//    }


//    val appComponent: AppComponent by lazy {
//        // Creates an instance of AppComponent using its Factory constructor
//        // We pass the applicationContext that will be used as Context in the graph
//        DaggerAppComponent.factory().create(applicationContext)
//    }
