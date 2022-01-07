package com.akame.videoplayer

import android.app.Dialog
import android.content.Context
import android.view.SurfaceView
import androidx.appcompat.app.AlertDialog
import com.akame.videoplayer.core.VideoPlayListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.*

class ExoPlayer(
    context: Context, private val externalScope: CoroutineScope,
    mediaType: MediaType, surfaceView: SurfaceView
) :
    IVideoPlayerControl {
    private val player: Player
    private var playerListener: VideoPlayListener? = null
    private var isRelease = false

    init {
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(createMediaItem(mediaType))
            prepare()
            play() // 自动播放
            setVideoSurfaceView(surfaceView)
        }

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

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
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