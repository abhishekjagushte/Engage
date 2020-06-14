package com.abhishekjagushte.engage.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject


const val TYPE_FR_ACCEPTED = 1
const val TYPE_FR_RECEIVED = 2

class NotificationHandler : FirebaseMessagingService(){

    @Inject
    lateinit var dataRepository: DataRepository

    private val TAG = "NotificationHandler"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        (application as EngageApplication).appComponent.inject(this)
        dataRepository.updateNotificationChannelID(p0)
        Log.d(TAG, "Notification token updated $p0")
    }


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        (application as EngageApplication).appComponent.inject(this)
        Log.d(TAG, "Inside onMessageReceived")
        Log.d(TAG, "Data size ${p0.data.size}")

        //Check if data is available
        if(p0.data.size>0){
            when(p0.data.get("type")){
                "1" -> {
                    buildNotification(p0.data, TYPE_FR_RECEIVED)
                    dataRepository.friendRequestRecieved(p0.data)
                }

                "2" -> {
                    buildNotification(p0.data, TYPE_FR_ACCEPTED)
                    dataRepository.friendRequestAccepted(p0.data)
                }
            }
        }
    }

    private fun buildNotification(data: Map<String, String>, type: Int){
        when(type){

            TYPE_FR_RECEIVED -> {
                val title: String = application.resources.getString(R.string.app_name)
                val content = application.resources.getString(R.string.FR_received_msg, data.get("name"), data.get("username"))
                val args = Bundle()
                args.putString(Constants.ARGUMENT_USERNAME, data.get(Constants.ARGUMENT_USERNAME))
                args.putString(Constants.ARGUMENT_NAME, data.get(Constants.ARGUMENT_NAME))
                makeNotification(title, content, args, R.id.profileFragment)
            }

            TYPE_FR_ACCEPTED -> {
                val title: String = application.resources.getString(R.string.app_name)
                val content = application.resources.getString(R.string.FR_accepted_msg, data.get("name"), data.get("username"))
                val args = Bundle()
                args.putString(Constants.ARGUMENT_USERNAME, data.get(Constants.ARGUMENT_USERNAME))
                args.putString(Constants.ARGUMENT_NAME, data.get(Constants.ARGUMENT_NAME))
                makeNotification(title, content, args, R.id.profileFragment)
            }

        }
    }

    private fun makeNotification(title: String, content: String, args: Bundle, destination: Int){

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_activity_nav_graph)
            .setDestination(destination)
            .setArguments(args)
            .createPendingIntent()

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        dataRepository.getNotificationChannelID().addOnSuccessListener {
            if(it!=null)
            {
                val notification = NotificationCompat.Builder(this, it.token)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)

                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(it.token,
                        "Friend Requests",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    manager.createNotificationChannel(channel)
                }

                manager.notify(0, notification.build())
            }
        }
    }

}


//private fun friendRequestAccepted(data: Map<String, String>) {
//    val intent = Intent(this, ProfileFragment::class.java)
//    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//    intent.putExtra(ARGUMENT_NAME, data.get("name"))
//    intent.putExtra(ARGUMENT_USERNAME, data.get("username"))
//    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//
//    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//    dataRepository.getNotificationChannelID().addOnSuccessListener {
//        if(it!=null)
//        {
//            val notification = NotificationCompat.Builder(this, it.token)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle(application.resources.getString(R.string.app_name))
//                .setContentText(application.resources.getString(R.string.FR_accepted_msg, data.get("name"), data.get("username")))
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(it.token,
//                    "Friend Requests",
//                    NotificationManager.IMPORTANCE_DEFAULT
//                )
//                manager.createNotificationChannel(channel)
//            }
//
//            manager.notify(0, notification.build())
//        }
//    }
//}
//
//fun friendRequestReceived(data: MutableMap<String, String>) {
//
//    val intent = Intent(this, ProfileFragment::class.java)
//    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//    intent.putExtra(ARGUMENT_NAME, data.get("name"))
//    intent.putExtra(ARGUMENT_USERNAME, data.get("username"))
//    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//
//    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//    dataRepository.getNotificationChannelID().addOnSuccessListener {
//        if(it!=null)
//        {
//            val notification = NotificationCompat.Builder(this, it.token)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle(application.resources.getString(R.string.app_name))
//                .setContentText(application.resources.getString(R.string.FR_received_msg, data.get("name"), data.get("username")))
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(it.token,
//                    "Friend Requests",
//                    NotificationManager.IMPORTANCE_DEFAULT
//                )
//                manager.createNotificationChannel(channel)
//            }
//
//            manager.notify(0, notification.build())
//        }
//    }
//}