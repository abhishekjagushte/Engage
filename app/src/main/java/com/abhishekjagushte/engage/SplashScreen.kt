package com.abhishekjagushte.engage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.abhishekjagushte.engage.database.UserData
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.main.MainActivity
import com.abhishekjagushte.engage.ui.setup.SetupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashScreen : AppCompatActivity() {

    @Inject
    lateinit var dataRepository: DataRepository

    private val TAG = "SplashScreen"

    private val splashScreenJob = Job()
    private val splashScreenScope = CoroutineScope(Dispatchers.Main + splashScreenJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as EngageApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        val liveUserData = MutableLiveData<UserData?>()
//
//        dataRepository.getCurrentLoggedInUserCredentials(liveUserData)

        if(dataRepository.getCurrentUser() == null){

            //If by any chance if this doesn't work then sign in using the saved email and password of the user

            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}


//        liveUserData.observe(this, Observer {
//
//            if(it == null){
//                val intent = Intent(this, SetupActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//            else{
//
//                Log.d(TAG, it.toString())
//                dataRepository.regularLogin(it).addOnSuccessListener {
//                        Log.d(TAG, dataRepository.getCurrentUserUID())
//                }
//
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        })