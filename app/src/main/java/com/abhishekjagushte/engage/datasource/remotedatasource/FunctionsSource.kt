package com.abhishekjagushte.engage.datasource.remotedatasource

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
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
        val data: Map<String, String> = mapOf(Pair("senderID", senderID), Pair("receiverID", receiverID), Pair("eventID", eventID))
        return functions.getHttpsCallable("markReminderDone").call(data)
    }

}

class ReminderMarkRequest(val senderID: String, val receiverID: String, val eventID: String)