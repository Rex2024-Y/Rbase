package com.zg.quickbase.module.ui.function

import android.annotation.SuppressLint
import android.view.View
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.XApplication
import com.zg.quickbase.databinding.ActivityErrMsgBinding


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