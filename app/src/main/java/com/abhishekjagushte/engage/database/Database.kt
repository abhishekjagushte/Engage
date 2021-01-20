package com.abhishekjagushte.engage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.abhishekjagushte.engage.database.entities.*
import com.abhishekjagushte.engage.database.views.ConversationView
import com.abhishekjagushte.engage.database.views.MessageNotificationView
import com.abhishekjagushte.engage.database.views.MessageView


@Database(
    entities = [Contact::class,
        UserData::class,
        ContactsConversationsCrossRef::class,
        Conversation::class,
        SuggestedContacts::class,
        Message::class,
        Event::class],
    version = 1,
    views = [MessageView::class, ConversationView::class, MessageNotificationView::class],
    exportSchema = false)
@TypeConverters(Converters::class)

abstract class AppDatabase: RoomDatabase(){

    abstract val databaseDao: DatabaseDao

    companion object{

        @Volatile
        private var INSTANCE: AppDatabase?= null
        private val CONVERSATIONS_TRIGGER = object: RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("CREATE TRIGGER IF NOT EXISTS conversations_trigger_message AFTER INSERT ON messages " +
                        "BEGIN " +
                        "UPDATE conversations SET lastMessageID = new.messageID WHERE conversationID = new.conversationID;" +
                        "END; " +
                        "END;")
            }
        }

        fun getInstance(context: Context): AppDatabase{

            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "engage_database"
                    ).addCallback(CONVERSATIONS_TRIGGER)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}