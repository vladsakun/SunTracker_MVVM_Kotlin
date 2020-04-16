package com.example.suntracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.suntracker.data.db.entity.SunInfo
import com.example.suntracker.data.db.entity.SunLocationEntity

@Database(
        entities = [SunInfo::class, SunLocationEntity::class],
        version = 1
)
abstract class SunInfoDatabase : RoomDatabase() {

    abstract fun sunInfoDao(): SunInfoDao
    abstract fun sunLocationDao(): SunLocationDao

    companion object {
        @Volatile
        private var instance: SunInfoDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDB(context).also { instance = it }
        }

        private fun buildDB(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        SunInfoDatabase::class.java, "sun_info.db")
                        .build()

    }
}