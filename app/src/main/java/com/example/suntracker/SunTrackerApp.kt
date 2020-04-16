package com.example.suntracker

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.example.suntracker.data.db.SunInfoDao
import com.example.suntracker.data.db.SunInfoDatabase
import com.example.suntracker.data.network.*
import com.example.suntracker.data.provider.LocationProvider
import com.example.suntracker.data.provider.LocationProviderImpl
import com.example.suntracker.data.repository.SunInfoRepository
import com.example.suntracker.data.repository.SunInfoRepositoryImpl
import com.example.suntracker.ui.suninfo.CurrentSunInfoViewModelFactory
import com.google.android.gms.location.LocationServices
import okhttp3.internal.connection.ConnectInterceptor
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class SunTrackerApp : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@SunTrackerApp))

        //Database
        bind() from singleton { SunInfoDatabase(instance()) }

        //Dao(s)
        bind() from singleton { instance<SunInfoDatabase>().sunInfoDao() }
        bind() from singleton { instance<SunInfoDatabase>().sunLocationDao() }

        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }

        //Api service
        bind() from singleton { SunApiService(instance()) }

        //Network Data Source
        bind<SunInfoNetworkDataSource>() with singleton { SunInfoNetworkDataSourceImpl(instance()) }

        //Repository
        bind<SunInfoRepository>() with singleton {
            SunInfoRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        //Location
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }

        //ViewModelFactories
        bind() from provider { CurrentSunInfoViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()

        //default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}