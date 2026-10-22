package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PhotoVaultEntity::class], version = 3, exportSchema = false)
abstract class PhotoVaultDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoVaultDao

    companion object {
        @Volatile
        private var INSTANCE: PhotoVaultDatabase? = null

        fun getDatabase(context: Context): PhotoVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoVaultDatabase::class.java,
                    "fotovault_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
