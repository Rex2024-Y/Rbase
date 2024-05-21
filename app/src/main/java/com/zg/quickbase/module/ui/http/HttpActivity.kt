package com.zg.quickbase.module.ui.http

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityHttpBinding

/**
 * 展示两个网络请求demo
 */
class HttpActivity : BaseActivity() {

    lateinit var binding: ActivityHttpBinding
    lateinit var mViewModel: HttpViewModel
    override fun getRoot(): View {

        binding = ActivityHttpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

        binding.btLogin.setOnClickListener {
            mViewModel.loginTest()
        }

        binding.btGet.setOnClickListener {
            mViewModel.queryTest()
        }
    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[HttpViewModel::class.java]

        mViewModel.mViewLiveData.observe(this) {
            it?.run {
                binding.tvRequest.text = it.request
                binding.tvResponse.text = it.response
            }
        }
    }

}