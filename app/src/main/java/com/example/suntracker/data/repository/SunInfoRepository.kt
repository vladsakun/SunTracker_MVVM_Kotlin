package com.example.suntracker.data.repository

import androidx.lifecycle.LiveData
import com.example.suntracker.data.db.entity.SunInfo
import com.example.suntracker.data.db.entity.SunLocationEntity

interface SunInfoRepository {

    suspend fun getCurrentSunInfo():LiveData<out SunInfo>

    suspend fun getSunLocation():LiveData<SunLocationEntity>
}