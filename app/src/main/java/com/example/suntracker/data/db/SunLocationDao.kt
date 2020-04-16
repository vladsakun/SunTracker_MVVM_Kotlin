package com.example.suntracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.suntracker.data.db.entity.SUN_LOCATION_ID
import com.example.suntracker.data.db.entity.SunLocationEntity

@Dao
interface SunLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(sunLocationEntity: SunLocationEntity)

    @Query("SELECT * FROM sun_location WHERE id=$SUN_LOCATION_ID")
    fun getLocation(): LiveData<SunLocationEntity>

    @Query("SELECT * FROM sun_location WHERE id=$SUN_LOCATION_ID")
    fun getLocationNonLive(): SunLocationEntity?
}