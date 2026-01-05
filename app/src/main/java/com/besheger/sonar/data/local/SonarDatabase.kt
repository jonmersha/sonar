package com.besheger.sonar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.besheger.sonar.data.local.dao.TrackDao
import com.besheger.sonar.data.local.entity.SonarTrackEntity

@Database(entities = [SonarTrackEntity::class], version = 1, exportSchema = false)
abstract class SonarDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao

    companion object {
        @Volatile
        private var INSTANCE: SonarDatabase? = null

        fun getDatabase(context: Context): SonarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SonarDatabase::class.java,
                    "sonar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}