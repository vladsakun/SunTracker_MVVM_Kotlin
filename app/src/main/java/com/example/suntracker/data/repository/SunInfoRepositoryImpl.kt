package com.example.suntracker.data.repository

import androidx.lifecycle.LiveData
import com.example.suntracker.data.db.SunInfoDao
import com.example.suntracker.data.db.SunLocationDao
import com.example.suntracker.data.db.entity.SunInfo
import com.example.suntracker.data.db.entity.SunLocationEntity
import com.example.suntracker.data.network.SunInfoNetworkDataSource
import com.example.suntracker.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SunInfoRepositoryImpl(
    private val sunInfoDao: SunInfoDao,
    private val locationDao: SunLocationDao,
    private val sunInfoNetworkDataSource: SunInfoNetworkDataSource,
    private val locationProvider: LocationProvider

) : SunInfoRepository {

    init {
        sunInfoNetworkDataSource.apply {
            downloadedCurrentSunInfo.observeForever { newSunInfo ->
                persistFetchedSunInfo(newSunInfo)
            }
        }
    }

    private fun persistFetchedSunInfo(newSunInfo: SunInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            sunInfoDao.upsert(newSunInfo)
        }
    }

    override suspend fun getCurrentSunInfo(): LiveData<out SunInfo> {
        return withContext(Dispatchers.IO) {
            initSunInfoData()
            return@withContext sunInfoDao.getCurrentSunInfo()
        }
    }

    private suspend fun initSunInfoData() {
        val lastSunLocation = locationDao.getLocationNonLive()

        if (lastSunLocation == null || locationProvider.hasSunLocationChanged(lastSunLocation)) {
            fetchCurrentSunInfo()
            return
        }

        fetchCurrentSunInfo()
    }

    private suspend fun fetchCurrentSunInfo() {

        val locationString = locationProvider.getPreferredLocationString()
        val locationArray: List<String> = locationString.split(",")

        sunInfoNetworkDataSource.fetchCurrentSunInfo(
            locationArray[0].toDouble(),
            locationArray[1].toDouble()
        )
    }

    override suspend fun getSunLocation(): LiveData<SunLocationEntity> {
        return withContext(Dispatchers.IO) {
            return@withContext locationDao.getLocation()
        }
    }
}