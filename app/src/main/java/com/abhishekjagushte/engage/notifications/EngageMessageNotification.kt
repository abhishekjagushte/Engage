package com.abhishekjagushte.engage.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.navigation.NavDeepLinkBuilder
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.database.views.MessageNotificationView
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.*
import kotlin.random.Random

class EngageMessageNotification(
    private val dataRepository: DataRepository,
    private val context: Context
){

    private val job = Job()
    private val coroutineScope = CoroutineScope(job+Dispatchers.Main)

    private val TAG = "EngageMessageNotification"

    fun makeMessageNotification(message: Message) {
        dataRepository.getNotificationChannelID().addOnSuccessListener {
            coroutineScope.launch {
                withContext(Dispatchers.IO){
                    Log.d(TAG, "makeMessageNotification: ${message.data}")

                    val msgNotification = dataRepository.getMessageNotification(message.messageID)

                    val args = Bundle()
                    args.putString("conversationID", message.conversationID)

                    val pendingIntent = NavDeepLinkBuilder(context)
                        .setGraph(R.navigation.main_activity_nav_graph)
                        .setDestination(R.id.chatFragment)
                        .setArguments(args)
                        .createPendingIntent()


                    //TODO Gives error at this stage because of the first message sent out by one creating the group

                    val person = Person.Builder()
                        .setName(msgNotification.nickname?:msgNotification.senderID)
                        .setKey(message.senderID)
                        .build()

                    val msg = NotificationCompat.MessagingStyle.Message(
                        getNotificationText(msgNotification),
                        msgNotification.timeStamp!!,
                        person
                    )

                    val msgStyle = NotificationCompat.MessagingStyle(person)
                        .addMessage(msg)

                    if (message.conType == Constants.CONVERSATION_TYPE_M2M)
                        msgStyle.conversationTitle = msgNotification.name

                    val notification = NotificationCompat.Builder(context, it.token)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setStyle(msgStyle)
                        .setContentIntent(pendingIntent)

                    val manager = NotificationManagerCompat.from(context)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            it.token,
                            "Messages",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        manager.createNotificationChannel(channel)
                    }

                    manager.notify(
                        message.messageID,
                        Random(System.currentTimeMillis()).nextInt(1000),
                        notification.build()
                    )
                    Log.d(TAG, "makeMessageNotification: senttt")
                }
            }
        }
    }


    //This will provide different text if mime type is different
    private fun getNotificationText(msgNotification: MessageNotificationView): String {
        return when(msgNotification.mime_type){
            Constants.MIME_TYPE_TEXT -> msgNotification.data!!
            Constants.MIME_TYPE_IMAGE_JPG -> "Image"//TODO
            else -> throw IllegalStateException("Message mime type is wrong")
        }
    }

}