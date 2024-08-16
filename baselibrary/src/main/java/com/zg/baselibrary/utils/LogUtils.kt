package com.zg.baselibrary.utils

import android.util.Log

object LogUtils {
    // 快速过滤所有主动打印的log
    const val BASE_TAG = "base-"


    fun log(msg: String) {
        Log.d(BASE_TAG, msg)
    }

    fun logD(tag: String, msg: String) {
        Log.d(BASE_TAG + tag, msg)
    }

    fun logI(tag: String, msg: String) {
        Log.i(BASE_TAG + tag, msg)
    }

    fun logE(tag: String, msg: String) {
        Log.e(BASE_TAG + tag, msg)
    }
}
