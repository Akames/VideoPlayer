package com.akame.videoplayer.wigth

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.akame.videoplayer.core.VideoPlayCore
import com.akame.videoplayer.layer.AlbumLayer
import com.akame.videoplayer.layer.BufferLoadLayer
import com.akame.videoplayer.layer.PlayCompleteLayer
import com.akame.videoplayer.layer.VideoPlayControlLayer
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.VideoPlayStatus
import kotlinx.coroutines.CoroutineScope

class VideoPlayView(context: Context, attributeSet: AttributeSet) : VideoPlayCore(context, attributeSet), DefaultLifecycleObserver {
    private var viewPosition = -1
    private var videoViewParent: ViewGroup? = null
    private val videoControlLayer by lazy {
        VideoPlayControlLayer(context, videoPlay)
    }
    private val albumLayer by lazy {
        AlbumLayer(context)
    }
    private val playCompleteLayer by lazy {
        PlayCompleteLayer(context, videoPlay).apply {
            onBackClickListener = {
                videoControlLayer.onBackPressed()
            }
        }
    }
    private val bufferLoadLayer by lazy {
        BufferLoadLayer(context)
    }

    init {
        addView(videoControlLayer)
        addView(albumLayer)
        addView(bufferLoadLayer)
        addView(playCompleteLayer)
        fullScreenChangeListener()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        videoControlLayer.showDelayGone()
        super.dispatchTouchEvent(ev)
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            videoControlLayer.changeVisibility()
        }
        return super.onTouchEvent(event)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        videoControlLayer.onIsPlayingChanged(isPlaying)
        albumLayer.onIsPlayingChanged(isPlaying)
    }

    override fun onPlayError(error: String) {
        bufferLoadLayer.onPlayerError()
        videoControlLayer.onPlayerError()
    }

    override fun onPlaybackStateChanged(videoPlayStatus: VideoPlayStatus) {
        videoControlLayer.onPlaybackStateChanged(videoPlayStatus)
        playCompleteLayer.onPlaybackStateChanged(videoPlayStatus)
        bufferLoadLayer.onPlaybackStateChanged(videoPlayStatus)
    }

    override fun onPlayingCurrentDuration(currentDuration: Long, bufferDuration: Long) {
        videoControlLayer.onPlayingCurrentDuration(currentDuration, bufferDuration)
    }

    override fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
        super.onVideoSizeChanged(videoWidth, videoHeight)
        videoControlLayer.onVideoSizeChanged(videoWidth, videoHeight)
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
        albumPath: String? = null,
        isAutoPlay: Boolean = true
    ) {
        super.setup(externalScope, mediaType, isAutoPlay)
        albumLayer.setUp(albumPath)
        videoControlLayer.setUp(videoTitle)
        playCompleteLayer.setUp(videoTitle)
    }

    private fun fullScreenChangeListener() {
        //进入全屏监听
        videoControlLayer.onEnterFullScreen = {
            val lp = layoutParams
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            super.onEnterFullScreen()
            if (viewPosition == -1 || videoViewParent == null) {
                val contentView = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
                this.parent?.let {
                    if (it is ViewGroup) {
                        viewPosition = it.indexOfChild(this)
                        videoViewParent = it
                        it.removeView(this)
                    }
                }
                contentView.addView(this)
            }
        }
        //退出全屏监听
        videoControlLayer.onExitFullScreen = {
            val layoutParams = layoutParams
            layoutParams.width = viewOriginalWidth
            layoutParams.height = viewOriginalHeight
            super.onExitFullScreen()
            val contentView = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
            contentView.removeView(this)
            videoViewParent?.addView(this, viewPosition)
            viewPosition = -1
            videoViewParent = null
        }
    }
}