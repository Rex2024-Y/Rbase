package com.zg.quickbase.module.ui.hardware.temperature

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityTempBinding

/**
 * 展示硬件通信相关的demo
 */
class TempActivity : BaseActivity() {

    lateinit var binding: ActivityTempBinding
    lateinit var mViewModel: TempViewModel
    override fun getRoot(): View {

        binding = ActivityTempBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.run {
            tvOpen.setOnClickListener {
                mViewModel.openCOM(this@TempActivity)
            }

            tvClose.setOnClickListener {
                mViewModel.closeCOM(this@TempActivity)
            }

            tvSend.setOnClickListener {
                mViewModel.sendMsg(this@TempActivity, etSendInfo.text.toString())
            }

            /**
             * 主控板发送-Tx:01 03 00 14 00 01 C4 0E
             * 解释：
             * 0x01            从机地址为1
             * 0x03            功能码读一个寄存器
             * 0X0014        起始地址为20
             * 0X0001        寄存个数1
             * 0XC4           CRC16低位
             * 0X0E           CRC16高位
             */
            tvFuncation.setOnClickListener {
                showListDialog(
                    "温控器", arrayOf("柜子温度", "化霜温度", "设置1℃", "设置2℃", "设置-1℃")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            etSendInfo.setText("01 03 00 00 00 01 84 0A")
                        }

                        1 -> {
                            etSendInfo.setText("01 03 00 01 00 01 D5 CA")
                        }

                        2 -> {
                            // 设置10/10 ℃ =1 ℃
//                            etSendInfo.setText("01 06 00 14 00 0A 49 C9")
                            etSendInfo.setText(HardWareUtils.setTemp("1"))
                        }

                        3 -> {
                            etSendInfo.setText(HardWareUtils.setTemp("2"))
                        }

                        4 -> {
                            etSendInfo.setText(HardWareUtils.setTemp("-1"))
                        }
                    }
                    mViewModel.sendMsg(this@TempActivity, etSendInfo.text.toString())
                    dialog.dismiss()
                }
            }
            etSendInfo.setText("01 03 00 00 00 01 84 0A")


            // 开所有锁 "8A 01 00 11 9A"
            // 查询所有锁状态 "80 01 00 33 B2"
            // 锁1开
            // 锁2开  80 01 00 00 02 33 B0
            // 锁5开  80 01 00 00 20 33 92


        }

    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[TempViewModel::class.java]
        mViewModel.initSerialConfig(this@TempActivity)

        mViewModel.mReceivedMsg.observe(this) {
            it?.run {
                binding.tvReceivedMsg.text = this
            }
        }


        mViewModel.mOpen.observe(this) {
            if (it == true) {
                binding.tvOpenStatus.text = "已打开"
            } else {
                binding.tvOpenStatus.text = "已关闭"
            }
        }


    }

}