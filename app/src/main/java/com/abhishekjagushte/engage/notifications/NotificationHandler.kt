package com.abhishekjagushte.engage.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.navigation.NavDeepLinkBuilder
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.Message
import com.abhishekjagushte.engage.database.views.MessageNotificationView
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import javax.inject.Inject
import kotlin.random.Random

const val TYPE_FR_ACCEPTED = 1
const val TYPE_FR_RECEIVED = 2
const val TYPE_MSG_RECEIVED_121 =  3

class NotificationHandler : FirebaseMessagingService(){

    @Inject
    lateinit var dataRepository: DataRepository
    private val TAG = "NotificationHandler"

    private val job = Job()
    private val coroutinScope = CoroutineScope(Dispatchers.Main + job)

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        (application as EngageApplication).appComponent.inject(this)
        dataRepository.updateNotificationChannelID(p0)
        Log.d(TAG, "Notification token updated $p0")
    }


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        (application as EngageApplication).appComponent.inject(this)
        //Log.d(TAG, "Inside onMessageReceived")
        //Log.d(TAG, "Data size ${p0.data.size} + ${p0.data}")

        //TODO don't make change in friend status if already confirmed
        //Check if data is available
        if(p0.data.size>0){
            when(p0.data.get("type")){
                "1" -> {
                    buildNotification(p0.data, TYPE_FR_RECEIVED)
                    dataRepository.friendRequestReceived(p0.data)
                }

                "2" -> {
                    buildNotification(p0.data, TYPE_FR_ACCEPTED)
                    dataRepository.friendRequestAccepted(p0.data)
                }

                "3" -> {
                    //Log.d(TAG, "onMessageReceived: ${p0.data.toString()}")
                    val msg = Message.mapToMessage121(p0.data)
                    //Log.d(TAG, "onMessageReceived: " +
                      //      "${msg.messageID} ${msg.senderID} ${msg.receiverID}")
                    coroutinScope.launch {
                        withContext(Dispatchers.IO) {
                            //TODO this is testing
                            //dataRepository.receiveMessage121(msg)
                            //makeMessageNotification(msg)
                        }
                    }
                }

                "4" -> {
                    //Log.d(TAG, "onMessageReceived: Added in group")
                    addNewGroup(p0.data)
                }

                "5" -> {
                    //Log.d(TAG, "onMessageReceived: M2M chat message = ${p0.data}")
                    val msg = Message.mapToMessageM2M(p0.data)
                    coroutinScope.launch {
                        withContext(Dispatchers.IO){
                            //TODO this is testing
                            //dataRepository.receiveMessageM2M(msg)
                            //makeMessageNotification(msg)
                        }
                    }
                }
            }
        }
    }

    //TODO make a new view to get only what is needed here to show notification
    //TODO See how to combine multiple notifications which belong to one chat/conversation
    //TODO add logic to remove notifications when clicked on them
    private fun makeMessageNotification(message: Message) {
        dataRepository.getNotificationChannelID().addOnSuccessListener {
            coroutinScope.launch {
                withContext(Dispatchers.IO){
                    //Log.d(TAG, "makeMessageNotification: ${message.data}")

                    val msgNotification = dataRepository.getMessageNotification(message.messageID)

                    val args = Bundle()
                    args.putString("conversationID", message.conversationID)

                    val pendingIntent = NavDeepLinkBuilder(this@NotificationHandler)
                        .setGraph(R.navigation.main_activity_nav_graph)
                        .setDestination(R.id.chatFragment)
                        .setArguments(args)
                        .createPendingIntent()


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

                    val notification = NotificationCompat.Builder(this@NotificationHandler, it.token)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setStyle(msgStyle)
                        .setContentIntent(pendingIntent)

                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
                }
            }
        }
    }

    //This will provide different text if mime type is different
    private fun getNotificationText(msgNotification: MessageNotificationView): String {
        return when(msgNotification.mime_type){
            Constants.MIME_TYPE_TEXT -> msgNotification.data!!
            else -> throw IllegalStateException("Message mime type is wrong")
        }
    }

    private fun addNewGroup(data: Map<String, String>) {
        val name: String? = data["name"]
        val conversationID: String? = data["conversationID"]
        dataRepository.addNewGroup(name, conversationID)
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
            it?.let{

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

    private fun makeAllMessagesNotification() {
        dataRepository.getNotificationChannelID().addOnSuccessListener {
            it?.let {

                coroutinScope.launch {
                    withContext(Dispatchers.IO){
                        val messages = dataRepository.getUnreadMessages()

                        for(message in messages){
                            //Log.d(TAG, "makeMessageNotification: ${message.data}")

                            val args = Bundle()
                            args.putString("conversationID", message.conversationID)

                            val pendingIntent = NavDeepLinkBuilder(this@NotificationHandler)
                                .setGraph(R.navigation.main_activity_nav_graph)
                                .setDestination(R.id.chatFragment)
                                .setArguments(args)
                                .createPendingIntent()


                            val person = Person.Builder()
                                .setName(message.name)
                                .setKey(message.senderID)
                                .build()

                            val msg = NotificationCompat.MessagingStyle.Message(
                                message.data,
                                message.timeStamp!!,
                                person
                            )

                            val msgStyle = NotificationCompat.MessagingStyle(person)
                                .addMessage(msg)

                            if(message.conType == Constants.CONVERSATION_TYPE_M2M)
                                msgStyle.conversationTitle = message.conversationID

                            val notification = NotificationCompat.Builder(this@NotificationHandler, it.token)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setStyle(msgStyle)
                                .setContentIntent(pendingIntent)

                            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(it.token,
                                    "Messages",
                                    NotificationManager.IMPORTANCE_DEFAULT
                                )
                                manager.createNotificationChannel(channel)
                            }

                            manager.notify(message.messageID, Random(System.currentTimeMillis()).nextInt(1000), notification.build())
                        }
                    }
                }
            }
        }
    }
}

/*
E/AndroidRuntime: FATAL EXCEPTION: main
Process: com.abhishekjagushte.engage, PID: 12689
java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.abhishekjagushte.engage.database.views.MessageNotificationView.getNickname()' on a null object reference
at com.abhishekjagushte.engage.notifications.NotificationHandler$makeMessageNotification$1$1$1.invokeSuspend(NotificationHandler.kt:120)
at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:56)
at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:738)
at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
*/


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