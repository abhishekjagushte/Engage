package com.abhishekjagushte.engage.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule (){

    @Provides
    @Singleton
    fun provideFirebaseDataSource(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthSource(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

}