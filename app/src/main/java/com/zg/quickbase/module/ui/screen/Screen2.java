package com.zg.quickbase.module.ui.screen;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.zg.quickbase.R;

public class Screen2 extends Presentation {


    public Screen2(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public Screen2(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_screen2);
    }
}
