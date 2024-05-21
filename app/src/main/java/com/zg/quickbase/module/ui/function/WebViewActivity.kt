package com.zg.quickbase.module.ui.function

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityWebviewBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 */
class WebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityWebviewBinding
    var mUrl = "https://blog.csdn.net/weixin_48618536/article/details/121699741"

    override fun getRoot(): View {
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun initView() {
        binding.webView.settings.run {

            //支持缩放，默认为true。
            setSupportZoom(true)
            // 设置出现缩放工具
            builtInZoomControls = true
            //调整图片至适合webview的大小
            useWideViewPort = true
            // 缩放至屏幕的大小
            loadWithOverviewMode = true
            //设置默认编码
            defaultTextEncodingName = "utf-8";
            loadsImagesAutomatically = true
            //开启javascript
            javaScriptEnabled = true
            //支持内容重新布局
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            //关闭webview中缓存
            // webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE)
            //缓存
            cacheMode = WebSettings.LOAD_DEFAULT
            // 开启 DOM storage API 功能
            domStorageEnabled = true
            //获取触摸焦点
            binding.webView.requestFocusFromTouch();
            //WebView.setInitialScale(100);   //100代表不缩放
            mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
            //设置不用系统浏览器打开,直接显示在当前Webview
            binding.webView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    request?.url?.run {
                        view?.loadUrl(this.toString())
                    }
                    return true
                }

            });

        }

        binding.btGo.setOnClickListener {
            mUrl = binding.etUrl.text.toString().trim()
            "mUrl:$mUrl".logI()
            binding.webView.loadUrl(mUrl)
        }

//        binding.savePhoto.setOnClickListener {
//            binding.savePhoto.visibility = View.GONE
//            val view = window.decorView
//            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//            val canvas = Canvas(bitmap)
//            view.draw(canvas)
//            try {
//                val fileName = Environment.getExternalStorageDirectory().path + "/webview_jietu.jpg"
//                "$fileName".logI()
//                val fos = FileOutputStream(fileName)
//                //压缩bitmap到输出流中
//                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 70, fos)
//                fos.close()
//                Toast.makeText(this@WebViewActivity, "截屏成功", Toast.LENGTH_LONG).show()
//                binding.ivPreview.setImageBitmap(bitmap)
//                binding.savePhoto.visibility = View.VISIBLE
//            } catch (e: Exception) {
//            } finally {
////                if (bitmap != null) {
////                    bitmap.recycle()
////                }
//            }
//        }
    }

    override fun initViewModel() {
        binding.webView.loadUrl(mUrl)
        binding.etUrl.setText(mUrl)
    }

    // 重写返回键 网页有跳转返回网页上一页
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        "onBackPressed".logD()
        //super.onBackPressed();
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            finish();
        }

    }


}