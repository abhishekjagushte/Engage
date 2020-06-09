package com.abhishekjagushte.engage.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import javax.inject.Inject

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    @Inject
    lateinit var dataRepository: DataRepository

    private val TAG = "SplashScreen"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity().application as EngageApplication).appComponent.inject(this)

        val navController = Navigation.findNavController(view)

        if(dataRepository.getCurrentUser() == null){
            //TODO If by any chance if this doesn't work then sign in using the saved email and password of the user
            navController.navigate(R.id.action_splashScreen_to_setup_activity_nav_graph)
            navController.popBackStack(R.id.chatListFragment, false)
        }else{
            navController.navigate(R.id.action_splashScreen_to_chatListFragment)
            navController.popBackStack(R.id.chatListFragment, false)
        }
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        (application as EngageApplication).appComponent.inject(this)
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_splash_screen)
//
////        val liveUserData = MutableLiveData<UserData?>()
////
////        dataRepository.getCurrentLoggedInUserCredentials(liveUserData)
//
//        if(dataRepository.getCurrentUser() == null){
//            //TODO If by any chance if this doesn't work then sign in using the saved email and password of the user
//            val intent = Intent(this, SetupActivity::class.java)
//            startActivity(intent)
//            finish()
//        }else{
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//    }
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