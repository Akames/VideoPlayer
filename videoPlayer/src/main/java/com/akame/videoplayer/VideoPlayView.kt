package com.akame.videoplayer

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.akame.videoplayer.core.VideoPlayCore
import com.akame.videoplayer.core.VideoPlayListener
import com.akame.videoplayer.layer.AlbumLayer
import com.akame.videoplayer.layer.VideoPlayControlLayer
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.VideoPlayStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoPlayView(context: Context, attributeSet: AttributeSet) :
    VideoPlayCore(context, attributeSet), VideoPlayListener, DefaultLifecycleObserver {
    private val videoControlLayer by lazy {
        VideoPlayControlLayer(this, videoPlay)
    }
    private val albumLayer by lazy {
        AlbumLayer()
    }
    private lateinit var controlView: View

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        videoPlay.setPlayListener(this)
        val albumView = albumLayer.injectView(context)
        addView(albumView)
        controlView = videoControlLayer.injectView()
        addView(controlView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        videoControlLayer.showDelayGone()
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        videoControlLayer.changeVisibility()
        return super.onTouchEvent(event)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        videoControlLayer.onIsPlayingChanged(isPlaying)
        albumLayer.onIsPlayingChanged(isPlaying)
    }

    override fun onPlayError(error: String) {

    }

    override fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus) {
        videoControlLayer.onPlaybackStateChanged(videoPlayStatus)
    }

    override fun onPlayingCurrentDuration(currentDuration: Long) {
        videoControlLayer.onPlayingCurrentDuration(currentDuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        removeView(controlView)
        controlView = videoControlLayer.injectView()
        addView(controlView)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        videoPlay.onLifecycleResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        videoPlay.onLifecyclePause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        videoPlay.release()
        videoControlLayer.release()
    }

    fun onBackPressed(): Boolean {
        return videoControlLayer.onBackPressed()
    }

    fun setup(
        externalScope: CoroutineScope,
        mediaType: MediaType,
        videoTitle: String,
        isAutoPlay: Boolean = true
    ) {
        this.isAutoPlay = isAutoPlay
        super.setup(externalScope, mediaType)
        albumLayer.setAlbum(externalScope, mediaType)
        videoControlLayer.setVideoTitle(videoTitle)
    }
}