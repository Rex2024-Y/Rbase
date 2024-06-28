package com.zg.baselibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    // 请求码用于回调
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String TAG = "PermissionUtils";

    /**
     * 检查权限并请求访问所有文件的权限
     *
     * @param activity 当前的Activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        // 检查权限是否被授予
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_EXTERNAL_STORAGE);
            return false;
        }

        return true;
    }


    public static boolean verifyWrite(Activity activity) {
        // 检查权限是否被授予
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


    public static boolean verifyCamera(Activity activity) {
        // 检查权限是否被授予
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public static boolean verifyAllPermissions(Activity activity) {
        boolean write = verifyWrite(activity);
        boolean camera = verifyCamera(activity);
        if (camera && write) {
            Log.i(TAG, "权限完整可以正常运行");
            return true;
        }
        Log.i(TAG, "开始请求权限");
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                REQUEST_EXTERNAL_STORAGE);
        return false;

    }
}
