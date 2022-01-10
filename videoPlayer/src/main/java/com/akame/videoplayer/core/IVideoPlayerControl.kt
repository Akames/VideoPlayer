package com.akame.videoplayer.core

import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.VideoPlayStatus
import kotlinx.coroutines.CoroutineScope

interface IVideoPlayerControl {
    fun setUp(externalScope: CoroutineScope, mediaType: MediaType, isAutoPlay: Boolean)

    fun play()

    fun pause()

    fun isPlaying(): Boolean

    fun seekTo(positionMs: Long)

    fun release()

    fun setPlayListener(playerListener: VideoPlayListener)

    fun getDuration(): Long

    fun getBufferDuration(): Long

    fun getCurrentDuration(): Long

    fun getCurrentPlayStatus(): VideoPlayStatus

    fun onLifecyclePause()

    fun onLifecycleResume()
}