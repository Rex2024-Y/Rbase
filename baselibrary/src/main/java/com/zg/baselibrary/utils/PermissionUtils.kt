package com.zg.baselibrary.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

object PermissionUtils {
    // 请求码用于回调
    private const val REQUEST_EXTERNAL_STORAGE = 1
    private const val TAG = "PermissionUtils"

    fun verifyWrite(activity: Activity?): Boolean {
        // 检查权限是否被授予
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    fun verifyCamera(activity: Activity?): Boolean {
        // 检查权限是否被授予
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.CAMERA
        )
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    fun verifyAllPermissions(activity: Activity?): Boolean {
        val write = verifyWrite(activity)
        val camera = verifyCamera(activity)
        if (camera && write) {
            Log.i(TAG, "权限完整可以正常运行")
            return true
        }
        Log.i(TAG, "开始请求权限")
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            REQUEST_EXTERNAL_STORAGE
        )
        return false
    }
}
