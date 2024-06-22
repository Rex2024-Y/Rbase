package com.zg.quickbase.module.ui.hardware

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityHardWareBinding

/**
 * 展示硬件通信相关的demo
 */
class HardwareActivity : BaseActivity() {

    lateinit var binding: ActivityHardWareBinding
    lateinit var mViewModel: HardwareViewModel
    override fun getRoot(): View {

        binding = ActivityHardWareBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.run {
            tvOpen.setOnClickListener {
                mViewModel.openCOM(this@HardwareActivity)
            }

            tvClose.setOnClickListener {
                mViewModel.closeCOM(this@HardwareActivity)
            }

            tvSend.setOnClickListener {
                mViewModel.sendMsg(this@HardwareActivity,etSendInfo.text.toString())
            }
            etSendInfo.setText("01 03 07 D0 00 02 C4 86")

        }

    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[HardwareViewModel::class.java]
        mViewModel.initSerialConfig(this@HardwareActivity)

        mViewModel.mReceivedMsg.observe(this) {
            it?.run {
                binding.tvReceivedMsg.text = this
            }
        }


        mViewModel.mOpen.observe(this) {
            if(it == true){
                binding.tvOpenStatus.text = "已打开"
            }else{
                binding.tvOpenStatus.text = "已关闭"
            }
        }


    }

}