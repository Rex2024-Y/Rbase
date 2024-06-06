package com.zg.quickbase.module.ui.bigdata.camera.debug

import android.Manifest
import android.hardware.Camera
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModelProvider
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityCameraDoubleBinding
import com.zg.quickbase.module.ui.bigdata.camera.CameraViewModel
import java.lang.Exception


/**
 * 尝试双摄影同时打开
 */
class CameraDoubleActivity : BaseActivity() {


    private lateinit var binding: ActivityCameraDoubleBinding

    // 请求权限相关
    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)


    private lateinit var mViewModel: CameraViewModel

    // 合格分数
    private val qualifiedScore = 0.9f

    override fun getRoot(): View {
        binding = ActivityCameraDoubleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[CameraViewModel::class.java]
        // 请求相机权限
        if (allPermissionsGranted()) {
            startFaceDetector() // 已经有权限，则直接调起人脸检测方法
        } else {
            // 申请权限
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    startFaceDetector()
                } else {
                    "Permission request denied".toast()
                }
            }.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startFaceDetector() {
        binding.run {

            btC1.setOnClickListener {
                val previewCamera1 = PreviewCamera()
                previewCamera1.startCamera(binding.texture1, 0, 640, 480, object : PreviewCamera.ICallback {
                    override fun onSucc(camera: Camera?) {

                    }

                    override fun onData(data: ByteArray?, camera: Camera?) {
                    }

                    override fun onError(e: Exception?) {
                    }

                })
            }

            btC2.setOnClickListener {
                PreviewCamera().startCamera(binding.texture2, 0, 640, 480, object : PreviewCamera.ICallback {
                    override fun onSucc(camera: Camera?) {

                    }

                    override fun onData(data: ByteArray?, camera: Camera?) {
                    }

                    override fun onError(e: Exception?) {
                    }

                })
            }
        }
    }
    // 检测是否拥有所需权限
    private fun allPermissionsGranted() = requiredPermissions.all {
        PermissionChecker.checkSelfPermission(
            this,
            it
        ) == PermissionChecker.PERMISSION_GRANTED
    }




}