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

}