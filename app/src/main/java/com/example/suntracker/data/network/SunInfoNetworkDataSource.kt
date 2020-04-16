package com.example.suntracker.data.network

import androidx.lifecycle.LiveData
import com.example.suntracker.data.db.entity.SunInfo

interface SunInfoNetworkDataSource {
    val downloadedCurrentSunInfo: LiveData<SunInfo>

    suspend fun fetchCurrentSunInfo(
        lat: Double,
        lng: Double
    )
}