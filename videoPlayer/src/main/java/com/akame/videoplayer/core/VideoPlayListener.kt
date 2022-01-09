package com.akame.videoplayer.core

import com.akame.videoplayer.utils.VideoPlayStatus

interface VideoPlayListener {
    //播放状态回调 暂停&播放
    fun onIsPlayingChanged(isPlaying: Boolean)

    //播放错误
    fun onPlayError(error: String)

    //播放器的状态
    fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus)

    //当前播放进度
    fun onPlayingCurrentDuration(currentDuration: Long)
}