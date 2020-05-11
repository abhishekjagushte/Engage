package com.abhishekjagushte.engage.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var dataRepository: DataRepository

    private val TAG = "MainActivity"

    val mainActivityJob = Job()
    val mainActivityScope = CoroutineScope(Dispatchers.Main + mainActivityJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as EngageApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataRepository.addContactsTest()

        dataRepository.getNotificationChannelID().addOnSuccessListener {
            Log.d(TAG, it.token)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        val navController = findNavController(R.id.main_activity_nav_host)




        NavigationUI.setupWithNavController(bottomNavigationView,navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }
}
