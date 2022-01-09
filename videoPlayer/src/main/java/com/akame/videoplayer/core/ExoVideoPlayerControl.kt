package com.akame.videoplayer.core

import android.content.Context
import android.view.SurfaceView
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.VideoPlayStatus
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*

class ExoVideoPlayerControl(context: Context, surfaceView: SurfaceView) : IVideoPlayerControl {
    private val player by lazy {
        ExoPlayer.Builder(context).build()
    }
    private lateinit var externalScope: CoroutineScope
    private var playerListener: VideoPlayListener? = null
    private var isRelease = false
    var lastStatePlaying = false //当前是否处于播放状态

    init {
        player.setVideoSurfaceView(surfaceView)
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playerListener?.onIsPlayingChanged(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> playerListener?.onPlaybackStateChanged(VideoPlayStatus.STATE_IDLE)
                    Player.STATE_BUFFERING -> playerListener?.onPlaybackStateChanged(VideoPlayStatus.STATE_BUFFERING)
                    Player.STATE_READY -> {
                        playerListener?.onPlaybackStateChanged(VideoPlayStatus.STATE_READY)
                        updateCurrentDuration()
                    }
                    Player.STATE_ENDED -> {
                        playerListener?.onPlaybackStateChanged(VideoPlayStatus.STATE_ENDED)
                    }
                }
            }
        })
    }

    override fun setUp(externalScope: CoroutineScope, mediaType: MediaType, isAutoPlay: Boolean) {
        this.externalScope = externalScope
        player.setMediaItem(createMediaItem(mediaType))
        player.prepare()
        if (isAutoPlay) player.play()
    }

    override fun play() {
        player.play()
        lastStatePlaying = true
    }

    override fun pause() {
        player.pause()
        lastStatePlaying = false
    }

    override fun isPlaying() = player.isPlaying

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override fun release() {
        player.release()
        isRelease = true
    }

    override fun setPlayListener(playerListener: VideoPlayListener) {
        this.playerListener = playerListener
    }

    override fun getDuration() = player.duration

    override fun getCurrentDuration(): Long = player.currentPosition

    override fun onLifecyclePause() {
        player.pause()
    }

    override fun onLifecycleResume() {
        if (lastStatePlaying) {
            player.play()
        }
    }

    private fun createMediaItem(mediaType: MediaType) = when (mediaType) {
        is MediaType.StringType -> MediaItem.fromUri(mediaType.mediaPath)
        is MediaType.UriType -> MediaItem.fromUri(mediaType.mediaPath)
    }

    private fun updateCurrentDuration() {
        externalScope.launch(Dispatchers.Default) {
            while (!isRelease) {
                withContext(Dispatchers.Main) {
                    if (isPlaying()) {
                        playerListener?.onPlayingCurrentDuration(player.currentPosition)
                    }
                    if (player.playbackState == Player.STATE_ENDED) {
                        playerListener?.onPlayingCurrentDuration(player.duration)
                    }
                }
                delay(1000)
            }
        }
    }
}