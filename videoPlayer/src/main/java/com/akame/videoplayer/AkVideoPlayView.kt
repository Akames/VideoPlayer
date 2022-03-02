package com.akame.videoplayer

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.akame.videoplayer.core.VideoPlayCore
import com.akame.videoplayer.core.VideoPlayListener
import com.akame.videoplayer.layer.AlbumLayer
import com.akame.videoplayer.layer.BufferLoadLayer
import com.akame.videoplayer.layer.PlayCompleteLayer
import com.akame.videoplayer.layer.VideoPlayControlLayer
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.ScreenUtils
import com.akame.videoplayer.utils.VideoPlayStatus
import kotlinx.coroutines.CoroutineScope

class AkVideoPlayView(context: Context, attributeSet: AttributeSet) :
    VideoPlayCore(context, attributeSet), VideoPlayListener, DefaultLifecycleObserver {
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
        videoPlay.setPlayListener(this)
        addView(videoControlLayer)
        addView(albumLayer)
        addView(bufferLoadLayer)
        addView(playCompleteLayer)

        videoControlLayer.onEnterFullScreen = {
            val layoutParams = layoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
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

        videoControlLayer.onExitFullScreen = {
            val layoutParams = layoutParams
            layoutParams.width = originalWidth
            layoutParams.height = originalHeight
            super.onExitFullScreen()
            val contentView = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
            contentView.removeView(this)
            videoViewParent?.addView(this, viewPosition)
            viewPosition = -1
            videoViewParent = null
        }
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
        super.onParentVideoSizeChanged(videoWidth, videoHeight, originalWidth, originalHeight)
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
}