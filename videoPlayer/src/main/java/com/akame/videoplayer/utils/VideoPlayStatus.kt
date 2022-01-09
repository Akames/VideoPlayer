package com.akame.videoplayer.utils

enum class VideoPlayStatus {
    STATE_IDLE, //未开始
    STATE_BUFFERING, //初始化中
    STATE_READY, //准备播放
    STATE_ENDED //播放结束
}