package com.zg.quickbase.module.ui.bigdata.camera.debug;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewCameraUtils {

    public static Bitmap cameraDataToBitmap(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        int format = parameters.getPreviewFormat();
        Bitmap bitmap = yuvToBitmap(data, format, width, height);
        return bitmap;
    }

    /**
     * 摄像头数据转换为Bitmap
     *
     * @param data
     * @param format
     * @param width
     * @param height
     * @return
     */
    public static Bitmap yuvToBitmap(byte[] data, int format, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(data, format, width, height, null);
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    /**
     * Bitmap进行旋转和翻转
     *
     * @param bitmap
     * @param rotate        旋转的角度
     * @param leftRightTurn 是否左右翻转
     * @param upDownTurn    是否上下翻转
     * @return
     */
    public static Bitmap bitmapXform(Bitmap bitmap, int rotate, boolean leftRightTurn, boolean upDownTurn) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        if (leftRightTurn) {
            matrix.preScale(-1f, 1f);
        }
        if (upDownTurn) {
            matrix.preScale(1f, -1f);
        }
        if (rotate > 0) {
            matrix.postRotate(rotate);
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return rotatedBitmap;
    }

    /**
     * 把Bitmap保存为jpg文件
     *
     * @param bitmap
     * @param filePath 绝对路径
     */
    public static void saveBitmapToFile(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        try {
            // 创建文件输出流
            FileOutputStream outputStream = new FileOutputStream(file);
            // 将bitmap写入输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            // 刷新缓冲区
            outputStream.flush();
            // 关闭输出流
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得SDCard根路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return "" + Environment.getExternalStorageDirectory();
    }

}
