package com.zg.baselibrary.utils

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera

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


    fun getCameraInfo(): String {
        var isSupportFront = false
        var isSupportBack = false
        val numberOfCameras: Int = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_BACK) {
                isSupportBack = true
                // 后置摄像头
            } else if (cameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 前置摄像头
                isSupportFront = true
            }
        }

        val info = "支持情况 前置摄像头:$isSupportFront, 后置摄像头:$isSupportBack"
        LogUtils.log("支持情况 前置摄像头:$isSupportFront, 后置摄像头:$isSupportBack")
        return info
    }


    /**
     * dp转px
     */
    fun dp2px(context: Context, dp: Float): Float {
        // 获取设备密度
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f)
    }

    /**
     * xp转dp
     */
    fun px2dp(context: Context, px: Int): Float {
        // 获取设备密度
        val density = context.resources.displayMetrics.density
        return px / density
    }

}