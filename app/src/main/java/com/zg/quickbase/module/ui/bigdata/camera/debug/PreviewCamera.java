package com.zg.quickbase.module.ui.bigdata.camera.debug;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

public class PreviewCamera {

    private Camera mCamera;
    private boolean mirror = false; // 镜像（左右翻转）
    private int rotate = -1; // 旋转（相机传感器方向相对于设备自然方向的角度）
    private int zoom = -1; // 焦距（有些摄像头可能不支持）

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public void startCamera(TextureView textureView, int cameraIndex, int width, int height, ICallback callback) {
        stopCamera();
        try {
            mCamera = Camera.open(cameraIndex);
            checkPreviewSize(width, height);
            // 设置参数（尺寸、对焦、焦距、旋转）
            Camera.Parameters params = mCamera.getParameters();
            //List<Integer> list = params.getSupportedPreviewFormats();
            params.setPreviewSize(width, height);
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (this.zoom >= 0 && params.isZoomSupported() && this.zoom <= params.getMaxZoom()) {
                params.setZoom(this.zoom);
                mCamera.setParameters(params);
            }
            mCamera.setParameters(params);
            if (this.rotate >= 0) {
                mCamera.setDisplayOrientation(this.rotate);
            }
            // 预览初始化（左右翻转）
            initPreview(textureView);
            if (this.mirror) {
                textureView.setRotationY(180);
            }
            // 回调
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (callback != null) {
                        callback.onData(data, camera);
                    }
                }
            });
            if (callback != null) {
                callback.onSucc(mCamera);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    public void stopCamera() {
        try {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
        }
    }

    private void checkPreviewSize(int width, int height) throws Exception {
        boolean sizeOk = false;
        List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
        for (Camera.Size size : sizeList) {
            if (size.width == width && size.height == height) {
                sizeOk = true;
            }
        }
        if (!sizeOk) {
            throw new Exception(String.format("不支持该预览尺寸: [%d,%d]", width, height));
        }
    }

    private TextureView initPreview(TextureView textureView) {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewTexture(surfaceTexture);
                        mCamera.startPreview();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                stopCamera();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
        return textureView;
    }

    public interface ICallback {
        void onSucc(Camera camera);

        void onData(byte[] data, Camera camera);

        void onError(Exception e);
    }
}