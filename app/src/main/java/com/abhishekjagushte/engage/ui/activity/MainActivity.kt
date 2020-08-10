package com.abhishekjagushte.engage.ui.activity

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    @Inject lateinit var dataRepository: DataRepository

    lateinit var viewModel: MainActivityViewModel

    private val TAG = "MainActivity"

    private val mainActivityJob = Job()
    val mainActivityScope = CoroutineScope(Dispatchers.Main + mainActivityJob)
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        //Dependency Injection (should be done before onCreate
        (application as EngageApplication).appComponent.inject(this)
        //(application as EngageApplication).appComponent.addMainComponent().create().inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory =
            MainActivityViewModelFactory(
                dataRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        val navController = findNavController(R.id.main_activity_nav_host);
        NavigationUI.setupWithNavController(bottomNavigationView,navController)
        navController.addOnDestinationChangedListener(this)

        //test()

        setUnsentMessageSender()
        //setUnPushedConversationsPusher()

        viewModel.testSetMessageListener()
    }

//    private fun setUnPushedConversationsPusher(){
//        viewModel.getUnPushedConversations().observe(this , Observer {
//            it?.let{
//               viewModel.pushConversations(it)
//            }
//        })
//    }

    private fun setUnsentMessageSender(){
        viewModel.getUnsentMessages().observe(this, Observer {
            it?.let {
                //Log.d(TAG, "setUnsentMessageSender: Triggered")
               viewModel.sendMessages(it)
            }
        })
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
            R.id.chooseMethodFragment -> bottomNavigationView.visibility = View.GONE

//            //Idk what destination is this
//            2131230819 -> bottomNavigationView.visibility = View.GONE

            else -> bottomNavigationView.visibility = View.VISIBLE
        }
    }


    private fun test(){
        //        dataRepository.addContactsTest()
//
//        dataRepository.getNotificationChannelID().addOnSuccessListener {
//            Log.d(TAG, it.token)
//            dataRepository.updateNotificationChannelID(it.token)
//        }

//        Log.d(TAG, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()))
//        Log.d(TAG, OffsetDateTime.now().toString())

        //val f = FirebaseFirestore.getInstance()
        //Log.d(TAG, f.collection("users").document().id + "is the id")

        //dataRepository.addTestDateData()
        //dataRepository.getTestDateData()

//
//        val date = Instant.parse("2020-06-17T08:06:58.133Z")
        //Log.d(TAG, "test: ${date.toString()} is the instant")
        
//        val offsetDateTime = OffsetDateTime.now()
//        Log.d(TAG, "test: ${offsetDateTime}")
//
//
//        Log.d(TAG, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime))
//
//        Log.d(TAG+"********", intent?.dataString?:"Not Found")

//        val o = OffsetDateTime.now(ZoneOffset.UTC)

        val time = 1592406179920
        //val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss")
        //val sdf = SimpleDateFormat("yyyy-MM-dd.HH:mm:ss", Locale.ENGLISH)
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getDefault()
        println(sdf.format(time))

    }
}
