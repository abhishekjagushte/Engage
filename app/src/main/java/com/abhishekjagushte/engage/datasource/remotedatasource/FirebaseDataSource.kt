package com.abhishekjagushte.engage.datasource.remotedatasource

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
){


}