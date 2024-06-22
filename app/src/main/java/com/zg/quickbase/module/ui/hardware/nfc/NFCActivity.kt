package com.zg.quickbase.module.ui.hardware.nfc

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.zg.baselibrary.base.BaseActivity
import com.zg.quickbase.databinding.ActivityNfcBinding

/**
 * 展示硬件通信相关的demo
 */
class NFCActivity : BaseActivity() {

    lateinit var binding: ActivityNfcBinding
    lateinit var mViewModel: NFCActivityViewModel
    override fun getRoot(): View {

        binding = ActivityNfcBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.run {
            tvOpen.setOnClickListener {
                openNFC()


            }
            tvCheck.setOnClickListener {
                checkNFC()
            }
        }
    }

    private fun openNFC() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this@NFCActivity);
        // 检查设备是否支持NFC，并且NFC是否已经开启

        if (nfcAdapter== null || !nfcAdapter.isEnabled){
            "NFC is not available".logE()
            "NFC不可用".toast()
            return
        }

        if (nfcAdapter != null && nfcAdapter.isEnabled) {
////             在onCreate方法中注册NFC事件处理器
//            val tagDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
//            val filters = arrayOf(tagDetected);
//
//            // 创建一个PendingIntent对象，以便系统可以在检测到NFC标签时通知你的应用
//            val pendingIntent = PendingIntent.getActivity(
//                this@NFCActivity,
//                0, Intent(this@NFCActivity, NFCActivity::class.java)
//                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
//            );
//            // 在onResume方法中启用前台调度
//            nfcAdapter.enableForegroundDispatch(
//                this@NFCActivity,
//                pendingIntent,
//                filters,
//                null
//            )
//            "enableReaderMode".logI()
//
//            nfcAdapter.enableReaderMode(
//                this@NFCActivity, object : NfcAdapter.ReaderCallback {
//                    override fun onTagDiscovered(tag: Tag?) {
//                        tag?.run {
//                            "onTagDiscovered ID:${byteArrayToHexString(id)}".logI()
//                            "onTagDiscovered tag:${tag}".logI()
//                            mViewModel.mReceivedMsg.postValue(
//                                "onTagDiscovered:${
//                                    byteArrayToHexString(
//                                        id
//                                    )
//                                }"
//                            )
//                        }
//                    }
//                },
//                NfcAdapter.FLAG_READER_NFC_A or
//                        NfcAdapter.FLAG_READER_NFC_B or
//                        NfcAdapter.FLAG_READER_NFC_F or
//                        NfcAdapter.FLAG_READER_NFC_V or
//                        NfcAdapter.FLAG_READER_NFC_BARCODE,
//                null
//            )
//            "打开成功".toast()
        }
    }

    fun byteArrayToHexString(inarray: ByteArray): String {
        var i: Int
        var j: Int
        var `in`: Int
        val hex =
            arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        var out = ""
        j = 0
        while (j < inarray.size) {
            `in` = inarray[j].toInt() and 0xff
            i = `in` shr 4 and 0x0f
            out += hex[i]
            i = `in` and 0x0f
            out += hex[i]
            ++j
        }
        return out
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
             tag?.run {
                "onNewIntent onTagDiscovered ID:${byteArrayToHexString(id)}".logI()
                "onNewIntent onTagDiscovered tag:${tag}".logI()
                mViewModel.mReceivedMsg.postValue("onTagDiscovered:${byteArrayToHexString(id)}")
            }
        }
    }


    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[NFCActivityViewModel::class.java]



        mViewModel.mReceivedMsg.observe(this) {
            it?.run {
                binding.tvReceivedMsg.text = this
            }
        }

    }

    private fun checkNFC() {
        // 检查设备是否支持NFC
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        val nfcAdapter = nfcManager.getDefaultAdapter();
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            // NFC可用，执行相应的操作
            openNFC()
        } else {
            // NFC不可用，给出相应的提示
            "NFC不可用".toast()
        }

    }

    override fun onResume() {
        super.onResume()
        checkNFC()
    }

}