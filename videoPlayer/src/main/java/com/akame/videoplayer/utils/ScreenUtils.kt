package com.akame.videoplayer.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.*

object ScreenUtils {
    /**
     *  设置全屏横屏
     */
    fun setFullLandscape(
        activity: Activity,
        isChangeOrientation: Boolean,
        orientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    ) {
        val currentOrganization = activity.resources.configuration.orientation
        if (isChangeOrientation && currentOrganization != orientation) {
            activity.requestedOrientation = orientation
        }
        hideSystemUI(activity.window)
    }

    /**
     * 设置竖屏
     */
    fun setOrientationPortrait(
        activity: Activity,
        isChangeOrientation: Boolean,
        orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    ) {
        val currentOrganization = activity.resources.configuration.orientation
        if (isChangeOrientation && currentOrganization != orientation) {
            activity.requestedOrientation = orientation
        }
        showSystemUI(activity.window)
    }

    private fun hideSystemUI(window:Window) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI(window: Window) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun getScreenWidth(context: Context) = context.resources.displayMetrics.widthPixels

    fun getScreenHeight(context: Context) = context.resources.displayMetrics.heightPixels
}