package com.zg.quickbase.module.ui.function

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.zg.quickbase.XApplication
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityErrMsgBinding
import com.zg.quickbase.databinding.ActivityFuncationBinding
import java.io.File


/**
 * 展示两个网络请求demo
 */
class ErrMsgActivity : BaseActivity() {

    private lateinit var binding: ActivityErrMsgBinding
    private lateinit var mViewModel: FunctionViewModel



    override fun getRoot(): View {

        binding = ActivityErrMsgBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {


    }

    override fun initViewModel() {
        binding.tvMsg.text = "${XApplication.errMsg}"
    }


}