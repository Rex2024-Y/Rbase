package com.zg.quickbase.module.ui.bigdata

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityBigDataBinding


class BigDataActivity : BaseActivity() {


    private lateinit var binding: ActivityBigDataBinding

    @SuppressLint("SetTextI18n")
    private val activityLauncher = this.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
            binding.ivPreview.setImageURI(it)
            binding.tvInfo.text = "[Uri]:  ${it}\n\n [Result]:   "
        }
    }

    override fun getRoot(): View {
        binding = ActivityBigDataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.btChoose.setOnClickListener {
            // 检查文件写入权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                // 已经具有权限
                activityLauncher.launch("image/*");
            } else {
                // 无权限，需要请求权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 999)
                }
            }

        }
    }

    override fun initViewModel() {
    }

}