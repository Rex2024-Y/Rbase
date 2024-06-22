package com.zg.baselibrary.utils

import android.content.Context
import android.content.res.Configuration

object ScreenUtils {

    const val TAG = "ScreenUtils"
    fun getScreenInfo(context: Context): String {
        val configuration: Configuration = context.resources.configuration //获取设备的配置信息
        val info =
            "副屏 WidthDp HeightDp: ${configuration.screenWidthDp}，${configuration.screenHeightDp}" +
                    "\ndensityDpi: ${configuration.densityDpi}" +
                    "\nscreen scale ${1335.00f / 1920}:"
        LogUtils.logI(TAG, "getScreenInfo:${info}")
        return info
    }
}