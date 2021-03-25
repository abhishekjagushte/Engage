package com.abhishekjagushte.engage.datasource.remotedatasource

import com.abhishekjagushte.engage.network.CreateGroupRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import org.json.JSONObject
import javax.inject.Inject

class FunctionsSource @Inject constructor(
    private val functions: FirebaseFunctions
){

    fun createGroup(request: JSONObject?): Task<HttpsCallableResult> {
        return functions.getHttpsCallable("createGroup")
            .call(request)
    }

    fun testSync(): Task<HttpsCallableResult> {
        return functions.getHttpsCallable(
            "sync"
        ).call()
    }

    fun markReminderDone(eventID: String, senderID: String, receiverID: String): Task<HttpsCallableResult> {

        val request =  ReminderMarkRequest(
            senderID = senderID,
            receiverID = receiverID,
            eventID = eventID
        )

        val gson = Gson()
        val jsonString = gson.toJson(request)
        var jsonObj: JSONObject? = null
        return functions.getHttpsCallable("markReminderDone").call(jsonObj)
    }

}

class ReminderMarkRequest(val senderID: String, val receiverID: String, val eventID: String)