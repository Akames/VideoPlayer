package com.akame.videoplayer.utils

import android.content.Context
import android.view.OrientationEventListener

/**
 * 根据重力感应自动旋转
 */
class AutoRotationScreenManager(private val context: Context) {
    private val orientationListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            if (orientation <= 0) {
                return
            }
            when (orientation) {
                // 竖屏 头朝上
                in 0..60 -> {
                    outFullScreen?.invoke()
                }
                //横屏 头朝右
                in 60..120 -> {
                    enterFullScreen?.invoke(true)
                }
                //竖屏 头朝下
                in 120..210 -> {
                    outFullScreen?.invoke()
                }
                //横屏 头朝左
                in 210..300 -> {
                    enterFullScreen?.invoke(false)
                }
                //竖屏 头朝下
                else -> {
                    outFullScreen?.invoke()
                }
            }
        }
    }

    init {
        if (orientationListener.canDetectOrientation()) {
            orientationListener.enable()
        } else {
            orientationListener.disable()
        }
    }

    fun release() {
        orientationListener.disable()
    }

    // boolean 参数表示是否为ReverseLandSpace
    var enterFullScreen: ((Boolean) -> Unit)? = null

    var outFullScreen: (() -> Unit)? = null
}