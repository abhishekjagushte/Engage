package com.abhishekjagushte.engage.broadcastreceiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.navigation.NavDeepLinkBuilder
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var dataRepository: DataRepository

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private val TAG = "AlarmReceiver"


    override fun onReceive(context: Context?, intent: Intent?) {
        (context!!.applicationContext as EngageApplication).appComponent.inject(this)
        makeEventReminderNotification(intent!!.getBundleExtra("args")!!, context)
    }


    fun makeEventReminderNotification(eventArgs: Bundle, context: Context?) {
        dataRepository.getNotificationChannelID().addOnSuccessListener {
            coroutineScope.launch {
                withContext(Dispatchers.IO){

                    val conversationID = eventArgs.getString(Constants.CONVERSATION_ID)
                    val title = eventArgs.getString(Constants.TITLE)
                    val description = eventArgs.getString(Constants.DESCRIPTION)
                    val reminderTime = eventArgs.getLong(Constants.REMINDER_TIME)
                    val eventID = eventArgs.getString(Constants.EVENT_ID)!!

                    val eventNotification = dataRepository.getEventNotification(eventID)

                    val args = Bundle()
                    args.putString("conversationID", conversationID)

                    val pendingIntent = NavDeepLinkBuilder(context!!)
                        .setGraph(R.navigation.main_activity_nav_graph)
                        .setDestination(R.id.chatFragment)
                        .setArguments(args)
                        .createPendingIntent()

                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


                    val content = "Reminder: $title | $description"

                    val notification = NotificationCompat.Builder(context, it.token)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(eventNotification.name)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)


                    val manager = NotificationManagerCompat.from(context)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            it.token,
                            "Events",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        manager.createNotificationChannel(channel)
                    }

                    manager.notify(
                        eventID,
                        Random(System.currentTimeMillis()).nextInt(1000),
                        notification.build()
                    )
                    Log.d(TAG, "makeEventNotification: senttt")
                }
            }
        }
    }

}