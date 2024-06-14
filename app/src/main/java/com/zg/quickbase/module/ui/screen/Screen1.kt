package com.zg.quickbase.module.ui.screen

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import com.zg.quickbase.R

class Screen1 : Presentation {
    constructor(outerContext: Context?, display: Display?) : super(outerContext, display)
    constructor(outerContext: Context?, display: Display?, theme: Int) : super(
        outerContext,
        display,
        theme
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation_screen1)
    }
}
