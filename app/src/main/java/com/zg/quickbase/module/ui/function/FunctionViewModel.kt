package com.zg.quickbase.module.ui.function

import android.app.Activity
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.zg.baselibrary.base.BaseViewModel
import com.zg.quickbase.net.ApiClient
import com.zg.quickbase.utils.DateUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.FileOutputStream
import kotlin.math.roundToInt

class FunctionViewModel : BaseViewModel() {

    //    val testApkUrl = "http://47.107.112.47:8080/02022300.apk"
    val testApkUrl = "https://files.hbzgyc.com/app/202408/accompany-v1.1.0.apk"
    val mViewText: MutableLiveData<String> = MutableLiveData<String>()
    val mViewDownInfo: MutableLiveData<String> = MutableLiveData<String>()
    var mPb = 0
    var mPath = "/storage/emulated/0/Android/data/com.zg.quickbase/files/Download/2024_05_08_11.apk"
    var mActivity: Activity? = null
    fun updateApk(activity: Activity) {
        "updateApk start".logE()
        "updateApk start ${Thread.currentThread()}".logE()
        mActivity = activity

        ApiClient.downloadFile(testApkUrl, object : ApiClient.ProgressListener {
            override fun onProgress(currentBytes: Long, contentLength: Long, done: Boolean) {
                if (done) {
                    "updateApk $contentLength onProgress:done".logE()
                    mPb = 100
                } else {
                    "updateApk $contentLength onProgress:${currentBytes}".logE()
                    mPb = (currentBytes * 1.00f / contentLength * 100).toInt()
                }
                val detail = "已下载${mPb}%"
                detail.logD()
                if (done) {
                    // 不切线程ui会等待结束才更新
                    mViewDownInfo.postValue("下载完成")
                } else {
                    // 不切线程ui会等待结束才更新
                    mViewDownInfo.postValue(detail)
                }

            }

        }, callback)

//        ApiClient.mainService().download(testApkUrl)
//            .enqueue(callback)
    }


    val callback = object : retrofit2.Callback<ResponseBody> {
        override fun onResponse(
            call: Call<ResponseBody>,
            response: Response<ResponseBody>
        ) {

            "onResponse ${Thread.currentThread()}".logD()
            "updateApk onResponse:".logD()
            mActivity.run {
                GlobalScope.launch {
                    "GlobalScope ${Thread.currentThread()}".logD()
                    "GlobalScope onResponse:".logD()
                    response.body()?.run {
                        val mediaType = contentType().toString();
                        val length = contentLength()
                        val sizeM = length * 1.00f / 1024 / 1024
                        val desc = String.format("文件类型为%s，文件大小为{%.2f}M", mediaType, sizeM);
                        desc.logD()
                        var currValue = "\n$desc"
                        mPath = String.format(
                            "%s/%s.apk",
                            mActivity?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                                .toString(),
                            DateUtils.timeForName
                        )
                        currValue += "\n$mPath"
                        "path:$mPath".logI()
                        mViewText.postValue(currValue)
                        // 下面从返回的输入流中读取字节数据并保存为本地文件
                        try {
                            val inputStream = byteStream()
                            val fos = FileOutputStream(mPath)
                            val buf = ByteArray(100 * 1024)
                            var sum = 0
                            var len = 0
                            inputStream.use { input ->
                                do {
                                    len = input.read(buf)
                                    if (len != -1) {
                                        fos.write(buf, 0, len)
                                        sum += len
                                        val progress = (sum * 1.0f / length * 100).toInt()
//                                        if (progress > mPb) {
//                                            mPb = progress
//
//                                        }
                                    }
                                } while (len != -1)
                            }
                        } catch (e: Exception) {
                            "updateApk onFailure:${e}".logE()
                            mActivity?.runOnUiThread {
                                mViewDownInfo.postValue("updateApk onFailure1:${e}")
                            }
                        }
                    }
                }
            }


        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            "updateApk onFailure:${t}".logE()
            mViewDownInfo.postValue("updateApk onFailure2:${t}")
        }
    }


}
