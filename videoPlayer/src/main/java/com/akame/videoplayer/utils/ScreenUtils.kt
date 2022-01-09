package com.akame.videoplayer.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager

object ScreenUtils {
    /**
     *  设置全屏横屏
     */
    fun setFullLandscape(
        activity: Activity,
        orientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    ) {
        activity.requestedOrientation = orientation
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    /**
     * 设置竖屏
     */
    fun setOrientationPortrait(
        activity: Activity,
        orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    ) {
        activity.requestedOrientation = orientation
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}