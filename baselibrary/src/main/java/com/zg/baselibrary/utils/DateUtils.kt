package com.zg.baselibrary.utils

import android.annotation.SuppressLint
import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

object DateUtils {
    val timeForName: String
        get() {
            @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy_MM_dd_HH")
            return dateFormat.format(Date())
        }


    fun getDateFormat(time: Long): String {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return dateFormat.format(time)
    }

    fun getDateFormatSS(time: Long): String {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormat.format(time)
    }

    fun getDateFormatChartX(time: Long): String {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("HH:mm")
        return dateFormat.format(time)
    }


    fun getDateFormatDay(time: Long): String {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(time)
    }

    fun getCurrentHourMin(): Pair<Int, Int> {
        // 获取当前时间的Calendar实例
        val calendar = Calendar.getInstance()
        // 获取小时（0-23）
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        // 获取分钟（0-59）
        val minute = calendar.get(Calendar.MINUTE)
        return Pair(hour, minute)
    }

    fun getDayKey(): Int {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return month * 100 + day
    }


    /**
     * 获取指定时间的时间戳
     */
    fun getHourMinTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }


    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    fun pareHourMin(timeStr: String): LocalTime {
        // 将字符串时间转换为LocalDateTime
        return LocalTime.parse(timeStr, formatter)
    }

}
