package com.abhishekjagushte.engage.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.permissions.WriteExternalStoragePermissionHelper
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    @Inject
    lateinit var dataRepository: DataRepository

    lateinit var viewModel: MainActivityViewModel

    private val TAG = "MainActivity"

    private val mainActivityJob = Job()
    val mainActivityScope = CoroutineScope(Dispatchers.Main + mainActivityJob)
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var appBar: AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        //Dependency Injection (should be done before onCreate
        (application as EngageApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory =
            MainActivityViewModelFactory(
                dataRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        val navController = findNavController(R.id.main_activity_nav_host);
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        navController.addOnDestinationChangedListener(this)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.chatListFragment,
                R.id.eventsFragment,
                R.id.searchFragment,
                R.id.peopleFragment
            )
        )
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        appBar = findViewById(R.id.app_bar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        viewModel.set121MessageListener()
        viewModel.set121EventsListener()

        WriteExternalStoragePermissionHelper(
            this,
            this.applicationContext,
            Constants.WRITE_PERMISSION_REQUEST_CODE
        ).permissionsForSave()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.profileFragment -> bottomNavigationView.visibility = View.GONE
            R.id.chatFragment -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.VISIBLE
            }
            R.id.splashScreen -> {
                bottomNavigationView.visibility = View.GONE
            }

            R.id.loginFragment -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.GONE
            }
            R.id.setUsernameFragment -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.GONE
            }
            R.id.signUpFragment -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.GONE
            }
            R.id.chooseMethodFragment -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.GONE
            }
            R.id.bottomSheet -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.GONE
            }

            R.id.imagePreviewFragment -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.GONE
            }
            R.id.createReminderDialog -> bottomNavigationView.visibility = View.GONE

            R.id.imageViewFragment -> {
                bottomNavigationView.visibility = View.GONE
                appBar.visibility = View.GONE
            }
            else -> {
                bottomNavigationView.visibility = View.VISIBLE
                appBar.visibility = View.VISIBLE
            }
        }
    }
}
