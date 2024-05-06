package com.zg.quickbase.module.ui.bigdatanet

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.URLEncoder

class AIViewModel : ViewModel() {
    fun parseImage(context: Context, uri: Uri) {
        try {

            GlobalScope.launch {
                val inputStream = context.contentResolver.openInputStream(uri);
                inputStream?.run {
                    val buffer = ByteArray(inputStream.available())
                    inputStream.read(buffer)
                    inputStream.close()
                    var imageBase64: String = Base64.encodeToString(buffer, Base64.DEFAULT)
                    imageBase64 =
                        imageBase64.substring(imageBase64.indexOf(',') + 1); // base64编码去除头部部分
                    imageBase64 = URLEncoder.encode(imageBase64, "UTF-8");
                    imageBase64 = "image=$imageBase64"
                    val baiduResponse = BaiduApi.baiduGeneralImage(imageBase64)
                    Log.i("AIViewModel", "baiduResponse:$baiduResponse")

                    try {
                        Gson().fromJson(baiduResponse, ImageResult::class.java).run {
                            var jsonAll = ""
                            result.forEach {
                                jsonAll += "${it.keyword} ${it.root} ${it.score}\n"
                            }
                            data.postValue(jsonAll)
                        }
                    } catch (e: Exception) {
                        Log.i("AIViewModel", "printStackTrace:$e")
                        data.postValue("识别异常")
                    }

                }
            }
            // 接下来的步骤将在这个回调方法中进行
        } catch (e: Exception) {
            Log.i("AIViewModel", "printStackTrace:$e")
            data.postValue("识别异常")
        }

    }

    override fun toString(): String {
        return "AIViewModel(data=$data)"
    }

    val data = MutableLiveData<String>()


    class ImageResult// name id的构造方法
        (var result: List<ImageResultItem>) {
        override fun toString(): String {
            return "ImageResult(result=$result)"
        }
    }

    class ImageResultItem// name id的构造方法
        (var keyword: String, var root: String, var score: Double) {
        override fun toString(): String {
            return "ImageResultItem(keyword='$keyword', root='$root', score=$score)"
        }
    }

}
