package com.abhishekjagushte.engage.datasource.localdatasource

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import javax.inject.Inject

class FirebaseInstanceSource @Inject constructor(
    private val firebaseInstanceId: FirebaseInstanceId
){
    private val TAG = "FirebaseInstanceSource"

    fun getNotificationChannelID(): Task<InstanceIdResult> {
        Log.d(TAG,"Inside FirebaseInstanceSiurce")
        return firebaseInstanceId.instanceId
    }

}