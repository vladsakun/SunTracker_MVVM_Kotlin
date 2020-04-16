package com.example.suntracker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_SUN_INFO_ID = 0

@Entity(tableName = "sun_info")
data class SunInfo(

    val sunrise: String,
    val sunset: String


) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_SUN_INFO_ID
}