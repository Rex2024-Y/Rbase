package com.zg.quickbase.module.ui.function

import android.annotation.SuppressLint
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebViewClient
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityWebviewLocalBinding


/**
 */
class WebViewLocalActivity : BaseActivity() {

    private lateinit var binding: ActivityWebviewLocalBinding
    var mLocalUrl = "file:///android_asset/localdemo.html"

    override fun getRoot(): View {
        binding = ActivityWebviewLocalBinding.inflate(layoutInflater)
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

            })

        }

        binding.webView.addJavascriptInterface(AndroidToJs(), "androidObj")
        binding.btClickH5.setOnClickListener {
            sendToH5()
        }
    }

    override fun initViewModel() {
        binding.webView.loadUrl(mLocalUrl)
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

    fun sendToH5() {
        binding.webView.evaluateJavascript(
            "javascript:callJS()"
        ) { value -> //此处为 js 返回的结果
            "onReceiveValue:$value".logI()
            "H5返回了$value".toast()
        }
    }


    inner class AndroidToJs {

        @JavascriptInterface
        fun sendToAndroid(msg: String) {
            "sendToAndroid:$msg".logI()
            "android收到了$msg".toast()
            binding.tvH5.text = "$msg"
        }

    }
}