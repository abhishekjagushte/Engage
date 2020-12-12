package com.abhishekjagushte.engage.di

import com.abhishekjagushte.engage.datasource.remotedatasource.FirebaseStorageSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class NetworkModule (){

    @Provides
    @Singleton
    fun provideFirebaseDataSource(): FirebaseFirestore{

        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthSource(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseInstanceSource(): FirebaseInstanceId{
        return FirebaseInstanceId.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFunctionsSource(): FirebaseFunctions{
        return FirebaseFunctions.getInstance()
    }

    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

}