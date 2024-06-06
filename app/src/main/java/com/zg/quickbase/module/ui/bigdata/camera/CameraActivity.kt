package com.zg.quickbase.module.ui.bigdata.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.Surface
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModelProvider
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.zg.quickbase.base.BaseActivity
import com.zg.quickbase.databinding.ActivityCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * 预览拍照的界面
 */
class CameraActivity : BaseActivity(), FaceDetectorHelper.DetectorListener {


    private lateinit var binding: ActivityCameraBinding
    private var imageAnalyzer: ImageAnalysis? = null

    // 请求权限相关
    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)

    // 相机相关
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 默认使用后置摄像头
    private var previewRotationTag = 1

    private var previewRotation = Surface.ROTATION_0
    private var previewJxFzTag = 0 // 是否镜像翻转, 0:否、1:是
    private var photoRotationTag = 0 // 0 默认
    private var photoRotation: Int? = null

    private lateinit var mViewModel: CameraViewModel

    private lateinit var faceDetectorHelper: FaceDetectorHelper

    // 合格分数
    private val qualifiedScore = 0.9f

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService
    private var mImageCapture: ImageCapture? = null
    override fun getRoot(): View {
        binding = ActivityCameraBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {

    }

    override fun initViewModel() {
        mViewModel = ViewModelProvider(this)[CameraViewModel::class.java]
        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()
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

    // 启动相机进行人脸检测
    private fun startFaceDetector() {
        // Create the FaceDetectionHelper that will handle the inference
        backgroundExecutor.execute {
            faceDetectorHelper =
                FaceDetectorHelper(
                    context = this@CameraActivity,
                    threshold = mViewModel.currentThreshold,
                    currentDelegate = mViewModel.currentDelegate,
                    faceDetectorListener = this,
                    runningMode = RunningMode.LIVE_STREAM
                )

            // Wait for the views to be properly laid out
            binding.viewFinder.post {
                startCamera()
            }
        }
    }

    // 调起相机
    private fun startCamera() {
        binding.viewFinder.implementationMode =
            PreviewView.ImplementationMode.COMPATIBLE // 指定模式，以便支持旋转
        binding.viewFinder.scaleType = PreviewView.ScaleType.FILL_START
        // handle click
        handleClick()
        // start
        startCamera(cameraSelector, previewRotation)
//        startCamera(cameraSelector2, previewRotation,binding.viewFinder2.surfaceProvider)
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera(
        selector: CameraSelector,
        @ImageOutputConfig.RotationValue rotation: Int
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // 当ProcessCameraProvider准备就绪时
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 预览用例
            val preview = Preview.Builder()
                // .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                // .setTargetRotation(_binding.viewFinder.display.rotation)
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }


            // 图像捕获用例
            // ImageAnalysis. Using RGBA 8888 to match how our models work
            imageAnalyzer = ImageAnalysis.Builder()
                // .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                // .setTargetRotation(_binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(
                        backgroundExecutor,
                        this::detectLivestreamFrame
                    )
                }

            // 拍照用例
            mImageCapture = ImageCapture.Builder()
                .setTargetRotation(rotation)
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    selector,
                    preview,
                    imageAnalyzer,
                    mImageCapture
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
                "Camera initialization error".toast()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun detectLivestreamFrame(imageProxy: ImageProxy) {
        faceDetectorHelper.detectLivestreamFrame(imageProxy, previewJxFzTag)
    }

    // 检测是否拥有所需权限
    private fun allPermissionsGranted() = requiredPermissions.all {
        PermissionChecker.checkSelfPermission(
            this,
            it
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun handleClick() {
        // 翻转相机（切换前置或后置摄像头）
        binding.fzCamera.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                previewJxFzTag = 0
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                previewJxFzTag = 1
            }

            "onClick: 切换相机，当前相机：${cameraSelector}".logI()
            startCamera()
        }

        // 旋转相机（+90度）
        binding.xzCamera.setOnClickListener {
            if (previewRotationTag >= 4) {
                previewRotationTag = 1
            } else {
                previewRotationTag++
            }
            previewRotation = when (previewRotationTag) {
                1 -> Surface.ROTATION_0
                2 -> Surface.ROTATION_90
                3 -> Surface.ROTATION_180
                4 -> Surface.ROTATION_270
                else -> Surface.ROTATION_0
            }
            startCamera()
            "onClick: 旋转相机，当前相机角度：${previewRotation}".logI()
        }

        // 镜像翻转
        binding.jxFzCamera.setOnClickListener {
            previewJxFzTag = if (previewJxFzTag == 0) {
                1
            } else {
                0
            }
            startCamera()
        }

        // 抓取照片
        binding.btPostFace.setOnClickListener {
            takeStaticPhoto()
        }

        // 识别照片
        binding.btDetectorFace.setOnClickListener {
            detectorStaticPhoto()
        }

        // 照片角度处理（自助机上照片角度默认的不对）
        binding.xzPhoto.setOnClickListener {
            if (photoRotationTag >= 4) {
                photoRotationTag = 0
            } else {
                photoRotationTag++
            }
            photoRotation = when (photoRotationTag) {
                1 -> 0
                2 -> 90
                3 -> 180
                4 -> 270
                else -> null
            }
            "onClick: 旋转照片，当前照片角度：${photoRotation}".logI()
        }

    }

    // Update UI after faces have been detected. Extracts original image height/width
    // to scale and place bounding boxes properly through OverlayView
    override fun onResults(resultBundle: FaceDetectorHelper.ResultBundle) {
        runOnUiThread {
            // Pass necessary information to OverlayView for drawing on the canvas
            val detectionResult = resultBundle.results[0]
            //            "onResults: $detectionResult".logI()
            // 此处可添加抓图、上传服务器比对的逻辑
            try {
                detectionResult?.let {
                    for (detection in it.detections()) {
                        val score = detection.categories()[0].score()
                        if (score > qualifiedScore) {
                            //  "分数达标了: $score".logI()
                            // takeStaticPhoto(score)
                            // 后续讨论上传频率 暂用按钮测试
                        }
                    }
                }
            } catch (e: Exception) {
                "分数解析异常: $e".logE()
            }


            // 绘制标识人脸的矩形框
            binding.overlay.setResults(
                detectionResult,
                resultBundle.inputImageHeight,
                resultBundle.inputImageWidth
            )
            // Force a redraw
            binding.overlay.invalidate()
        }
    }

    fun showInputDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        callback: (String?) -> Unit
    ) {
        val input = EditText(context)
        input.hint = "请输入文字"

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setView(input)
            .setPositiveButton(positiveButtonText) { _, _ ->
                // 用户点击确定时触发，获取输入的文本并传递给回调函数
                val userInput = input.text.toString()
                callback(userInput)
            }
            .setNegativeButton(negativeButtonText, null) // 设置取消按钮，不执行任何操作
            .show()
    }

    /**
     * 在达到预期后 立马抓取图片 获取静态图片 根据需求转化为想要的格式和大小
     * 1.手动拍照为当前测试简单 暂时调接口用
     * 2.判断数值大于多少后自动拍照 todo
     */
    private fun takeStaticPhoto() {
        "开始拍照".logI()
        mImageCapture?.run {
            takePicture(
                ContextCompat.getMainExecutor(this@CameraActivity),
                object : ImageCapture.OnImageCapturedCallback() {

                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        "takeStaticPhoto onCaptureSuccess".logI()
                        // 拍照预览
                        // ImageProxy 转 Bitmap
                        val bitmap =
                            imageProxyToBitmapAndHandle(image, previewJxFzTag, photoRotation)
                        binding.ivPreview.setImageBitmap(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        "takeStaticPhoto onError $exception".logE()
                    }
                })
        }
    }

    private fun detectorStaticPhoto() {
        "开始识别".logI()
        mImageCapture?.run {
            takePicture(
                ContextCompat.getMainExecutor(this@CameraActivity),
                object : ImageCapture.OnImageCapturedCallback() {

                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        "detectorStaticPhoto onCaptureSuccess".logI()
                        // 拍照预览
                        // ImageProxy 转 Bitmap
                        val bitmap =
                            imageProxyToBitmapAndHandle(image, previewJxFzTag, photoRotation)
                        binding.ivPreview.setImageBitmap(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        "detectorStaticPhoto onError $exception".logE()
                    }
                })
        }
    }

    fun imageProxyToBitmapAndHandle(
        imageProxy: ImageProxy,
        scaleFlag: Int = 1,
        rotation: Int?
    ): Bitmap {
        val buffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer[bytes]
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        imageProxy.close()

        val matrix =
            Matrix().apply {
                if (rotation != null) {
                    postRotate(rotation.toFloat()) // 角度处理(按摄像头实际角度)
                } else {
                    postRotate(imageProxy.imageInfo.rotationDegrees.toFloat()) // 角度处理(按摄像头实际角度)
                }

                // 镜像翻转
                if (scaleFlag == 1) {
                    postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
                }
            }

        val rotatedBitmap =
            Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )

        return rotatedBitmap
    }

    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        // 获取图像的ByteBuffer
        val buffer = image.planes[0].buffer
        // 创建一个byte数组来保存图像数据
        val bytes = ByteArray(buffer.capacity())
        // 将ByteBuffer的内容复制到byte数组中
        buffer[bytes]
        // 使用BitmapFactory创建Bitmap
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        // 释放ImageProxy资源
        image.close()
        return bitmap
    }

    //    public void takeStaticPhoto(View view) {
    //
    //    if (mImageCapture != null) {
    //        //开始拍照
    //        mImageCapture.takePicture(ContextCompat.getMainExecutor(this), new
    //                ImageCapture.OnImageCapturedCallback() {
    //            @Override
    //            public void onCaptureSuccess(ImageProxy image) {
    //                super.onCaptureSuccess(image);

    //            }
    //
    //            @Override
    //            public void onError(ImageCaptureException exception) {
    //                super.onError(exception);
    //                Log.d(TAG, "onError: ");
    //            }
    //        });
    //    }

    override fun onError(error: String, errorCode: Int) {
        error.toast()
    }

    override fun onResume() {
        super.onResume()

        backgroundExecutor.execute {
            if (this::faceDetectorHelper.isInitialized && faceDetectorHelper.isClosed()) {
                faceDetectorHelper.setupFaceDetector()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // save FaceDetector settings
        if (this::faceDetectorHelper.isInitialized) {
            mViewModel.setDelegate(faceDetectorHelper.currentDelegate)
            mViewModel.setThreshold(faceDetectorHelper.threshold)
            // Close the face detector and release resources
            backgroundExecutor.execute { faceDetectorHelper.clearFaceDetector() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shut down our background executor.
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE,
            TimeUnit.NANOSECONDS
        )
    }
}