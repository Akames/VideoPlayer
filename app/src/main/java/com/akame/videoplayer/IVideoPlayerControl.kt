package com.akame.videoplayer

import android.content.Context

interface IVideoPlayerControl {
    fun init(context: Context, mediaType: MediaType)

    fun play()

    fun pause()

    fun release()
}