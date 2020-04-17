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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln

class SunInfoRepositoryImpl(
    private val sunInfoDao: SunInfoDao,
    private val locationDao: SunLocationDao,
    private val sunInfoNetworkDataSource: SunInfoNetworkDataSource,
    private val locationProvider: LocationProvider

) : SunInfoRepository {

    private var dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
    private var displayDateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

    init {

        sunInfoNetworkDataSource.apply {
            downloadedCurrentSunInfo.observeForever { newSunInfo ->
                persistFetchedSunInfo(newSunInfo)
            }
        }
    }

    private fun persistFetchedSunInfo(newSunInfo: SunInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val sunRiseTime = dateFormat.parse(newSunInfo.sunrise)
            val sunSetTime = dateFormat.parse(newSunInfo.sunset)

            displayDateFormat.timeZone = TimeZone.getDefault()

            val preparedSunInfo = SunInfo(displayDateFormat.format(sunRiseTime), displayDateFormat.format(sunSetTime))

            sunInfoDao.upsert(preparedSunInfo)
        }
    }

    private fun persistFetchedLocationData(lat:Double, lng:Double){
        GlobalScope.launch(Dispatchers.IO){
            locationDao.upsert(SunLocationEntity(lat, lng))
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

        persistFetchedLocationData(locationArray[0].toDouble(), locationArray[1].toDouble())

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