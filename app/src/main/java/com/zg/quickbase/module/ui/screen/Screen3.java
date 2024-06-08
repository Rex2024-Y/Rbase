package com.zg.quickbase.module.ui.screen;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;

import androidx.camera.core.CameraSelector;
import androidx.camera.view.PreviewView;

import com.zg.quickbase.R;
import com.zhige.checkstand_app.view.OverlayView;

public class Screen3 extends Presentation {
    // 请求权限相关


    public ScreenMainActivity parentActivity;
    public PreviewView previewView;
    public OverlayView overlay;


    public Screen3(Context outerContext, Display display) {
        super(outerContext, display);
        parentActivity = (ScreenMainActivity) outerContext;

    }

    public Screen3(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
        parentActivity = (ScreenMainActivity) outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_screen3);
        previewView = findViewById(R.id.previewView);
        overlay = findViewById(R.id.overlay);
        parentActivity.dispatchCamera();
    }

    public void switchCamera() {
        parentActivity.dispatchCamera();
    }
}
