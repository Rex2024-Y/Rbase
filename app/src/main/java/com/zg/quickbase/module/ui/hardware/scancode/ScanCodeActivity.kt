package com.zg.quickbase.module.ui.hardware.scancode

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.chice.scangun.ScanGun
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityScanCodeBinding

/**
 * 展示硬件通信相关的demo
 */
open class ScanCodeActivity : BaseActivity() {

    lateinit var binding: ActivityScanCodeBinding
    var allText = ""
    var mScanGun: ScanGun? = null

    override fun getRoot(): View {

        binding = ActivityScanCodeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

        mScanGun = ScanGun { scanResult ->
            if (!TextUtils.isEmpty(scanResult)) {
                allText += "$scanResult "
                binding.tvReceivedMsg.text = allText
            }
        }
        ScanGun.setMaxKeysInterval(50)
    }


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode
            if (mScanGun?.isMaybeScanning(keyCode, event) == true) {
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun initViewModel() {
    }


}