package com.zg.quickbase.net


import com.zg.quickbase.net.service.Mainservice
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "http://47.107.112.47:8080/"


    var client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.NONE))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun mainService(): Mainservice {
        return retrofit.create(Mainservice::class.java)
    }
}


