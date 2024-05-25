package com.zg.quickbase

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.zg.quickbase.module.ui.tabbase.TabBaseActivity
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityMainBinding
import com.zg.quickbase.module.ui.bigdata.BigDataActivity
import com.zg.quickbase.module.ui.bigdatanet.BigDataNetActivity
import com.zg.quickbase.module.ui.http.HttpActivity
import com.zg.quickbase.module.ui.bottomnav.BottomNavActivity
import com.zg.quickbase.module.ui.function.FunctionActivity
import com.zg.quickbase.module.ui.function.WebViewActivity
import com.zg.quickbase.module.ui.function.WebViewLocalActivity
import com.zg.quickbase.module.ui.hardware.HardwareActivity
import com.zg.quickbase.module.ui.login.LoginActivity
import com.zg.quickbase.module.ui.verticaltab.VerticalTabActivity
import com.zg.quickbase.viewmodel.MainViewModel


class MainActivity : BaseActivity() {

    var mBinding: ActivityMainBinding? = null
    lateinit var mViewModel: MainViewModel

    override fun getRoot(): View? {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        return mBinding?.root
    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mViewModel.run {
            mAdater.addAll(listData)
        }

        "Build.VERSION.SDK_INT ${Build.VERSION.SDK_INT}".logI()
    }

    override fun initView() {
        mBinding?.run {
            tvName.text = mViewModel.title
            rvMain.adapter = mAdater
            val layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            rvMain.setLayoutManager(layoutManager)
            mAdater.setOnItemClickListener { adapter, view, position ->
                run {
                    mAdater.getItem(position)?.let {
                        onItemClick(it, position)
                    }
                }
            }
        }
    }

    private fun onItemClick(it: MainViewModel.MainRvBean, position: Int) {
        "onItemClick".logD()
        when (it.id) {

            1 -> {
                showListDialog(
                    it.text, arrayOf("服务端网页", "本地网页")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            WebViewActivity::class.java.start()
                        }

                        1 -> {
                            WebViewLocalActivity::class.java.start()
                        }
                    }
                    dialog.dismiss()
                }
            }

            2 -> {
                showListDialog(
                    it.text, arrayOf("人脸识别（本地库）", "人脸识别（云端库）", "在线识别")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            BigDataNetActivity::class.java.start()
                        }

                        1 -> {
                            BigDataNetActivity::class.java.start()
                        }

                        2 -> {
                            BigDataNetActivity::class.java.start()
                        }

                        3 -> {
                            "暂不好用".toast()
//                            BigDataActivity::class.java.start()
                        }
                    }
                    dialog.dismiss()
                }
            }

            3 -> {
                showListDialog(
                    it.text, arrayOf("升级检测")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            FunctionActivity::class.java.start()
                        }

                    }
                    dialog.dismiss()
                }
            }

            4 -> {
                showListDialog(
                    it.text, arrayOf("网络框架")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            HttpActivity::class.java.start()
                        }
                    }
                    dialog.dismiss()
                }
            }

            5 -> {
                showListDialog(
                    it.text, arrayOf("Login", "Tab", "Nav", "VTab")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            LoginActivity::class.java.start()
                        }

                        1 -> {
                            TabBaseActivity::class.java.start()
                        }

                        2 -> {
                            BottomNavActivity::class.java.start()
                        }

                        3 -> {
                            VerticalTabActivity::class.java.start()
                        }
                    }
                    dialog.dismiss()
                }
            }

            6 -> {
                showListDialog(
//                    , "蓝牙", "WIFI", "USB"
                    it.text, arrayOf("串口")
                ) { dialog, which ->
                    when (which) {
                        0 -> {
                            HardwareActivity::class.java.start()
                        }

                        1 -> {
                            HardwareActivity::class.java.start()
                        }

                        2 -> {
                            HardwareActivity::class.java.start()
                        }

                        3 -> {
                            HardwareActivity::class.java.start()
                        }
                    }
                    dialog.dismiss()
                }
            }

            else -> {
                "未定义事件".toast()
            }
        }
    }


    val mAdater = object : BaseQuickAdapter<MainViewModel.MainRvBean, QuickViewHolder>() {

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): QuickViewHolder {
            return QuickViewHolder(R.layout.item_main_rv, parent)
        }

        override fun onBindViewHolder(
            holder: QuickViewHolder,
            position: Int,
            item: MainViewModel.MainRvBean?
        ) {
            item?.run {
                holder.setText(R.id.btTitle, text)
            }
        }
    }

}