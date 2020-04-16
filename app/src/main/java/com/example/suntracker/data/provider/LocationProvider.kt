package com.example.suntracker.data.provider

import android.location.Address
import android.location.Location
import com.example.suntracker.data.db.entity.SunLocationEntity

interface LocationProvider {

    suspend fun hasSunLocationChanged(lastSunLocation:SunLocationEntity):Boolean
    suspend fun getPreferredLocationString(): String
}