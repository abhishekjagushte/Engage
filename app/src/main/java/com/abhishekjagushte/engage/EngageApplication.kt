package com.abhishekjagushte.engage

import android.app.Application
import androidx.work.*
import com.abhishekjagushte.engage.di.AppComponent
import com.abhishekjagushte.engage.di.DaggerAppComponent
import com.abhishekjagushte.engage.workmanager.factories.EngageWorkerFactory
import com.abhishekjagushte.engage.workmanager.workers.PushWorker
import com.abhishekjagushte.engage.workmanager.workers.SyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EngageApplication : Application(), Configuration.Provider {

    @Inject lateinit var engageWorkerFactory: EngageWorkerFactory
    @Inject lateinit var workManager: WorkManager

    lateinit var appComponent: AppComponent

    private fun initializeComponent(): AppComponent {
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


    override fun onCreate() {
        super.onCreate()
        appComponent = initializeComponent()
        setUpSyncWorker()
        setUpPushWorker()
    }

    private fun setUpSyncWorker(){
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val syncWorker = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES).setConstraints(constraints).build()
        workManager.enqueueUniquePeriodicWork("EngageSync", ExistingPeriodicWorkPolicy.KEEP, syncWorker)
    }

    private fun setUpPushWorker(){
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val pushWorker = PeriodicWorkRequestBuilder<PushWorker>(15, TimeUnit.MINUTES).setConstraints(constraints).build()
        workManager.enqueueUniquePeriodicWork("EngagePush", ExistingPeriodicWorkPolicy.KEEP, pushWorker)
    }

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
