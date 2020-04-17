package com.example.suntracker.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.suntracker.data.db.entity.SunLocationEntity
import com.example.suntracker.exceptions.LocationPermissionNotGrantedException
import com.example.suntracker.internal.asDeferred
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.*
import java.io.IOException


const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"
const val TAG = "LOCATION"

const val DEFAULT_LAT = 51
const val DEFAULT_LNG = 30

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext

    override suspend fun hasSunLocationChanged(lastSunLocation: SunLocationEntity): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastSunLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }

        return deviceLocationChanged || hasCustomLocationChanged(lastSunLocation)
    }

    override suspend fun getPreferredLocationString(): String {

        return withContext(Dispatchers.IO) {
            val gc = Geocoder(appContext)
            lateinit var ads: List<Address>

            ads = gc.getFromLocationName(getCustomLocationName(), 1)

            try {
                if (isUsingDeviceLocation()) {
                    try {
                        val deviceLocation = getLastDeviceLocation().await()
                            ?: return@withContext "${ads[0].latitude},${ads[0].longitude}"
                        return@withContext "${deviceLocation.latitude},${deviceLocation.longitude}"
                    } catch (e: LocationPermissionNotGrantedException) {
                        return@withContext "${ads[0].latitude},${ads[0].longitude}"
                    }
                } else {
                    return@withContext "${ads[0].latitude},${ads[0].longitude}"
                }
            } catch (e: IndexOutOfBoundsException) {
                return@withContext "${DEFAULT_LAT},${DEFAULT_LNG}"
            }

        }

    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: SunLocationEntity): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        // Comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.03
        return Math.abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                Math.abs(deviceLocation.longitude - lastWeatherLocation.lng) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: SunLocationEntity): Boolean {
        if (!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()
            return customLocationName != lastWeatherLocation.lat.toString()
        }
        return false
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}