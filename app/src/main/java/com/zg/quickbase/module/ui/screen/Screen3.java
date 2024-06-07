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

public class Screen3 extends Presentation {
    // 请求权限相关


    public ScreenMainActivity parentActivity;


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
        PreviewView previewView = findViewById(R.id.previewView);
        ScreenMainActivity.Companion.dispatchCamera(previewView, parentActivity);
    }
}
