package com.zg.quickbase.module.ui.hardware.lock

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityLockBinding

/**
 * 展示硬件通信相关的demo
 */
class LockActivity : BaseActivity() {

    lateinit var binding: ActivityLockBinding
    lateinit var mViewModel: LockViewModel
    override fun getRoot(): View {

        binding = ActivityLockBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.run {
            tvOpen.setOnClickListener {
                mViewModel.openCOM(this@LockActivity)
            }

            tvClose.setOnClickListener {
                mViewModel.closeCOM(this@LockActivity)
            }

            tvSend.setOnClickListener {
                mViewModel.sendMsg(this@LockActivity,etSendInfo.text.toString())
            }

            tvFuncation.setOnClickListener {
                showListDialog(
                   "锁孔板", arrayOf("查所有锁","开所有锁","开6","开7")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            //查询所有锁状态
                           etSendInfo.setText("80 01 00 33 B2")
                        }

                        1 -> {
                            // 开所有锁 "8A 01 00 11 9A"
                            etSendInfo.setText("8A 01 00 11 9A")
                        }

                        2 -> {
                            // 开第6把锁
                            etSendInfo.setText("8A 01 06 11 9C")
                        }


                        3 -> {
                            // 开第7把锁
                            etSendInfo.setText("8A 01 07 11 9D")
                        }

                        4 -> {
                        }
                    }
                    mViewModel.sendMsg(this@LockActivity,etSendInfo.text.toString())
                    dialog.dismiss()
                }
            }
            //查询所有锁状态
            etSendInfo.setText("80 01 00 33 B2")

            // 开所有锁 "8A 01 00 11 9A"
            // 查询所有锁状态 "80 01 00 33 B2"
            // 锁1开
            // 锁2开  80 01 00 00 02 33 B0
            // 锁5开  80 01 00 00 20 33 92


        }

    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[LockViewModel::class.java]
        mViewModel.initSerialConfig(this@LockActivity)

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