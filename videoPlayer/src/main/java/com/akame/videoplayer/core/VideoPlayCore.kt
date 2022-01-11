package com.akame.videoplayer.core

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.SurfaceView
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.ScreenUtils
import kotlinx.coroutines.CoroutineScope

abstract class VideoPlayCore(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {
    private var videoWidth = 0
    private var videoHeight = 0
    private val textureView by lazy {
        TextureView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }
    }
    var originalWidth = 0
    var originalHeight = 0
    val videoPlay: IVideoPlayerControl by lazy {
        ExoVideoPlayerControl(context, textureView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setBackgroundColor(Color.BLACK)
        textureView.parent?.let {
            if (it is ViewGroup) {
                it.removeView(textureView)
            }
        }
        addView(textureView)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (originalHeight == 0 && originalWidth == 0) {
            originalWidth = w
            originalHeight = h
        }
    }

    open fun setup(externalScope: CoroutineScope, mediaType: MediaType, isAutoPlay: Boolean) {
        videoPlay.setUp(externalScope, mediaType, isAutoPlay)
    }

    fun onParentVideoSizeChanged(videoWidth: Int, videoHeight: Int, viewWidth: Int, viewHeight: Int) {
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
        updateVideoPlaySize(viewWidth, viewHeight)
    }

    fun onEnterFullScreen() {
        //获取全屏的宽高
        val widthPixels = ScreenUtils.getScreenWidth(context)
        val heightPixels = ScreenUtils.getScreenHeight(context)
        //因为是发生了旋转。所有宽高要倒过来
        val reverse = context.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
        val isLandVideo = videoWidth > videoHeight //是否为横屏的视频
        if (reverse && isLandVideo) {
            updateVideoPlaySize(heightPixels, widthPixels)
        } else {
            updateVideoPlaySize(widthPixels, heightPixels)
        }
    }

    fun onExitFullScreen() {
        updateVideoPlaySize(originalWidth, originalHeight)
    }

    private fun updateVideoPlaySize(viewWidth: Int, viewHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            val videoViewLP = textureView.layoutParams
            val isLanded = layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT
            if (videoWidth >= videoHeight && !isLanded) {
                // 横屏视频 并且为半屏播放
                videoViewLP.width = viewWidth
                val ratio = viewWidth * 1f / videoWidth
                videoViewLP.height = (ratio * videoHeight).toInt()
            } else {
                videoViewLP.height = viewHeight
                val ratio = viewHeight * 1f / videoHeight
                videoViewLP.width = (ratio * videoWidth).toInt()
            }
        }
    }
}