package com.zg.quickbase.module.ui.hardware.print

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityHardWareBinding

/**
 * 展示硬件通信相关的demo
 */
class PrintActivity : BaseActivity() {

    lateinit var binding: ActivityHardWareBinding
    lateinit var mViewModel: PrintViewModel
    override fun getRoot(): View {

        binding = ActivityHardWareBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.run {
            tvOpen.setOnClickListener {
                mViewModel.openCOM(this@PrintActivity)
            }

            tvClose.setOnClickListener {
                mViewModel.closeCOM(this@PrintActivity)
            }

            tvSend.setOnClickListener {
                mViewModel.sendMsg(this@PrintActivity,etSendInfo.text.toString())
            }
            etSendInfo.setText("12 54")

        }

    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[PrintViewModel::class.java]
        mViewModel.initSerialConfig(this@PrintActivity)

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