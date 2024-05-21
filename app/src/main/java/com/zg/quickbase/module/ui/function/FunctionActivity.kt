package com.zg.quickbase.module.ui.function

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityFuncationBinding
import java.io.File


/**
 * 展示两个网络请求demo
 */
class FunctionActivity : BaseActivity() {

    private lateinit var binding: ActivityFuncationBinding
    private lateinit var mViewModel: FunctionViewModel

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            "回调权限结果 packageManager.canRequestPackageInstalls:" +
                    "${packageManager.canRequestPackageInstalls()}"
        }
    }

    override fun getRoot(): View {

        binding = ActivityFuncationBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {

        binding.btLogin.setOnClickListener {
            checkUpdate()
        }

        binding.btInstall.setOnClickListener {
            if (mViewModel.mPath.isNotEmpty()) {
                mViewModel.mPath.run {
                    try {

                        "Build.VERSION.SDK_INT:${Build.VERSION.SDK_INT}".logI()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (!packageManager.canRequestPackageInstalls()) {
                                // 用户尚未授权安装应用，需要请求权限
                                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                                activityResultLauncher.launch(intent)
                                return@setOnClickListener
                            }
                        }
                        installApk(this@FunctionActivity, this)
                    } catch (e: Exception) {
                        "安装失败:$e".logE()
                        binding.tvRequest.text = "安装失败:$e"
                    }
                }
            } else {
                "没有下载记录".toast()
            }

        }

    }

    private fun installApk(context: Context, path: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val file = File(path)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val uri = FileProvider.getUriForFile(context, "$packageName.fileprovider", file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }


    private fun checkUpdate() {
        showCheckDialog(
            "检测更新", "模拟说明当前模拟有了新版本需要更新，检测下载和安装的功能，是否需要下载？"
        ) { dialog, _ ->
            run {
                dialog.dismiss()
                mViewModel.updateApk(this@FunctionActivity)
            }
        }

    }

    fun isUnknownSourcesEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.INSTALL_NON_MARKET_APPS, 0
        ) === 1
    }


    // 原始实现下载
    override fun initViewModel() {
        binding.tvRequest.text = "当前模拟有了新版本需要更新，检测下载和安装的功能"
        mViewModel = ViewModelProvider(this)[FunctionViewModel::class.java]
        mViewModel.mViewText.observe(this) {
            it?.run {
                binding.tvResponse.text = this
            }
        }

        mViewModel.mViewDownInfo.observe(this) {
            it?.run {
                binding.tvRequest.text = this
            }
        }
    }

}