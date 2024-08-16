package com.zg.quickbase.net

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class DynamicUrlInterceptor : Interceptor {

    val TAG = "DynamicUrlInterceptor"
    val CANTEEN_TAG = "canteen"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {


        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()

        var newUrl = originalUrl
        val originalUrlStr = originalUrl.toString()

//
//        val cacheUrl = MMKVUtils.instance().getString(KEY_SETTING_API, BASE_URL_DEFAULT)
//            ?: BASE_URL_DEFAULT
//        val canteenCode = MMKVUtils.instance().getString(
//            KEY_CANTEEN_CODE,
//            CANTEEN_CODE_DEFAULT
//        ) ?: CANTEEN_CODE_DEFAULT
//
//        if (cacheUrl == BASE_URL_DEFAULT
//            && canteenCode == CANTEEN_CODE_DEFAULT
//        ) {
//            LogUtils.logD(TAG, "原始代码不需要代理 originalUrl:$originalUrl")
//            return chain.proceed(originalRequest)
//        }
//
//        if (originalUrlStr.contains(CANTEEN_TAG)) {
//            LogUtils.logD(TAG, "需要代理 originalUrl:$originalUrl")
//            val host = cacheUrl.replace("https://", "").replace("/","")
//            val newUrlStr = originalUrl.toString().replace(HOST_DEFAULT, host)
//                .replace(CANTEEN_CODE_DEFAULT, canteenCode)
//            newUrl = HttpUrl.parse(newUrlStr) ?: originalUrl
//            LogUtils.logD(TAG, "http newUrl:$newUrl")
//        } else {
//            LogUtils.logD(TAG, "不需要代理 originalUrl:$originalUrl")
//        }

        // 创建新的请求
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        // 发送新的请求
        return chain.proceed(newRequest)
    }


}
