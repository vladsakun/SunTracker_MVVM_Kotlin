package com.example.suntracker.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suntracker.data.db.entity.SunInfo
import com.example.suntracker.exceptions.NoConnectivityException

class SunInfoNetworkDataSourceImpl(
    private val sunApiService: SunApiService
) : SunInfoNetworkDataSource {

    private val _downloadedCurrentSunInfo = MutableLiveData<SunInfo>()
    override val downloadedCurrentSunInfo: LiveData<SunInfo>
        get() = _downloadedCurrentSunInfo

    override suspend fun fetchCurrentSunInfo(lat: Double, lng: Double) {
        try {
            val fetchedSunInfo = sunApiService.getCurrentSunInfo(lat, lng).await()
            val sunInfo = SunInfo(
                fetchedSunInfo.results?.sunrise.toString(),
                fetchedSunInfo.results?.sunset.toString()
            )
            _downloadedCurrentSunInfo.postValue(sunInfo)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }
}