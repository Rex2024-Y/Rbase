package com.zg.baselibrary.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateUtils {
    val timeForName: String
        get() {
            @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy_MM_dd_HH")
            return dateFormat.format(Date())
        }
}
