package com.example.suntracker.data.network

import com.example.suntracker.data.db.entity.SunInfo
import com.example.suntracker.data.response.Results
import com.example.suntracker.data.response.SunInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SunApiService {

    @GET("json")
    fun getCurrentSunInfo(
        @Query("lat") lat:Double,
        @Query("lng") lng:Double
    ):Deferred<SunInfoResponse>
    companion object {

        const val BASE_URL = "https://api.sunrise-sunset.org/"
        const val DEFAULT_DATE = "today"

        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): SunApiService {
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("date", DEFAULT_DATE)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SunApiService::class.java)
        }
    }
}