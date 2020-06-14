package com.abhishekjagushte.engage.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    @Inject lateinit var dataRepository: DataRepository

    private val TAG = "MainActivity"

    val mainActivityJob = Job()
    val mainActivityScope = CoroutineScope(Dispatchers.Main + mainActivityJob)
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as EngageApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataRepository.addContactsTest()

        dataRepository.getNotificationChannelID().addOnSuccessListener {
            Log.d(TAG, it.token)
            dataRepository.updateNotificationChannelID(it.token)
        }

        Log.d(TAG, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()))
        Log.d(TAG, OffsetDateTime.now().toString())

        //dataRepository.addTestDateData()
        //dataRepository.getTestDateData()


        val date = Instant.now()
        val offsetDateTime = date
            .atOffset(ZoneOffset.UTC)

        Log.d(TAG, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime))

        Log.d(TAG+"********", intent?.dataString?:"Not Found")

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navController = findNavController(R.id.main_activity_nav_host);

        NavigationUI.setupWithNavController(bottomNavigationView,navController)

        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        Log.d(TAG, "SplashScreen = ${R.id.splashScreen}, nav_graph = ${R.id.setup_activity_nav_graph}," +
                " chat_list = ${R.id.chatListFragment} dest = ${destination.id} and people = ${R.id.peopleFragment}" +
                "event = ${R.id.eventsFragment} search = ${R.id.searchFragment}")

        when(destination.id){
            R.id.profileFragment -> bottomNavigationView.visibility = View.GONE
            R.id.chatFragment -> bottomNavigationView.visibility = View.GONE
            R.id.splashScreen -> bottomNavigationView.visibility = View.GONE

            R.id.loginFragment -> bottomNavigationView.visibility = View.GONE
            R.id.setUsernameFragment -> bottomNavigationView.visibility = View.GONE
            R.id.signUpFragment -> bottomNavigationView.visibility = View.GONE

            //Idk what destination is this
            2131230819 -> bottomNavigationView.visibility = View.GONE

            else -> bottomNavigationView.visibility = View.VISIBLE
        }
    }
}
