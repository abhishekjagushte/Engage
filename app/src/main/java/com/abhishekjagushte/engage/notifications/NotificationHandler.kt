package com.abhishekjagushte.engage.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.main.fragments.profile.ARGUMENT_NAME
import com.abhishekjagushte.engage.ui.main.fragments.profile.ARGUMENT_USERNAME
import com.abhishekjagushte.engage.ui.main.fragments.profile.ProfileActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject


class NotificationHandler : FirebaseMessagingService(){

    @Inject
    lateinit var dataRepository: DataRepository

    private val TAG = "NotificationHandler"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        //TODO send new token to server
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
                    friendRequestReceived(p0.data)
                    //dataRepository.friendRequestRecieved(p0.data)
                    //TODO uncomment this
                }
            }

        }
    }

    fun friendRequestReceived(data: MutableMap<String, String>) {

        val intent = Intent(this, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra(ARGUMENT_NAME, data.get("name"))
                intent.putExtra(ARGUMENT_USERNAME, data.get("username"))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        dataRepository.getNotificationChannelID().addOnSuccessListener {
            if(it!=null)
            {
                val notification = NotificationCompat.Builder(this, it.token)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(application.resources.getString(R.string.app_name))
                    .setContentText(application.resources.getString(R.string.FR_received_msg, data.get("name"), data.get("username")))
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