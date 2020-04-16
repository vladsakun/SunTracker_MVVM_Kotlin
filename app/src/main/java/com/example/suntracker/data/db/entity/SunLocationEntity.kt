package com.example.suntracker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

const val SUN_LOCATION_ID = 0

@Entity(tableName = "sun_location")
data class SunLocationEntity(
    val lat: Double,
    val lng: Double
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = SUN_LOCATION_ID

}