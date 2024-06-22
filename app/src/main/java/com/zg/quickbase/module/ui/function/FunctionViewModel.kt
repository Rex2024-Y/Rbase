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

class FunctionViewModel : BaseViewModel() {

    val testApkUrl = "http://47.107.112.47:8080/02022300.apk"
    val mViewText: MutableLiveData<String> = MutableLiveData<String>()
    val mViewDownInfo: MutableLiveData<String> = MutableLiveData<String>()
    var mPb = 0
    var mPath = "/storage/emulated/0/Android/data/com.zg.quickbase/files/Download/2024_05_08_11.apk"
    fun updateApk(activity: Activity) {
        "updateApk start".logE()
        "updateApk start ${Thread.currentThread()}".logE()
        ApiClient.mainService().download(testApkUrl)
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    "onResponse ${Thread.currentThread()}".logD()
                    "updateApk onResponse:".logD()
                    GlobalScope.launch {
                        "GlobalScope ${Thread.currentThread()}".logD()
                        "GlobalScope onResponse:".logD()
                        response.body()?.run {
                            val mediaType = contentType().toString();
                            val length = contentLength();
                            val desc = String.format("文件类型为%s，文件大小为%d", mediaType, length);
                            desc.logD()
                            var currValue = "\n$desc"
                            mPath = String.format(
                                "%s/%s.apk",
                                activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
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
                                            if (progress > mPb) {
                                                mPb = progress
                                                val detail = "已下载${mPb}%"
                                                detail.logD()
                                                // 不切线程ui会等待结束才更新
                                                mViewDownInfo.postValue(detail)
                                            }
                                        }
                                    } while (len != -1)
                                }
                            } catch (e: Exception) {
                                "updateApk onFailure:${e}".logE()
                                activity.runOnUiThread {
                                    mViewDownInfo.postValue("updateApk onFailure1:${e}")
                                }
                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    "updateApk onFailure:${t}".logE()
                    mViewDownInfo.postValue("updateApk onFailure2:${t}")
                }
            })
    }

}
