package com.zg.quickbase.module.ui.function

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebViewClient
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityWebviewBinding


/**
 * 展示两个网络请求demo
 */
class WebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityWebviewBinding


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
                        view?.loadUrl(this.path.toString())
                    }
                    return true;
                }

            });

        }
    }

    override fun initViewModel() {
        binding.webView.loadUrl("https://blog.csdn.net/weixin_48618536/article/details/121699741")
    }


}