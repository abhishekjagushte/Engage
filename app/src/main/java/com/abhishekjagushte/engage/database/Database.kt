package com.abhishekjagushte.engage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Contact::class,
        UserData::class,
        ContactsConversationsCrossRef::class,
        Conversation::class,
        SuggestedContacts::class],
    version = 1,
    exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract val databaseDao: DatabaseDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase?= null

        fun getInstance(context: Context): AppDatabase{

            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "engage_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

}