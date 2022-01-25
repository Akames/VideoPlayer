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
    var isAutoEnterFulls = true

    //是否自动退出全屏
    var isAutoExitFulls = true
    private val orientationListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            if (orientation <= 0 || !canConsumption) {
                return
            }
            canConsumption = false
            when (orientation) {
                // 竖屏 头朝上
                in 0..60 -> exitFulls()
                //横屏 头朝右
                in 60..120 -> enterFulls(true)
                //竖屏 头朝下
                in 120..210 -> exitFulls()
                //横屏 头朝左
                in 210..300 -> enterFulls(false)
                //竖屏 头朝下
                else -> exitFulls()
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

    private fun enterFulls(isReverseLandSpace: Boolean) {
        if (isAutoExitFulls) {
            enterFullScreen?.invoke(isReverseLandSpace)
        }
        isAutoEnterFulls = true
    }

    private fun exitFulls() {
        if (isAutoEnterFulls) {
            exitFullScreen?.invoke()
        }
        isAutoExitFulls = true
    }
}