package com.nakharin.placesapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by ton on 6/22/2016 AD.
 */
class ConnectionService {

    companion object {

        private fun getRetrofit(): Retrofit {

            val httpClient = OkHttpClient.Builder()

            httpClient.connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)

            // Set log
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)

            return Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
        }

        fun getApiService(): APIService {
            return getRetrofit().create(APIService::class.java)
        }
    }
}
