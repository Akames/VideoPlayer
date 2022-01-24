package com.akame.videoplayer.utils

import android.content.Context
import android.view.OrientationEventListener
import kotlinx.coroutines.*

/**
 * 根据重力感应自动旋转
 */
class AutoRotationScreenManager(private val context: Context) {
    @Volatile
    private var canConsumption = true
    private var scopeJob: Job? = null

    //是否自动进入得全屏
    var isAutoFullScreen = false
    private val orientationListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            if (orientation <= 0 || !canConsumption) {
                return
            }
            canConsumption = false
            when (orientation) {
                // 竖屏 头朝上
                in 0..60 -> {
                    if (isAutoFullScreen) {
                        exitFullScreen?.invoke()
                    }
                }
                //横屏 头朝右
                in 60..120 -> {
                    enterFullScreen?.invoke(true)
                }
                //竖屏 头朝下
                in 120..210 -> {
                    if (isAutoFullScreen) {
                        exitFullScreen?.invoke()
                    }
                }
                //横屏 头朝左
                in 210..300 -> {
                    enterFullScreen?.invoke(false)
                }
                //竖屏 头朝下
                else -> {
                    if (isAutoFullScreen) {
                        exitFullScreen?.invoke()
                    }
                }
            }
            changeConsumptionStatus()
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
        scopeJob?.cancel()
    }

    // boolean 参数表示是否为ReverseLandSpace
    var enterFullScreen: ((Boolean) -> Unit)? = null

    var exitFullScreen: (() -> Unit)? = null

    private fun changeConsumptionStatus() {
        scopeJob = CoroutineScope(Dispatchers.IO).launch {
            delay(800)
            canConsumption = true
        }
    }
}