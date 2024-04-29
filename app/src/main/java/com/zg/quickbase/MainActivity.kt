package com.zg.quickbase

import android.view.View
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    lateinit var mBinding: ActivityMainBinding


    override fun getRoot(): View {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        return mBinding.root
    }


    override fun initView() {
        mBinding.tvName.text = "我是修改后的数据"
    }




}