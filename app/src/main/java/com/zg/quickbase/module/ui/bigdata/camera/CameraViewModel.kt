
package com.zg.quickbase.module.ui.bigdata.camera

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.zg.quickbase.utils.Base64Utils
import com.zg.quickbase.utils.LogUtils
import java.io.ByteArrayOutputStream


/**
 *  This ViewModel is used to store face detector helper settings
 */
class CameraViewModel : ViewModel() {
    private var _delegate: Int = FaceDetectorHelper.DELEGATE_CPU
    private var _threshold: Float =
        FaceDetectorHelper.THRESHOLD_DEFAULT

    val currentDelegate: Int get() = _delegate
    val currentThreshold: Float get() = _threshold

    fun setDelegate(delegate: Int) {
        _delegate = delegate
    }

    fun setThreshold(threshold: Float) {
        _threshold = threshold
    }

    /**
     * @param ivPreview: ImageView 暂时传进来预览下压缩后的效果 后续会去掉
     */
    fun postFace(bitmap: Bitmap, name: String, ivPreview: ImageView) {
        val header = "data:image/image/jpeg;base64,"
        // 1.bitmap检测是否大于2M 如果大于则压缩到2M为止
        val bitmapToBase64 = bitmapToBase64(bitmap, ivPreview)
        LogUtils.logI("CameraViewModel", "bitmapToBase64:$header$bitmapToBase64")

        val mapJson = HashMap<String, String>()
        // mapJson["name"] = "yc-test" + System.currentTimeMillis()
        mapJson["name"] = name
        mapJson["image"] = "$header$bitmapToBase64"

        LogUtils.logI("CameraViewModel", "faceAdd")
    //  http
    }

    /**
     * 1. JPG  2. 2M以内
     */
    private fun bitmapToBase64(bitmap: Bitmap, ivPreview: ImageView): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        // 初始化质量
        var options = 100
        val maxSize = 2 * 1024 * 1024
        // 循环直到压缩后的Bitmap小于指定大小
        while (byteArrayOutputStream.toByteArray().size > maxSize) {
            byteArrayOutputStream.reset(); // 重置 ByteArrayOutputStream
            options -= 10 // 降低压缩质量
            if (options <= 0) {
                break; // 如果质量过低则停止尝试
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream)
        }
        ivPreview.setImageBitmap(bitmap)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64Utils.encoder.encodeToString(byteArray)
    }
}