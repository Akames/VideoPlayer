package com.akame.videoplayer.core

import com.akame.videoplayer.utils.MediaType
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

    fun getCurrentDuration(): Long

    fun onLifecyclePause()

    fun onLifecycleResume()
}