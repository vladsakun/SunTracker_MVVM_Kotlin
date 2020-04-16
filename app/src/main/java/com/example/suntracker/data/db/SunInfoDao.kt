package com.example.suntracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.suntracker.data.db.entity.CURRENT_SUN_INFO_ID
import com.example.suntracker.data.db.entity.SunInfo

@Dao
interface SunInfoDao {

    @Query("DELETE FROM sun_info WHERE id=:id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM sun_info WHERE id = $CURRENT_SUN_INFO_ID")
    fun getCurrentSunInfo(): LiveData<SunInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(sunInfo: SunInfo)

    @Query("DELETE FROM sun_info")
    fun deleteAll()
}