package com.zg.quickbase.module.ui.hardware.nfc

import androidx.lifecycle.MutableLiveData
import com.zg.baselibrary.base.BaseViewModel
import tp.xmaihh.serialport.SerialHelper


class NFCActivityViewModel : BaseViewModel() {

    private var serialHelper: SerialHelper? = null
    val mReceivedMsg: MutableLiveData<String> = MutableLiveData<String>()
    val mOpen: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
}
