package com.akame.videoplayer.core

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.akame.videoplayer.cleanParent
import com.akame.videoplayer.utils.MediaType
import com.akame.videoplayer.utils.ScreenUtils
import kotlinx.coroutines.CoroutineScope

abstract class VideoPlayCore(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet), VideoPlayListener {
    private val textureView by lazy {
        TextureView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
        }
    }

    //video的宽高
    private var videoWidth = 0
    private var videoHeight = 0

    //view的原始宽高
    var viewOriginalWidth = 0
    var viewOriginalHeight = 0

    val videoPlay: IVideoPlayerControl by lazy {
        ExoVideoPlayerControl(context, textureView).apply {
            setPlayListener(this@VideoPlayCore)
        }
    }

    private var defaultLayoutParamsWidth = 0
    private var defaultLayoutParamsHeight = 0
    private var fullLayoutParamsWidth = 0
    private var fullLayoutParamsHeight = 0

    init {
        this.setBackgroundColor(Color.BLACK)
        this.addView(textureView.cleanParent())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (viewOriginalHeight == 0 && viewOriginalWidth == 0) {
            viewOriginalWidth = w
            viewOriginalHeight = h
        }
    }

    override fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
        if (this.videoWidth == 0 && this.videoHeight == 0) {
            this.videoWidth = videoWidth
            this.videoHeight = videoHeight
            setVideoDefaultLayoutParams()
        }
    }

    open fun setup(externalScope: CoroutineScope, mediaType: MediaType, isAutoPlay: Boolean) {
        reset()
        videoPlay.setUp(externalScope, mediaType, isAutoPlay)
    }

    fun onEnterFullScreen() {
        val videoViewLP = textureView.layoutParams
        if (fullLayoutParamsWidth > 0 && fullLayoutParamsHeight > 0) {
            videoViewLP.width = fullLayoutParamsWidth
            videoViewLP.height = fullLayoutParamsHeight
            return
        }
        val videoLand = videoWidth > videoHeight
        if (videoLand) {
            videoViewLP.height = ScreenUtils.getScreenWidth(context)
            val ratio = videoViewLP.height * 1f / videoHeight
            videoViewLP.width = (videoWidth * ratio).toInt()
        } else {
            videoViewLP.width = ScreenUtils.getScreenWidth(context)
            val ratio = videoViewLP.width * 1f / videoWidth
            videoViewLP.height = (videoHeight * ratio).toInt()
        }
        fullLayoutParamsWidth = videoViewLP.width
        fullLayoutParamsHeight = videoViewLP.height
    }

    fun onExitFullScreen() {
        textureView.layoutParams.apply {
            width = defaultLayoutParamsWidth
            height = defaultLayoutParamsHeight
        }
    }

    private fun setVideoDefaultLayoutParams() {
        val videoViewLP = textureView.layoutParams
        val videoLand = videoWidth > videoHeight
        if (videoLand) {
            videoViewLP.width = width
            val ratio = videoViewLP.width * 1f / videoWidth
            videoViewLP.height = (videoHeight * ratio).toInt()
        } else {
            videoViewLP.height = height
            val ratio = videoViewLP.height * 1f / videoHeight
            videoViewLP.width = (videoWidth * ratio).toInt()
        }
        defaultLayoutParamsWidth = videoViewLP.width
        defaultLayoutParamsHeight = videoViewLP.height
    }

    private fun reset() {
        fullLayoutParamsWidth = 0
        fullLayoutParamsHeight = 0
        videoWidth = 0
        videoHeight = 0
    }
}