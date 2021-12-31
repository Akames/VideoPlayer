package com.akame.videoplayer

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class ExoPlayer : IVideoPlayerControl {
    // https://media.w3.org/2010/05/sintel/trailer.mp4
    private lateinit var player: Player
    override fun init(context: Context, mediaType: MediaType) {
        val mediaItem = when (mediaType) {
            is MediaType.StringType -> MediaItem.fromUri(mediaType.mediaPath)
            is MediaType.UriType -> MediaItem.fromUri(mediaType.mediaPath)
        }
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    override fun play() {

    }

    override fun pause() {

    }

    override fun release() {

    }
}