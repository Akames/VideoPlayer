package com.akame.videoplayer

import com.akame.videoplayer.core.VideoPlayListener

interface IVideoPlayerControl {
    fun play()

    fun pause()

    fun isPlaying(): Boolean

    fun seekTo(positionMs: Long)

    fun release()

    fun setPlayListener(playerListener: VideoPlayListener)

    fun getDuration(): Long
}