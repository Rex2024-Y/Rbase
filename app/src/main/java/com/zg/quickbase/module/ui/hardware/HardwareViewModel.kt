package com.zg.quickbase.module.ui.hardware

import android.app.Activity
import android.os.SystemClock
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.zg.baselibrary.base.BaseViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import tp.xmaihh.serialport.stick.AbsStickPackageHelper
import tp.xmaihh.serialport.utils.ByteUtil


class HardwareViewModel : BaseViewModel() {

    private var serialHelper: SerialHelper? = null
    val mReceivedMsg: MutableLiveData<String> = MutableLiveData<String>()
    val mOpen: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun initSerialConfig(hardwareActivity: HardwareActivity) {
        //初始化SerialHelper对象，设定串口名称和波特率（此处为接收扫码数据）/dev/ttyS1
        serialHelper = object : SerialHelper("/dev/ttyS1", 19200) {
            override fun onDataReceived(paramComBean: ComBean) {

                try {
                    val time = paramComBean.sRecTime;
                    var rxText = String(paramComBean.bRec);
//                if (isHexType) {
//                    //转成十六进制数据
//                    rxText = ByteUtil.ByteArrToHex(comBean.bRec);
//                }
                    rxText = ByteUtil.ByteArrToHex(paramComBean.bRec);
                    var rText = "Rx-> $time: $rxText\r\n"

                    // 去掉字符串全部空格
                    val string1 = rxText.replace(" ", "")
                    val string2 = string1.substring(6, 14)
                    // 16进制转10进制
                    val num = string2.toInt(16)
                    val des = "当前重物传感重量为:   ${num * 100.00f / 10000}  KG"
                    rText += des
                    mReceivedMsg.postValue(rText)
                    "onDataReceived: ${Gson().toJson(paramComBean)}".logI()
                } catch (e: Exception) {
                    Toast.makeText(hardwareActivity, "onDataReceived异常：$e", Toast.LENGTH_SHORT)
                        .show();
                }


            }

        }


        /*
         * 默认的BaseStickPackageHelper将接收的数据扩展成64位，一般用不到这么多位
         * 我这里重新设定一个自适应数据位数的
         */
        serialHelper?.stickPackageHelper = AbsStickPackageHelper { inputStream ->
            try {
                val available = inputStream.available()
                if (available > 0) {
                    val buffer = ByteArray(available)
                    val size = inputStream.read(buffer)
                    if (size > 0) {
                        return@AbsStickPackageHelper buffer
                    }
                } else {
                    SystemClock.sleep(50)
                }
            } catch (e: Exception) {
                Toast.makeText(hardwareActivity, "AbsStickPackageHelper异常：$e", Toast.LENGTH_SHORT)
                    .show();
                "AbsStickPackageHelper: $e".logE()
            }
            null
        }
    }

    fun openCOM(activity: Activity) {
        serialHelper?.run {
            if (isOpen) {
                Toast.makeText(activity, "串口已经打开", Toast.LENGTH_SHORT).show();
                return
            }
            GlobalScope.launch {
                "opening".logI()
                try {
                    open()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "openCOM: $e".logE()
                    activity.runOnUiThread {
                        Toast.makeText(activity, "串口打开异常:$e", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            Toast.makeText(activity, "串口打开成功", Toast.LENGTH_SHORT).show();

        } ?: let {
            Toast.makeText(activity, "请先初始化串口", Toast.LENGTH_SHORT).show();
        }
    }

    fun closeCOM(activity: Activity) {

        serialHelper?.run {
            if (isOpen) {
                close()
                Toast.makeText(activity, "-串口已经关闭", Toast.LENGTH_SHORT).show();
            }
        } ?: let {
            Toast.makeText(activity, "请先初始化串口", Toast.LENGTH_SHORT).show();
        }


    }

    fun sendMsg(activity: HardwareActivity, text: String) {
        "sendMsg $text".logI()

        serialHelper?.run {
            if (isOpen) {
                try {
                    sendHex(text.replace(" ", ""))
                } catch (e: Exception) {
                    Toast.makeText(activity, "sendMsg异常：$e", Toast.LENGTH_SHORT).show();
                }
            }
        } ?: let {
            Toast.makeText(activity, "请先初始化串口", Toast.LENGTH_SHORT).show();
        }
//        serialHelper.send(byte[] bytes);  //byte数组
//        serialHelper.sendHex(String str); //十六进制数
//        serialHelper.sendTxt(String str); //文本
    }

}
