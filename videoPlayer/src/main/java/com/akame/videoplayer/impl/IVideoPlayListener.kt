package com.akame.videoplayer.impl

/**
 * 状态监听回调
 * @see com.akame.videoplayer.layer.VideoPlayControlLayer
 */
interface IVideoPlayListener {
    /**
     * 进入全屏
     */
    fun enterFullScreen()

    /**
     * 退出全屏
     */
    fun exitFullScreen()
}