package com.zg.quickbase.net


import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.zg.quickbase.net.service.Mainservice
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    private const val BASE_URL = "http://47.107.112.47:8080/"


    var client = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun mainService(): Mainservice {
        return retrofit.create(Mainservice::class.java)
    }

//        .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //            .addConverterFactory(ScalarsConverterFactory.create())


    private val okHttpClient = OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
        .readTimeout(10000, TimeUnit.MILLISECONDS)
        .writeTimeout(10000, TimeUnit.MILLISECONDS).build()

    var downRetrofit: Retrofit? = null
    fun downloadFile(
        testApkUrl: String,
        listener: ProgressListener?,
        callback: retrofit2.Callback<ResponseBody>
    ) {
        if (downRetrofit == null) {
            val clientDown = okHttpClient.newBuilder().addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder().body(ProgressResponseBody(response.body(), listener)).build()
            }.build()

            downRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientDown)
                .build()
        }
        downRetrofit?.create(Mainservice::class.java)?.download(testApkUrl)?.enqueue(callback)
    }


    interface ProgressListener {
        fun onProgress(currentBytes: Long, contentLength: Long, done: Boolean)
    }


    class ProgressModel : Parcelable {
        var currentBytes: Long
        var contentLength: Long
        var isDone = false

        constructor(currentBytes: Long, contentLength: Long, done: Boolean) {
            this.currentBytes = currentBytes
            this.contentLength = contentLength
            isDone = done
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {
            parcel.writeLong(currentBytes)
            parcel.writeLong(contentLength)
            parcel.writeByte((if (isDone == true) 1 else 0).toByte())
        }

        protected constructor(parcel: Parcel) {
            currentBytes = parcel.readLong()
            contentLength = parcel.readLong()
            isDone = parcel.readByte().toInt() != 0
        }

        companion object CREATOR : Creator<ProgressModel> {
            override fun createFromParcel(parcel: Parcel): ProgressModel {
                return ProgressModel(parcel)
            }

            override fun newArray(size: Int): Array<ProgressModel?> {
                return arrayOfNulls(size)
            }
        }
    }


}


