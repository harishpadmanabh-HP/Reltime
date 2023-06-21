package com.accubits.reltime.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 3, exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {
    abstract val contactDao: ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null
        fun getInstance(context: Context): ContactDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "contact_database"
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }
    }
}

