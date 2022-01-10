package com.akame.videoplayer.core

import android.content.Context
import android.view.SurfaceView
import android.view.TextureView
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.VideoPlayStatus
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.HttpDataSource
import kotlinx.coroutines.*

class ExoVideoPlayerControl(context: Context, playView: TextureView) : IVideoPlayerControl {
    private val player by lazy {
        ExoPlayer.Builder(context).build()
    }
    private lateinit var externalScope: CoroutineScope
    private var playerListener: VideoPlayListener? = null
    private var isRelease = false
    var lastStatePlaying = false //当前是否处于播放状态

    init {
        player.setVideoTextureView(playView)
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

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                val errorMsg = "code: ${error.errorCode} errorCodeName: ${error.errorCodeName} message: ${error.message}"
                playerListener?.onPlayError(errorMsg)

                //如果是网络问题一直重试
                if (error.cause is HttpDataSource.HttpDataSourceException) {
                    player.prepare()
                    player.playWhenReady = lastStatePlaying
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

    override fun onLifecyclePause() {
        player.pause()
    }

    override fun onLifecycleResume() {
        if (lastStatePlaying) {
            player.play()
        }
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

    override fun getBufferDuration(): Long = player.bufferedPosition

    override fun getCurrentDuration(): Long = player.currentPosition

    override fun getCurrentPlayStatus(): VideoPlayStatus {
        return when (player.playbackState) {
            Player.STATE_IDLE -> VideoPlayStatus.STATE_IDLE
            Player.STATE_BUFFERING -> VideoPlayStatus.STATE_BUFFERING
            Player.STATE_READY -> VideoPlayStatus.STATE_READY
            Player.STATE_ENDED -> VideoPlayStatus.STATE_ENDED
            else -> VideoPlayStatus.STATE_IDLE
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
                        playerListener?.onPlayingCurrentDuration(getCurrentDuration(), getBufferDuration())
                    }
                    if (player.playbackState == Player.STATE_ENDED) {
                        playerListener?.onPlayingCurrentDuration(getDuration(), getDuration())
                    }
                }
                delay(1000)
            }
        }
    }
}