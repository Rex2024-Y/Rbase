package com.zg.quickbase.utils

import android.util.Log

object LogUtils {
    fun logD(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    fun logI(tag: String, msg: String) {
        Log.i(tag, msg)
    }

    fun logE(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}
